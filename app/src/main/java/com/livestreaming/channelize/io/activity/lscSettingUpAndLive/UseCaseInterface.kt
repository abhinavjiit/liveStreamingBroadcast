package com.livestreaming.channelize.io.activity.lscSettingUpAndLive

import androidx.lifecycle.LiveData

interface UseCaseInterface {
    fun getTotalTime(endTime: String?): String
    fun getCountdownTime(endTime: String?): LiveData<String>

}