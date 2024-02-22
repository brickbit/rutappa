package com.rgr.rutappa.app.viewModel

import com.rgr.rutappa.app.flow.toCommonStateFlow
import com.rgr.rutappa.app.state.DetailState
import com.rgr.rutappa.domain.error.FirestoreError
import com.rgr.rutappa.domain.model.ResultKMM
import com.rgr.rutappa.domain.model.TapaItemBo
import com.rgr.rutappa.domain.useCase.GetTapaDetailUseCase
import com.rgr.rutappa.domain.useCase.TapaVotedUseCase
import com.rgr.rutappa.domain.useCase.VoteUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DetailViewModel(
    private val detailUseCase: GetTapaDetailUseCase,
    private val voteUseCase: VoteUseCase,
    private val tapaVotedUseCase: TapaVotedUseCase
): BaseViewModel() {
    private val _state: MutableStateFlow<DetailState> = MutableStateFlow(DetailState.Loading)
    val state = _state.stateIn(
        scope = scope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DetailState.Loading
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
            val result = detailUseCase.invoke(configuration, id)
                when(result) {
                    is ResultKMM.Success -> {
                        tapaDetail = result.data
                        val voted = tapaVotedUseCase.invoke(id)
                        _state.update {
                            DetailState.Loaded(result.data, voted)
                        }
                    }
                    is ResultKMM.Failure -> {
                        tapaDetail?.let { tapaDetail ->
                            val voted = tapaVotedUseCase.invoke(id)
                            _state.update {
                                DetailState.Loaded(tapaDetail, voted)
                            }
                        }
                    }
                }
        }
    }

    fun vote(vote: Int, tapa: String) {
        scope.launch {
            _state.update {
                DetailState.Loading
            }
            val result = voteUseCase.invoke(vote,tapa)
            if (result.isSuccess) {
                tapaDetail?.let { tapaDetail ->
                    _state.update {
                        DetailState.Voted(tapaDetail,true)
                    }
                }
            } else {
                tapaDetail?.let { tapaDetail ->
                    if(result.exceptionOrNull() is FirestoreError.TapaVotedYet) {
                        _state.update {
                            DetailState.Voted(tapaDetail, true)
                        }
                    } else {
                        val voted = tapaVotedUseCase.invoke(tapa)
                        _state.update {
                            DetailState.Loaded(tapaDetail,voted)
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
