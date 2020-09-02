package com.jera.vision.presentation.util.base

import android.app.Dialog
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.jera.vision.presentation.util.dialog.DialogData
import com.jera.vision.presentation.util.extension.consume
import com.jera.vision.presentation.util.extension.observe
import com.jera.vision.presentation.util.extension.showDialog
import com.jera.vision.presentation.util.navigation.NavData

abstract class BaseActivity : AppCompatActivity() {

    private var dialog: Dialog? = null

    abstract val baseViewModel: BaseViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        subscribeUi()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> consume { onBackPressed() }
            else -> super.onOptionsItemSelected(item)
        }
    }

    open fun subscribeUi() {
        baseViewModel.dialog.observe(this, ::onNextDialog)
        baseViewModel.goTo.observe(this, ::onGoTo)
    }

    private fun onNextDialog(dialogData: DialogData?) {
        dialog?.dismiss()
        dialog = dialogData?.let { showDialog(it) }
    }

    private fun onGoTo(navData: NavData?) {
        navData?.navigate(this)
    }
}
