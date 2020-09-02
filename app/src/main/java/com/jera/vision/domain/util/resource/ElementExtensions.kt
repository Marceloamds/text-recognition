package com.jera.vision.domain.util.resource

import android.graphics.Point
import com.google.mlkit.vision.text.Text
import com.jera.vision.domain.bill.CopelBill.Companion.TAG_VARIANCE
import kotlin.math.abs

fun Text.Element.matchesHorizontally(comparedCornerPoints: Array<Point>?): Boolean {
    return if (cornerPoints?.size ?: 0 == 4 && comparedCornerPoints?.size ?: 0 == 4) {
        cornerPoints?.get(0)?.x!! + TAG_VARIANCE >= comparedCornerPoints?.get(0)?.x!!
                && cornerPoints?.get(1)?.x!! - TAG_VARIANCE <= comparedCornerPoints[1].x
    } else true
}

fun Text.Element.isBeneath(comparedCornerPoints: Array<Point>?): Boolean {
    return if (cornerPoints?.size ?: 0 == 4 && comparedCornerPoints?.size ?: 0 == 4) {
        cornerPoints?.get(1)?.y!! > comparedCornerPoints?.get(2)?.y!!
    } else true
}

fun Text.Element.matchesVertically(comparedCornerPoints: Array<Point>?): Boolean {
    return if (cornerPoints?.size ?: 0 == 4 && comparedCornerPoints?.size ?: 0 == 4) {
        cornerPoints?.get(0)?.y!! + TAG_VARIANCE >= comparedCornerPoints?.get(0)?.y!!
                && cornerPoints?.get(1)?.y!! - TAG_VARIANCE <= comparedCornerPoints[1].y
    } else true
}

fun Text.Line.matchesVertically(comparedCornerPoints: Array<Point>?): Boolean {
    return if (cornerPoints?.size ?: 0 == 4 && comparedCornerPoints?.size ?: 0 == 4) {
        cornerPoints?.get(0)?.y!! + TAG_VARIANCE >= comparedCornerPoints?.get(0)?.y!!
                && cornerPoints?.get(1)?.y!! - TAG_VARIANCE <= comparedCornerPoints[1].y
    } else true
}

fun Text.Element.isOnLeftOf(comparedCornerPoints: Array<Point>?): Boolean {
    return if (cornerPoints?.size ?: 0 == 4 && comparedCornerPoints?.size ?: 0 == 4) {
        cornerPoints?.get(3)?.x!! < comparedCornerPoints?.get(0)?.x!!
    } else true
}

fun Text.Element.isClosestTo(element: Text.Element, comparedElement: Text.Element?): Boolean {
    return comparedElement?.let {
        abs(cornerPoints?.get(0)?.x!! - element.cornerPoints?.get(0)?.x!!) <
                abs(cornerPoints?.get(0)?.x!! - comparedElement.cornerPoints?.get(0)?.x!!)
    } ?: true
}
