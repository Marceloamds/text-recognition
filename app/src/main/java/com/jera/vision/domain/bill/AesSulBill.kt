package com.jera.vision.domain.bill

import com.google.mlkit.vision.text.Text
import com.jera.vision.domain.entity.MonthConsumption
import com.jera.vision.domain.util.resource.*
import com.jera.vision.presentation.util.extension.isEqualString

data class AesSulBill(
    override val name: String = "AES Sul"
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
            if (element.text.contains("/")) {
                val splittedText = element.text.split("/")
                if (splittedText.size == 2) {
                    val month = splittedText[0]
                    val year = splittedText[1]
                    monthList.forEach { monthConstant ->
                        if (monthConstant.isEqualString(month)) {
                            val dateWithYear = monthConstant.withYear(year.toIntOrNull() ?: 2020)
                            numberList.forEach { numberElement ->
                                if (
                                    element.matchesVertically(numberElement.cornerPoints) &&
                                    element.isOnLeftOf(numberElement.cornerPoints)
                                ) {
                                    monthComsumptionList.find { it.month == dateWithYear }?.let {
                                        if (numberElement.isClosestTo(element, it.monthElement)){
                                            monthComsumptionList.remove(it)
                                            monthComsumptionList.add(
                                                MonthConsumption(
                                                    dateWithYear,
                                                    numberElement.text.toDouble(),
                                                    element,
                                                    numberElement
                                                )
                                            )
                                        }
                                    } ?: run {
                                        monthComsumptionList.add(
                                            MonthConsumption(
                                                dateWithYear,
                                                numberElement.text.toDouble(),
                                                element,
                                                numberElement
                                            )
                                        )
                                    }
                                }
                            }
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