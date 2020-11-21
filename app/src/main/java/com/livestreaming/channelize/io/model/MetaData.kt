package com.livestreaming.channelize.io.model


import com.google.gson.annotations.SerializedName

data class MetaData(
    @SerializedName("platform")
    val platform: String
)