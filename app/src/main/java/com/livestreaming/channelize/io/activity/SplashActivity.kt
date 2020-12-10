
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
        val intent = if (SharedPrefUtils.getLoggedInFlag(this)) {
            Intent(this, EventBroadCastListingActivity::class.java)
        } else {
            Intent(this, LSCBroadcastLoginActivity::class.java)
        }
        intent.flags =
            Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
        finish()
    }

}


