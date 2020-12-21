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
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
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
const val DRAWABLE_FOCUS_STROKE = 3
const val DRAWABLE_DEFAULT_STROKE = 1

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
        tvLogin.setOnClickListener {
            if (isValid()) {
                startLogin()
            }
        }
    }

    private val initUi: Unit
        get() {
            etPublicKey.onFocusChangeListener =
                View.OnFocusChangeListener { _, hasFocus ->
                    if (hasFocus) {
                        setFocusEditTextDrawables(cslPublicKeyContainer, R.drawable.ic_public_key, etPublicKey)
                    } else {
                        setDefaultEditTextDrawables(cslPublicKeyContainer, R.drawable.ic_public_key, etPublicKey)
                    }
                }
            etStoreUrl.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    setFocusEditTextDrawables(cslStoreUrlContainer, R.drawable.ic_store_url, etStoreUrl)
                } else {
                    setDefaultEditTextDrawables(cslStoreUrlContainer, R.drawable.ic_store_url, etStoreUrl)
                }
            }
            etEmail.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    setFocusEditTextDrawables(cslEmailContainer, R.drawable.ic_email, etEmail)
                } else {
                    setDefaultEditTextDrawables(cslEmailContainer, R.drawable.ic_email, etEmail)
                }
            }
            etPassword.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    setFocusEditTextDrawables(cslPasswordContainer, R.drawable.ic_password, etPassword)
                } else {
                    setDefaultEditTextDrawables(cslPasswordContainer, R.drawable.ic_password, etPassword)
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
        SharedPrefUtils.setPublicApiKey(this, etPublicKey.text.toString())
        SharedPrefUtils.setStoreUrl(this, etStoreUrl.text.toString())
        BaseApplication.getInstance().initChannelize()
        Channelize.getInstance().apiKey = etPublicKey.text.toString()
        Channelize.getInstance().removeHeaders(PUBLIC_KEY)
        Channelize.getInstance().addHeaders(PUBLIC_KEY, SharedPrefUtils.getPublicApiKey(this))
        SharedPrefUtils.setUniqueId(this, System.currentTimeMillis())
        loginUserViewModel.onUserLogin(etEmail.text.toString(), etPassword.text.toString())?.observe(this,
            Observer { logInSuccess ->
                when (logInSuccess.status) {
                    Resource.Status.SUCCESS -> {
                        progressBar.dismiss()
                        logInSuccess.data?.user?.let { user ->
                            ChannelizePreferences.setCurrentUserName(BaseApplication.getInstance(), user.displayName)
                            ChannelizePreferences.setCurrentUserProfileImage(
                                BaseApplication.getInstance(),
                                user.profileImageUrl
                            )
                            SharedPrefUtils.isUserLoggedIn(this, isLoggedIn = true)
                            Log.d("LOGIN", "success")
                            startAppIdService()
                            gotoEventListingActivity()
                        }
                    }
                    Resource.Status.ERROR -> {
                        progressBar.dismiss()
                        SharedPrefUtils.isUserLoggedIn(this, isLoggedIn = false)
                        Log.d("LOGIN", "failed")
                        showToast(this, getString(R.string.login_failed))
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
        if (etPublicKey.text.toString().isBlank()) {
            showToast(this, getString(R.string.public_key_validation_check))
            return false
        }
        if (etStoreUrl.text.toString().isBlank()) {
            showToast(this, getString(R.string.store_url_key_validation_string))
            return false
        }
        if (etEmail.text.toString().isBlank() || !etEmail.text.toString().trim().isEmailValid()) {
            showToast(this, getString(R.string.email_validation_string))
            return false
        }
        if (etPassword.text.toString().isBlank()) {
            showToast(this, getString(R.string.password_validation_string))
            return false
        }
        return true
    }

    private fun setFocusEditTextDrawables(container: ConstraintLayout, drawableId: Int, text: TextView) {
        val gradientDrawable = container.background as GradientDrawable
        gradientDrawable.mutate()
        gradientDrawable.setColor(ContextCompat.getColor(this, R.color.white))
        gradientDrawable.setStroke(DRAWABLE_FOCUS_STROKE, ContextCompat.getColor(this, R.color.btn_bg_blue))
        val drawable = ContextCompat.getDrawable(this, drawableId)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            drawable?.colorFilter = BlendModeColorFilter(
                ContextCompat.getColor(this, R.color.btn_bg_blue), BlendMode.SRC_ATOP
            )
        } else {
            drawable?.colorFilter = PorterDuffColorFilter(
                ContextCompat.getColor(this, R.color.btn_bg_blue), PorterDuff.Mode.SRC_ATOP
            )
        }
        text.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
        text.setTextColor(ContextCompat.getColor(this, R.color.btn_bg_blue))
    }

    private fun setDefaultEditTextDrawables(container: ConstraintLayout, drawableId: Int, text: TextView) {
        val defaultGradientDrawable = container.background as GradientDrawable
        defaultGradientDrawable.mutate()
        defaultGradientDrawable.setColor(ContextCompat.getColor(this, R.color.white))
        defaultGradientDrawable.setStroke(DRAWABLE_DEFAULT_STROKE, ContextCompat.getColor(this, R.color.dark_grey))
        val drawable = ContextCompat.getDrawable(this, drawableId)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            drawable?.colorFilter =
                BlendModeColorFilter(ContextCompat.getColor(this, R.color.dark_grey), BlendMode.SRC_ATOP)
        } else {
            drawable?.colorFilter = PorterDuffColorFilter(
                ContextCompat.getColor(this, R.color.dark_grey), PorterDuff.Mode.SRC_ATOP
            )
        }
        text.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
        text.setTextColor(
            ContextCompat.getColor(this, R.color.dark_grey)
        )
    }

}

fun String.isEmailValid(): Boolean {
    return !TextUtils.isEmpty(this) && android.util.Patterns.EMAIL_ADDRESS.matcher(this)
        .matches()
}
