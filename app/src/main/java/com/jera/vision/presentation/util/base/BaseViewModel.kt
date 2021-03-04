package com.jera.vision.presentation.util.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jera.vision.presentation.util.dialog.DialogData
import com.jera.vision.presentation.util.error.ErrorHandler
import com.jera.vision.presentation.util.navigation.NavData
import com.jera.vision.presentation.util.placeholder.Placeholder
import com.jera.vision.presentation.util.placeholder.types.Hide
import com.jera.vision.presentation.util.placeholder.types.Loading
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject

abstract class BaseViewModel : ViewModel(), KoinComponent {

    private val errorHandler: ErrorHandler by inject()

    val placeholder: LiveData<Placeholder> get() = _placeholder
    val goTo: LiveData<NavData> get() = _goTo
    val dialog: LiveData<DialogData> get() = _dialog
    val finish: LiveData<Unit> get() = _finish

    private val _placeholder by lazy { MutableLiveData<Placeholder>() }
    private val _goTo by lazy { MutableLiveData<NavData>() }
    private val _dialog by lazy { MutableLiveData<DialogData>() }
    private val _finish by lazy { MutableLiveData<Unit>() }

    protected fun setPlaceholder(placeholder: Placeholder) {
        _placeholder.postValue(placeholder)
    }

    protected fun setDialog(dialogData: DialogData) {
        _dialog.postValue(dialogData)
    }

    protected fun setDialog(
        throwable: Throwable,
        retryAction: (() -> Unit)? = null
    ) {
        setDialog(errorHandler.getDialogData(throwable, retryAction))
    }

    protected fun finish(){
        _finish.value = Unit
    }

    protected fun goTo(navData: NavData) {
        _goTo.postValue(navData)
    }

    protected fun launchDataLoad(
        shouldLoad: Boolean = true,
        onFailure: (Throwable) -> Unit = ::onFailure,
        block: suspend () -> Unit
    ): Job {
        return viewModelScope.launch {
            try {
                if (shouldLoad) setPlaceholder(Loading())
                block()
            } catch (error: Throwable) {
                onFailure(error)
            } finally {
                setPlaceholder(Hide())
            }
        }
    }

    private fun onFailure(trowable: Throwable) {
        setDialog(trowable)
    }
}