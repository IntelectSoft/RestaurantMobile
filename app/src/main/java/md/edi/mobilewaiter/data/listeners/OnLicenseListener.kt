package md.edi.mobilewaiter.data.listeners

import android.app.Dialog

interface OnLicenseListener {
    fun onLicenseActivate(dialogs: Dialog, toString: String)
    fun onCancelActivate(dialogs: Dialog)
}