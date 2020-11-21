package com.livestreaming.channelize.io.activity.eventListing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData

class EventsBroadCastListingViewModel(private val eventListingRepo: EventListingRepo) :
    ViewModel() {


    fun getEventList() = liveData {
        val res = eventListingRepo.getEvents()
        emit(res)
    }


}