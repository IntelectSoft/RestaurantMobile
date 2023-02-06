package md.edi.mobilewaiter.presentation.views

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import md.edi.mobilewaiter.databinding.ToolbarViewBinding

class ToolbarView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0,
) : BaseNavigationToolbar<ToolbarViewBinding>(context, attrs, defStyleAttr) {


    private val viewBinding =
        ToolbarViewBinding.inflate(
            LayoutInflater.from(context), this,
            false
        )

    override fun getViewBinding(): ToolbarViewBinding = viewBinding

    init {
        viewBinding.back.setOnClickListener {
            leftClickListener?.invoke()
        }

        viewBinding.backText.setOnClickListener {
            leftClickListener?.invoke()
        }
        viewBinding.next.setOnClickListener {
            rightClickListener?.invoke()
        }
        viewBinding.closeIconCobineBill.setOnClickListener {
            closeCombineMenuClickListener?.invoke()
        }

        viewBinding.next.isSelected = true
    }

    private var leftClickListener: (() -> Unit)? = null
    private var rightClickListener: (() -> Unit)? = null
    private var closeCombineMenuClickListener: (() -> Unit)? = null

    fun setSelectedRightIcon(isSelected: Boolean) {
        viewBinding.rightIcon.isSelected = isSelected
    }

    fun setLeftClickListener(onClickListener: () -> Unit) {
        this.leftClickListener = onClickListener
    }

    fun setRightClickListener(onClickListener: () -> Unit) {
        this.rightClickListener = onClickListener
    }

    fun setCloseCombineMenuClickListener(onClickListener: () -> Unit) {
        this.closeCombineMenuClickListener = onClickListener
    }

    fun setShowCombineMenu(show: Boolean){
        if(show){
            viewBinding.layoutCombineBills.isVisible = true
        }
        else{
            viewBinding.layoutCombineBills.isVisible = false
            closeCombineMenuClickListener = null
        }
    }

    fun setCombineMenuTitle(titleCombineMenu: String){
        viewBinding.titleCombineBill.text = titleCombineMenu
    }

    fun showSearchText(isShow: Boolean){
        if(isShow){
            viewBinding.searchText.isVisible = true
            viewBinding.bodyContainer.isVisible = false
            viewBinding.searchText.findViewById<View>(androidx.appcompat.R.id.search_button).performClick()
        }
        else{
            viewBinding.bodyContainer.isVisible = true
            viewBinding.searchText.isVisible = false
        }
    }

    fun getSearchView() = viewBinding.searchText


    fun setBackColor(colorResId: Int) {
        viewBinding.back.setColorFilter(colorResId)
    }

    fun setShowBottomLine(show: Boolean) {
        viewBinding.line.visibility = if (show) View.VISIBLE else View.GONE
    }

    fun setBackgroundLine(@ColorRes color: Int) {
        viewBinding.line.background = ContextCompat.getDrawable(context, color)
    }

    fun setRightIcon(idRes: Int, clickListener: (() -> Unit)) {
        viewBinding.rightIcon.setImageResource(idRes)
        viewBinding.rightIcon.visibility = View.VISIBLE
        viewBinding.rightIcon.setOnClickListener {
            clickListener.invoke()
        }
    }

    fun setSecondRightIcon(idRes: Int, clickListener: (() -> Unit)) {
        viewBinding.secondRightIcon.setImageResource(idRes)
        viewBinding.secondRightIcon.visibility = View.VISIBLE
        viewBinding.secondRightIcon.setOnClickListener {
            clickListener.invoke()
        }
    }

    fun setLeftIcon(idRes: Int, clickListener: (() -> Unit)) {
        viewBinding.back.setImageResource(idRes)
        viewBinding.back.visibility = View.VISIBLE
        viewBinding.back.setOnClickListener {
            clickListener.invoke()
        }
    }

    fun setLeftText(idRes: Int, clickListener: (() -> Unit)) {
        viewBinding.backText.text = resources.getString(idRes)
        viewBinding.backText.visibility = View.VISIBLE
        viewBinding.backText.setOnClickListener {
            clickListener.invoke()
        }
    }

    fun setLeftIconButtonVisibility(visible: Boolean) {
        viewBinding.back.visibility = if (visible) View.VISIBLE else View.GONE
    }

    fun setRightTextVisibility(visible: Boolean) {
        viewBinding.next.visibility = if (visible) View.VISIBLE else View.GONE
    }

    fun setRightTextButton(idRes: Int) {
        viewBinding.next.text = context.getText(idRes)
        viewBinding.next.visibility = View.VISIBLE
    }

    fun setRightTextButtonColor(idRes: Int) {
        viewBinding.next.setTextColor(idRes)
        viewBinding.next.visibility = View.VISIBLE
    }

    fun setLeftTextButtonColor(idRes: Int) {
        viewBinding.backText.setTextColor(idRes)
        viewBinding.backText.visibility = View.VISIBLE
    }

    fun showLeftBtn(isShow: Boolean){
        viewBinding.back.visibility = if(isShow) View.VISIBLE else View.INVISIBLE
    }

    fun setSelectedRightText(isSelected: Boolean) {
        viewBinding.next.isSelected = isSelected
        viewBinding.next.isEnabled = isSelected
    }

    fun setTitle(text: String) {
        viewBinding.title.text = text
    }

    fun setSubTitle(text: String) {
        viewBinding.subTitle.visibility = View.VISIBLE
        viewBinding.subTitle.text = text
    }

    fun setTitle(idRes: Int) {
        viewBinding.title.text = context.getString(idRes)
    }

    fun setTitleColor(colorResId: Int) {
        viewBinding.title.setTextColor(colorResId)
    }

    fun setRightTextColor(colorResId: Int) {
        viewBinding.next.setTextColor(colorResId)
    }

    fun showBottomLine(isShow: Boolean) {
        viewBinding.line.visibility = when {
            isShow -> View.VISIBLE
            else -> View.GONE
        }
    }

    fun showLeftIcon(isShow: Boolean) {
        viewBinding.back.visibility = when {
            isShow -> View.VISIBLE
            else -> View.GONE
        }
    }

    fun customizeRightTextButton(@DimenRes textSize : Int, @DrawableRes drawableRight : Int){
        viewBinding.next.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.resources.getDimensionPixelSize(textSize).toFloat())
        viewBinding.next.setCompoundDrawablesWithIntrinsicBounds(0,0,drawableRight,0)
    }

    fun setCartCount(count: Double) {
        if (count > 0) {
            viewBinding.notificationsBadge.visibility = View.VISIBLE

            viewBinding.notificationsBadge.text = try {
                count.toInt().toString()
            }catch (e: Exception){
                count.toString()
            }
        } else {
            viewBinding.notificationsBadge.visibility = View.GONE
            viewBinding.notificationsBadge.text = ""
        }
    }
}
