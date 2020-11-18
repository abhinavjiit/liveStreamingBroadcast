package com.livestreaming.channelize.io.activity

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.channelize.apisdk.Channelize
import com.channelize.apisdk.ChannelizeConfig
import com.livestreaming.channelize.io.R

 abstract class BaseActivity : AppCompatActivity() {

    private lateinit var toast: Toast

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val channelizeConfig =
            ChannelizeConfig.Builder(this).setAPIKey("wZNxsLnDlgdBhFy6").setLoggingEnabled(false)
                .build()
        Channelize.initialize(channelizeConfig)

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

    fun progressDialog(context: Context): Dialog {
        val dialog = Dialog(context)
        val inflate = LayoutInflater.from(context).inflate(R.layout.progress_dialog, null)
        dialog.setContentView(inflate)
        dialog.setCancelable(false)
        dialog.window!!.setBackgroundDrawable(
            ColorDrawable(Color.TRANSPARENT)
        )
        return dialog
    }
}