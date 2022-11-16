package com.example.igor.restaurantmobile.common.styles

import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.annotation.DimenRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView

interface IMargin {
    object None : IMargin
}

sealed class Margin : IMargin {
    sealed class Symmetric : Margin() {
        class Horizontal(@DimenRes val resource: Int) : Symmetric()
        class Vertical(@DimenRes val resource: Int) : Symmetric()
    }

    data class Only(
        @DimenRes val top: Int = -1,
        @DimenRes val bottom: Int = -1,
        @DimenRes val left: Int = -1,
        @DimenRes val right: Int = -1
    ) : Margin()
}



fun View.setMargin(margin: IMargin) {
    val layoutParams = this.layoutParams
    val context = this.context
    var leftMargin = 0;
    var rightMargin = 0;
    var topMargin = 0;
    var bottomMargin = 0;

    //setup defaults (from xml file)
    when (layoutParams) {
        is LinearLayout.LayoutParams -> {
            topMargin = layoutParams.topMargin
            bottomMargin = layoutParams.bottomMargin
            leftMargin = layoutParams.leftMargin
            rightMargin = layoutParams.rightMargin
        }

        is RelativeLayout.LayoutParams -> {
            topMargin = layoutParams.topMargin
            bottomMargin = layoutParams.bottomMargin
            leftMargin = layoutParams.leftMargin
            rightMargin = layoutParams.rightMargin
        }

        is ConstraintLayout.LayoutParams -> {
            topMargin = layoutParams.topMargin
            bottomMargin = layoutParams.bottomMargin
            leftMargin = layoutParams.leftMargin
            rightMargin = layoutParams.rightMargin
        }

        is RecyclerView.LayoutParams -> {
            topMargin = layoutParams.topMargin
            bottomMargin = layoutParams.bottomMargin
            leftMargin = layoutParams.leftMargin
            rightMargin = layoutParams.rightMargin
        }
    }

    //check configurations
    when (margin) {
        is Margin.Symmetric.Horizontal -> {
            leftMargin = context.resources.getDimension(margin.resource).toInt()
            rightMargin = context.resources.getDimension(margin.resource).toInt()
        }

        is Margin.Symmetric.Vertical -> {
            topMargin = context.resources.getDimension(margin.resource).toInt()
            bottomMargin = context.resources.getDimension(margin.resource).toInt()
        }

        is Margin.Only -> {
            if (margin.left != -1)
                leftMargin = context.resources.getDimension(margin.left).toInt()
            if (margin.right != -1)
                rightMargin = context.resources.getDimension(margin.right).toInt()
            if (margin.top != -1)
                topMargin = context.resources.getDimension(margin.top).toInt()
            if (margin.bottom != -1)
                bottomMargin = context.resources.getDimension(margin.bottom).toInt()
        }
    }

    println("layoutParams " + layoutParams)

    //setup new configurations
    when (layoutParams) {
        is LinearLayout.LayoutParams -> {
            layoutParams.topMargin = topMargin
            layoutParams.bottomMargin = bottomMargin
            layoutParams.leftMargin = leftMargin
            layoutParams.rightMargin = rightMargin
        }

        is RelativeLayout.LayoutParams -> {
            layoutParams.topMargin = topMargin
            layoutParams.bottomMargin = bottomMargin
            layoutParams.leftMargin = leftMargin
            layoutParams.rightMargin = rightMargin
        }

        is ConstraintLayout.LayoutParams -> {
            layoutParams.topMargin = topMargin
            layoutParams.bottomMargin = bottomMargin
            layoutParams.leftMargin = leftMargin
            layoutParams.rightMargin = rightMargin
        }

        is RecyclerView.LayoutParams -> {
            layoutParams.topMargin = topMargin
            layoutParams.bottomMargin = bottomMargin
            layoutParams.leftMargin = leftMargin
            layoutParams.rightMargin = rightMargin
        }
    }
    this.layoutParams = layoutParams
}