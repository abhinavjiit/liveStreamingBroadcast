package com.livestreaming.channelize.io.activity.eventListing

import com.livestreaming.channelize.io.model.EventDetailResponse
import com.livestreaming.channelize.io.model.EventsResponse
import com.livestreaming.channelize.io.networkCallErrorAndSuccessHandler.Resource
import dagger.Provides


interface EventRepo {
    suspend fun getEventList(): Resource<EventsResponse>?
}