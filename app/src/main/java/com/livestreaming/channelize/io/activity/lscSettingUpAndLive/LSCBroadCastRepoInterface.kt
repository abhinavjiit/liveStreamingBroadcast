package com.livestreaming.channelize.io.activity.lscSettingUpAndLive

import com.livestreaming.channelize.io.model.MessageCommentData
import com.livestreaming.channelize.io.model.StartBroadcastRequiredResponse
import com.livestreaming.channelize.io.model.lscDetailsModel.LSCBroadCastLiveUpdateDetailsResponse
import com.livestreaming.channelize.io.model.productdetailModel.ProductItemsResponse
import com.livestreaming.channelize.io.networkCallErrorAndSuccessHandler.Resource
import okhttp3.ResponseBody

interface LSCBroadCastRepoInterface {

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

    suspend fun onStartConversation(conversationId: String)

    suspend fun onStopConversation(conversationId: String)

}