package com.livestreaming.channelize.io.activity.eventListing

import com.channelize.apisdk.Channelize
import com.livestreaming.channelize.io.`interface`.networkCallInterface.LSCApiCallInterface
import com.livestreaming.channelize.io.model.EventDetailResponse
import com.livestreaming.channelize.io.model.EventsResponse
import com.livestreaming.channelize.io.networkCallErrorAndSuccessHandler.Resource
import com.livestreaming.channelize.io.networkCallErrorAndSuccessHandler.ResponseHandler

class EventsBroadCastRepoInterfaceImpl(private val apiCall: LSCApiCallInterface) :
    EventRepo {


    override suspend fun getEventList(): Resource<EventsResponse>? {
//        return try {
//            ResponseHandler().handleSuccess(
//               // apiCall.getEvents(Channelize)
//            null
//            )
//
//        } catch (e: Exception) {
//            ResponseHandler().handleException(e)
//        }
        return null
    }


}