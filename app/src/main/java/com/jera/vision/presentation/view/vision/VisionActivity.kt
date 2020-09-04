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
import android.provider.MediaStore
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
import com.jera.vision.presentation.util.SpinnerSelectionHelper
import com.jera.vision.presentation.util.base.BaseActivity
import com.jera.vision.presentation.util.base.BaseViewModel
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
                        val galleryIntent =
                            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                        startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE)
                    }
                }.let(CompositeDisposable()::add)
        }
        adapter = MonthConsumptionAdapter()
        binding.recyclerResult.adapter = adapter

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
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            data?.data?.run {
                val bitmap = getBitmap(this)
                bitmap?.run {
                    textRecognizer.process(InputImage.fromBitmap(this, 0)).addOnSuccessListener {
                        val monthConsumptionList = currentBill.getConsumptionList(it.textBlocks)
                        adapter.submitList(monthConsumptionList)
                        binding.textViewExplanation.text = getString(
                            if (monthConsumptionList.isEmpty()) R.string.no_month_consumption_text
                            else R.string.global_blank
                        )
                        binding.textViewExplanation.visibility = if (monthConsumptionList.isEmpty()) View.VISIBLE else View.GONE
                    }
                }
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


    // Trying to sort from left to right without messing up the order. Couldn't do it
    private fun List<Text.Element>.sortedByPixelPosition(): List<Text.Element> {

        val varianceList = mutableListOf<Text.Element>()
        val parentsIndex = mutableListOf<Int>()
        var isParent: Boolean

        forEachIndexed { index, element ->
            isParent = true
            parentsIndex.forEachIndexed { index2, parentIndex ->
                if (
                    element.cornerPoints != null &&
                    element.cornerPoints!![0].x > varianceList[parentIndex].cornerPoints?.get(0)?.x!! - TAG_VARIANCE &&
                    isParent
                ) {
                    isParent = false
                    if (index2 == parentsIndex.lastIndex) varianceList.add(element)
                    else varianceList.add(parentsIndex[index2 + 1] - 1, element)
                }
            }

            if (isParent) {
                varianceList.add(element)
                parentsIndex.add(index)
            }
        }

        val sortedList = mutableListOf<List<Text.Element>>()
        parentsIndex.forEachIndexed { index, parentIndex ->
            if (index == parentsIndex.lastIndex) {
                sortedList.add(varianceList.slice(parentIndex..varianceList.lastIndex))
            } else {
                sortedList.add(varianceList.slice(parentIndex until parentsIndex[index + 1]))
            }
        }

        val finalList = mutableListOf<Text.Element>()
        sortedList.sortedBy { it.firstOrNull()?.cornerPoints?.get(0)?.x }
            .forEach { finalList.addAll(it) }

        return finalList
    }

    companion object {
        const val GALLERY_REQUEST_CODE = 321

        fun createIntent(context: Context) = Intent(context, VisionActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    }
}