package com.livestreaming.channelize.io.activity.lscSettingUp

import com.livestreaming.channelize.io.`interface`.networkCallInterface.LSCApiCallInterface
import com.livestreaming.channelize.io.model.MessageCommentData
import com.livestreaming.channelize.io.model.productdetailModel.ProductItemsResponse
import com.livestreaming.channelize.io.networkCallErrorAndSuccessHandler.Resource
import com.livestreaming.channelize.io.networkCallErrorAndSuccessHandler.ResponseHandler
import retrofit2.Retrofit

class LSCBroadCastRepoImpl(private val retrofit: Retrofit) : LSCBroadCastRepoInterface {
    override suspend fun getProducts(): Resource<ProductItemsResponse> {
        return try {
            val response = retrofit.create(LSCApiCallInterface::class.java)
                .getProducts(

                    "lsc-staging.myshopify.com",
                    "5701810258080,5701809635488"
                )
            ResponseHandler().handleSuccess(response)

        } catch (e: Exception) {
            ResponseHandler().handleException(e)

        }

    }

    override suspend fun sendComment(messageCommentData: MessageCommentData) {
        try {

            val res = retrofit.create(LSCApiCallInterface::class.java).sendComment(messageCommentData)
        } catch (e: Exception) {

        }


    }

}