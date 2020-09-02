package com.jera.vision.domain.util.resource

import android.text.format.Time.TIMEZONE_UTC
import java.text.SimpleDateFormat
import java.util.*

fun createMonthsList(year: Int = 2020): List<Date> {
    return listOf(
        createMonths(year, Calendar.JANUARY),
        createMonths(year, Calendar.FEBRUARY),
        createMonths(year, Calendar.MARCH),
        createMonths(year, Calendar.APRIL),
        createMonths(year, Calendar.MAY),
        createMonths(year, Calendar.JUNE),
        createMonths(year, Calendar.JULY),
        createMonths(year, Calendar.AUGUST),
        createMonths(year, Calendar.SEPTEMBER),
        createMonths(year, Calendar.OCTOBER),
        createMonths(year, Calendar.NOVEMBER),
        createMonths(year, Calendar.DECEMBER)
    )
}

private fun createMonths(year: Int, month: Int): Date {
    return GregorianCalendar(year, month, 1).time
}

fun Date.format(pattern: String, timeZone: String? = TIMEZONE_UTC): String {
    val dateFormatter = SimpleDateFormat(pattern, Locale.getDefault())
    dateFormatter.timeZone = TimeZone.getTimeZone(timeZone)
    return dateFormatter.format(this).toUpperCase()
}

fun Date.withYear(year: Int): Date {
    var trueYear = year
    if (trueYear < 100) trueYear += 2000
    val calendar = Calendar.getInstance()
    calendar.apply {
        time = this@withYear
        set(Calendar.YEAR, trueYear)
    }
    return calendar.time
}