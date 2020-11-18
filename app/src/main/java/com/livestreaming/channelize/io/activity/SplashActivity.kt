package com.livestreaming.channelize.io.activity

import android.content.Intent
import android.os.Bundle
import com.livestreaming.channelize.io.R
import com.livestreaming.channelize.io.SharedPrefUtils
import com.livestreaming.channelize.io.activity.login.LoginActivity

class SplashActivity : BaseActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        when (SharedPrefUtils.getLoggedInFlag(this)) {
            true -> {
                ///event listing activity

            }
            else -> {
                //// login screen
                val loginIntent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }


        }

    }
}
