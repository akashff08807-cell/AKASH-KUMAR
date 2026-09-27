package com.example.ui.camera

import androidx.camera.core.Preview
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

/**
 * Reusable Jetpack Compose wrapper for CameraX PreviewView.
 */
@Composable
fun CameraPreview(
    onSurfaceProviderReady: (Preview.SurfaceProvider) -> Unit,
    modifier: Modifier = Modifier
) {
    AndroidView(
        factory = { context ->
            PreviewView(context).apply {
                implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                scaleType = PreviewView.ScaleType.FILL_CENTER
                onSurfaceProviderReady(this.surfaceProvider)
            }
        },
        update = { previewView ->
            onSurfaceProviderReady(previewView.surfaceProvider)
        },
        modifier = modifier.fillMaxSize()
    )
}
