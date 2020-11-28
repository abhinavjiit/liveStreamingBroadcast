package com.livestreaming.channelize.io.activity.lscSettingUpAndLive

import androidx.lifecycle.LiveData
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

class LSCBroadCastViewModel(
    private var lscBroadCastRepoImpl: LSCBroadCastRepoInterface,
    private val useCase: UseCaseInterface
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
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            lscBroadCastRepoImpl.onStartLSCBroadCast(
                broadcastId = broadcastId,
                broadcastRequiredResponse = broadcastResponse
            )
        }

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


    fun getTotalTime(endTime: String?): LiveData<String> {
        totalTime.value = useCase.getTotalTime(endTime)
        return totalTime
    }

    fun getCounterTime(endTime: String?): LiveData<String> {
//        counterTime.value = useCase.getCountdownTime(endTime).value
//        return counterTime
        ////// have to remove manually ,will be active forever
        useCase.getCountdownTime(endTime).observeForever {
            counterTime.value = it
        }
        return counterTime

    }

}


