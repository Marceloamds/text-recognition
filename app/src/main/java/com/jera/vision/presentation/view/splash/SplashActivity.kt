package com.jera.vision.presentation.view.splash

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.jera.vision.R
import com.jera.vision.databinding.ActivitySplashBinding
import com.jera.vision.presentation.util.base.BaseActivity
import com.jera.vision.presentation.util.base.BaseViewModel
import com.jera.vision.presentation.util.extension.transparentStatusAndNavigation
import org.koin.android.viewmodel.ext.android.viewModel

class SplashActivity : BaseActivity() {

    override val baseViewModel: BaseViewModel get() = _viewModel
    private val _viewModel: SplashViewModel by viewModel()

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        transparentStatusAndNavigation(window)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash)
    }
}