package com.jera.vision.presentation.util

import android.view.View
import android.widget.AdapterView
import android.widget.Spinner

class SpinnerSelectionHelper <T>(
        private val spinner: Spinner,
        private val onItemSelectedCallback: (T) -> Unit
) : AdapterView.OnItemSelectedListener {

    override fun onNothingSelected(parent: AdapterView<*>?) {}

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        (spinner.selectedItem as? T)?.let { onItemSelectedCallback.invoke(it) }
    }
}