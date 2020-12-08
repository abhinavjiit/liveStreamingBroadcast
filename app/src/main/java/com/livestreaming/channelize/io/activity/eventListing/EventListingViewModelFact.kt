package com.livestreaming.channelize.io.activity.eventListing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject

@Suppress("UNCHECKED_CAST")
class EventListingViewModelFact @Inject constructor(
   private val eventListingRepo: EventListingDataSource
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return EventsBroadCastListingViewModel(eventListingRepo) as T
    }
}