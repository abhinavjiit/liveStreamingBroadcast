package com.livestreaming.channelize.io.activity.lscSettingUpAndLive

import androidx.lifecycle.MutableLiveData
import com.channelize.apisdk.network.response.RequestResponse
import com.livestreaming.channelize.io.model.MessageCommentData
import com.livestreaming.channelize.io.model.StartBroadcastRequiredResponse
import com.livestreaming.channelize.io.model.lscDetailsModel.LSCBroadCastLiveUpdateDetailsResponse
import com.livestreaming.channelize.io.model.productdetailModel.ProductItemsResponse
import com.livestreaming.channelize.io.networkCallErrorAndSuccessHandler.Resource
import okhttp3.ResponseBody

interface ILscBroadCastRepositoryCallBack {

    suspend fun getProducts(productsIds: String?): Resource<ProductItemsResponse>?

    suspend fun sendComment(messageCommentData: MessageCommentData)

    suspend fun onStartLSCBroadCast(
        broadcastId: String,
        broadcastRequiredResponse: StartBroadcastRequiredResponse
    ): Resource<ResponseBody>

    suspend fun onStopLSCBroadCast(broadcastId: String)

    suspend fun getBroadCastDetails(
        broadcastId: String,
        conversationId: String
    ): Resource<LSCBroadCastLiveUpdateDetailsResponse>

     fun onStartConversation(conversationId: String):MutableLiveData<RequestResponse>

     fun onStopConversation(conversationId: String):MutableLiveData<RequestResponse>

}