package com.uziarnami.searchmaterialview.utils

import android.content.Context
import android.support.design.widget.Snackbar
import android.view.View

/**
 * Created by Fauzi Arnami on 11/7/16.
 */

class Util {
    fun dpToPx(context: Context, dp: Float): Int {
        val scale = context.resources.displayMetrics.density
        return Math.round(dp * scale)
    }

    fun showSnackBarMessage(view: View, message: String) {
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).setAction("Action", null).show()
    }
}