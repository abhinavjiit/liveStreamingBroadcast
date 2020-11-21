package com.livestreaming.channelize.io.activity.eventListing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Provides
import javax.inject.Inject
import javax.inject.Singleton

@Suppress("UNCHECKED_CAST")
class EventListingViewModelFact @Inject constructor(
   private val eventListingRepo: EventListingRepo
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return EventsBroadCastListingViewModel(eventListingRepo) as T
    }
}