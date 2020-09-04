package com.jera.vision.domain.bill

import com.google.mlkit.vision.text.Text
import com.jera.vision.domain.entity.MonthConsumption
import com.jera.vision.domain.util.resource.createMonthsList
import com.jera.vision.domain.util.resource.matchesHorizontally
import com.jera.vision.domain.util.resource.withYear
import com.jera.vision.presentation.util.extension.isEqualString

data class CelpaBill(
    override val name: String = "CELPA"
) : Bill(name) {

    override fun toString(): String {
        return name
    }

    private var elements: MutableList<Text.Element> = mutableListOf()

    override fun getConsumptionList(textBlocks: List<Text.TextBlock>): List<MonthConsumption> {
        elements = mutableListOf()
        textBlocks.forEach {
            it.lines.forEach { line -> elements.addAll(line.elements) }
        }

        val numberList = getNumberList()
        val consumptionList = compareNumberAndDates(numberList)

        return consumptionList.toList()
    }

    private fun compareNumberAndDates(numberList: List<Text.Element>): List<MonthConsumption> {
        val monthComsumptionList = mutableListOf<MonthConsumption>()
        val monthList = createMonthsList()
        elements.forEach { element ->
            monthList.forEach { monthConstant ->
                if (monthConstant.isEqualString(element.text)) {
                    val dateWithYear = monthConstant.withYear(2020)
                    numberList.forEach { numberElement ->
                        if (numberElement.matchesHorizontally(element.cornerPoints)) {
                            monthComsumptionList.add(
                                MonthConsumption(
                                    dateWithYear,
                                    numberElement.text.toDouble()
                                )
                            )
                        }
                    }
                }
            }
        }
        return monthComsumptionList
    }

    private fun getNumberList(): List<Text.Element> {
        val numberList = mutableListOf<Text.Element>()
        elements.forEach {
            if (Regex("^(-?)(0|([1-9][0-9]*))(\\.[0-9]+)?$").matches(it.text)) {
                numberList.add(it)
            }
        }
        return numberList
    }
}