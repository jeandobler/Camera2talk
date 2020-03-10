package com.dobler.camera2talk

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Surface
import android.view.TextureView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_with_texture.*

class CameraWithTextureActivity : AppCompatActivity() {

    private lateinit var previewSurface: Surface
    lateinit var cameraManager: CameraManager
    private lateinit var cameraCharacteristics: CameraCharacteristics
    private lateinit var backCamera: String
    private lateinit var currentCameraDevice: CameraDevice

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_with_texture)
        setupManagers()
        startCamera()
    }

    private fun startCamera() {
        camera.setSurfaceTextureListener(surfaceReadyCallback)
    }

    private fun setupManagers() {
        cameraManager = this.getSystemService(CAMERA_SERVICE) as CameraManager
    }


    val surfaceReadyCallback = object : TextureView.SurfaceTextureListener {
        override fun onSurfaceTextureSizeChanged(
            surface: SurfaceTexture?,
            width: Int,
            height: Int
        ) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onSurfaceTextureUpdated(surface: SurfaceTexture?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onSurfaceTextureDestroyed(surface: SurfaceTexture?): Boolean {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onSurfaceTextureAvailable(surface: SurfaceTexture?, width: Int, height: Int) {
            getBackCamera()

            if (ActivityCompat.checkSelfPermission(
                    this@CameraWithTextureActivity,
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

            previewSurface = Surface(camera.surfaceTexture)

            cameraDevice.createCaptureSession(
                mutableListOf(previewSurface),
                captureCallback,
                Handler { true }
            )
        }

        override fun onDisconnected(camera: CameraDevice) {
        }

        override fun onError(camera: CameraDevice, error: Int) {
        }
    }


    val captureCallback = object : CameraCaptureSession.StateCallback() {
        override fun onConfigureFailed(session: CameraCaptureSession) {}

        override fun onConfigured(session: CameraCaptureSession) {
            val previewRequestBuilder =
                currentCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
                    .apply {
                        addTarget(previewSurface)
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
/*
*
*
*
*
    //    private lateinit var options: FirebaseVisionBarcodeDetectorOptions
    private lateinit var windowManager: WindowManager
    private lateinit var cameraCharacteristics: CameraCharacteristics
    //    private lateinit var metadata: FirebaseVisionImageMetadata
//    private lateinit var detector: FirebaseVisionBarcodeDetector
    private lateinit var backCamera: String
    private lateinit var cameraManager: CameraManager

    private lateinit var currentCameraDevice: CameraDevice
//    private var firebaseApp: FirebaseApp? = null

    private var mImageReader = ImageReader.newInstance(640, 960, ImageFormat.JPEG, 30)

    private val qrCodeStringResultData = MutableLiveData<String>()
    val qrResult: LiveData<String> = qrCodeStringResultData

    private val ORIENTATIONS = SparseIntArray()

    init {
        ORIENTATIONS.append(Surface.ROTATION_0, 90)
        ORIENTATIONS.append(Surface.ROTATION_90, 0)
        ORIENTATIONS.append(Surface.ROTATION_180, 270)
        ORIENTATIONS.append(Surface.ROTATION_270, 180)
//        firebaseApp = FirebaseApp.initializeApp(context)
        init(context, attrs)
    }

    var flashlightState = false

    private fun init(context: Context, attrs: AttributeSet?) {
        bindViews(context)
    }

    val captureCallback = object : CameraCaptureSession.StateCallback() {
        override fun onConfigureFailed(session: CameraCaptureSession) {}

        override fun onConfigured(session: CameraCaptureSession) {
            val previewRequestBuilder =
                currentCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
                    .apply {
                        //                        addTarget(camera.holder.surface)
                        addTarget(cameraMatch.holder.surface)
                        addTarget(mImageReader.surface)
                        set(
                            CaptureRequest.CONTROL_AF_MODE,
                            CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE
                        )
                    }

            flashLight.setOnClickListener {

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

            session.setRepeatingRequest(
                previewRequestBuilder.build(),
                object : CameraCaptureSession.CaptureCallback() {},
                Handler { true }
            )
        }
    }

    val stateCallback = object : CameraDevice.StateCallback() {

        override fun onDisconnected(p0: CameraDevice?) {}
        override fun onError(p0: CameraDevice?, p1: Int) {}
        override fun onClosed(camera: CameraDevice?) {}
        override fun onOpened(cameraDevice: CameraDevice?) {
            cameraCharacteristics[CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP]?.let { streamConfigurationMap ->
                streamConfigurationMap.getOutputSizes(ImageFormat.YUV_420_888)
                    ?.let { yuvSizes ->
                        val previewSize = yuvSizes.last()
                        val displayRotation = windowManager.defaultDisplay.rotation
                        val swappedDimensions =
                            areDimensionsSwapped(displayRotation, cameraCharacteristics)

                        val rotatedPreviewWidth =
                            if (swappedDimensions) previewSize.height else previewSize.width
                        val rotatedPreviewHeight =
                            if (swappedDimensions) previewSize.width else previewSize.height

//                        camera.holder.setFixedSize(
//                            rotatedPreviewWidth,
//                            rotatedPreviewHeight
//                        )

                        if (cameraDevice != null) {
                            currentCameraDevice = cameraDevice
                        }

                        Log.e("QRCode", "CurrentCameraSetted")

                        cameraDevice?.createCaptureSession(
                            mutableListOf(cameraMatch.holder.surface, mImageReader.surface),
                            captureCallback,
                            Handler { true }
                        )
                        Log.e("QRCode", "CameraOpened")

                        val barcodeDetector = BarcodeDetector.Builder(context!!)
                            .setBarcodeFormats(Barcode.QR_CODE or Barcode.DATA_MATRIX)
                            .build()

//
                        mImageReader.setOnImageAvailableListener({ reader ->
                            //                            val image = reader.acquireLatestImage()
                            val cameraImage = reader.acquireNextImage()
                            try {

                                val buffer = cameraImage.planes.first().buffer
                                val bytes = ByteArray(buffer.capacity())
                                buffer.get(bytes)

                                val bitmap =
                                    BitmapFactory.decodeByteArray(bytes, 0, bytes.count(), null)
                                val frameToProcess = Frame.Builder().setBitmap(bitmap).build()
                                val barcodeResults: SparseArray<Barcode> =
                                    barcodeDetector.detect(frameToProcess)

                                if (barcodeResults.size() > 0) {
                                    qrCodeStringResultData.value =
                                        barcodeResults.valueAt(0).rawValue
                                }

                                cameraImage.close()

//                                firebaseApp = FirebaseApp.initializeApp(context)
//                                detector = FirebaseVision.getInstance(firebaseApp!!)
//                                    .getVisionBarcodeDetector(options)

//                                if (image != null) {
//                                    Log.e("Tag", reader.toString())
//                                    val buffer = image.getPlanes()[0].getBuffer()
//                                    val bitmap =
//                                        FirebaseVisionImage.fromByteBuffer(buffer, metadata)
//                                    val result = detector.detectInImage(bitmap)
//                                        .addOnSuccessListener { barcodes ->
//                                            // Task completed successfully
//                                            // ...
//                                            Log.e("Encontrou", barcodes[0].displayValue)
//                                        }
//                                        .addOnFailureListener {
//                                            Log.e("NaoEncontrou", it.message)
//                                        }
//                                }
                            } catch (e: Exception) {
                                Log.e(
                                    "ErroFirebase",
                                    "FirebaseNotInicialized" + e.stackTrace.toString()
                                )
                            }
                            cameraImage.close()

                        }, Handler())

                    }
            }
        }
    }

    val surfaceReadyCallback = object : SurfaceHolder.Callback {
        override fun surfaceChanged(surfaceHolder: SurfaceHolder?, p1: Int, p2: Int, p3: Int) {}

        override fun surfaceDestroyed(p0: SurfaceHolder?) {}

        override fun surfaceCreated(p0: SurfaceHolder?) {
            getBackCamera()

            if (!checkPermission()) {
                return
            }

//            setupFirebase()

            Log.e("QrCOde", "Firebase Configured")
            cameraManager.openCamera(backCamera, stateCallback, Handler { true })
        }
    }

    private fun bindViews(context: Context) {
        val inflater = LayoutInflater.from(context)
        inflater.inflate(R.layout.itau_qrcode_view, this, true)
    }

    private fun setupManagers() {
        cameraManager = context.getSystemService(CAMERA_SERVICE) as CameraManager
        windowManager = context.getSystemService(WINDOW_SERVICE) as WindowManager
    }

    private fun setupFirebase() {

//        options = FirebaseVisionBarcodeDetectorOptions.Builder()
//            .setBarcodeFormats(
//                FirebaseVisionBarcode.FORMAT_QR_CODE
//            )
//            .build()
//
//        metadata = FirebaseVisionImageMetadata.Builder()
//            .setWidth(480) // 480x360 is typically sufficient for
//            .setHeight(360) // image recognition
//            .setFormat(FirebaseVisionImageMetadata.IMAGE_FORMAT_YV12)
//            .setRotation(getRotationCompensation(backCamera))
//            .build()
    }

    fun start() {
        setupManagers()
//        setupFirebase()

        camera.holder.addCallback(surfaceReadyCallback)
    }

    private fun getBackCamera() {
        if (cameraManager.cameraIdList.isEmpty()) {
            Log.e("qrCode", "No camera foound")
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

    private fun checkPermission(): Boolean {
        if (backCamera == null) {
            Log.e("QrCOde", "Camera is null")
            return false
        }

        val permission =
            ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA)
        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.e("QrCOde", "WithoutPermission")
            return false
        }
        return true
    }

    private fun areDimensionsSwapped(
        displayRotation: Int,
        cameraCharacteristics: CameraCharacteristics
    ): Boolean {
        var swappedDimensions = false
        when (displayRotation) {
            Surface.ROTATION_0, Surface.ROTATION_180 -> {
                if (cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) == 90 ||
                    cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) == 270
                ) {
                    swappedDimensions = true
                }
            }
            Surface.ROTATION_90, Surface.ROTATION_270 -> {
                if (cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) == 0 || cameraCharacteristics.get(
                        CameraCharacteristics.SENSOR_ORIENTATION
                    ) == 180
                ) {
                    swappedDimensions = true
                }
            }
            else -> {
                // invalid display rotation
            }
        }
        return swappedDimensions
    }

    /**
     * Get the angle by which an image must be rotated given the device's current
     * orientation.
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Throws(CameraAccessException::class)
    private fun getRotationCompensation(cameraId: String?): Int {
        var result = 1
        // Get the device's current rotation relative to its "native" orientation.
        // Then, from the ORIENTATIONS table, look up the angle the image must be
        // rotated to compensate for the device's rotation.
//        val deviceRotation =
//            (context.getSystemService(WINDOW_SERVICE) as WindowManager).defaultDisplay.rotation
//        var rotationCompensation = ORIENTATIONS.get(deviceRotation)
//
//        // On most devices, the sensor orientation is 90 degrees, but for some
//        // devices it is 270 degrees. For devices with a sensor orientation of
//        // 270, rotate the image an additional 180 ((270 + 270) % 360) degrees.
//        val cameraManager = context.getSystemService(CAMERA_SERVICE) as CameraManager
//        val sensorOrientation = cameraManager
//            .getCameraCharacteristics(cameraId)
//            .get(CameraCharacteristics.SENSOR_ORIENTATION)!!
//        rotationCompensation = (rotationCompensation + sensorOrientation + 270) % 360
//
//        // Return the corresponding FirebaseVisionImageMetadata rotation value.

//        when (rotationCompensation) {
//            0 -> result = FirebaseVisionImageMetadata.ROTATION_0
//            90 -> result = FirebaseVisionImageMetadata.ROTATION_90
//            180 -> result = FirebaseVisionImageMetadata.ROTATION_180
//            270 -> result = FirebaseVisionImageMetadata.ROTATION_270
//            else -> {
//                result = FirebaseVisionImageMetadata.ROTATION_0
//                Log.e("QRCDODE", "Bad rotation value: $rotationCompensation")
//            }
//        }
        return result
    }
}
* */