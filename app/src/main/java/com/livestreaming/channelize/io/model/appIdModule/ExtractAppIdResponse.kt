
package com.livestreaming.channelize.io.model.appIdModule


import com.google.gson.annotations.SerializedName

data class ExtractAppIdResponse(

    @SerializedName("disabledBy")
    val disabledBy: String,

    @SerializedName("enabled")
    val enabled: Boolean,

    @SerializedName("_id")
    val id: String,

    @SerializedName("identifier")
    val identifier: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("priority")
    val priority: Int,

    @SerializedName("settings")
    val settings: List<Setting>
)