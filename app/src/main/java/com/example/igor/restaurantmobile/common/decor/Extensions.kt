package com.example.igor.restaurantmobile.common.decor

import android.content.res.ColorStateList
import android.graphics.Color
import android.widget.ImageView

fun ImageView.setTint(parcelableColor: String){
    val color = ColorStateList.valueOf(Color.parseColor(parcelableColor))
    this.imageTintList = color
}