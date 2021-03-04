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
    val shouldShowSponsor: LiveData<Boolean> get() = _shouldShowSponsor

    private val _sponsorText by lazy { MutableLiveData<String>() }
    private val _shouldShowSponsor by lazy { MutableLiveData<Boolean>() }

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

    fun onTextFound(foundText: String) {
        val sponsorIndex = sponsorsList.indexOf(foundText.capitalize())
        if (sponsorIndex != -1) {
            _sponsorText.value = sponsorsList[sponsorIndex]
            goTo(ARNavData())
            finish()
        }
    }
}