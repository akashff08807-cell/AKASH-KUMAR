package com.example

import com.example.ui.camera.CameraViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File

class CameraViewModelTest {

    private lateinit var viewModel: CameraViewModel

    @Before
    fun setUp() {
        viewModel = CameraViewModel()
    }

    @Test
    fun testInitialState() {
        val state = viewModel.uiState.value
        assertFalse(state.isInitialized)
        assertFalse(state.isBound)
        assertFalse(state.isRecording)
        assertFalse(state.isFlashOn)
        assertEquals(0, state.recordingDurationSeconds)
        assertEquals(1.0f, state.zoomRatio)
        assertNull(state.videoOutputFile)
        assertNull(state.errorMessage)
    }

    @Test
    fun testPermissionGrantedUpdate() {
        viewModel.setPermissionGranted(true)
        assertTrue(viewModel.uiState.value.hasCameraPermission)

        viewModel.setPermissionGranted(false)
        assertFalse(viewModel.uiState.value.hasCameraPermission)
    }

    @Test
    fun testRecordingLifecycle() {
        val mockFile = File("/tmp/mock_recording.mp4")
        viewModel.startRecording(mockFile)

        val recordingState = viewModel.uiState.value
        assertTrue(recordingState.isRecording)
        assertEquals(0, recordingState.recordingDurationSeconds)
        assertEquals(mockFile, recordingState.videoOutputFile)

        viewModel.updateRecordingDuration(5)
        assertEquals(5, viewModel.uiState.value.recordingDurationSeconds)

        viewModel.stopRecording()
        assertFalse(viewModel.uiState.value.isRecording)
    }

    @Test
    fun testVideoCaptureMethods() {
        val testFile = File("/tmp/test_fab_capture.mp4")
        
        // Start video capture
        viewModel.startVideoCapture(testFile)
        assertTrue(viewModel.uiState.value.isRecording)
        assertEquals(testFile, viewModel.uiState.value.videoOutputFile)

        // Stop video capture
        viewModel.stopVideoCapture()
        assertFalse(viewModel.uiState.value.isRecording)

        // Toggle captureVideo when idle -> starts
        val captureStarted = viewModel.captureVideo()
        assertTrue(captureStarted)
        assertTrue(viewModel.uiState.value.isRecording)

        // Toggle captureVideo when recording -> stops
        val captureStopped = viewModel.captureVideo()
        assertFalse(captureStopped)
        assertFalse(viewModel.uiState.value.isRecording)
    }

    @Test
    fun testZoomRatioUpdate() {
        viewModel.setZoom(2.5f)
        assertEquals(2.5f, viewModel.uiState.value.zoomRatio)
    }

    @Test
    fun testClearError() {
        viewModel.clearError()
        assertNull(viewModel.uiState.value.errorMessage)
    }
}
