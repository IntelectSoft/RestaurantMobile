package com.example.igor.restaurantmobile.common.decor

import android.animation.Animator
import android.view.View
import android.view.ViewGroup
import androidx.transition.Fade
import androidx.transition.Transition
import androidx.transition.TransitionManager

fun getGoneListener(view: View): Animator.AnimatorListener{
    return object : Animator.AnimatorListener{
        override fun onAnimationStart(p0: Animator) {
        }

        override fun onAnimationEnd(p0: Animator) {
            view.visibility = View.GONE
        }

        override fun onAnimationCancel(p0: Animator) {
        }

        override fun onAnimationRepeat(p0: Animator) {
        }

    }
}
