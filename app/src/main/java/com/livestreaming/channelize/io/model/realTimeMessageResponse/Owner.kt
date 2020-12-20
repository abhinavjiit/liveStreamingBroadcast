
package com.livestreaming.channelize.io.model.realTimeMessageResponse


import com.google.gson.annotations.SerializedName

data class Owner(

    @SerializedName("displayName")
    val displayName: String,

    @SerializedName("id")
    val id: String
)