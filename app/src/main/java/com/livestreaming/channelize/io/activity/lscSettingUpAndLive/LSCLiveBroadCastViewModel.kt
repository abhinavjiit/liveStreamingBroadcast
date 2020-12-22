package com.livestreaming.channelize.io.activity.lscSettingUpAndLive

import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.channelize.apisdk.network.response.RequestResponse
import com.livestreaming.channelize.io.model.MessageCommentData
import com.livestreaming.channelize.io.model.StartBroadcastRequiredResponse
import com.livestreaming.channelize.io.model.productdetailModel.ProductItemsResponse
import com.livestreaming.channelize.io.networkCallErrorAndSuccessHandler.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LSCLiveBroadCastViewModel(private var lscBroadCastDataSource: LSCBroadCastDataSource) : ViewModel() {

    private var startStopConversationResponse = MutableLiveData<RequestResponse>()
    var beautificationCustomizationValuesClassHolder = MutableLiveData<BeautificationCustomizationValuesClassHolder>()
    var onchangeBeautification = MutableLiveData<BeautificationCustomizationValuesClassHolder>()
    var onUnsubscribeTopics = MutableLiveData<Boolean>()
    var onFinishBroadcast = MutableLiveData<Boolean>()
    var onRemoveFragment = MutableLiveData<Fragment>()
    var onSettingUpFragment = MutableLiveData<Boolean>()

    fun getProductItems(productsIds: String?) = liveData<Resource<ProductItemsResponse>?> {
        val productItemsData = lscBroadCastDataSource.getProducts(productsIds = productsIds)
        emit(productItemsData)
    }

    fun postComment(messageCommentData: MessageCommentData) = liveData<Unit> {
        lscBroadCastDataSource.sendComment(messageCommentData = messageCommentData)
    }

    fun onStartLSCBroadCast(broadcastId: String, broadcastResponse: StartBroadcastRequiredResponse) = liveData {
        val res = lscBroadCastDataSource.onStartLSCBroadCast(
            broadcastId = broadcastId,
            broadcastRequiredResponse = broadcastResponse
        )
        emit(res)
    }

    fun onStartConversation(conversationId: String): MutableLiveData<RequestResponse> {
        startStopConversationResponse = lscBroadCastDataSource.onStartConversation(conversationId = conversationId)
        return startStopConversationResponse
    }

    fun onStopLSCBroadCast(broadcastId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            lscBroadCastDataSource.onStopLSCBroadCast(broadcastId = broadcastId)
        }
    }

    fun onStopConversation(conversationId: String): MutableLiveData<RequestResponse> {
        startStopConversationResponse = lscBroadCastDataSource.onStopConversation(conversationId = conversationId)
        return startStopConversationResponse
    }

    fun getAllDetailsOfBroadCast(broadcastId: String, conversationId: String) = liveData {
        val res = lscBroadCastDataSource.getBroadCastDetails(broadcastId = broadcastId, conversationId = conversationId)
        emit(res)
    }

    fun onRemoveFragment(fragment: Fragment) {
        onRemoveFragment.value = fragment
    }

}



