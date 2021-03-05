package com.app.tawktest.uc

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.app.tawktest.R


class CustomDialog(private val context: Context) {
    private var aDProgress: AlertDialog? = null

    fun showProgressDialog() {
        val builder =
            AlertDialog.Builder(context)
        val view =
            LayoutInflater.from(context).inflate(R.layout.custom_progress_bar, null)
        builder.setCancelable(false)
        builder.setView(view)
        aDProgress = builder.create()
        aDProgress!!.window!!.setBackgroundDrawable(ColorDrawable(Color.parseColor("#00000000")))
        aDProgress!!.setCanceledOnTouchOutside(false)
        aDProgress!!.setCancelable(false)
        aDProgress!!.show()
    }

    fun dismissDialog() {
        if (aDProgress != null) {
            aDProgress!!.dismiss()
        }
    }

    fun isNotNull() : Boolean{
        return aDProgress != null
    }

}