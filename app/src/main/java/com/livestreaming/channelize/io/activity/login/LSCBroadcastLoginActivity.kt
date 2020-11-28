package com.livestreaming.channelize.io.activity.login

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.PorterDuff
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.channelize.apisdk.Channelize
import com.channelize.apisdk.ChannelizeConfig
import com.channelize.apisdk.network.api.ChannelizeOkHttpUtil
import com.livestreaming.channelize.io.BuildConfig
import com.livestreaming.channelize.io.R
import com.livestreaming.channelize.io.SharedPrefUtils
import com.livestreaming.channelize.io.activity.BaseActivity
import com.livestreaming.channelize.io.activity.eventListing.EventBroadCastListingActivity

class LSCBroadcastLoginActivity : BaseActivity(), View.OnClickListener {


    private lateinit var channelizeImageView: ImageView
    private lateinit var publicKeyEditTextView: EditText
    private lateinit var storeUrlEditTextView: EditText
    private lateinit var emailEditTextView: EditText
    private lateinit var passwordEditTextView: EditText
    private lateinit var loginButton: TextView
    private lateinit var publicKeyContainer: RelativeLayout
    private lateinit var storeUrlContainer: RelativeLayout
    private lateinit var emailContainer: RelativeLayout
    private lateinit var passwordContainer: RelativeLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        initUi()
        loginButton.setOnClickListener(this)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initUi() {
        channelizeImageView = findViewById(R.id.channelizeImageView)
        publicKeyEditTextView = findViewById(R.id.publicKeyEditTextView)
        storeUrlEditTextView = findViewById(R.id.storeUrlEditTextView)
        emailEditTextView = findViewById(R.id.emailEditTextView)
        passwordEditTextView = findViewById(R.id.passwordEditTextView)
        loginButton = findViewById(R.id.loginButton)
        publicKeyContainer = findViewById(R.id.publicKeyContainer)
        passwordContainer = findViewById(R.id.passwordContainer)
        storeUrlContainer = findViewById(R.id.storeUrlContainer)
        emailContainer = findViewById(R.id.emailContainer)

        publicKeyEditTextView.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                val tvBackground: GradientDrawable =
                    publicKeyContainer.background as GradientDrawable
                tvBackground.setColor(ContextCompat.getColor(this, R.color.white))
                tvBackground.setStroke(3, ContextCompat.getColor(this, R.color.btn_bg_blue))
                val drawable = ContextCompat.getDrawable(this, R.drawable.ic_public_key)
                drawable?.setColorFilter(
                    ContextCompat.getColor(this, R.color.btn_bg_blue),
                    PorterDuff.Mode.SRC_IN
                )

                publicKeyEditTextView.setCompoundDrawablesWithIntrinsicBounds(
                    drawable,
                    null,
                    null,
                    null
                )
                publicKeyEditTextView.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.btn_bg_blue
                    )
                )
            } else {
                val tvBackground: GradientDrawable =
                    publicKeyContainer.background as GradientDrawable
                tvBackground.setColor(ContextCompat.getColor(this, R.color.white))
                tvBackground.setStroke(1, ContextCompat.getColor(this, R.color.dark_grey))
                var drawable = ContextCompat.getDrawable(this, R.drawable.ic_public_key)
                drawable?.setColorFilter(
                    ContextCompat.getColor(this, R.color.dark_grey),
                    PorterDuff.Mode.SRC_IN
                )
                publicKeyEditTextView.setCompoundDrawablesWithIntrinsicBounds(
                    drawable,
                    null,
                    null,
                    null
                )
                publicKeyEditTextView.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.dark_grey
                    )
                )
            }
        }
        storeUrlEditTextView.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            // colorLine, colorLineFocus is vars of ColorStateList
            if (hasFocus) {
                val tvBackground: GradientDrawable =
                    storeUrlContainer.background as GradientDrawable
                tvBackground.setColor(ContextCompat.getColor(this, R.color.white))
                tvBackground.setStroke(3, ContextCompat.getColor(this, R.color.btn_bg_blue))
                val drawable = ContextCompat.getDrawable(this, R.drawable.ic_store_url)
                drawable?.setColorFilter(
                    ContextCompat.getColor(this, R.color.btn_bg_blue),
                    PorterDuff.Mode.SRC_IN
                )

                storeUrlEditTextView.setCompoundDrawablesWithIntrinsicBounds(
                    drawable,
                    null,
                    null,
                    null
                )
                storeUrlEditTextView.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.btn_bg_blue
                    )
                )
            } else {
                val tvBackground: GradientDrawable =
                    storeUrlContainer.background as GradientDrawable
                tvBackground.setColor(ContextCompat.getColor(this, R.color.white))
                tvBackground.setStroke(1, ContextCompat.getColor(this, R.color.dark_grey))
                var drawable = ContextCompat.getDrawable(this, R.drawable.ic_store_url)
                drawable?.setColorFilter(
                    ContextCompat.getColor(this, R.color.dark_grey),
                    PorterDuff.Mode.SRC_IN
                )
                storeUrlEditTextView.setCompoundDrawablesWithIntrinsicBounds(
                    drawable,
                    null,
                    null,
                    null
                )
                storeUrlEditTextView.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.dark_grey
                    )
                )
            }
        }
        emailEditTextView.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            // colorLine, colorLineFocus is vars of ColorStateList
            if (hasFocus) {
                val tvBackground: GradientDrawable =
                    emailContainer.background as GradientDrawable
                tvBackground.setColor(ContextCompat.getColor(this, R.color.white))
                tvBackground.setStroke(3, ContextCompat.getColor(this, R.color.btn_bg_blue))
                val drawable = ContextCompat.getDrawable(this, R.drawable.ic_email)
                drawable?.setColorFilter(
                    ContextCompat.getColor(this, R.color.btn_bg_blue),
                    PorterDuff.Mode.SRC_IN
                )

                emailEditTextView.setCompoundDrawablesWithIntrinsicBounds(
                    drawable,
                    null,
                    null,
                    null
                )
                emailEditTextView.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.btn_bg_blue
                    )
                )
            } else {
                val tvBackground: GradientDrawable =
                    emailContainer.background as GradientDrawable
                tvBackground.setColor(ContextCompat.getColor(this, R.color.white))
                tvBackground.setStroke(1, ContextCompat.getColor(this, R.color.dark_grey))
                var drawable = ContextCompat.getDrawable(this, R.drawable.ic_email)
                drawable?.setColorFilter(
                    ContextCompat.getColor(this, R.color.dark_grey),
                    PorterDuff.Mode.SRC_IN
                )
                emailEditTextView.setCompoundDrawablesWithIntrinsicBounds(
                    drawable,
                    null,
                    null,
                    null
                )
                emailEditTextView.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.dark_grey
                    )
                )
            }
        }
        passwordEditTextView.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            // colorLine, colorLineFocus is vars of ColorStateList
            if (hasFocus) {
                val tvBackground: GradientDrawable =
                    passwordContainer.background as GradientDrawable
                tvBackground.setColor(ContextCompat.getColor(this, R.color.white))
                tvBackground.setStroke(3, ContextCompat.getColor(this, R.color.btn_bg_blue))
                val drawable = ContextCompat.getDrawable(this, R.drawable.ic_password)
                drawable?.setColorFilter(
                    ContextCompat.getColor(this, R.color.btn_bg_blue),
                    PorterDuff.Mode.SRC_IN
                )

                passwordEditTextView.setCompoundDrawablesWithIntrinsicBounds(
                    drawable,
                    null,
                    null,
                    null
                )
                passwordEditTextView.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.btn_bg_blue
                    )
                )
            } else {
                val tvBackground: GradientDrawable =
                    passwordContainer.background as GradientDrawable
                tvBackground.setColor(ContextCompat.getColor(this, R.color.white))
                tvBackground.setStroke(1, ContextCompat.getColor(this, R.color.dark_grey))
                var drawable = ContextCompat.getDrawable(this, R.drawable.ic_password)
                drawable?.setColorFilter(
                    ContextCompat.getColor(this, R.color.dark_grey),
                    PorterDuff.Mode.SRC_IN
                )
                passwordEditTextView.setCompoundDrawablesWithIntrinsicBounds(
                    drawable,
                    null,
                    null,
                    null
                )
                passwordEditTextView.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.dark_grey
                    )
                )
            }
        }
    }

    override fun onClick(v: View?) {
        if (v?.id == R.id.loginButton) {
            if (isValid()) {
                startLogin()
            }
        }
    }


    private fun startLogin() {
        val progressBar = progressDialog(this)
        progressBar.show()
        val channelizeConfig = if (BuildConfig.DEBUG) {
            ChannelizeConfig.Builder(this).setAPIKey(publicKeyEditTextView.text.toString())
                .setLoggingEnabled(true)
                .build()
        } else {
            ChannelizeConfig.Builder(this).setAPIKey(publicKeyEditTextView.text.toString())
                .setLoggingEnabled(false)
                .build()
        }

        Channelize.initialize(channelizeConfig)
        Channelize.getInstance().apiKey = publicKeyEditTextView.text.toString()
        Channelize.getInstance().loginWithEmailPassword(
            emailEditTextView.text.toString(),
            passwordEditTextView.text.toString()
        ) { result, error ->
            progressBar.dismiss()
            if (result != null && result.user != null) {
                runOnUiThread {
                    SharedPrefUtils.setLoggedInFlag(this, isLoggedIn = true)
                    SharedPrefUtils.setPublicApiKey(this, publicKeyEditTextView.text.toString())
                    SharedPrefUtils.setStoreUrl(this, storeUrlEditTextView.text.toString())
                    Log.d("TAGGGG", "success")
                    //showToast(this, "success")
                    startAppIdService()
                    gotoEventListingActivity()
                }

            } else if (error != null) {
                runOnUiThread {
                    SharedPrefUtils.setLoggedInFlag(this, isLoggedIn = false)
                    Log.d("TAGGGG", "failed")
                    showToast(this, error.message)
                }
            } else {
                runOnUiThread {
                    showToast(this, "Login Failed")
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

    /*   override fun onTouch(view: View?, event: MotionEvent?): Boolean {
           if (view is EditText) {
               view.setOnFocusChangeListener(this)
           }
           return false
       }

       override fun onFocusChange(v: View?, hasFocus: Boolean) {

           Log.d("Tag", "v")

       }*/


}