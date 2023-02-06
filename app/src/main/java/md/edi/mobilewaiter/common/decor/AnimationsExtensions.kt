package md.edi.mobilewaiter.common.decor

import android.animation.Animator
import android.view.View

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
