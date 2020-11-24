package com.livestreaming.channelize.io.di

import com.livestreaming.channelize.io.`interface`.networkCallInterface.LSCApiCallInterface
import com.livestreaming.channelize.io.activity.eventListing.EventRepo
import com.livestreaming.channelize.io.activity.eventListing.EventsBroadCastRepoInterfaceImpl
import com.livestreaming.channelize.io.activity.lscSettingUp.LSCBroadCastRepoImpl
import com.livestreaming.channelize.io.activity.lscSettingUp.LSCBroadCastRepoInterface
import com.livestreaming.channelize.io.activity.lscSettingUp.LSCBroadcastAndLiveViewModelFact
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

@Module
class EventListingAndLiveBroadCastModule {


    fun providesEventRepoInstance(apiCall: LSCApiCallInterface): EventRepo {
        return EventsBroadCastRepoInterfaceImpl(apiCall)
    }


    @Provides
    fun providesEventLiveBroadcastRepoInstance(@ProductsListRetrofit retrofit: Retrofit): LSCBroadCastRepoInterface {
        return LSCBroadCastRepoImpl(retrofit)

    }


    @Provides
    fun providesLiveBroadcastViewModelFact(lscBroadCastRepoInterface: LSCBroadCastRepoInterface): LSCBroadcastAndLiveViewModelFact {
        return LSCBroadcastAndLiveViewModelFact(lscBroadCastRepoInterface)
    }

}