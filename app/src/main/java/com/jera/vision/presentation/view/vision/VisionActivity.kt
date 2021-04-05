package com.jera.vision.presentation.view.vision

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.jera.vision.R
import com.jera.vision.databinding.ActivityVisionBinding
import com.jera.vision.presentation.util.base.BaseActivity
import com.jera.vision.presentation.util.base.BaseViewModel
import com.jera.vision.presentation.util.extension.observe
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.disposables.CompositeDisposable
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class VisionActivity : BaseActivity() {

    override val baseViewModel: BaseViewModel get() = _viewModel
    private val _viewModel: VisionViewModel by viewModel()
    private lateinit var textRecognizer: TextRecognizer

    private lateinit var binding: ActivityVisionBinding
    private val cameraExecutor: ExecutorService by lazy { Executors.newSingleThreadExecutor() }
    private val imageAnalyzer by lazy {
        ImageAnalysis.Builder()
            .setTargetAspectRatio(AspectRatio.RATIO_16_9)
            .build()
            .also {
                it.setAnalyzer(
                    cameraExecutor,
                    TextReaderAnalyzer(::onTextFound)
                )
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_vision)
        textRecognizer = TextRecognition.getClient()
        RxPermissions(this)
            .request(Manifest.permission.CAMERA)
            .subscribe { granted: Boolean ->
                if (granted) {
                    startCamera()
                }
            }.let(CompositeDisposable()::add)
    }

    override fun onResume() {
        _viewModel.onResume()
        super.onResume()
    }

    override fun subscribeUi() {
        super.subscribeUi()
        _viewModel.sponsorText.observe(this) {
            Toast.makeText(this, "Patrocinador: $it", Toast.LENGTH_LONG).show()
        }
    }

    private fun onTextFound(foundText: String) {
        _viewModel.onTextFound(foundText)
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener(
            Runnable {
                val preview = Preview.Builder()
                    .build()
                    .also { it.setSurfaceProvider(binding.cameraPreviewView.surfaceProvider) }
                cameraProviderFuture.get().bind(preview, imageAnalyzer)
            },
            ContextCompat.getMainExecutor(this)
        )
    }

    private fun ProcessCameraProvider.bind(
        preview: Preview,
        imageAnalyzer: ImageAnalysis
    ) = try {
        unbindAll()
        bindToLifecycle(
            this@VisionActivity,
            CameraSelector.DEFAULT_BACK_CAMERA,
            preview,
            imageAnalyzer
        )
    } catch (ise: IllegalStateException) {
        // Thrown if binding is not done from the main thread
        Log.e("TAG", "Binding failed", ise)
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    companion object {

        fun createIntent(context: Context) = Intent(context, VisionActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    }
}