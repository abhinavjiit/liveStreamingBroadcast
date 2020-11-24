package com.livestreaming.channelize.io.model.lscLiveUpdatedModel


import com.google.gson.annotations.SerializedName

data class LiveBroadcast(
    @SerializedName("id")
    val id: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("watchersCount")
    val watchersCount: Int
)