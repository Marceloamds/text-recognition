package com.jera.vision.domain.bill

import android.graphics.Point
import com.google.mlkit.vision.text.Text
import com.jera.vision.domain.entity.MonthConsumption
import com.jera.vision.domain.util.resource.createMonthsList
import com.jera.vision.domain.util.resource.isBeneath
import com.jera.vision.domain.util.resource.matchesHorizontally
import com.jera.vision.domain.util.resource.withYear
import com.jera.vision.presentation.util.extension.isEqualString
import com.jera.vision.presentation.view.vision.VisionActivity
import java.util.*

data class CopelBill(
    override val name: String = "Copel"
): Bill(name) {

    override fun toString(): String {
        return name
    }

    private var elements: MutableList<Text.Element> = mutableListOf()
    private var kwhTagCornerPoints: Array<Point>? = null
    private var monthTagCornerPoints: Array<Point>? = null

    override fun getConsumptionList(textBlocks: List<Text.TextBlock>): List<MonthConsumption> {
        elements = mutableListOf()
        textBlocks.forEach {
            it.lines.forEach { line ->
                if (line.text == COPEL_KWH_TAG) kwhTagCornerPoints = line.cornerPoints
                if (line.text == COPEL_MONTH_TAG) monthTagCornerPoints = line.cornerPoints
                elements.addAll(line.elements)
            }
        }

        val numberList = getNumberList()
        val dateList = getDateList()
        val consumptionList = mutableListOf<MonthConsumption>()

        if (dateList.size <= numberList.size) {
            dateList.forEachIndexed { index, _ ->
                consumptionList.add(MonthConsumption(dateList[index], numberList[index]))
            }
        } else {
            numberList.forEachIndexed { index, _ ->
                consumptionList.add(MonthConsumption(dateList[index], numberList[index]))
            }
        }
        return consumptionList.toList()
    }

    private fun getDateList(): List<Date> {
        val dateList = mutableListOf<Date>()
        val monthList = createMonthsList()
        elements.forEach { element ->
            if (element.text.contains("/")) {
                val splittedText = element.text.split("/")
                if (splittedText.size == 2) {
                    val month = splittedText[0]
                    val year = splittedText[1]
                    monthList.forEach {
                        if (
                            it.isEqualString(month, true) &&
                            element.matchesHorizontally(monthTagCornerPoints) &&
                            element.isBeneath(monthTagCornerPoints)
                        ) {
                            val dateWithYear = it.withYear(year.toIntOrNull() ?: 2020)
                            dateList.add(dateWithYear)
                        }
                    }
                }
            } else {
                monthList.forEach {
                    if (
                        it.isEqualString(element.text) &&
                        element.matchesHorizontally(monthTagCornerPoints) &&
                        element.isBeneath(monthTagCornerPoints)
                    ) {
                        dateList.add(it)
                    }
                }
            }
        }
        return dateList
    }

    private fun getNumberList(): List<Double> {
        val numberList = mutableListOf<Double>()
        elements.forEach {
            if (Regex("^(-?)(0|([1-9][0-9]*))(\\.[0-9]+)?$").matches(it.text) &&
                it.matchesHorizontally(kwhTagCornerPoints) &&
                it.isBeneath(kwhTagCornerPoints)
            ) {
                numberList.add(it.text.toDouble())
            }
        }
        return numberList
    }

    companion object {
        const val COPEL_KWH_TAG = "Fora Pta."
        const val COPEL_MONTH_TAG = "Mes/Ano"
        const val TAG_VARIANCE = 15
    }
}