package com.livestreaming.channelize.io

import android.app.Application
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
        applicationComponent = DaggerApplicationComponent.builder().retrofitModule(
            RetrofitModule(
                LiveBroadcasterConstants.CHANNELIZE_LIVE_BROADCAST_URL,
                this, "https://shopify.channelize.io/"
            )
        ).build()

    }


    override fun createAppComponent(): ApplicationComponent {
        return applicationComponent
    }


}