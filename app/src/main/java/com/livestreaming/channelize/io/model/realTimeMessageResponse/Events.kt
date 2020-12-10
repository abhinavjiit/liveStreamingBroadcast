
package com.livestreaming.channelize.io.model.realTimeMessageResponse


import com.google.gson.annotations.SerializedName

data class Events(
    @SerializedName("sendPushNotification")
    val sendPushNotification: Boolean,
    @SerializedName("updateUnreadCount")
    val updateUnreadCount: Boolean
)