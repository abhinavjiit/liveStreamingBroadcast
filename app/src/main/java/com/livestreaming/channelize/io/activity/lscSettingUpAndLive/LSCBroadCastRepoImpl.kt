package com.livestreaming.channelize.io.activity.lscSettingUpAndLive

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.channelize.apisdk.network.api.ChannelizeApi
import com.channelize.apisdk.network.response.RequestResponse
import com.livestreaming.channelize.io.BaseApplication
import com.livestreaming.channelize.io.SharedPrefUtils
import com.livestreaming.channelize.io.`interface`.networkCallInterface.ILscApiCallBack
import com.livestreaming.channelize.io.model.MessageCommentData
import com.livestreaming.channelize.io.model.StartBroadcastRequiredResponse
import com.livestreaming.channelize.io.model.lscDetailsModel.LSCBroadCastLiveUpdateDetailsResponse
import com.livestreaming.channelize.io.model.productdetailModel.ProductItemsResponse
import com.livestreaming.channelize.io.networkCallErrorAndSuccessHandler.Resource
import com.livestreaming.channelize.io.networkCallErrorAndSuccessHandler.ResponseHandler
import okhttp3.ResponseBody
import retrofit2.Retrofit

class LSCBroadCastRepoImpl(
    private val productsRetrofit: Retrofit,
    private val lscRetrofit: Retrofit,
    private val coreUrlRetrofit: Retrofit,
    private val channelizeApiClient: ChannelizeApi
) :
    ILscBroadCastRepositoryCallBack {

    private var startConversationResponse = MutableLiveData<RequestResponse>()
    private var stopConversationResponse = MutableLiveData<RequestResponse>()

    override suspend fun getProducts(productsIds: String?): Resource<ProductItemsResponse>? {
        return try {
            productsIds?.let { eventProductsIds ->
                SharedPrefUtils.getStoreUrl(BaseApplication.getInstance())?.let { storeUrl ->
                    val response = productsRetrofit.create(ILscApiCallBack::class.java)
                        .getProducts(
                            storeUrl,
                            eventProductsIds
                        )
                    ResponseHandler().handleSuccess(response)
                }
            }
        } catch (e: Exception) {
            ResponseHandler().handleException(e)
        } catch (t: Throwable) {
            ResponseHandler().handleThrowable(t)
        }
    }

    override suspend fun sendComment(messageCommentData: MessageCommentData) {
        try {
            coreUrlRetrofit.create(ILscApiCallBack::class.java)
                .sendComment(messageCommentData)
        } catch (e: Exception) {
            Log.d("sendCommentException", e.toString())
        }
    }

    override suspend fun onStartLSCBroadCast(
        broadcastId: String,
        broadcastRequiredResponse: StartBroadcastRequiredResponse
    ): Resource<ResponseBody> {
        return try {
            val response =
                lscRetrofit.create(ILscApiCallBack::class.java)
                    .onStartBroadCast(broadcastId = broadcastId, broadcastRequiredResponse)

            ResponseHandler().handleSuccess(response)
        } catch (e: Throwable) {
            Log.d("onStartLSCBroadCastEx", e.toString())
            ResponseHandler().handleThrowable(e)
        } catch (e: Exception) {
            Log.d("onStartLSCBroadCastEx", e.toString())
            ResponseHandler().handleException(e)
        }
    }

    override fun onStartConversation(conversationId: String): MutableLiveData<RequestResponse> {
        try {
            channelizeApiClient.startWatching(conversationId) { result, _ ->
                if (result != null && result.isSuccessful) {
                    startConversationResponse.postValue(result)

                } else {
                    Log.d("onStartConversationEx", "Failure")
                    startConversationResponse.postValue(null)
                }
            }
        } catch (e: Exception) {
            Log.d("onStartConversationEx", e.toString())
            startConversationResponse.postValue(null)
        } catch (t: Throwable) {
            Log.d("onStartConversationEx", t.toString())
            startConversationResponse.postValue(null)
        }
        return startConversationResponse
    }

    override suspend fun onStopLSCBroadCast(broadcastId: String) {
        try {
            lscRetrofit.create(ILscApiCallBack::class.java).onStopBroadCast(broadcastId)
        } catch (e: Exception) {
            Log.d("onStopLSCBroadCastEx", e.toString())
        }
    }

    override fun onStopConversation(conversationId: String): MutableLiveData<RequestResponse> {
        try {
            channelizeApiClient.stopWatching(conversationId) { result, _ ->
                if (result != null && result.isSuccessful) {
                    stopConversationResponse.postValue(result)
                } else {
                    stopConversationResponse.postValue(null)
                }
            }
        } catch (e: Exception) {
            Log.d("onStopConversationEx", e.toString())
            stopConversationResponse.postValue(null)
        } catch (t: Throwable) {
            Log.d("onStopConversationEx", t.toString())
            stopConversationResponse.postValue(null)
        }
        return stopConversationResponse
    }

    override suspend fun getBroadCastDetails(
        broadcastId: String,
        conversationId: String
    ): Resource<LSCBroadCastLiveUpdateDetailsResponse> {
        return try {
            val lscBroadCastLiveUpdateDetailsResponse: LSCBroadCastLiveUpdateDetailsResponse
            val response1 =
                lscRetrofit.create(ILscApiCallBack::class.java)
                    .getAllDetailsOfBroadCast(broadcastId = broadcastId)
            val response2 =
                coreUrlRetrofit.create(ILscApiCallBack::class.java)
                    .getCommentsCount(conversation_id = conversationId)
            lscBroadCastLiveUpdateDetailsResponse = LSCBroadCastLiveUpdateDetailsResponse(
                viewersCount = response1.viewersCount,
                messageCount = response2.messageCount,
                reactionsCount = response1.reactionsCount
            )
            ResponseHandler().handleSuccess(lscBroadCastLiveUpdateDetailsResponse)
        } catch (e: Exception) {
            ResponseHandler().handleException(e)
        }
    }

}