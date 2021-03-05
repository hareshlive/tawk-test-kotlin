package com.app.tawktest.utils

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.graphics.Rect
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.app.tawktest.R
import com.app.tawktest.interfaceClass.SnackRetryInterface
import com.app.tawktest.uc.CustomDialog
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.Snackbar.SnackbarLayout
import java.util.regex.Pattern

class Utils(val context: Context) {

    lateinit var customDialog: CustomDialog

    fun isNetworkAvailable(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE)
        return if (connectivityManager is ConnectivityManager) {
            val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
            networkInfo?.isConnected ?: false
        } else false
    }

    fun hideKeyBoardFromView() {
        val activity = context as AppCompatActivity
        val view = activity.currentFocus
        if (view != null) {
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    fun isValidEmail(email: String?): Boolean {
        return email != null && Pattern.compile(emailPattern).matcher(email).matches()
    }

    fun showAlert(msg: String?) {
        val builder = AlertDialog.Builder(context)
        builder.setCancelable(false)
        builder.setTitle("Alert")
        builder.setMessage(msg)
        builder.setPositiveButton("Ok") { dialogInterface: DialogInterface, i: Int ->
            dialogInterface.dismiss()
        }
        builder.show()
    }

    fun showCustomToast(msg: String?) {
        val view =
            LayoutInflater.from(context).inflate(R.layout.custom_toast, null)
        val tvProgress = view.findViewById<TextView>(R.id.custom_toast_text)
        tvProgress.text = msg

        val myToast = Toast(context)
        myToast.duration = Toast.LENGTH_SHORT
        myToast.setGravity(Gravity.BOTTOM or Gravity.FILL_HORIZONTAL, 0, 0)
        myToast.view = view//setting the view of custom toast layout
        myToast.show()
    }

    fun showToast(msg: String) {
        val toast = Toast(context)
        val view = LayoutInflater.from(context).inflate(R.layout.custom_toast, null)
        val textView: TextView = view.findViewById(R.id.custom_toast_text)
        textView.text = msg
        toast.view = view
        toast.setGravity(Gravity.BOTTOM or Gravity.CENTER, 0, 70)
        toast.duration = Toast.LENGTH_SHORT
        toast.show()
    }


    lateinit var snackRetryInterface: SnackRetryInterface

    fun setRetryClickListener(SnackRetryInterface: SnackRetryInterface) {
        snackRetryInterface = SnackRetryInterface
    }

    fun showSnackBar(view: View?, context: Context, data: String?, type: String?, duration: Int) {
        try {
            val snackbar = Snackbar.make(view!!, "", duration)
            val layout = snackbar.view as SnackbarLayout
            val snackView = LayoutInflater.from(context).inflate(R.layout.snack_bar_layout, null)
            val snackbar_text = snackView.findViewById<TextView>(R.id.snackbar_text)
            val linearSnackBar = snackView.findViewById<LinearLayout>(R.id.linearSnackBar)
            val snackImg = snackView.findViewById<ImageView>(R.id.snackImg)
            val snackbar_btn = snackView.findViewById<TextView>(R.id.snackbar_btn)
            snackbar_text.text = data
            if (type == Const.noInternet) {
                snackbar_btn.setText(R.string.setting)
                snackbar_btn.visibility = View.VISIBLE
                linearSnackBar.setBackgroundColor(context.resources.getColor(R.color.snackRed))
                snackbar_text.setTextColor(context.resources.getColor(R.color.white))
                snackImg.background = context.resources.getDrawable(R.drawable.internet_connection)
            } else if (type == Const.success) {
                snackbar_btn.visibility = View.GONE;
                snackbar_text.setTextColor(context.resources.getColor(R.color.white));
                linearSnackBar.setBackgroundColor(context.resources.getColor(R.color.snackGreen));
                snackImg.background = context.resources.getDrawable(R.drawable.success);
            }
            layout.setBackgroundColor(context.resources.getColor(R.color.transparent))
            layout.setPadding(0, 0, 0, 0)
            layout.addView(snackView, 0)
            snackbar.show()
            try {
                snackbar_btn.setOnClickListener {
                    snackRetryInterface.onReTryClick(snackbar_btn, snackbar_btn)
                }
            } catch (e: Exception) {
                Log.e("Exception", "showSnackBar: $e")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("Exception123", "showSnackBar: $e")
        }
    }

    fun getScreenSizeDialog(context: Activity, resourceId: Int): View? {
        val displayRectangle = Rect()
        val window = context.window
        window.decorView.getWindowVisibleDisplayFrame(displayRectangle)
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val layout = inflater.inflate(resourceId, null)
        layout.minimumWidth = (displayRectangle.width() * 1.0f).toInt()
        layout.minimumHeight = (displayRectangle.height() * 1.0f).toInt()
        return layout
    }

    companion object {
        private const val emailPattern = ("^[_A-Za-z0-9-+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$")
    }

}