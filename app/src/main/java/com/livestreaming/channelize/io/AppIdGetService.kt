package com.livestreaming.channelize.io

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.JobIntentService
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.livestreaming.channelize.io.`interface`.networkCallInterface.LSCApiCallInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import javax.inject.Inject

class AppIdGetService : JobIntentService() {
    companion object {
        fun enqueueWork(context: Context, intent: Intent) {
            enqueueWork(context, AppIdGetService::class.java, 1, intent)
        }
    }

    @Inject
    @com.livestreaming.channelize.io.di.Retrofit
    lateinit var retrofit: Retrofit


    override fun onCreate() {
        super.onCreate()
        (BaseApplication.getInstance() as Injector).createAppComponent().inject(this)
        var channelId: String = ""
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channelId = createNotificationChannel()
        }
        val builder: NotificationCompat.Builder = NotificationCompat.Builder(this, channelId)
        val notification: Notification = builder.setOngoing(true)
            .setPriority(NotificationManagerCompat.IMPORTANCE_DEFAULT)
            .setCategory(Notification.CATEGORY_SERVICE).build()
        startForeground(1, notification)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(
        channelId: String = "my_app_id_service",
        channelName: String = "My Background Service"
    ): String {
        val channel = NotificationChannel(
            channelId,
            channelName, NotificationManager.IMPORTANCE_NONE
        )
        channel.lightColor = Color.BLUE
        channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        val manager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
        return channelId
    }

    override fun onHandleWork(intent: Intent) {
        onHandleIntent()
    }


    private fun onHandleIntent() {
        try {
            GlobalScope.launch(Dispatchers.IO) {
                val response = retrofit.create(LSCApiCallInterface::class.java)
                    .getAppID(LiveBroadcasterConstants.CHANNELIZE_CORE_BASE_URL + "/modules/")
                response.forEach {
                    if (it.identifier == "live-broadcast") {
                        it.settings.forEach {
                            if (it.key == "appId") {
                                launch(Dispatchers.Main) {
                                    SharedPrefUtils.setAppID(
                                        BaseApplication.getInstance(),
                                        it.value
                                    )
                                }
                            }
                        }

                    }
                }

            }
        } catch (e: Exception) {
            Log.d("Exception", e.message.toString())
        }


    }
}