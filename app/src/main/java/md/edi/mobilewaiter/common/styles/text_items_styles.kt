package md.edi.mobilewaiter.common.styles

import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.StringRes
import androidx.core.content.res.ResourcesCompat
import java.util.*

sealed class SpanText(
    @ColorInt open val foregroundColor: Int,
    open val isUnderline: Boolean = true
) {
    data class Click(
        val text: Text.Resource,
        val span: Text,
        @ColorInt override val foregroundColor: Int,
        val font: Int? = null,
        override val isUnderline: Boolean
    ) : SpanText(foregroundColor, isUnderline)
}


sealed class Text {
    data class Simple(val value: String) : Text()
    data class Resource(@StringRes val resource: Int, val arguments: List<Text> = mutableListOf()) :
        Text()
}

fun SpanText.getSpannableText(context: Context, action: () -> Unit): Spannable {
    return when (this) {
        is SpanText.Click -> {
            val spannedText = this.span.getStringText(context)
            val fullTextString =
                context.getStringWithArgs(this.text.resource, mutableListOf(this.span))
            val spannableText = SpannableString(fullTextString)

            val clickableSpan = object : ClickableSpan() {
                override fun onClick(widget: View) {
                    action.invoke()
                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    font?.let {
                        val typeface = ResourcesCompat.getFont(context, it)
                        ds.typeface = typeface
                    }
                    ds.color = foregroundColor
                    ds.isUnderlineText = isUnderline
                }
            }
            val startIndex = fullTextString.toLowerCase(Locale.getDefault())
                .indexOf(spannedText.toLowerCase(Locale.getDefault()))


            try {
                spannableText.setSpan(
                    clickableSpan,
                    startIndex,
                    startIndex + spannedText.length,
                    Spanned.SPAN_INCLUSIVE_INCLUSIVE
                )
            } catch (e: java.lang.Exception) {
            }

            spannableText
        }
    }
}

fun Text.getStringText(context: Context): String {
    return when (this) {
        is Text.Resource -> {
            if (arguments.isNotEmpty()) {
                context.getStringWithArgs(this.resource, arguments)
            } else {
                context.getString(this.resource)
            }
        }
        is Text.Simple -> this.value
    }
}


fun Context.getStringWithArgs(@StringRes resourceId: Int, arguments: List<Text>): String {
    return if (!arguments.isNullOrEmpty()) {
        val mappedArguments = arguments.map {
            when (it) {
                is Text.Simple -> it.value
                is Text.Resource -> this.getString(it.resource)
            }
        }
        String.format(this.getString(resourceId), *mappedArguments.toTypedArray())
    } else {
        this.getString(resourceId)
    }
}