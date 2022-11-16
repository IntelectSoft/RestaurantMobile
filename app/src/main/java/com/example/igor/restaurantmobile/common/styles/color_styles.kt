package com.example.igor.restaurantmobile.common.styles

import android.content.Context
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat

sealed class TextColor {
    data class Resource(@ColorRes val color: Int): TextColor()
    data class Simple(@ColorInt val color: Int) : TextColor()
    data class StateList(@ColorRes val color: Int) : TextColor()
}

fun TextView.setTextColor(color : TextColor, context: Context) {
    when (color) {
        is TextColor.Resource -> this.setTextColor(ContextCompat.getColor(context, color.color))
        is TextColor.Simple -> this.setTextColor(color.color)
        is TextColor.StateList -> this.setTextColor(ContextCompat.getColorStateList(context, color.color))
    }
}