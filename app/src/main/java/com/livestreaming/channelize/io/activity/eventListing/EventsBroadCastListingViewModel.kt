package com.livestreaming.channelize.io.activity.eventListing

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData

class EventsBroadCastListingViewModel(private val eventListingRepo: EventListingDataSource) :
    ViewModel() {

    fun getEventList() = liveData {
        val res = eventListingRepo.getEvents()
        emit(res)
    }

    fun onUserLogout(): MutableLiveData<Boolean> {
        return eventListingRepo.onUserLogout()
    }

}