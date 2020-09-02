package com.jera.vision.presentation.di

import com.jera.vision.presentation.view.splash.SplashViewModel
import com.jera.vision.presentation.view.vision.VisionViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

fun viewModelModule() = module {

    viewModel {
        VisionViewModel()
    }

    viewModel { SplashViewModel() }
}