package com.rgr.rutappa.android.provider

import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ConfigUpdate
import com.google.firebase.remoteconfig.ConfigUpdateListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.rgr.rutappa.domain.error.RemoteConfigError
import com.rgr.rutappa.domain.model.PartnersListBO
import com.rgr.rutappa.domain.model.ResultKMM
import com.rgr.rutappa.domain.model.TapaItemBo
import com.rgr.rutappa.domain.provider.FirebaseRemoteDataProvider
import kotlinx.serialization.json.Json
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class FirebaseRemoteDataProviderImpl(
    private val activityProvider: ActivityProvider
): FirebaseRemoteDataProvider {

    private val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig

    init {
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3600
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.addOnConfigUpdateListener(object : ConfigUpdateListener {
            override fun onUpdate(configUpdate: ConfigUpdate) {
                if (configUpdate.updatedKeys.contains("TapasV2")) {
                    remoteConfig.activate().addOnCompleteListener {
                    }
                }
            }

            override fun onError(error: FirebaseRemoteConfigException) {
            }
        })

    }

    override suspend fun getListTapas(configuration: Int): ResultKMM<List<TapaItemBo>> {
        remoteConfig.setDefaultsAsync(configuration)
        val activity = activityProvider.getActivity()
        val result = suspendCoroutine { cont ->
            activity?.let {
                remoteConfig.fetchAndActivate().addOnCompleteListener(it) { task ->
                    if (task.isSuccessful) {
                        val structureString = remoteConfig.getString("TapasV2")
                        val tapas = Json.decodeFromString<List<TapaItemBo>>(structureString)
                        cont.resume(ResultKMM.Success(tapas))
                    } else {
                        cont.resume(ResultKMM.Failure(RemoteConfigError.RemoteConfigTaskFailed))
                    }
                }
            } ?: cont.resume(ResultKMM.Failure(RemoteConfigError.NoActivityError))
        }
        return result
    }

    override suspend fun getTapaDetail(configuration: Int, id: String): ResultKMM<TapaItemBo> {
        remoteConfig.setDefaultsAsync(configuration)
        val activity = activityProvider.getActivity()
        val result = suspendCoroutine<ResultKMM<TapaItemBo>> { cont ->
            activity?.let {
                remoteConfig.fetchAndActivate().addOnCompleteListener(it) { task ->
                    if (task.isSuccessful) {
                        val structureString = remoteConfig.getString("TapasV2")
                        val tapas = Json.decodeFromString<List<TapaItemBo>>(structureString)
                        tapas.firstOrNull {tapa -> tapa.id == id }?.let { tapa ->
                            cont.resume(ResultKMM.Success(tapa))
                        } ?: cont.resume(ResultKMM.Failure(RemoteConfigError.ItemNotFound))

                    } else {
                        cont.resume(ResultKMM.Failure(RemoteConfigError.RemoteConfigTaskFailed))
                    }
                }
            } ?: cont.resume(ResultKMM.Failure(RemoteConfigError.NoActivityError))
        }
        return result
    }

    override suspend fun getPartners(configuration: Int): ResultKMM<PartnersListBO> {
        remoteConfig.setDefaultsAsync(configuration)
        val activity = activityProvider.getActivity()
        val result = suspendCoroutine { cont ->
            activity?.let {
                remoteConfig.fetchAndActivate().addOnCompleteListener(it) { task ->
                    if (task.isSuccessful) {
                        val structureString = remoteConfig.getString("Patrocinadores")
                        val tapas = Json.decodeFromString<PartnersListBO>(structureString)
                        cont.resume(ResultKMM.Success(tapas))
                    } else {
                        cont.resume(ResultKMM.Failure(RemoteConfigError.RemoteConfigTaskFailed))
                    }
                }
            } ?: cont.resume(ResultKMM.Failure(RemoteConfigError.NoActivityError))
        }
        return result
    }
}