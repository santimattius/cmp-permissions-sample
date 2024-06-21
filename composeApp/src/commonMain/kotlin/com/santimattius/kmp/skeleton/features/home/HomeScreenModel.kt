package com.santimattius.kmp.skeleton.features.home

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.santimattius.kmp.skeleton.core.data.PictureRepository
import com.santimattius.kmp.skeleton.core.domain.Picture
import dev.icerock.moko.permissions.DeniedAlwaysException
import dev.icerock.moko.permissions.DeniedException
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionsController
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


data class HomeUiState(
    val isLoading: Boolean = false,
    val hasError: Boolean = false,
    val permissionGranted: Boolean = false,
    val permissionMessage: String = "",
    val data: Picture? = null,
)

class HomeScreenModel(
    private val repository: PictureRepository,
    val controller: PermissionsController
) : StateScreenModel<HomeUiState>(HomeUiState()) {

    private val exceptionHandler = CoroutineExceptionHandler { _, _ ->
        mutableState.update { it.copy(isLoading = false, hasError = true) }
    }
    private var job: Job? = null

    init {
        randomImage()
    }

    fun randomImage() {
        mutableState.update { it.copy(isLoading = true, hasError = false) }
        job?.cancel()
        job = screenModelScope.launch(exceptionHandler) {
            repository.random().onSuccess { picture ->
                mutableState.update { it.copy(isLoading = false, data = picture) }
            }.onFailure {
                mutableState.update { it.copy(isLoading = false, hasError = true) }
            }
        }
    }

    fun checkPermission() {
        job?.cancel()
        job = screenModelScope.launch(exceptionHandler) {
            try {
                controller.providePermission(Permission.GALLERY)
                mutableState.update {
                    it.copy(
                        permissionGranted = true,
                        permissionMessage = "Permission granted"
                    )
                }
            } catch (deniedAlways: DeniedAlwaysException) {
                mutableState.update {
                    it.copy(
                        permissionGranted = false,
                        permissionMessage = "Permission denied always"
                    )
                }
            } catch (denied: DeniedException) {
                // Permission was denied.
                mutableState.update {
                    it.copy(
                        permissionGranted = false,
                        permissionMessage = "Permission denied"
                    )
                }
            }
        }
    }
}