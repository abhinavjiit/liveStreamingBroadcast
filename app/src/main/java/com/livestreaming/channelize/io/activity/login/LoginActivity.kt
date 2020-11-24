package com.livestreaming.channelize.io.activity.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.channelize.apisdk.Channelize
import com.channelize.apisdk.ChannelizeConfig
import com.livestreaming.channelize.io.BuildConfig
import com.livestreaming.channelize.io.R
import com.livestreaming.channelize.io.SharedPrefUtils
import com.livestreaming.channelize.io.activity.BaseActivity
import com.livestreaming.channelize.io.activity.eventListing.EventBroadCastListingActivity

class LoginActivity : BaseActivity(), View.OnClickListener {


    private lateinit var channelizeImageView: ImageView
    private lateinit var publicKeyEditTextView: EditText
    private lateinit var storeUrlEditTextView: EditText
    private lateinit var emailEditTextView: EditText
    private lateinit var passwordEditTextView: EditText
    private lateinit var loginButton: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        initUi()
        loginButton.setOnClickListener(this)
    }

    private fun initUi() {
        channelizeImageView = findViewById(R.id.channelizeImageView)
        publicKeyEditTextView = findViewById(R.id.publicKeyEditTextView)
        storeUrlEditTextView = findViewById(R.id.storeUrlEditTextView)
        emailEditTextView = findViewById(R.id.emailEditTextView)
        passwordEditTextView = findViewById(R.id.passwordEditTextView)
        loginButton = findViewById(R.id.loginButton)
    }

    override fun onClick(v: View?) {
        if (v?.id == R.id.loginButton) {
            if (isValid()) {
                SharedPrefUtils.setPublicApiKey(this, publicKeyEditTextView.text.toString())
                SharedPrefUtils.setStoreUrl(this, storeUrlEditTextView.text.toString())
                startLogin()
            }
        }
    }


    private fun startLogin() {
        val progressBar = progressDialog(this)
        progressBar.show()
        val channelizeConfig = if (BuildConfig.DEBUG) {
            ChannelizeConfig.Builder(this).setAPIKey(SharedPrefUtils.getPublicApiKey(this))
                .setLoggingEnabled(true)
                .build()
        } else {
            ChannelizeConfig.Builder(this).setAPIKey(SharedPrefUtils.getPublicApiKey(this))
                .setLoggingEnabled(false)
                .build()
        }

        Channelize.initialize(channelizeConfig)
        Channelize.getInstance().loginWithEmailPassword(
            emailEditTextView.text.toString(),
            passwordEditTextView.text.toString()
        ) { result, error ->
            progressBar.dismiss()
            if (result != null && result.user != null) {
                runOnUiThread {
                    SharedPrefUtils.setLoggedInFlag(this, isLoggedIn = true)
                    Log.d("TAGGGG", "success")
                    showToast(this, "success")
                    gotoEventListingActivity()
                }

            } else if (error != null) {
                runOnUiThread {
                    SharedPrefUtils.setLoggedInFlag(this, isLoggedIn = false)
                    Log.d("TAGGGG", "failed")
                    showToast(this, error.message)
                }
            }
        }
    }


    private fun gotoEventListingActivity() {
        val intent = Intent(this, EventBroadCastListingActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    private fun isValid(): Boolean {
        if (publicKeyEditTextView.text.toString().isBlank()) {
            showToast(this, "public key is not valid")
            return false
        }
        if (storeUrlEditTextView.text.toString().isBlank()) {
            showToast(this, "store url is not valid")
            return false
        }
        if (emailEditTextView.text.toString().isBlank()) {
            showToast(this, "email is not valid")

            return false
        }
        if (passwordEditTextView.text.toString().isBlank()) {
            showToast(this, "password is not valid")
            return false
        }
        return true
    }


}