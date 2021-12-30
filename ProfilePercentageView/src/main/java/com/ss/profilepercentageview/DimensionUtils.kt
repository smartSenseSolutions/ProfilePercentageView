package com.ss.profilepercentageview

import android.content.Context
import android.util.DisplayMetrics

object DimensionUtils {
    fun getSizeInPixels(dp: Float, context: Context): Int {
        val metrics = context.resources.displayMetrics
        val pixels = metrics.density * dp
        return (pixels + 0.5f).toInt()
    }
}