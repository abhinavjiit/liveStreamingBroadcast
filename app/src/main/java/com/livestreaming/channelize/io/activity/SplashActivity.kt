package com.livestreaming.channelize.io.activity

import android.content.Intent
import android.os.Bundle
import com.livestreaming.channelize.io.SharedPrefUtils
import com.livestreaming.channelize.io.activity.eventListing.EventBroadCastListingActivity
import com.livestreaming.channelize.io.activity.login.LSCBroadcastLoginActivity

class SplashActivity : BaseActivity() {
/////will handle notification and deep link flow later in this class

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        when (SharedPrefUtils.getLoggedInFlag(this)) {
            true -> {
                val eventBroadCastListingActivity =
                    Intent(this, EventBroadCastListingActivity::class.java)
                eventBroadCastListingActivity.flags =
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(eventBroadCastListingActivity)
                finish()
            }
            else -> {
                val loginIntent = Intent(this, LSCBroadcastLoginActivity::class.java)
                loginIntent.flags =
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(loginIntent)
                finish()
            }
        }
    }
}
