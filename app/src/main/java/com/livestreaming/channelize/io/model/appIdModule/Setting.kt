package com.livestreaming.channelize.io.model.appIdModule


import com.google.gson.annotations.SerializedName

data class Setting(
    @SerializedName("key")
    val key: String,
    @SerializedName("value")
    val value: String
)