package com.livestreaming.channelize.io.activity.lscSettingUpAndLive

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject

@Suppress("UNCHECKED_CAST")
class LSCBroadcastAndLiveViewModelFact(private val lscBroadCastDataSource: LSCBroadCastDataSource) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return LSCLiveBroadCastViewModel(lscBroadCastDataSource) as T
    }

}