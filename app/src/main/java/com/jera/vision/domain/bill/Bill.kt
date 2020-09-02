package com.jera.vision.domain.bill

import com.google.mlkit.vision.text.Text
import com.jera.vision.domain.entity.MonthConsumption
import java.io.Serializable

abstract class Bill(
    open val name: String
) : Serializable {

    abstract fun getConsumptionList(textBlocks: List<Text.TextBlock>): List<MonthConsumption>
}