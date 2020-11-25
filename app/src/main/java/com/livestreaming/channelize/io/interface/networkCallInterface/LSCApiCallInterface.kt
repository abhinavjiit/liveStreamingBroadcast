package com.livestreaming.channelize.io.`interface`.networkCallInterface

import com.livestreaming.channelize.io.model.EventDetailResponse
import com.livestreaming.channelize.io.model.MessageCommentData
import com.livestreaming.channelize.io.model.appIdModule.ExtractAppIdResponse
import com.livestreaming.channelize.io.model.productdetailModel.ProductItemsResponse
import okhttp3.ResponseBody
import retrofit2.http.*

interface LSCApiCallInterface {

    @GET("/v2/live_broadcasts/")
    suspend fun getEvents(
        @Query("hosts") hosts: String,
        @Query("sort") sort: String,
        @Query("limit") limit: Int,
        @Query("skip") skip: Int
    ): List<EventDetailResponse>

    @GET("/buyers/products/")
    suspend fun getProducts(
        @Header("x-shopify-shop-origin") header: String,
        @Query("ids") ids: String
    ): ProductItemsResponse


    @POST("v2/messages/send")
    suspend fun sendComment(@Body body: MessageCommentData): ResponseBody

    //("/modules/")
    @GET
    suspend fun getAppID(
        @Url url: String,
        @Query("enabled") enabled: String = "true"
    ): List<ExtractAppIdResponse>


}