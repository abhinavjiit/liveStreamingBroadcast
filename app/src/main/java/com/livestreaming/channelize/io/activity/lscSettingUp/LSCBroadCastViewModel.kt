package com.livestreaming.channelize.io.activity.lscSettingUp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.livestreaming.channelize.io.model.MessageCommentData
import com.livestreaming.channelize.io.model.productdetailModel.ProductItemsResponse
import com.livestreaming.channelize.io.networkCallErrorAndSuccessHandler.Resource

class LSCBroadCastViewModel(private var lscBroadCastRepoImpl: LSCBroadCastRepoInterface) :
    ViewModel() {


    fun getProductItems() =
        liveData<Resource<ProductItemsResponse>>
        {
            val productItemsData = lscBroadCastRepoImpl.getProducts()
            emit(productItemsData)
        }


    fun postComment(messageCommentData: MessageCommentData) = liveData<Unit
            > {
        lscBroadCastRepoImpl.sendComment(messageCommentData)
    }


}