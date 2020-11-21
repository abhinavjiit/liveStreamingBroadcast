package com.livestreaming.channelize.io.model


import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("displayName")
    val displayName: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("profileImageUrl")
    val profileImageUrl: String
)