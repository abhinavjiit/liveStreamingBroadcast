package com.livestreaming.channelize.io.model.realTimeMessageResponse


import com.google.gson.annotations.SerializedName

data class Message(
    @SerializedName("attachments")
    val attachments: List<Any>,
    @SerializedName("body")
    val body: String,
    @SerializedName("conversationId")
    val conversationId: String,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("encrypted")
    val encrypted: Boolean,
    @SerializedName("events")
    val events: Events,
    @SerializedName("id")
    val id: String,
    @SerializedName("isDeleted")
    val isDeleted: Boolean,
    @SerializedName("mentionedUsers")
    val mentionedUsers: List<Any>,
    @SerializedName("owner")
    val owner: Owner,
    @SerializedName("ownerId")
    val ownerId: String,
    @SerializedName("replyCount")
    val replyCount: Int,
    @SerializedName("showInConversation")
    val showInConversation: Boolean,
    @SerializedName("type")
    val type: String,
    @SerializedName("updatedAt")
    val updatedAt: String
)