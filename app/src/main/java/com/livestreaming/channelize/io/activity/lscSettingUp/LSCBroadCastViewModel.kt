package com.livestreaming.channelize.io.activity.lscSettingUp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.livestreaming.channelize.io.model.MessageCommentData
import com.livestreaming.channelize.io.model.productdetailModel.ProductItemsResponse
import com.livestreaming.channelize.io.networkCallErrorAndSuccessHandler.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LSCBroadCastViewModel(private var lscBroadCastRepoImpl: LSCBroadCastRepoInterface) :
    ViewModel() {


    fun getProductItems(productsIds: String?) =
        liveData<Resource<ProductItemsResponse>?>
        {
            val productItemsData = lscBroadCastRepoImpl.getProducts(productsIds)
            emit(productItemsData)
        }


    fun postComment(messageCommentData: MessageCommentData) = liveData<Unit
            > {
        lscBroadCastRepoImpl.sendComment(messageCommentData)
    }


    fun onStartLSCBroadCast(broadcastId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            lscBroadCastRepoImpl.onStartLSCBroadCast(broadcastId)
        }

    }

    fun onStopLSCBroadCast(broadcastId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            lscBroadCastRepoImpl.onStopLSCBroadCast(broadcastId)
        }
    }

    fun getAllDetailsOfBroadCast(broadcastId: String) = liveData {
        val res = lscBroadCastRepoImpl.getBroadCastDetails(broadcastId = broadcastId)
        emit(res)
    }

}


