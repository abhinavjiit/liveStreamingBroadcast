package com.livestreaming.channelize.io.di

import com.livestreaming.channelize.io.BaseApplication
import com.livestreaming.channelize.io.activity.eventListing.EventBroadCastListingActivity
import dagger.Component
import dagger.Provides
import javax.inject.Singleton

@Singleton
@Component(modules = [RetrofitModule::class])
interface ApplicationComponent {
    fun inject(eventBroadCastListingActivity: EventBroadCastListingActivity)

}

