package md.edi.mobilewaiter.presentation.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.Window
import androidx.annotation.ColorInt
import md.edi.mobilewaiter.R
import md.edi.mobilewaiter.databinding.DialogInputTextBinding
import md.edi.mobilewaiter.utils.InputFilterMinMax


class DialogActionInputText(
    activity: Context,
    private val title: String? = null,
    private val description: String? = null,
    private val okButtonLabel: String? = null,
    private val closeButtonLabel: String? = null,
    private val onSuccessClick: ((dialogAction: DialogActionInputText, text: String) -> Unit)? = null,
    private var onCloseClick: ((dialogAction: DialogActionInputText) -> Unit)? = null,
    @ColorInt private val titleColor: Int? = null
) : Dialog(activity) {

    var isFirstSelectable = true
    lateinit var binding: DialogInputTextBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogInputTextBinding.inflate(layoutInflater)
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

        binding.button0.setOnClickListener { setNumber("0") }
        binding.button1.setOnClickListener { setNumber("1") }
        binding.button2.setOnClickListener { setNumber("2") }
        binding.button3.setOnClickListener { setNumber("3") }
        binding.button4.setOnClickListener { setNumber("4") }
        binding.button5.setOnClickListener { setNumber("5") }
        binding.button6.setOnClickListener { setNumber("6") }
        binding.button7.setOnClickListener { setNumber("7") }
        binding.button8.setOnClickListener { setNumber("8") }
        binding.button9.setOnClickListener { setNumber("9") }
        binding.buttonClear.setOnClickListener { binding.textInput.text = "" }
        binding.buttonBack.setOnClickListener {
            var text = binding.textInput.text.toString()
            if (text.isNotEmpty()) {
                text = text.substring(0, text.lastIndex)
                if (text.isBlank()) {
                    binding.textInput.text = ""
                } else {
                    binding.textInput.text = text
                }
            }
        }

//        binding.textInput.requestFocus()
//        binding.textInput.onFocusChangeListener = OnFocusChangeListener { v, hasFocus ->
//            if (hasFocus) {
//                window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
//            }
//        }
//        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//        imm.showSoftInput(binding.textInput, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun setNumber(number: String) {
        if(isFirstSelectable){
            binding.textInput.text = ""
            isFirstSelectable = false
        }
        if (binding.textInput.text.toString().isEmpty()) {
            if (number != "0") {
                binding.textInput.text = number
            }
        } else {
            binding.textInput.append(number)
        }
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
            onSuccessClick?.invoke(this, binding.textInput.text.toString())
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