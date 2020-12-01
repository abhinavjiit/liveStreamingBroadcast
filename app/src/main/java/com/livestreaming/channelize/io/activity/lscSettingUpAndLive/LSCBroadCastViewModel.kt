package com.livestreaming.channelize.io.activity.lscSettingUpAndLive

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.livestreaming.channelize.io.model.MessageCommentData
import com.livestreaming.channelize.io.model.StartBroadcastRequiredResponse
import com.livestreaming.channelize.io.model.productdetailModel.ProductItemsResponse
import com.livestreaming.channelize.io.networkCallErrorAndSuccessHandler.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.ResponseBody

class LSCBroadCastViewModel(
    private var lscBroadCastRepoImpl: LSCBroadCastRepoInterface

) :
    ViewModel() {


    private val totalTime = MutableLiveData<String>()
    private val counterTime = MutableLiveData<String>()


    fun getProductItems(productsIds: String?) =
        liveData<Resource<ProductItemsResponse>?>
        {
            val productItemsData = lscBroadCastRepoImpl.getProducts(productsIds = productsIds)
            emit(productItemsData)
        }


    fun postComment(messageCommentData: MessageCommentData) = liveData<Unit
            > {
        lscBroadCastRepoImpl.sendComment(messageCommentData = messageCommentData)
    }


    fun onStartLSCBroadCast(
        broadcastId: String,
        broadcastResponse: StartBroadcastRequiredResponse
    ) = liveData<Resource<ResponseBody>> {
        val res = lscBroadCastRepoImpl.onStartLSCBroadCast(
            broadcastId = broadcastId,
            broadcastRequiredResponse = broadcastResponse
        )
        emit(res)
    }


    fun onStartConversation(conversationId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            lscBroadCastRepoImpl.onStartConversation(conversationId = conversationId)

        }
    }

    fun onStopLSCBroadCast(broadcastId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            lscBroadCastRepoImpl.onStopLSCBroadCast(broadcastId = broadcastId)
        }
    }

    fun onStopConversation(conversationId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            lscBroadCastRepoImpl.onStopConversation(conversationId)
        }
    }

    fun getAllDetailsOfBroadCast(broadcastId: String, conversationId: String) = liveData {
        val res = lscBroadCastRepoImpl.getBroadCastDetails(
            broadcastId = broadcastId,
            conversationId = conversationId
        )
        emit(res)
    }
}


