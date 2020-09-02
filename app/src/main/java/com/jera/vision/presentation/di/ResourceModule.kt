package com.jera.vision.presentation.di

import com.jera.vision.domain.util.resource.Strings
import com.jera.vision.presentation.util.error.ErrorHandler
import com.jera.vision.presentation.util.logger.Logger
import org.koin.dsl.module

fun resourceModule() = module {

    single {
        Strings(get())
    }

    single {
        Logger(get())
    }

    single {
        ErrorHandler(get(), get())
    }
}
