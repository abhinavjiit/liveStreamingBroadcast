package com.livestreaming.channelize.io.activity

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.livestreaming.channelize.io.AppIdGetService
import com.livestreaming.channelize.io.R
import com.livestreaming.channelize.io.SharedPrefUtils

abstract class BaseActivity : AppCompatActivity() {

    private lateinit var toast: Toast


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    fun showToast(context: Context, msg: String) {
        if (::toast.isInitialized) {
            toast.cancel()
        }
        if (msg.isNotBlank()) {
            toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT)
            toast.show()
        }
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }


    fun progressDialog(context: Context, text: String = "", onlyText: Boolean = false): Dialog {
        val dialog = Dialog(context)
        val inflate = LayoutInflater.from(context).inflate(R.layout.progress_dialog, null)
        val textView: TextView = inflate.findViewById(R.id.text)
        val progressBar: ProgressBar = inflate.findViewById(R.id.progressBar)

        if (onlyText) {
            textView.text = text
            textView.visibility = View.VISIBLE
            dialog.window!!.setBackgroundDrawable(
                ColorDrawable(ContextCompat.getColor(context, R.color.transparent))
            )
        } else {
            progressBar.visibility = View.VISIBLE
            dialog.window!!.setBackgroundDrawable(
                ColorDrawable(ContextCompat.getColor(context, R.color.transparent))
            )
        }
        dialog.setContentView(inflate)
        dialog.setCancelable(false)
        return dialog
    }

    fun startAppIdService() {
        if (SharedPrefUtils.getAppId(this).isNullOrBlank()) {
            val intent = Intent(this, AppIdGetService::class.java)
            AppIdGetService.enqueueWork(this, intent = intent)
        }
    }

    override fun onResume() {
        super.onResume()

    }

    fun showAlertDialogBox(msg: String, title: String) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(msg)
            .setTitle(title)
        builder.setPositiveButton(
            "DONE"
        ) { dialog, _ ->
            dialog.dismiss()
            Log.i("TAG", "Clicked")
        }

        val dialog = builder.create()
        dialog.show()
    }

}