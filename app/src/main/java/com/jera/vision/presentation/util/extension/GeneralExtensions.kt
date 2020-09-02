package com.jera.vision.presentation.util.extension

import com.jera.vision.domain.util.resource.format
import java.util.*

fun consume(f: () -> Unit): Boolean {
    f()
    return true
}

fun Date.isEqualString(monthString: String, shouldAcceptNumber: Boolean = false): Boolean {
    return format("MMMM").equals(monthString, true) ||
            format("MMMMM").equals(monthString, true) ||
            format("MMM").equals(monthString, true) ||
            (format("MM").equals(monthString, true) && shouldAcceptNumber) ||
            monthString.equals("mal", true) && format("MMM").equals("Mai", true) ||
            monthString.equals("ma", true) && format("MMM").equals("Mai", true)
}