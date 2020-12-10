
package com.livestreaming.channelize.io.di

import com.livestreaming.channelize.io.AppIdGetService
import com.livestreaming.channelize.io.activity.eventListing.EventBroadCastListingActivity
import com.livestreaming.channelize.io.activity.login.LSCBroadcastLoginActivity
import com.livestreaming.channelize.io.activity.lscSettingUpAndLive.LSCBroadCastSettingUpAndLiveActivity
import com.livestreaming.channelize.io.fragment.LSCBroadCastDetailAfterFinishedFragment
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [RetrofitModule::class, EventListingAndLiveBroadCastModule::class, LoginUserModule::class])
interface ApplicationComponent {
    fun inject(eventBroadCastListingActivity: EventBroadCastListingActivity)
    fun inject(liveActivity: LSCBroadCastSettingUpAndLiveActivity)
    fun inject(appIdService: AppIdGetService)
    fun inject(lscBroadCastDetailAfterEndingFragment: LSCBroadCastDetailAfterFinishedFragment)
    fun inject(lscLoginActivity:LSCBroadcastLoginActivity)
}

