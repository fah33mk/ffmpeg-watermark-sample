package com.example.ffmpeg_hello_world

import android.app.Activity
import android.app.ProgressDialog
import android.util.Log
import java.lang.Exception
import java.util.logging.Logger

class Loading {
    private var pDialog: ProgressDialog? = null
    private var activity: Activity? = null
    fun show(context: Activity, cancelable: Boolean, message: String?): ProgressDialog {
        if (pDialog == null) {
            pDialog = ProgressDialog(context)
            activity = context
            try {
                context.runOnUiThread {
                    pDialog!!.setCancelable(cancelable)
                    pDialog!!.setMessage(message)
                    pDialog!!.show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            Log.d("Loading","loading already shown")
        }
        return pDialog!!
    }

    fun cancel() {
        try {
            if (pDialog != null && pDialog!!.isShowing) {
                pDialog!!.dismiss()
                pDialog!!.cancel()
                pDialog = null
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun updateMessage(message: String?) {
        activity!!.runOnUiThread {
            if (pDialog != null && pDialog!!.isShowing) pDialog!!.setMessage(
                message
            )
        }
    }

    val isVisible: Boolean
        get() = pDialog != null && pDialog!!.isShowing
}