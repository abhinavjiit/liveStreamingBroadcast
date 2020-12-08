package com.livestreaming.channelize.io

import android.app.Application
import com.channelize.apisdk.Channelize
import com.channelize.apisdk.ChannelizeConfig
import com.livestreaming.channelize.io.di.ApplicationComponent
import com.livestreaming.channelize.io.di.DaggerApplicationComponent
import com.livestreaming.channelize.io.di.RetrofitModule

class BaseApplication : Application(), Injector {

    companion object {
        @JvmField
        var appContext: BaseApplication? = null

        fun setInstance(application: BaseApplication) {
            appContext = application
        }

        @JvmStatic
        fun getInstance(): BaseApplication {
            return appContext as BaseApplication
        }
    }

    private lateinit var applicationComponent: ApplicationComponent

    override fun onCreate() {
        super.onCreate()
        setInstance(this)
        initChannelize()
        applicationComponent = DaggerApplicationComponent.builder().retrofitModule(
            RetrofitModule(
                LiveBroadcasterConstants.CHANNELIZE_LIVE_BROADCAST_URL,
                this,
                LiveBroadcasterConstants.CHANNELIZE_LIVE_BROADCAST_SHOPIFY_URL,
                LiveBroadcasterConstants.CHANNELIZE_CORE_BASE_URL
            )
        ).build()
    }

    override fun createAppComponent(): ApplicationComponent {
        return applicationComponent
    }

    fun initChannelize() {
        if (null != SharedPrefUtils.getPublicApiKey(this)) {
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
            Channelize.connect()
        }
    }

}