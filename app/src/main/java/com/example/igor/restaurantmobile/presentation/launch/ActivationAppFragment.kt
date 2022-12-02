package com.example.igor.restaurantmobile.presentation.launch

import android.app.Dialog
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.example.igor.restaurantmobile.R
import com.example.igor.restaurantmobile.data.listeners.OnLicenseListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ActivationAppFragment : DialogFragment(){

    private val dialogs by lazy {
        Dialog(requireActivity())
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

         //, R.style.FullScreenDialog
        dialogs.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogs.setContentView(layoutInflater.inflate(R.layout.dialog_license, null))
        dialogs.setCancelable(false)

        val displayMetrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
        dialogs.window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)

        val onCancel = dialogs.findViewById<TextView>(R.id.textCancel)
        val onActivate = dialogs.findViewById<Button>(R.id.btn_activate)
        val textCode = dialogs.findViewById<EditText>(R.id.editTextCode)

        onActivate.setOnClickListener {
            onLicenseListener?.onLicenseActivate(dialogs, textCode.text.toString())
        }

        onCancel.setOnClickListener {
            onLicenseListener?.onCancelActivate(dialogs)
        }

        return dialogs
    }

    companion object {
        var onLicenseListener: OnLicenseListener? = null

    }

    fun setListener(list: OnLicenseListener) {
        onLicenseListener = list
    }
}