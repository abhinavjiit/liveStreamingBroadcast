package com.livestreaming.channelize.io.activity.lscSettingUp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import javax.inject.Inject

class LSCBroadCastViewModel : ViewModel() {

    @Inject
    lateinit var lscBroadCastRepoImpl: LSCBroadCastRepoImpl

    fun getInstructions() = liveData<Unit
            > {
        lscBroadCastRepoImpl.getInstructions()


    }


}