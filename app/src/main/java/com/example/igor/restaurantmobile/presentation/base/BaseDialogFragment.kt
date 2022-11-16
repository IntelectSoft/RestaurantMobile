package com.example.igor.restaurantmobile.presentation.base

import android.content.Context
import androidx.fragment.app.DialogFragment


abstract class BaseDialogFragment<T> : DialogFragment() {
    var activityInstance: T? = null
        private set

    override fun onAttach(activity: Context) {
        activityInstance = activity as T
        super.onAttach(activity)
    }

    override fun onDetach() {
        super.onDetach()
        activityInstance = null
    }
}