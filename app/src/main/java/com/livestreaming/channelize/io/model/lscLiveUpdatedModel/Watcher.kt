
package com.livestreaming.channelize.io.model.lscLiveUpdatedModel


import com.google.gson.annotations.SerializedName

data class Watcher(
    @SerializedName("displayName")
    val displayName: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("profileImageUrl")
    val profileImageUrl: String
)