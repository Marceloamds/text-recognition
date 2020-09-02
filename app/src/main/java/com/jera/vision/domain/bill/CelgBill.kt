package com.jera.vision.domain.bill

import com.google.mlkit.vision.text.Text
import com.jera.vision.domain.entity.MonthConsumption
import com.jera.vision.domain.util.resource.createMonthsList
import com.jera.vision.domain.util.resource.matchesVertically
import com.jera.vision.domain.util.resource.withYear
import com.jera.vision.presentation.util.extension.isEqualString

data class CelgBill(
    override val name: String = "CELG"
) : Bill(name) {

    override fun toString(): String {
        return name
    }

    private var elements: MutableList<Text.Line> = mutableListOf()

    override fun getConsumptionList(textBlocks: List<Text.TextBlock>): List<MonthConsumption> {
        elements = mutableListOf()
        textBlocks.forEach {
            elements.addAll(it.lines)
        }

        val numberList = getNumberList()
        val consumptionList = compareNumberAndDates(numberList)

        return consumptionList.toList()
    }

    private fun compareNumberAndDates(numberList: List<Text.Line>): List<MonthConsumption> {
        val monthComsumptionList = mutableListOf<MonthConsumption>()
        val monthList = createMonthsList()
        elements.forEach { element ->
            if (element.text.contains("/")) {
                val splittedText = element.text.split("/")
                if (splittedText.size == 2) {
                    val month = splittedText[0]
                    val year = splittedText[1].replace(" ", "")
                    monthList.forEach { monthConstant ->
                        if (monthConstant.isEqualString(month)) {
                            val dateWithYear = monthConstant.withYear(year.toIntOrNull() ?: 2020)
                            numberList.forEach { numberElement ->
                                if (
                                    element.matchesVertically(numberElement.cornerPoints)
                                ) {
                                    monthComsumptionList.add(
                                        MonthConsumption(
                                            dateWithYear,
                                            numberElement.text.replace(",", ".").toDouble()
                                        )
                                    )
                                }

                            }
                        }
                    }
                }
            }
        }
        return monthComsumptionList
    }

    private fun getNumberList(): List<Text.Line> {
        val numberList = mutableListOf<Text.Line>()
        elements.forEach {
            if (Regex("^(-?)(0|([1-9][0-9]*))(\\,[0-9]+)?$").matches(it.text) &&
                it.text.contains(",")
            ) {
                numberList.add(it)
            }
        }
        return numberList
    }
}