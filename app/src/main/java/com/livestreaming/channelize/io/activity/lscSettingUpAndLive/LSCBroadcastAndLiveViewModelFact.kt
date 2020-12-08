package com.livestreaming.channelize.io.activity.lscSettingUpAndLive

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

@Suppress("UNCHECKED_CAST")
class LSCBroadcastAndLiveViewModelFact(
    private val ILscBroadCastRepositoryCallBack: ILscBroadCastRepositoryCallBack
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return LSCLiveBroadCastViewModel(ILscBroadCastRepositoryCallBack) as T
    }

}