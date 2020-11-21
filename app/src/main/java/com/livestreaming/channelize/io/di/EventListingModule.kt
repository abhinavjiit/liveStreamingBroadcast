package com.livestreaming.channelize.io.di

import com.livestreaming.channelize.io.`interface`.networkCallInterface.LSCApiCallInterface
import com.livestreaming.channelize.io.activity.eventListing.EventListingViewModelFact
import com.livestreaming.channelize.io.activity.eventListing.EventRepo
import com.livestreaming.channelize.io.activity.eventListing.EventsBroadCastRepoInterfaceImpl
import dagger.Module
import dagger.Provides


class EventListingModule {


    fun providesEventRepoInstance( apicall: LSCApiCallInterface): EventRepo {
        return EventsBroadCastRepoInterfaceImpl(apicall)
    }
//
//    @Provides
//    fun providesEventViewModelFact(eventsBroadCastRepoInterface: EventRepo): EventListingViewModelFact {
//        return EventListingViewModelFact(eventsBroadCastRepoInterface)*/
//    }

}