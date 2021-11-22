package com.francis.bluechat.utils

import android.app.Activity
import android.graphics.Color
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar

class SnackBarUtil {
    private var snackBar: Snackbar? = null

    fun hideSnackBar() {
        snackBar?.dismiss()
    }

    fun showSnackBar(activity: Activity, message: String?) {
        snackBar = Snackbar.make(
            activity.findViewById(android.R.id.content),
            message.toString(),
            Snackbar.LENGTH_LONG
        )
        val sv = snackBar?.view
        val st = sv?.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
        st?.setTextColor(Color.WHITE)
        snackBar?.show()
    }

    companion object {
        private var mInstance: SnackBarUtil? = null
        val instance: SnackBarUtil?
            get() {
                if (mInstance == null) {
                    mInstance = SnackBarUtil()
                }

                return mInstance
            }
    }
}