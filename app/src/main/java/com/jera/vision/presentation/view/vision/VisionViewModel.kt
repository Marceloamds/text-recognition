package com.jera.vision.presentation.view.vision

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.jera.vision.presentation.util.base.BaseViewModel
import com.jera.vision.presentation.view.ar.ARNavData
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class VisionViewModel : BaseViewModel() {

    val sponsorText: LiveData<String> get() = _sponsorText

    private val _sponsorText by lazy { MutableLiveData<String>() }

    private var shouldOpenAr = true

    private val sponsorsList = listOf(
        "Itau",
        "Heineken",
        "Ipiranga",
        "Rio",
        "Riotur",
        "Doritos",
        "Americanas",
        "Natura"
    )

    fun onResume(){
        shouldOpenAr = true
    }

    fun onTextFound(foundText: String) {
        if (shouldOpenAr) {
            val sponsorIndex = sponsorsList.indexOf(foundText.capitalize())
            if (sponsorIndex != -1) {
                _sponsorText.value = sponsorsList[sponsorIndex]
                goTo(ARNavData())
                shouldOpenAr = false
            }
        }
    }
}