package com.ss.profilepercentageview

import android.graphics.Canvas

interface Painter {
    fun draw(canvas: Canvas?)
    fun setDashWidth(value: Float)
    fun setArcWidth(value: Float)
    fun setDashSpace(value: Float)
    fun setDashEnable(value: Boolean)
    fun setRounded(value: Boolean)
    fun setBackColor(color: Int)
    fun getBackColor(): Int
    fun onSizeChanged(height: Int, width: Int)
}