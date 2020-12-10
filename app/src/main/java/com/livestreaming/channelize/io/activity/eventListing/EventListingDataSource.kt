
package com.livestreaming.channelize.io.activity.eventListing

import androidx.lifecycle.MutableLiveData
import com.channelize.apisdk.Channelize
import com.channelize.apisdk.utils.ChannelizePreferences
import com.livestreaming.channelize.io.BaseApplication
import com.livestreaming.channelize.io.`interface`.networkCallInterface.ILscApiCallBack
import com.livestreaming.channelize.io.model.EventDetailResponse
import com.livestreaming.channelize.io.networkCallErrorAndSuccessHandler.Resource
import com.livestreaming.channelize.io.networkCallErrorAndSuccessHandler.ResponseHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import retrofit2.Retrofit
import javax.inject.Inject

class EventListingDataSource @Inject constructor(@com.livestreaming.channelize.io.di.Retrofit private val retrofit: Retrofit) {
    private var isUserLoggedOut = MutableLiveData<Boolean>()

    suspend fun getEvents(): Resource<List<EventDetailResponse>> {
        return try {
            coroutineScope {
                val res = async(Dispatchers.IO) {
                    retrofit.create(ILscApiCallBack::class.java).getEvents(
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

    fun onUserLogout(): MutableLiveData<Boolean> {
        Channelize.logout { result, _ ->
            if (result.isSuccessful && result != null) {
                isUserLoggedOut.postValue(true)
            } else {
                isUserLoggedOut.postValue(null)
            }
        }
        return isUserLoggedOut
    }

}