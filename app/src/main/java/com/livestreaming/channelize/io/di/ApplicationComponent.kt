package com.livestreaming.channelize.io.di

import com.livestreaming.channelize.io.AppIdGetService
import com.livestreaming.channelize.io.activity.eventListing.EventBroadCastListingActivity
import com.livestreaming.channelize.io.activity.lscSettingUp.LSCBroadCastSettingUpAndLiveActivity
import com.livestreaming.channelize.io.fragment.LSCProductsListDialogFragment
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [RetrofitModule::class, EventListingAndLiveBroadCastModule::class])
interface ApplicationComponent {
    fun inject(eventBroadCastListingActivity: EventBroadCastListingActivity)
    fun inject(liveActivity: LSCBroadCastSettingUpAndLiveActivity)
    fun inject(appIdService:AppIdGetService)

}

