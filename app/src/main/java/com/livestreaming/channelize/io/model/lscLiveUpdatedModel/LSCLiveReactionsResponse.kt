
package com.livestreaming.channelize.io.model.lscLiveUpdatedModel


import com.google.gson.annotations.SerializedName

data class LSCLiveReactionsResponse(
    @SerializedName("liveBroadcast")
    val liveBroadcast: LiveBroadcastX,
    @SerializedName("reaction")
    val reaction: Reaction,
    @SerializedName("timestamp")
    val timestamp: String,
    @SerializedName("type")
    val type: String
)

data class LiveBroadcastX(
    @SerializedName("id")
    val id: String,
    @SerializedName("reactionsCount")
    val reactionsCount: ReactionsCount,
    @SerializedName("title")
    val title: String
)

data class Reaction(
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("user")
    val user: User,
    @SerializedName("userId")
    val userId: String
)

data class ReactionsCount(
    @SerializedName("star")
    val star: Int
)

data class User(
    @SerializedName("displayName")
    val displayName: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("profileImageUrl")
    val profileImageUrl: String
)