package com.livestreaming.channelize.io.model.lscLiveUpdatedModel


import com.google.gson.annotations.SerializedName

data class LSCLiveUpdatesResponse(
    @SerializedName("liveBroadcast")
    val liveBroadcast: LiveBroadcast,
    @SerializedName("timestamp")
    val timestamp: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("watcher")
    val watcher: Watcher
)