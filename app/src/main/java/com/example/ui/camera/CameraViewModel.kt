package com.example.ui.camera

import android.content.Context
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.core.UseCase
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File

/**
 * UI State representing camera preview and video recording preparation status.
 */
data class CameraUiState(
    val isInitialized: Boolean = false,
    val isBound: Boolean = false,
    val lensFacing: Int = CameraSelector.LENS_FACING_BACK,
    val isFlashOn: Boolean = false,
    val isRecording: Boolean = false,
    val recordingDurationSeconds: Int = 0,
    val videoOutputFile: File? = null,
    val zoomRatio: Float = 1.0f,
    val hasCameraPermission: Boolean = false,
    val errorMessage: String? = null
)

/**
 * ViewModel responsible for managing CameraX ProcessCameraProvider lifecycle,
 * camera controls (flip, torch, zoom), and video recording preparation.
 */
class CameraViewModel(
    private var cameraManager: CameraManager? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(CameraUiState())
    val uiState: StateFlow<CameraUiState> = _uiState.asStateFlow()

    private fun getOrCreateManager(context: Context): CameraManager {
        return cameraManager ?: CameraManager(context.applicationContext).also {
            cameraManager = it
        }
    }

    /**
     * Initializes CameraX and binds the preview UseCase to the given LifecycleOwner.
     */
    fun bindCamera(
        context: Context,
        lifecycleOwner: LifecycleOwner,
        surfaceProvider: Preview.SurfaceProvider? = null,
        vararg additionalUseCases: UseCase
    ) {
        viewModelScope.launch {
            try {
                val manager = getOrCreateManager(context)
                val camera = manager.startCamera(
                    lifecycleOwner = lifecycleOwner,
                    surfaceProvider = surfaceProvider,
                    lensFacing = _uiState.value.lensFacing,
                    *additionalUseCases
                )
                _uiState.update {
                    it.copy(
                        isInitialized = true,
                        isBound = camera != null,
                        lensFacing = manager.currentLensFacing,
                        isFlashOn = manager.isTorchEnabled,
                        errorMessage = if (camera == null) "Unable to bind camera use cases" else null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isInitialized = false,
                        isBound = false,
                        errorMessage = e.localizedMessage ?: "Failed to initialize camera"
                    )
                }
            }
        }
    }

    /**
     * Updates the surface provider when a PreviewView is attached.
     */
    fun attachSurfaceProvider(surfaceProvider: Preview.SurfaceProvider) {
        cameraManager?.attachSurfaceProvider(surfaceProvider)
    }

    /**
     * Flips between Front and Back camera lenses.
     */
    fun flipCamera(
        lifecycleOwner: LifecycleOwner,
        surfaceProvider: Preview.SurfaceProvider? = null,
        vararg additionalUseCases: UseCase
    ) {
        viewModelScope.launch {
            try {
                cameraManager?.let { manager ->
                    val camera = manager.flipCamera(
                        lifecycleOwner = lifecycleOwner,
                        surfaceProvider = surfaceProvider,
                        *additionalUseCases
                    )
                    _uiState.update {
                        it.copy(
                            isBound = camera != null,
                            lensFacing = manager.currentLensFacing,
                            isFlashOn = manager.isTorchEnabled
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(errorMessage = e.localizedMessage ?: "Failed to flip camera")
                }
            }
        }
    }

    /**
     * Toggles flashlight / torch state on the active camera.
     */
    fun toggleFlash() {
        cameraManager?.let { manager ->
            manager.toggleTorch()
            _uiState.update {
                it.copy(isFlashOn = manager.isTorchEnabled)
            }
        }
    }

    /**
     * Sets optical / digital zoom ratio.
     */
    fun setZoom(ratio: Float) {
        cameraManager?.setZoomRatio(ratio)
        _uiState.update { it.copy(zoomRatio = ratio) }
    }

    /**
     * Prepares an MP4 output file ready for video recording.
     */
    fun prepareVideoOutputFile(context: Context): File {
        val manager = getOrCreateManager(context)
        val file = manager.createVideoOutputFile()
        _uiState.update { it.copy(videoOutputFile = file) }
        return file
    }

    /**
     * Video capture method that toggles video recording.
     * When idle, prepares the target file (if context provided) and begins video capture.
     * When recording, halts recording and finalizes output.
     */
    fun captureVideo(context: Context? = null): Boolean {
        return if (_uiState.value.isRecording) {
            stopVideoCapture()
            false
        } else {
            val file = if (context != null) prepareVideoOutputFile(context) else _uiState.value.videoOutputFile
            startVideoCapture(file)
            true
        }
    }

    /**
     * Starts video capture explicitly to the provided or active output file.
     */
    fun startVideoCapture(targetFile: File? = null) {
        startRecording(targetFile)
    }

    /**
     * Starts video capture explicitly with Context to ensure output file is prepared.
     */
    fun startVideoCapture(context: Context) {
        val file = prepareVideoOutputFile(context)
        startRecording(file)
    }

    /**
     * Stops active video capture.
     */
    fun stopVideoCapture() {
        stopRecording()
    }

    /**
     * Flags recording state as started.
     */
    fun startRecording(targetFile: File? = null) {
        _uiState.update {
            it.copy(
                isRecording = true,
                recordingDurationSeconds = 0,
                videoOutputFile = targetFile ?: it.videoOutputFile
            )
        }
    }

    /**
     * Flags recording state as stopped.
     */
    fun stopRecording() {
        _uiState.update {
            it.copy(isRecording = false)
        }
    }

    /**
     * Updates recording duration counter in seconds.
     */
    fun updateRecordingDuration(seconds: Int) {
        _uiState.update {
            it.copy(recordingDurationSeconds = seconds)
        }
    }

    /**
     * Updates runtime camera permission state.
     */
    fun setPermissionGranted(granted: Boolean) {
        _uiState.update {
            it.copy(hasCameraPermission = granted)
        }
    }

    /**
     * Clears any active error message.
     */
    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    /**
     * Unbinds camera from lifecycle.
     */
    fun unbind() {
        cameraManager?.unbind()
        _uiState.update {
            it.copy(isBound = false, isFlashOn = false)
        }
    }

    override fun onCleared() {
        super.onCleared()
        unbind()
    }
}
