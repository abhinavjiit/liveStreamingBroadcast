package com.livestreaming.channelize.io.activity.lscSettingUp

import com.channelize.apisdk.utils.ChannelizePreferences
import com.livestreaming.channelize.io.BaseApplication
import com.livestreaming.channelize.io.`interface`.networkCallInterface.LSCApiCallInterface
import com.livestreaming.channelize.io.networkCallErrorAndSuccessHandler.Resource
import com.livestreaming.channelize.io.networkCallErrorAndSuccessHandler.ResponseHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import retrofit2.Retrofit
import javax.inject.Inject

class LSCBroadCastRepoImpl @Inject constructor(private val retrofit: Retrofit?) : LSCBroadCastRepo {
    override suspend fun getInstructions() {

    }
}