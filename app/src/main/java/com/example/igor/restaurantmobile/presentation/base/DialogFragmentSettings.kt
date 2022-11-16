package com.example.igor.restaurantmobile.presentation.base

import android.app.Dialog
import android.os.Bundle
import android.provider.Settings
import android.util.Base64
import android.util.DisplayMetrics
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import com.example.igor.restaurantmobile.R
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*


class DialogFragmentSettings : BaseDialogFragment<DialogFragmentSettings.OnStateLicense>() {
    interface OnStateLicense {
        fun onLicenseActive(isActive: Boolean)
        fun onConnectionStable(isConnected: Boolean)
    }
    // Create a Dialog using default AlertDialog builder , if not inflate custom view in onCreateView
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val isConnectionFragment = requireArguments().getBoolean("type")

        val dialogView: View = requireActivity().layoutInflater.inflate(R.layout.fragment_license, null)

        val dialog = Dialog(requireActivity())  //, R.style.FullScreenDialog
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(dialogView)
        dialog.setCancelable(false)

        if(isConnectionFragment){


            val displayMetrics = DisplayMetrics()
            requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
            dialog.window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)

        }else{
            //declare and set listener for fragment when set license
            var licenseVerified = false

            val securityCode = dialogView.findViewById<TextView>(R.id.code_lic2)
            val activationCode = dialogView.findViewById<EditText>(R.id.et_key2)
            val textError = dialogView.findViewById<TextView>(R.id.textErrorIncorectCode)
            val buttonCheck = dialogView.findViewById<Button>(R.id.btn_verify2)
            val buttonCancel = dialogView.findViewById<TextView>(R.id.textCancel)

            val androidId = "" + Settings.Secure.getString(
                context?.contentResolver,
                Settings.Secure.ANDROID_ID
            )
            val deviceUuid = UUID(
                androidId.hashCode().toLong(),
                androidId.hashCode().toLong() shl 32 or androidId.hashCode().toLong()
            )
            var deviceId = deviceUuid.toString()
            deviceId = deviceId.replace("-", "")
            val code = deviceId.substring(10, 18)
            securityCode.text = code.uppercase(Locale.getDefault())

            val internCode = getMD5Code(code.uppercase(Locale.getDefault()) + "ENCEFALOMIELOPOLIRADICULONEVRITA")

//            if(CustomLocalStorage.readString(LICENSE_KEY) != "" && CustomLocalStorage.readString(LICENSE_KEY) == internCode){
//                licenseVerified = true
//                activationCode.setText(internCode)
//                buttonCheck.isEnabled = false
//                activationCode.isEnabled = false
//            }
//
//            buttonCheck.setOnClickListener {
//                if(internCode == activationCode.text.toString()){
//                    licenseVerified = true
//                    CustomLocalStorage.writeString(LICENSE_KEY, activationCode.text.toString())
//                    activityInstance!!.onLicenseActive(licenseVerified)
//                    this@DialogFragmentSettings.dismiss()
//                }
//                else{
//                    textError.visibility = View.VISIBLE
//                }
//            }

            activationCode.addTextChangedListener {
                textError.visibility = View.GONE
            }


            buttonCancel.setOnClickListener {
                activityInstance!!.onLicenseActive(licenseVerified)
                this@DialogFragmentSettings.dismiss()
            }

            val displayMetrics = DisplayMetrics()
            requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
            val displayWidth = WindowManager.LayoutParams.MATCH_PARENT
            val displayHeight = displayMetrics.heightPixels
            val dialogWindowWidth = (displayWidth * 0.8f).toInt()
            val dialogWindowHeight = (displayHeight * 0.6f).toInt()

//        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.window!!.setLayout(displayWidth, dialogWindowHeight)
        }





        return dialog
    }


    companion object {
        // Create an instance of the Dialog with the description
        fun newInstance(isConnectionFragment: Boolean): DialogFragmentSettings {
            val frag = DialogFragmentSettings()
            val args = Bundle()
            args.putBoolean("type", isConnectionFragment)

            frag.arguments = args
            return frag
        }
    }

    private fun getMD5Code(s: String): String {
        val MD5 = "MD5"
        try {
            // Create MD5 Hash
            val digest = MessageDigest
                .getInstance(MD5)
            digest.update(s.toByteArray())
            val messageDigest = digest.digest()
            val encode = Base64.encode(messageDigest, 0)
            val respencode = String(encode).uppercase(Locale.getDefault())
            // Create String
            var digits = ""
            for (i in 0 until respencode.length) {
                val chrs = respencode[i]
                if (!Character.isDigit(chrs)) digits = digits + chrs
            }
            var keyLic = ""
            for (k in 0 until digits.length) {
                if (Character.isLetter(digits[k])) {
                    keyLic = keyLic + digits[k]
                }
            }
            keyLic = keyLic.substring(0, 8)
            return keyLic
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
        return ""
    }
}