package com.livestreaming.channelize.io.activity.lscSettingUp

import com.livestreaming.channelize.io.model.MessageCommentData
import com.livestreaming.channelize.io.model.lscDetailsModel.LSCBroadCastLiveUpdateDetailsResponse
import com.livestreaming.channelize.io.model.productdetailModel.ProductItemsResponse
import com.livestreaming.channelize.io.networkCallErrorAndSuccessHandler.Resource

interface LSCBroadCastRepoInterface {

    suspend fun getProducts(productsIds: String?): Resource<ProductItemsResponse>?

    suspend fun sendComment(messageCommentData: MessageCommentData)

    suspend fun onStartLSCBroadCast(broadcastId: String)

    suspend fun onStopLSCBroadCast(broadcastId: String)

    suspend fun getBroadCastDetails(broadcastId: String): Resource<LSCBroadCastLiveUpdateDetailsResponse>


}