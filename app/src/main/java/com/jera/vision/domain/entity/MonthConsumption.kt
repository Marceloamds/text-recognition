package com.jera.vision.domain.entity

import com.google.mlkit.vision.text.Text
import java.io.Serializable
import java.util.*

data class MonthConsumption(
    val month: Date,
    val kWhConsumption: Double,
    val monthElement: Text.Element? = null,
    val kwhElement: Text.Element? = null
) : Serializable