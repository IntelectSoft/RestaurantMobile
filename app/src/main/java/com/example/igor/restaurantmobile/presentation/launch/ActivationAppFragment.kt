package com.example.igor.restaurantmobile.presentation.launch

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.igor.restaurantmobile.R
import com.example.igor.restaurantmobile.data.listeners.OnLicenseListener
import com.example.igor.restaurantmobile.utils.ContextManager
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

        textCode.afterTextChanged {
            if (it.isEmpty()){
                textCode.error = "Completati codul licentei!"
            }
            else{
                textCode.error = null
            }
        }

        onActivate.setOnClickListener {
            if(textCode.error == null && textCode.text.toString().isNotEmpty()){
                onLicenseListener?.onLicenseActivate(dialogs, textCode.text.toString())
            }
            else{
                Toast.makeText(ContextManager.retrieveApplicationContext(), "Completati codul de activare!", Toast.LENGTH_SHORT).show()
            }
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
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}