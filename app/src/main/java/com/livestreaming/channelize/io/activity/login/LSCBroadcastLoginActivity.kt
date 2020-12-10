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
import com.channelize.apisdk.utils.ChannelizePreferences
import com.livestreaming.channelize.io.BaseApplication
import com.livestreaming.channelize.io.Injector
import com.livestreaming.channelize.io.R
import com.livestreaming.channelize.io.SharedPrefUtils
import com.livestreaming.channelize.io.activity.BaseActivity
import com.livestreaming.channelize.io.activity.eventListing.EventBroadCastListingActivity
import com.livestreaming.channelize.io.networkCallErrorAndSuccessHandler.Resource
import kotlinx.android.synthetic.main.activity_login.*
import javax.inject.Inject

const val PUBLIC_KEY = "Public-Key"

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
                        setFocusEditTextDrawables(
                            publicKeyContainer,
                            R.drawable.ic_public_key,
                            publicKeyEditTextView
                        )
                    } else {
                        setDefaultEditTextDrawables(
                            publicKeyContainer,
                            R.drawable.ic_public_key,
                            publicKeyEditTextView
                        )
                    }
                }
            storeUrlEditTextView.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    setFocusEditTextDrawables(
                        storeUrlContainer,
                        R.drawable.ic_store_url,
                        storeUrlEditTextView
                    )
                } else {
                    setDefaultEditTextDrawables(
                        storeUrlContainer,
                        R.drawable.ic_store_url,
                        storeUrlEditTextView
                    )
                }
            }
            emailEditTextView.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    setFocusEditTextDrawables(
                        emailContainer,
                        R.drawable.ic_email,
                        emailEditTextView
                    )
                } else {
                    setDefaultEditTextDrawables(
                        emailContainer,
                        R.drawable.ic_email,
                        emailEditTextView
                    )
                }
            }
            passwordEditTextView.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    setFocusEditTextDrawables(
                        passwordContainer,
                        R.drawable.ic_password,
                        passwordEditTextView
                    )
                } else {
                    setDefaultEditTextDrawables(
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
        SharedPrefUtils.setPublicApiKey(this, publicKeyEditTextView.text.toString())
        SharedPrefUtils.setStoreUrl(this, storeUrlEditTextView.text.toString())
        BaseApplication.getInstance().initChannelize()
        Channelize.getInstance().apiKey = publicKeyEditTextView.text.toString()
        Channelize.getInstance().removeHeaders(PUBLIC_KEY)
        Channelize.getInstance().addHeaders(PUBLIC_KEY, SharedPrefUtils.getPublicApiKey(this))
        SharedPrefUtils.setUniqueId(this, System.currentTimeMillis())
        loginUserViewModel.onUserLogin(
            emailEditTextView.text.toString(),
            passwordEditTextView.text.toString()
        ).observe(this,
            Observer { logInSuccess ->
                when (logInSuccess.status) {
                    Resource.Status.SUCCESS -> {
                        progressBar.dismiss()
                        logInSuccess.data?.user?.let { user ->
                            ChannelizePreferences.setCurrentUserName(
                                BaseApplication.getInstance(),
                                user.displayName
                            )
                            ChannelizePreferences.setCurrentUserProfileImage(
                                BaseApplication.getInstance(),
                                user.profileImageUrl
                            )
                            SharedPrefUtils.setLoggedInFlag(this, isLoggedIn = true)
                            Log.d("LOGIN", "success")
                            startAppIdService()
                            gotoEventListingActivity()
                        }
                    }
                    Resource.Status.LOADING -> {
                        progressBar.show()
                    }
                    Resource.Status.ERROR -> {
                        progressBar.dismiss()
                        SharedPrefUtils.setLoggedInFlag(this, isLoggedIn = false)
                        Log.d("LOGIN", "failed")
                        showToast(this, "Login Failed")
                    }
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

    private fun setFocusEditTextDrawables(
        container: RelativeLayout,
        drawableId: Int,
        text: TextView
    ) {
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

    private fun setDefaultEditTextDrawables(
        container: RelativeLayout,
        drawableId: Int,
        text: TextView
    ) {
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