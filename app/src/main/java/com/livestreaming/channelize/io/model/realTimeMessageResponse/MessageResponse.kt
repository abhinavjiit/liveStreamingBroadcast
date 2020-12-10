
package com.livestreaming.channelize.io.model.realTimeMessageResponse


import com.google.gson.annotations.SerializedName

data class MessageResponse(
    @SerializedName("message")
    val message: Message,
    @SerializedName("timestamp")
    val timestamp: String,
    @SerializedName("type")
    val type: String
)