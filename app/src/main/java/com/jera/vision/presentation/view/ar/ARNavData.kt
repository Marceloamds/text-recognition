package com.jera.vision.presentation.view.ar

import android.content.Context
import com.jera.vision.presentation.util.navigation.NavData

class ARNavData : NavData {

    override fun navigate(context: Context) {
        context.startActivity(ARActivity.createIntent(context))
    }
}