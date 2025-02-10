package com.rgr.rutappa.app.viewModel

import com.rgr.rutappa.app.flow.toCommonStateFlow
import com.rgr.rutappa.app.state.DetailState
import com.rgr.rutappa.app.state.VoteStatus
import com.rgr.rutappa.domain.error.FirestoreError
import com.rgr.rutappa.domain.model.ResultKMM
import com.rgr.rutappa.domain.model.TapaItemBo
import com.rgr.rutappa.domain.useCase.ActiveLocationUseCase
import com.rgr.rutappa.domain.useCase.GetLocationUseCase
import com.rgr.rutappa.domain.useCase.GetTapaDetailUseCase
import com.rgr.rutappa.domain.useCase.HasLocationPermissionUseCase
import com.rgr.rutappa.domain.useCase.IsLocationActiveUseCase
import com.rgr.rutappa.domain.useCase.IsWithinRadiusUseCase
import com.rgr.rutappa.domain.useCase.RequestLocationPermissionUseCase
import com.rgr.rutappa.domain.useCase.TapaVotedUseCase
import com.rgr.rutappa.domain.useCase.VoteUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DetailViewModel(
    private val detailUseCase: GetTapaDetailUseCase,
    private val voteUseCase: VoteUseCase,
    private val tapaVotedUseCase: TapaVotedUseCase,
    private val locationUseCase: GetLocationUseCase,
    private val hasLocationPermissionUseCase: HasLocationPermissionUseCase,
    private val requestLocationPermissionUseCase: RequestLocationPermissionUseCase,
    private val isLocationActiveUseCase: IsLocationActiveUseCase,
    private val activeLocationUseCase: ActiveLocationUseCase,
    private val isWithinRadiusUseCase: IsWithinRadiusUseCase
): BaseViewModel() {
    private val _state: MutableStateFlow<DetailState> = MutableStateFlow(DetailState())
    val state = _state.stateIn(
        scope = scope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DetailState()
    ).toCommonStateFlow()
    private val _errorState: MutableStateFlow<FirestoreError> = MutableStateFlow(FirestoreError.NoError)
    val errorState = _errorState.stateIn(
        scope = scope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = FirestoreError.NoError
    ).toCommonStateFlow()

    private var tapaDetail: TapaItemBo? = null

    fun getDetail(configuration: Int, id: String) {
        scope.launch {
            _state.update { it.copy(isLoading = true) }
            val result = detailUseCase.invoke(configuration, id)
                when(result) {
                    is ResultKMM.Success -> {
                        tapaDetail = result.data
                        val voted = tapaVotedUseCase.invoke(id)
                        _state.update {
                            it.copy(isLoading = false, tapa = result.data, voteStatus = if(state.value.voteStatus == VoteStatus.CAN_VOTE && voted) VoteStatus.VOTED else state.value.voteStatus)
                        }
                    }
                    is ResultKMM.Failure -> {
                        tapaDetail?.let { tapaDetail ->
                            val voted = tapaVotedUseCase.invoke(id)
                            _state.update {
                                it.copy(isLoading = false, tapa = tapaDetail, voteStatus = if(state.value.voteStatus == VoteStatus.CAN_VOTE && voted) VoteStatus.VOTED else state.value.voteStatus)
                            }
                        }
                    }
                }
        }
    }

    fun getLocation() {
        scope.launch {
            handleLocationPermission()
            if (state.value.voteStatus != VoteStatus.UNKNOWN && state.value.voteStatus != VoteStatus.LOCATION_INACTIVE) {
                if (isLocationActiveUseCase.invoke()) {
                    handleLocation()
                } else {
                    _state.update { it.copy(voteStatus = VoteStatus.LOCATION_INACTIVE) }
                    activeLocationUseCase.invoke()
                }
            }
        }
    }

    private fun handleLocationPermission() {
        if(hasLocationPermissionUseCase.invoke()) {
            handleLocation()
        } else {
            requestLocationPermissionUseCase.invoke()
        }
    }


    private fun handleLocation() {
        scope.launch {
            _state.update { it.copy(isLoading = true) }
            when(val location = locationUseCase.invoke()) {
                is ResultKMM.Success -> {
                    _state.update {
                        val isWithinRadius = isWithinRadiusUseCase(
                            deviceLat = location.data.first,
                            deviceLon = location.data.second,
                            localLat = state.value.tapa!!.local.latitude,
                            localLon = state.value.tapa!!.local.longitude
                        )
                        it.copy(isLoading = false, location = location.data, voteStatus = if (isWithinRadius) VoteStatus.CAN_VOTE else VoteStatus.OUT_OF_RANGE)
                    }
                }
                is ResultKMM.Failure -> {
                    _state.update {
                        it.copy(isLoading = false, voteStatus = VoteStatus.UNABLE_OBTAIN_LOCATION)
                    }
                }
            }
        }
    }

    fun vote(vote: Int, tapa: String) {
        scope.launch {
            _state.update { it.copy(isLoading = true) }
            val result = voteUseCase.invoke(vote,tapa)
            if (result.isSuccess) {
                tapaDetail?.let { tapaDetail ->
                    _state.update {
                        it.copy(isLoading = false, tapa = tapaDetail, voteStatus = if(state.value.voteStatus == VoteStatus.CAN_VOTE) VoteStatus.VOTED else state.value.voteStatus)
                    }
                }
            } else {
                tapaDetail?.let { tapaDetail ->
                    if(result.exceptionOrNull() is FirestoreError.TapaVotedYet) {
                        _state.update {
                            it.copy(isLoading = false, tapa = tapaDetail, voteStatus = if(state.value.voteStatus == VoteStatus.CAN_VOTE) VoteStatus.VOTED else state.value.voteStatus)
                        }
                    } else {
                        val voted = tapaVotedUseCase.invoke(tapa)
                        _state.update {
                            it.copy(isLoading = false, tapa = tapaDetail, voteStatus = if(state.value.voteStatus == VoteStatus.CAN_VOTE && voted) VoteStatus.VOTED else state.value.voteStatus)
                        }
                    }
                }
                result.exceptionOrNull()?.let {
                    processError(it)
                }
            }
        }
    }

    private fun processError(throwable: Throwable) {
        when (throwable as FirestoreError) {
            FirestoreError.TapaVotedYet -> {
                _errorState.update {
                    FirestoreError.TapaVotedYet
                }
            }
            FirestoreError.NoError -> { /*Nothing to do here*/ }
            FirestoreError.NoUserError -> {
                _errorState.update {
                    FirestoreError.NoUserError
                }
            }
            FirestoreError.ReadingError -> {
                _errorState.update {
                    FirestoreError.ReadingError
                }
            }
            FirestoreError.WritingError -> {
                _errorState.update {
                    FirestoreError.WritingError
                }
            }
        }
    }

    fun clearError() {
        _errorState.update {
            FirestoreError.NoError
        }
    }
}
