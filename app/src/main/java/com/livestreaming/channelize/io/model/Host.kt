package com.livestreaming.channelize.io.model


import com.google.gson.annotations.SerializedName

data class Host(

    @SerializedName("id")
    val id: String,

    @SerializedName("user")
    val user: User,

    @SerializedName("userId")
    val userId: String
)