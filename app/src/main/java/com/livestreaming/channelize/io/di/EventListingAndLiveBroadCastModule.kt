package com.livestreaming.channelize.io.di

import com.livestreaming.channelize.io.`interface`.networkCallInterface.LSCApiCallInterface
import com.livestreaming.channelize.io.activity.eventListing.EventRepo
import com.livestreaming.channelize.io.activity.eventListing.EventsBroadCastRepoInterfaceImpl
import com.livestreaming.channelize.io.activity.lscSettingUpAndLive.LSCBroadCastRepoImpl
import com.livestreaming.channelize.io.activity.lscSettingUpAndLive.LSCBroadCastRepoInterface
import com.livestreaming.channelize.io.activity.lscSettingUpAndLive.LSCBroadcastAndLiveViewModelFact
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

@Module
class EventListingAndLiveBroadCastModule {

    @Provides
    fun providesEventLiveBroadcastRepoInstance(
        @ProductsListRetrofit retrofit: Retrofit,
        @com.livestreaming.channelize.io.di.Retrofit lscRetrofit: Retrofit,
        @CoreUrlRetrofit coreUrlRetrofit: Retrofit
    ): LSCBroadCastRepoInterface {
        return LSCBroadCastRepoImpl(retrofit, lscRetrofit, coreUrlRetrofit)
    }

    @Provides
    fun providesLiveBroadcastViewModelFact(
        lscBroadCastRepoInterface: LSCBroadCastRepoInterface): LSCBroadcastAndLiveViewModelFact {
        return LSCBroadcastAndLiveViewModelFact(lscBroadCastRepoInterface)
    }

}