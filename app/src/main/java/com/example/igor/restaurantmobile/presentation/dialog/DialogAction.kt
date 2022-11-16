package com.example.igor.restaurantmobile.presentation.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.Window
import androidx.annotation.ColorInt
import com.example.igor.restaurantmobile.R
import com.example.igor.restaurantmobile.databinding.DialogTerminalRegisterBinding

class DialogAction(
    activity: Context,
    private val title: String? = null,
    private val description: String? = null,
    private val okButtonLabel: String? = null,
    private val closeButtonLabel: String? = null,
    private val onSuccessClick: ((dialogAction: DialogAction) -> Unit)? = null,
    private var onCloseClick: ((dialogAction: DialogAction) -> Unit)? = null,
    @ColorInt private val titleColor: Int? = null
) : Dialog(activity) {

    lateinit var binding: DialogTerminalRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogTerminalRegisterBinding.inflate(layoutInflater)
        init()
    }

    private fun init() {
        setupWindow()
        setContentView(binding.root)
        setupTitle()
        setupDescription()
        setupCancelBtn()
        setOkButton()
        setVerticalLine()
    }

    private fun setVerticalLine() {
        binding.verticalLine.visibility =
            if (closeButtonLabel == null) View.GONE
            else View.VISIBLE
    }

    private fun setOkButton() {
        if (okButtonLabel != null) {
            binding.ok.text = okButtonLabel
            binding.okBtn.visibility = View.VISIBLE
        } else {
            binding.okBtn.visibility = View.GONE
        }
    }

    private fun setupCancelBtn() {
        if (closeButtonLabel != null) {
            binding.cancel.text = closeButtonLabel
            binding.cancelBtn.visibility = View.VISIBLE
        } else {
            binding.cancelBtn.visibility = View.GONE
        }

        binding.okBtn.setOnClickListener {
            onSuccessClick?.invoke(this)
            dismiss()
        }
    }

    private fun setupDescription() {
        if (description != null) {
            binding.description.text = description
            binding.description.visibility = View.VISIBLE
        } else {
            binding.description.visibility = View.GONE
        }

        binding.cancelBtn.setOnClickListener {
            onCloseClick?.invoke(this)
            dismiss()
        }
    }

    private fun setupTitle() {
        if (title != null) {
            binding.title.visibility = View.VISIBLE
            binding.title.text = title
        } else {
            binding.title.visibility = View.GONE
        }

        if (titleColor != null)
            binding.title.setTextColor(titleColor)
    }

    private fun setupWindow() {
        if (window != null) {
            window!!.requestFeature(Window.FEATURE_NO_TITLE)
            window!!.setBackgroundDrawableResource(android.R.color.transparent)
            window!!.decorView.setBackgroundResource(android.R.color.transparent)
            window!!.setDimAmount(0.3f)
            setCanceledOnTouchOutside(false)

            val displayMetrics = context.resources.displayMetrics
            val width = displayMetrics.widthPixels
            val wmlp = window!!.attributes
            window?.attributes?.windowAnimations = R.style.DialogAnimationFromCenter
            wmlp.width = width
            wmlp.height = displayMetrics.heightPixels
        }
    }
}