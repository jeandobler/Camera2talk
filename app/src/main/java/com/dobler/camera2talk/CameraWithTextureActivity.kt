package com.dobler.camera2talk

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.ImageFormat
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
import android.os.Bundle
import android.os.Handler
import android.util.DisplayMetrics
import android.view.Surface
import android.view.TextureView
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_with_texture.*


class CameraWithTextureActivity : BaseActivity() {

//    private lateinit var previewSurface: Surface //Apenas se usar TextureView

    private lateinit var cameraCharacteristics: CameraCharacteristics
    private lateinit var backCamera: String
    private lateinit var currentCameraDevice: CameraDevice

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_with_texture)
        initCameraPermission()
        setupManagers()
        startCamera()
    }

    override fun startCamera() {
        cameraView.setSurfaceTextureListener(surfaceReadyCallback)
    }

    lateinit var cameraManager: CameraManager
    private fun setupManagers() {
        cameraManager = this.getSystemService(CAMERA_SERVICE) as CameraManager
    }


    val surfaceReadyCallback = object : TextureView.SurfaceTextureListener {
        override fun onSurfaceTextureSizeChanged(
            surface: SurfaceTexture?,
            width: Int,
            height: Int
        ) {
        }

        override fun onSurfaceTextureUpdated(surface: SurfaceTexture?) {
        }

        override fun onSurfaceTextureDestroyed(surface: SurfaceTexture?): Boolean {
            return true
        }

        @SuppressLint("MissingPermission")
        override fun onSurfaceTextureAvailable(surface: SurfaceTexture?, width: Int, height: Int) {
            getBackCamera()


            cameraManager.openCamera(backCamera, stateCallback, Handler { true })
        }

    }

    private fun getBackCamera() {
        if (cameraManager.cameraIdList.isEmpty()) {
            return
        }

        cameraManager.cameraIdList.forEach { foundCamera ->
            cameraCharacteristics = cameraManager.getCameraCharacteristics(foundCamera)

            if (cameraCharacteristics.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_BACK) {
                backCamera = foundCamera
            }
        }
    }

    private lateinit var previewSurface: Surface //Apenas se usar TextureView
    val stateCallback = object : CameraDevice.StateCallback() {

        override fun onOpened(cameraDevice: CameraDevice) {
            if (cameraDevice != null) {
                currentCameraDevice = cameraDevice
            }
            // Para a imagem do preview não ficar distorcida precisamos pegar os tipos de resolução
            // que a camera suporta, para isso usaremos o Camera Characteristics
            cameraCharacteristics[CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP]?.let { streamConfigurationMap ->
                streamConfigurationMap.getOutputSizes(ImageFormat.YUV_420_888)
                    ?.let { yuvSizes ->
                        // A primcire resolução no meu caso é a mais alta mas você pode debugar
                        // caso não saiba qual é a melhor resolução
                        val previewSize = yuvSizes.first()

                        // Aqui adicionarmos o tamanho da resolução como default
                        cameraView.surfaceTexture.setDefaultBufferSize(
                            previewSize.width, previewSize.height
                        )
                        //Chamamos o adaptScreen para ajustar a view caso necessário

                        adaptScreen()
                        previewSurface =
                            Surface(cameraView.surfaceTexture) //Apenas se usar Textureview

                        cameraDevice.createCaptureSession(
                            mutableListOf(previewSurface),
//                            mutableListOf(cameraView.holder.surface) //Substituir caso vá usar SurfaceView
                            captureCallback,
                            Handler { true }
                        )

                    }
            }
        }

        override fun onDisconnected(camera: CameraDevice) {
        }

        override fun onError(camera: CameraDevice, error: Int) {
        }
    }

    fun adaptScreen() {
        val cameraAspectRatio = 0.75.toFloat()

        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metrics)
        val screenWidth = metrics.widthPixels
        val screenHeight = metrics.heightPixels
        var finalWidth = screenWidth
        var finalHeight = screenHeight
        val screenAspectRatio = screenWidth.toFloat() / screenHeight

        if (screenAspectRatio > cameraAspectRatio) {
            finalHeight = (screenWidth / cameraAspectRatio).toInt()
        } else {
            finalWidth = (screenHeight * cameraAspectRatio).toInt()
        }

        val lp = cameraView.layoutParams

        lp.width = finalWidth
        lp.height = finalHeight
        cameraView.setLayoutParams(lp)
    }

    val captureCallback = object : CameraCaptureSession.StateCallback() {
        override fun onConfigureFailed(session: CameraCaptureSession) {}

        override fun onConfigured(session: CameraCaptureSession) {
            val previewRequestBuilder =
                currentCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
                    .apply {
                        addTarget(previewSurface) // Apenas em caso de TextureView
//                        addTarget(cameraView.holder.surface) // caso use o SurfaceView
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

            /*

//          Aqui voce pode criar alguma função para trocar os CaptureRequests(ligar e desligar led)
//          vou deixar um código apenas de exemplo

                flashLight.setOnClickListener {

                //Propiedade Boolean
                flashlightState = !flashlightState

                flashLight.isChecked = flashlightState

                previewRequestBuilder.apply {
                    set(
                        CaptureRequest.CONTROL_AE_MODE, if (flashlightState)
                            CaptureRequest.CONTROL_AE_MODE_ON else
                            CaptureRequest.CONTROL_AE_MODE_OFF
                    )
                    set(
                        CaptureRequest.FLASH_MODE, if (flashlightState)
                            CaptureRequest.FLASH_MODE_TORCH else
                            CaptureRequest.FLASH_MODE_OFF
                    )
                }

                session.setRepeatingRequest(
                    previewRequestBuilder.build(),
                    object : CameraCaptureSession.CaptureCallback() {},
                    Handler { true }
                )
            }

             */


        }
    }


}
