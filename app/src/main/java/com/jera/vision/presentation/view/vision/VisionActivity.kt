package com.jera.vision.presentation.view.vision

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.jera.vision.R
import com.jera.vision.databinding.ActivityVisionBinding
import com.jera.vision.domain.bill.Bill
import com.jera.vision.domain.bill.CopelBill
import com.jera.vision.domain.bill.CopelBill.Companion.TAG_VARIANCE
import com.jera.vision.presentation.util.base.BaseActivity
import com.jera.vision.presentation.util.base.BaseViewModel
import com.jera.vision.presentation.view.vision.camera.CameraSource
import com.jera.vision.presentation.view.vision.camera.TextRecognitionProcessor
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.disposables.CompositeDisposable
import org.koin.android.viewmodel.ext.android.viewModel
import java.io.IOException

class VisionActivity : BaseActivity() {

    override val baseViewModel: BaseViewModel get() = _viewModel
    private val _viewModel: VisionViewModel by viewModel()
    private lateinit var textRecognizer: TextRecognizer

    private lateinit var binding: ActivityVisionBinding
    private lateinit var adapter: MonthConsumptionAdapter
    private var cameraSource: CameraSource? = null

    private var sponsorsList = listOf("americanas", "ipiranga", "doritos", "heineken", "natura","itau", "rio", "riotur")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_vision)
        textRecognizer = TextRecognition.getClient()
        RxPermissions(this)
            .request(Manifest.permission.CAMERA)
            .subscribe { granted: Boolean ->
                if (granted) {
                    if (cameraSource == null) {
                        cameraSource = CameraSource(this, binding.graphicOverlay)
                    }
                    cameraSource!!.setMachineLearningFrameProcessor(TextRecognitionProcessor(this, sponsorsList))
                    startCameraSource()
                }
            }.let(CompositeDisposable()::add)
        adapter = MonthConsumptionAdapter()

    }

    private fun startCameraSource() {
        if (cameraSource != null) {
            try {
                binding.previewView.start(cameraSource, binding.graphicOverlay)
            } catch (e: IOException) {
                cameraSource!!.release()
                cameraSource = null
            }
        }
    }

    private fun getBitmap(file: Uri): Bitmap? {
        return try {
            val inputStream = contentResolver.openInputStream(file)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()
            bitmap
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
    companion object {
        const val GALLERY_REQUEST_CODE = 321

        fun createIntent(context: Context) = Intent(context, VisionActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    }
}