package com.livestreaming.channelize.io.`interface`.networkCallInterface

import com.livestreaming.channelize.io.model.EventDetailResponse
import com.livestreaming.channelize.io.model.EventsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface LSCApiCallInterface {

    @GET("/v2/live_broadcasts/")
    suspend fun getEvents(
        @Query("hosts") hosts: String,
        @Query("sort") sort: String,
        @Query("limit") limit: Int,
        @Query("skip") skip: Int
    ): List<EventDetailResponse>
}