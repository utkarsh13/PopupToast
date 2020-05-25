package com.utkarshr.popup_toast

import android.content.res.Resources

class Utils {
    companion object {
        val screenWidth = Resources.getSystem().displayMetrics.widthPixels
        val screenHeight = Resources.getSystem().displayMetrics.heightPixels

        fun dpToPx(dp: Int): Int {
            return (dp * Resources.getSystem().displayMetrics.density).toInt()
        }

        inline fun <T: Any> guardLet(vararg elements: T?, closure: () -> Nothing): List<T> {
            return if (elements.all { it != null }) {
                elements.filterNotNull()
            } else {
                closure()
            }
        }

        inline fun <T: Any> ifLet(vararg elements: T?, closure: (List<T>) -> Unit) {
            if (elements.all { it != null }) {
                closure(elements.filterNotNull())
            }
        }
    }

}