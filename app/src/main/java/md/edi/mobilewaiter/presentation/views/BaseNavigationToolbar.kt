package md.edi.mobilewaiter.presentation.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.viewbinding.ViewBinding
import md.edi.mobilewaiter.databinding.BaseNavToolbarBinding

abstract class BaseNavigationToolbar<T : ViewBinding> @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0,
) : RelativeLayout(context, attrs, defStyleAttr) {

    var binding: BaseNavToolbarBinding? = null


    abstract fun getViewBinding(): T


    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        val viewBinding = getViewBinding()
        binding = BaseNavToolbarBinding.inflate(
            LayoutInflater.from(context),
            this, true
        )

        val parent = viewBinding.root.parent
        parent?.let {
            ((it as ViewGroup).removeView(viewBinding.root))
        }
        binding?.contentView?.addView(viewBinding.root)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        binding = null
    }
}