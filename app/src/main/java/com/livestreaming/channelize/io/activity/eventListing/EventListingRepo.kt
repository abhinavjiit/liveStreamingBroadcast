package com.livestreaming.channelize.io.activity.eventListing

import com.channelize.apisdk.utils.ChannelizePreferences
import com.livestreaming.channelize.io.BaseApplication
import com.livestreaming.channelize.io.`interface`.networkCallInterface.LSCApiCallInterface
import com.livestreaming.channelize.io.model.EventDetailResponse
import com.livestreaming.channelize.io.networkCallErrorAndSuccessHandler.Resource
import com.livestreaming.channelize.io.networkCallErrorAndSuccessHandler.ResponseHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import retrofit2.Retrofit
import javax.inject.Inject

class EventListingRepo @Inject constructor(@com.livestreaming.channelize.io.di.Retrofit private val retrofit: Retrofit) {

    suspend fun getEvents(): Resource<List<EventDetailResponse>> {
        return try {
            coroutineScope {
                val res = async(Dispatchers.IO) {
                    retrofit.create(LSCApiCallInterface::class.java).getEvents(
                        hosts = ChannelizePreferences.getCurrentUserId(BaseApplication.getInstance()),
                        skip = 0,
                        limit = 25,
                        sort = "startTime ASC"
                    )
                }
                ResponseHandler().handleSuccess(res.await())
            }


        } catch (e: Exception) {
            ResponseHandler().handleException(e)
        }

    }
}