package md.edi.mobilewaiter.utils

import java.text.DecimalFormat

object HelperFormatter {

    fun formatDouble(number: Double, withMdl: Boolean): String {
        return if (number == 0.0) {
            if (withMdl) "0 MDL" else "0"
        } else {
            if (withMdl) DecimalFormat("0.0#").format(number) + " MDL"
            else DecimalFormat("0.0#").format(number)
        }
    }
}