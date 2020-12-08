package com.livestreaming.channelize.io.di

import com.channelize.apisdk.network.api.ChannelizeApi
import com.livestreaming.channelize.io.activity.lscSettingUpAndLive.LSCBroadCastRepoImpl
import com.livestreaming.channelize.io.activity.lscSettingUpAndLive.ILscBroadCastRepositoryCallBack
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
        @CoreUrlRetrofit coreUrlRetrofit: Retrofit, apiClient: ChannelizeApi
    ): ILscBroadCastRepositoryCallBack {
        return LSCBroadCastRepoImpl(retrofit, lscRetrofit, coreUrlRetrofit, apiClient)
    }

    @Provides
    fun providesLiveBroadcastViewModelFact(
        ILscBroadCastRepositoryCallBack: ILscBroadCastRepositoryCallBack
    ): LSCBroadcastAndLiveViewModelFact {
        return LSCBroadcastAndLiveViewModelFact(ILscBroadCastRepositoryCallBack)
    }

}