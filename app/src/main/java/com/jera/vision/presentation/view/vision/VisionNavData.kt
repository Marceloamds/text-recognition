package com.jera.vision.presentation.view.vision

import android.content.Context
import com.jera.vision.presentation.util.navigation.NavData

class VisionNavData : NavData {

    override fun navigate(context: Context) {
        context.startActivity(VisionActivity.createIntent(context))
    }
}