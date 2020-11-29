package com.livestreaming.channelize.io.activity.lscSettingUpAndLive

import android.util.Log
import com.livestreaming.channelize.io.BaseApplication
import com.livestreaming.channelize.io.SharedPrefUtils
import com.livestreaming.channelize.io.`interface`.networkCallInterface.LSCApiCallInterface
import com.livestreaming.channelize.io.model.MessageCommentData
import com.livestreaming.channelize.io.model.StartBroadcastRequiredResponse
import com.livestreaming.channelize.io.model.lscDetailsModel.LSCBroadCastLiveUpdateDetailsResponse
import com.livestreaming.channelize.io.model.productdetailModel.ProductItemsResponse
import com.livestreaming.channelize.io.networkCallErrorAndSuccessHandler.Resource
import com.livestreaming.channelize.io.networkCallErrorAndSuccessHandler.ResponseHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import retrofit2.Retrofit

class LSCBroadCastRepoImpl(
    private val productsRetrofit: Retrofit,
    private val lscRetrofit: Retrofit,
    private val coreUrlRetrofit: Retrofit
) :
    LSCBroadCastRepoInterface {
    override suspend fun getProducts(productsIds: String?): Resource<ProductItemsResponse>? {
        return try {
            productsIds?.let {
                val response = productsRetrofit.create(LSCApiCallInterface::class.java)
                    .getProducts(
                        SharedPrefUtils.getStoreUrl(BaseApplication.getInstance())!!,
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
                coreUrlRetrofit.create(LSCApiCallInterface::class.java).sendComment(messageCommentData)
        } catch (e: Exception) {
            Log.d("sendCommentException", e.toString())
        }
    }

    override suspend fun onStartLSCBroadCast(
        broadcastId: String,
        broadcastRequiredResponse: StartBroadcastRequiredResponse
    ) {
        try {
            val response =
                lscRetrofit.create(LSCApiCallInterface::class.java)
                    .onStartBroadCast(broadcastId, broadcastRequiredResponse)
        } catch (e: Exception) {
            Log.d("onStartLSCBroadCastEx", e.toString())
        }
    }

    override suspend fun onStartConversation(conversationId: String) {
        try {
            coreUrlRetrofit.create(LSCApiCallInterface::class.java)
                .onStartConversation(conversation_id = conversationId)
        } catch (e: Exception) {
            Log.d("onStartConversationEx", e.toString())
        }
    }

    override suspend fun onStopLSCBroadCast(broadcastId: String) {
        try {
            lscRetrofit.create(LSCApiCallInterface::class.java).onStopBroadCast(broadcastId)
        } catch (e: Exception) {
            Log.d("onStopLSCBroadCastEx", e.toString())
        }
    }


    override suspend fun onStopConversation(conversationId: String) {
        try {
            coreUrlRetrofit.create(LSCApiCallInterface::class.java)
                .onStopConversation(conversationId)
        } catch (e: Exception) {
            Log.d("onStopConversationEx", e.toString())
        }
    }

    override suspend fun getBroadCastDetails(
        broadcastId: String,
        conversationId: String
    ): Resource<LSCBroadCastLiveUpdateDetailsResponse> {
        return try {
            val lscBroadCastLiveUpdateDetailsResponse: LSCBroadCastLiveUpdateDetailsResponse
            coroutineScope {
                val response1 = async {
                    lscRetrofit.create(LSCApiCallInterface::class.java)
                        .getAllDetailsOfBroadCast(broadcastId = broadcastId)
                }
                val response2 = async {
                    coreUrlRetrofit.create(LSCApiCallInterface::class.java)
                        .getCommentsCount(conversation_id = conversationId)
                }
                val viewOrReactionsCount = response1.await()
                val messageCount = response2.await()
                lscBroadCastLiveUpdateDetailsResponse = LSCBroadCastLiveUpdateDetailsResponse(
                    viewersCount = viewOrReactionsCount.viewersCount,
                    messageCount = messageCount.messageCount,
                    reactionsCount = viewOrReactionsCount.reactionsCount
                )
            }

            ResponseHandler().handleSuccess(lscBroadCastLiveUpdateDetailsResponse)
        } catch (e: Exception) {
            ResponseHandler().handleException(e)
        }
    }
}