package com.livestreaming.channelize.io.activity.lscSettingUp

import com.livestreaming.channelize.io.`interface`.networkCallInterface.LSCApiCallInterface
import com.livestreaming.channelize.io.model.MessageCommentData
import com.livestreaming.channelize.io.model.lscDetailsModel.LSCBroadCastLiveUpdateDetailsResponse
import com.livestreaming.channelize.io.model.productdetailModel.ProductItemsResponse
import com.livestreaming.channelize.io.networkCallErrorAndSuccessHandler.Resource
import com.livestreaming.channelize.io.networkCallErrorAndSuccessHandler.ResponseHandler
import retrofit2.Retrofit

class LSCBroadCastRepoImpl(
    private val productsRetrofit: Retrofit,
    private val lscRetrofit: Retrofit
) :
    LSCBroadCastRepoInterface {
    override suspend fun getProducts(productsIds: String?): Resource<ProductItemsResponse>? {
        return try {
            productsIds?.let {
                val response = productsRetrofit.create(LSCApiCallInterface::class.java)
                    .getProducts(
                        "lsc-staging.myshopify.com",
                        it
                    )
                ResponseHandler().handleSuccess(response)
            }


        } catch (e: Exception) {
            ResponseHandler().handleException(e)

        }

    }

    override suspend fun sendComment(messageCommentData: MessageCommentData) {
        try {
            val response =
                lscRetrofit.create(LSCApiCallInterface::class.java).sendComment(messageCommentData)
        } catch (e: Exception) {

        }
    }

    override suspend fun onStartLSCBroadCast(broadcastId: String) {
        try {
            val response =
                lscRetrofit.create(LSCApiCallInterface::class.java).onStartBroadCast(broadcastId)
        } catch (e: Exception) {

        }
    }

    override suspend fun onStopLSCBroadCast(broadcastId: String) {
        try {
            lscRetrofit.create(LSCApiCallInterface::class.java).onStopBroadCast(broadcastId)
        } catch (e: Exception) {

        }
    }

    override suspend fun getBroadCastDetails(broadcastId: String): Resource<LSCBroadCastLiveUpdateDetailsResponse> {
        return try {
            val response = lscRetrofit.create(LSCApiCallInterface::class.java)
                .getAllDetailsOfBroadCast(broadcastId)
            ResponseHandler().handleSuccess(response)
        } catch (e: Exception) {
            ResponseHandler().handleException(e)
        }
    }
}