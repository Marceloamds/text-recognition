package com.jera.vision.presentation.view.splash

import com.jera.vision.presentation.util.base.BaseViewModel
import com.jera.vision.presentation.util.constants.SPLASH_DELAY
import com.jera.vision.presentation.view.vision.VisionNavData
import kotlinx.coroutines.delay

class SplashViewModel : BaseViewModel() {

    init {
        launchDataLoad {
            delay(SPLASH_DELAY)
            goTo(VisionNavData())
        }
    }
}