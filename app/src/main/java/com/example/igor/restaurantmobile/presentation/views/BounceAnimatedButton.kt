package com.example.igor.restaurantmobile.presentation.views

import android.content.Context
import android.os.Handler
import android.os.SystemClock
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.RelativeLayout
import com.example.igor.restaurantmobile.R

class BounceAnimatedButton : RelativeLayout {
    private val isFullscreen = false
    private var downWasAnimated = false
    private var wasCanceled = false
    private var mLastClickTime: Long = 0

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init(context, attrs)
    }

    fun init(context: Context?, attrs: AttributeSet?) {
        setOnTouchListener { v: View?, event: MotionEvent ->
            if (event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_CANCEL) {
                wasCanceled = true
                if (downWasAnimated) {
                    animateButtonUp()
                    downWasAnimated = false
                    println("animateButtonUp")
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                        disable()
                        val handler = Handler()
                        handler.postDelayed({ enable() }, 1000)
                    }
                    mLastClickTime = SystemClock.elapsedRealtime()
                }
            } else if (event.action == MotionEvent.ACTION_DOWN) {
                if (!wasCanceled) {
                    animateButtonDown()
                }
            }
            false
        }
        setOnClickListener { println("click this") }
    }

    fun animateButtonDown() {
        downWasAnimated = true
        val myAnimBounce = AnimationUtils.loadAnimation(context, R.anim.bounce_down)
        //  final Animation myAnimAlpha = AnimationUtils.loadAnimation(getContext(), R.anim.alpha_down);
        val interpolator = MyBounceInterpolator(0.07f, 10f)
        myAnimBounce.interpolator = interpolator
        // myAnimAlpha.setInterpolator(interpolator);
        myAnimBounce.fillAfter = true
        //  myAnimAlpha.setFillAfter(true);
        if (isFullscreen) {
            if (childCount > 0) getChildAt(0).startAnimation(myAnimBounce) else startAnimation(
                myAnimBounce
            )
        } else {
            startAnimation(myAnimBounce)
        }
    }

    fun animateButtonUp() {
        wasCanceled = false
        val myAnimBounce = AnimationUtils.loadAnimation(context, R.anim.bounce_up)
        //   final Animation myAnimAlpha = AnimationUtils.loadAnimation(getContext(), R.anim.alpha_up);
        val interpolator = MyBounceInterpolator(0.07f, 10f)
        myAnimBounce.interpolator = interpolator
        //   myAnimAlpha.setInterpolator(interpolator);
        myAnimBounce.fillAfter = true
        //  myAnimAlpha.setFillAfter(true);
        if (isFullscreen) {
            getChildAt(0).startAnimation(myAnimBounce)
        } else {
            startAnimation(myAnimBounce)
        }
    }

    override fun setVisibility(visibility: Int) {
        super.setVisibility(visibility)
        for (i in 0 until childCount) {
            getChildAt(i).visibility = visibility
        }
    }

    fun enable() {
        isEnabled = true
        for (i in 0 until childCount) {
            getChildAt(i).isEnabled = true
        }
    }

    fun disable() {
        isEnabled = false
        for (i in 0 until childCount) {
            getChildAt(i).isEnabled = false
        }
    }
}