package com.example.ui.camera

import android.content.Context
import android.util.Log
import androidx.camera.core.Camera
import androidx.camera.core.CameraControl
import androidx.camera.core.CameraInfo
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.core.UseCase
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.Executor
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Utility manager that encapsulates CameraX ProcessCameraProvider initialization,
 * lifecycle observation, use-case binding, lens switching, torch control, and
 * output file preparation for video recording.
 */
class CameraManager(private val context: Context) : DefaultLifecycleObserver {

    companion object {
        private const val TAG = "CameraManager"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
    }

    private var cameraProvider: ProcessCameraProvider? = null
    private var camera: Camera? = null
    private var previewUseCase: Preview? = null
    private var currentLifecycleOwner: LifecycleOwner? = null

    var currentLensFacing: Int = CameraSelector.LENS_FACING_BACK
        private set

    var isTorchEnabled: Boolean = false
        private set

    /**
     * Asynchronously retrieves the ProcessCameraProvider instance using coroutines.
     */
    suspend fun getCameraProvider(): ProcessCameraProvider {
        cameraProvider?.let { return it }

        return suspendCancellableCoroutine { continuation ->
            val providerFuture: ListenableFuture<ProcessCameraProvider> =
                ProcessCameraProvider.getInstance(context)

            val executor: Executor = ContextCompat.getMainExecutor(context)
            providerFuture.addListener({
                try {
                    val provider = providerFuture.get()
                    cameraProvider = provider
                    continuation.resume(provider)
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to obtain ProcessCameraProvider", e)
                    continuation.resumeWithException(e)
                }
            }, executor)
        }
    }

    /**
     * Binds preview and any additional use cases to the provided LifecycleOwner.
     */
    suspend fun startCamera(
        lifecycleOwner: LifecycleOwner,
        surfaceProvider: Preview.SurfaceProvider? = null,
        lensFacing: Int = currentLensFacing,
        vararg additionalUseCases: UseCase
    ): Camera? {
        currentLifecycleOwner?.lifecycle?.removeObserver(this)
        currentLifecycleOwner = lifecycleOwner
        lifecycleOwner.lifecycle.addObserver(this)

        val provider = getCameraProvider()
        currentLensFacing = lensFacing

        val cameraSelector = CameraSelector.Builder()
            .requireLensFacing(lensFacing)
            .build()

        // Unbind previous use cases before rebinding
        provider.unbindAll()

        val useCases = mutableListOf<UseCase>()

        // Configure Preview UseCase
        val preview = Preview.Builder().build().also {
            if (surfaceProvider != null) {
                it.setSurfaceProvider(surfaceProvider)
            }
            previewUseCase = it
        }
        useCases.add(preview)

        // Attach any extra use cases (e.g. video capture / analysis)
        useCases.addAll(additionalUseCases)

        return try {
            if (provider.hasCamera(cameraSelector)) {
                camera = provider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    *useCases.toTypedArray()
                )
                // Restore torch if back camera supports it
                if (isTorchEnabled && lensFacing == CameraSelector.LENS_FACING_BACK) {
                    camera?.cameraControl?.enableTorch(true)
                }
                camera
            } else {
                Log.w(TAG, "Requested camera lens not available on this device: $lensFacing")
                null
            }
        } catch (exc: Exception) {
            Log.e(TAG, "CameraX use case binding failed", exc)
            null
        }
    }

    /**
     * Attaches or updates the SurfaceProvider for the active preview use case.
     */
    fun attachSurfaceProvider(surfaceProvider: Preview.SurfaceProvider) {
        previewUseCase?.setSurfaceProvider(surfaceProvider)
    }

    /**
     * Flips between Front and Back camera lenses.
     */
    suspend fun flipCamera(
        lifecycleOwner: LifecycleOwner,
        surfaceProvider: Preview.SurfaceProvider? = null,
        vararg additionalUseCases: UseCase
    ): Camera? {
        val newLens = if (currentLensFacing == CameraSelector.LENS_FACING_BACK) {
            CameraSelector.LENS_FACING_FRONT
        } else {
            CameraSelector.LENS_FACING_BACK
        }
        return startCamera(lifecycleOwner, surfaceProvider, newLens, *additionalUseCases)
    }

    /**
     * Toggles flashlight / torch mode.
     */
    fun toggleTorch(enabled: Boolean? = null) {
        val newState = enabled ?: !isTorchEnabled
        camera?.cameraControl?.enableTorch(newState)
        isTorchEnabled = newState
    }

    /**
     * Sets optical / digital zoom ratio.
     */
    fun setZoomRatio(ratio: Float) {
        camera?.cameraControl?.setZoomRatio(ratio)
    }

    /**
     * Returns the camera control instance for advanced gestures (e.g., tap to focus).
     */
    fun getCameraControl(): CameraControl? = camera?.cameraControl

    /**
     * Returns camera info (torch state, zoom state, sensor rotation).
     */
    fun getCameraInfo(): CameraInfo? = camera?.cameraInfo

    /**
     * Generates a unique target MP4 video file in the app storage directory.
     */
    fun createVideoOutputFile(directory: File = context.cacheDir): File {
        val timeStamp = SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(Date())
        return File(directory, "TIKTOK_VID_$timeStamp.mp4")
    }

    /**
     * Safely unbinds all CameraX use cases and releases camera references.
     */
    fun unbind() {
        try {
            cameraProvider?.unbindAll()
            camera = null
            previewUseCase = null
        } catch (e: Exception) {
            Log.e(TAG, "Error unbinding camera use cases", e)
        }
    }

    // Lifecycle observation hooks
    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        Log.d(TAG, "LifecycleOwner onResume: camera active")
    }

    override fun onPause(owner: LifecycleOwner) {
        super.onPause(owner)
        Log.d(TAG, "LifecycleOwner onPause: safely disabling torch")
        if (isTorchEnabled) {
            toggleTorch(false)
        }
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        Log.d(TAG, "LifecycleOwner onDestroy: unbinding camera and releasing resources")
        unbind()
        currentLifecycleOwner?.lifecycle?.removeObserver(this)
        currentLifecycleOwner = null
    }
}
