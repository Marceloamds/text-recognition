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
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import com.camerakit.CameraKitView
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.jera.vision.R
import com.jera.vision.databinding.ActivityVisionBinding
import com.jera.vision.domain.bill.Bill
import com.jera.vision.domain.bill.CopelBill
import com.jera.vision.domain.bill.CopelBill.Companion.TAG_VARIANCE
import com.jera.vision.domain.entity.MonthConsumption
import com.jera.vision.domain.util.resource.format
import com.jera.vision.presentation.util.SpinnerSelectionHelper
import com.jera.vision.presentation.util.base.BaseActivity
import com.jera.vision.presentation.util.base.BaseViewModel
import com.tbruyelle.rxpermissions2.RxPermissions
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import io.reactivex.disposables.CompositeDisposable
import org.koin.android.viewmodel.ext.android.viewModel
import java.io.IOException

class VisionActivity : BaseActivity() {

    override val baseViewModel: BaseViewModel get() = _viewModel
    private val _viewModel: VisionViewModel by viewModel()
    private lateinit var textRecognizer: TextRecognizer

    private lateinit var binding: ActivityVisionBinding
    var currentBill: Bill = CopelBill()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_vision)
        textRecognizer = TextRecognition.getClient()
        binding.button.setOnClickListener {
            RxPermissions(this)
                .request(Manifest.permission.READ_EXTERNAL_STORAGE)
                .subscribe { granted: Boolean ->
                    if (granted) {
                        CropImage.startPickImageActivity(this)
//                        val galleryIntent =
//                            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//                        startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE)
                    }
                }.let(CompositeDisposable()::add)
        }

        with(binding.spinnerBillType) {
            adapter =
                BillTypesAdapter(this@VisionActivity, R.layout.support_simple_spinner_dropdown_item)
            onItemSelectedListener = SpinnerSelectionHelper(this, ::onSpinnerItemSelected)
        }
    }

    private fun onSpinnerItemSelected(bill: Bill) {
        currentBill = bill
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            var imageUri = CropImage.getPickImageResultUri(this, data)
            startCrop(imageUri)
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            var resultUri: Uri = result.uri

            resultUri?.run {
                val bitmap = getBitmap(this)
                bitmap?.run {
                    textRecognizer.process(InputImage.fromBitmap(this, 0)).addOnSuccessListener {
                        binding.tvResult.setText(getTextFromBlocks(it.textBlocks))
                    }
                }
            }
        }
    }

    private fun startCrop(imageUri: Uri) {
        CropImage.activity(imageUri)
            .setGuidelines(CropImageView.Guidelines.ON)
            .setMultiTouchEnabled(true)
            .start(this)
    }

    private fun getTextFromBlocks(textBlocks: List<Text.TextBlock>): String {
        val consumptionList = currentBill.getConsumptionList(textBlocks)
        var formattedText = ""
        consumptionList.sortedByDescending { it.month }.forEach {
            formattedText += formatText(it)
        }
        return formattedText
    }

    private fun formatText(monthConsumption: MonthConsumption): String {
        return monthConsumption.month.format("MMMM").toLowerCase().capitalize() +
                "/" +
                monthConsumption.month.format("YYYY") +
                "   kWh: ${monthConsumption.kWhConsumption} \n"
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