package com.livestreaming.channelize.io.`interface`.networkCallInterface

import com.livestreaming.channelize.io.model.EventDetailResponse
import com.livestreaming.channelize.io.model.MessageCommentData
import com.livestreaming.channelize.io.model.StartBroadcastRequiredResponse
import com.livestreaming.channelize.io.model.appIdModule.ExtractAppIdResponse
import com.livestreaming.channelize.io.model.lscDetailsModel.LSCBroadCastLiveUpdateDetailsResponse
import com.livestreaming.channelize.io.model.productdetailModel.ProductItemsResponse
import okhttp3.ResponseBody
import retrofit2.http.*

interface ILscApiCallBack {

    @GET("/v2/live_broadcasts/")
    suspend fun getEvents(
        @Query("hosts") hosts: String, @Query("sort") sort: String,
        @Query("limit") limit: Int,
        @Query("skip") skip: Int
    ): List<EventDetailResponse>

    @GET("/buyers/products/")
    suspend fun getProducts(
        @Header("x-shopify-shop-origin") header: String,
        @Query("ids") ids: String
    ): ProductItemsResponse

    @POST("messages/send")
    suspend fun sendComment(@Body body: MessageCommentData): ResponseBody

    @GET
    suspend fun getAppId(@Url url: String, @Query("enabled") enabled: String = "true"): List<ExtractAppIdResponse>

    @POST("/v2/live_broadcasts/{broadcastId}/start")
    suspend fun onStartBroadCast(
        @Path("broadcastId") broadcastId: String,
        @Body startBroadcastRequiredResponse: StartBroadcastRequiredResponse
    ): ResponseBody

    @POST("/v2/live_broadcasts/{broadcastId}/end")
    suspend fun onStopBroadCast(@Path("broadcastId") broadcastId: String)

    @GET("/v2/live_broadcasts/{broadcastId}")
    suspend fun getAllDetailsOfBroadCast(@Path("broadcastId") broadcastId: String): LSCBroadCastLiveUpdateDetailsResponse

    @GET("conversations/{conversation_id}/messages/count")
    suspend fun getCommentsCount(@Path("conversation_id") conversation_id: String): LSCBroadCastLiveUpdateDetailsResponse

}