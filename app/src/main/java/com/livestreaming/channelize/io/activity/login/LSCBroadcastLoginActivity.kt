package com.livestreaming.channelize.io.activity.login

import android.content.Intent
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.channelize.apisdk.Channelize
import com.channelize.apisdk.ChannelizeConfig
import com.channelize.apisdk.network.api.ChannelizeOkHttpUtil
import com.livestreaming.channelize.io.*
import com.livestreaming.channelize.io.activity.BaseActivity
import com.livestreaming.channelize.io.activity.eventListing.EventBroadCastListingActivity
import kotlinx.android.synthetic.main.activity_login.*
import javax.inject.Inject


class LSCBroadcastLoginActivity : BaseActivity() {

    @Inject
    lateinit var loginUserViewModelFact: LoginUserViewModelFact
    private lateinit var loginUserViewModel: LoginUserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        (BaseApplication.getInstance() as Injector).createAppComponent().inject(this)
        initUi
        initViewModel
        loginButton.setOnClickListener {
            if (isValid()) {
                startLogin()
            }
        }
    }

    private val initUi: Unit
        get() {
            publicKeyEditTextView.onFocusChangeListener =
                View.OnFocusChangeListener { _, hasFocus ->
                    if (hasFocus) {
                        setFocusDrawables(
                            publicKeyContainer,
                            R.drawable.ic_public_key,
                            publicKeyEditTextView
                        )
                    } else {
                        setDefaultDrawables(
                            publicKeyContainer,
                            R.drawable.ic_public_key,
                            publicKeyEditTextView
                        )
                    }
                }
            storeUrlEditTextView.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    setFocusDrawables(
                        storeUrlContainer,
                        R.drawable.ic_store_url,
                        storeUrlEditTextView
                    )
                } else {
                    setDefaultDrawables(
                        storeUrlContainer,
                        R.drawable.ic_store_url,
                        storeUrlEditTextView
                    )
                }
            }
            emailEditTextView.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    setFocusDrawables(
                        emailContainer,
                        R.drawable.ic_email,
                        emailEditTextView
                    )
                } else {
                    setDefaultDrawables(
                        emailContainer,
                        R.drawable.ic_email,
                        emailEditTextView
                    )
                }
            }
            passwordEditTextView.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    setFocusDrawables(
                        passwordContainer,
                        R.drawable.ic_password,
                        passwordEditTextView
                    )
                } else {
                    setDefaultDrawables(
                        passwordContainer,
                        R.drawable.ic_password,
                        passwordEditTextView
                    )
                }
            }
        }

    private val initViewModel: Unit
        get() {
            loginUserViewModel =
                ViewModelProvider(this, loginUserViewModelFact).get(LoginUserViewModel::class.java)
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
        ChannelizeOkHttpUtil.getInstance(BaseApplication.getInstance()).removeHeader()
        ChannelizeOkHttpUtil.getInstance(BaseApplication.getInstance()).addHeaders()
        SharedPrefUtils.setUniqueId(this, System.currentTimeMillis())
        loginUserViewModel.onUserLogin(
            emailEditTextView.text.toString(),
            passwordEditTextView.text.toString()
        ).observe(this,
            Observer { logInSuccess ->
                progressBar.dismiss()
                if (logInSuccess != null && logInSuccess.user != null) {
                    SharedPrefUtils.setLoggedInFlag(this, isLoggedIn = true)
                    SharedPrefUtils.setPublicApiKey(this, publicKeyEditTextView.text.toString())
                    SharedPrefUtils.setStoreUrl(this, storeUrlEditTextView.text.toString())
                    Log.d("LOGIN", "success")
                    startAppIdService()
                    gotoEventListingActivity()
                } else if (logInSuccess == null) {
                    SharedPrefUtils.setLoggedInFlag(this, isLoggedIn = false)
                    Log.d("LOGIN", "failed")
                    showToast(this, "Login Failed")
                } else {
                    SharedPrefUtils.setLoggedInFlag(this, isLoggedIn = false)
                    Log.d("LOGIN", "failed")
                    showToast(this, "Login Failed")
                }
            })
    }

    private fun gotoEventListingActivity() {
        val intent = Intent(this, EventBroadCastListingActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    private fun isValid(): Boolean {
        if (publicKeyEditTextView.text.toString().isBlank()) {
            showToast(this, getString(R.string.public_key_validation_check))
            return false
        }
        if (storeUrlEditTextView.text.toString().isBlank()) {
            showToast(this, getString(R.string.store_url_key_validation_string))
            return false
        }
        if (emailEditTextView.text.toString().isBlank() || !emailEditTextView.text.toString()
                .trim().isEmailValid()
        ) {
            showToast(this, getString(R.string.email_validation_string))
            return false
        }
        if (passwordEditTextView.text.toString().isBlank()) {
            showToast(this, getString(R.string.password_validation_string))
            return false
        }
        return true
    }

    private fun setFocusDrawables(container: RelativeLayout, drawableId: Int, text: TextView) {
        val gradientDrawable: GradientDrawable =
            container.background as GradientDrawable
        gradientDrawable.mutate()
        gradientDrawable.setColor(ContextCompat.getColor(this, R.color.white))
        gradientDrawable.setStroke(3, ContextCompat.getColor(this, R.color.btn_bg_blue))
        val drawable = ContextCompat.getDrawable(this, drawableId)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            drawable?.colorFilter = BlendModeColorFilter(
                ContextCompat.getColor(
                    this,
                    R.color.btn_bg_blue
                ), BlendMode.SRC_ATOP
            )
        } else {
            drawable?.colorFilter = PorterDuffColorFilter(
                ContextCompat.getColor(
                    this,
                    R.color.btn_bg_blue
                ), PorterDuff.Mode.SRC_ATOP
            )
        }
        text.setCompoundDrawablesWithIntrinsicBounds(
            drawable,
            null,
            null,
            null
        )
        text.setTextColor(
            ContextCompat.getColor(
                this,
                R.color.btn_bg_blue
            )
        )
    }

    private fun setDefaultDrawables(container: RelativeLayout, drawableId: Int, text: TextView) {
        val defaultGradientDrawable: GradientDrawable =
            container.background as GradientDrawable
        defaultGradientDrawable.mutate()
        defaultGradientDrawable.setColor(ContextCompat.getColor(this, R.color.white))
        defaultGradientDrawable.setStroke(1, ContextCompat.getColor(this, R.color.dark_grey))
        val drawable = ContextCompat.getDrawable(this, drawableId)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            drawable?.colorFilter = BlendModeColorFilter(
                ContextCompat.getColor(
                    this,
                    R.color.dark_grey
                ), BlendMode.SRC_ATOP
            )
        } else {
            drawable?.colorFilter = PorterDuffColorFilter(
                ContextCompat.getColor(
                    this,
                    R.color.dark_grey
                ), PorterDuff.Mode.SRC_ATOP
            )
        }
        text.setCompoundDrawablesWithIntrinsicBounds(
            drawable,
            null,
            null,
            null
        )
        text.setTextColor(
            ContextCompat.getColor(
                this,
                R.color.dark_grey
            )
        )
    }

}

fun String.isEmailValid(): Boolean {
    return !TextUtils.isEmpty(this) && android.util.Patterns.EMAIL_ADDRESS.matcher(this)
        .matches()
}