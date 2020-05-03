package com.dobler.camera2talk

import android.Manifest
import android.content.pm.PackageManager
import android.hardware.camera2.*
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.SurfaceHolder
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_with_surface.*

class CameraWithSurfaceActivity : AppCompatActivity() {

    lateinit var cameraManager: CameraManager
    private lateinit var cameraCharacteristics: CameraCharacteristics
    private lateinit var backCamera: String
    private lateinit var currentCameraDevice: CameraDevice

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_with_surface)
        setupManagers()
        startCamera()
    }

    private fun startCamera() {
        cameraView.holder.addCallback(surfaceReadyCallback)
    }

    private fun setupManagers() {
        cameraManager = this.getSystemService(CAMERA_SERVICE) as CameraManager
    }


    val surfaceReadyCallback = object : SurfaceHolder.Callback {
        override fun surfaceChanged(surfaceHolder: SurfaceHolder?, p1: Int, p2: Int, p3: Int) {}

        override fun surfaceDestroyed(p0: SurfaceHolder?) {}

        override fun surfaceCreated(p0: SurfaceHolder?) {
            getBackCamera()

            if (ActivityCompat.checkSelfPermission(
                    this@CameraWithSurfaceActivity,
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }

            cameraManager.openCamera(backCamera, stateCallback, Handler { true })
        }
    }

    private fun getBackCamera() {
        if (cameraManager.cameraIdList.isEmpty()) {
            Log.e("qrCode", "No camera found")
            return
        }
        for (foundCamera in cameraManager.cameraIdList) {

            cameraCharacteristics = cameraManager.getCameraCharacteristics(foundCamera)

            if (cameraCharacteristics.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_BACK) {
                Log.e("qrCode", "Camera found")
                backCamera = cameraManager.cameraIdList[0]
            }
        }
    }


    val stateCallback = object : CameraDevice.StateCallback() {

        override fun onOpened(cameraDevice: CameraDevice) {
            if (cameraDevice != null) {
                currentCameraDevice = cameraDevice
            }


            cameraDevice.createCaptureSession(
                mutableListOf(cameraView.holder.surface),
                captureCallback,
                Handler { true }
            )
        }

        override fun onDisconnected(camera: CameraDevice) {
        }

        override fun onError(cameraDevice: CameraDevice, error: Int) {
        }
    }


    val captureCallback = object : CameraCaptureSession.StateCallback() {
        override fun onConfigureFailed(session: CameraCaptureSession) {}

        override fun onConfigured(session: CameraCaptureSession) {
            val previewRequestBuilder =
                currentCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
                    .apply {
                        addTarget(cameraView.holder.surface)
                        set(
                            CaptureRequest.CONTROL_AF_MODE,
                            CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE
                        )
                    }

            session.setRepeatingRequest(
                previewRequestBuilder.build(),
                object : CameraCaptureSession.CaptureCallback() {},
                Handler { true }
            )
        }
    }


}