package com.livestreaming.channelize.io.activity.lscSettingUp

import com.livestreaming.channelize.io.model.MessageCommentData
import com.livestreaming.channelize.io.model.productdetailModel.ProductItemsResponse
import com.livestreaming.channelize.io.networkCallErrorAndSuccessHandler.Resource

interface LSCBroadCastRepoInterface {

    suspend fun getProducts(): Resource<ProductItemsResponse>

    suspend fun sendComment(messageCommentData: MessageCommentData)

}