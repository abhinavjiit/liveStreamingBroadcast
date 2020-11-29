package com.livestreaming.channelize.io.model


import com.google.gson.annotations.SerializedName

data class EventDetailResponse(
    @SerializedName("bannerImageUrl")
    val bannerImageUrl: String,
    @SerializedName("bannerVideoUrl")
    val bannerVideoUrl: String,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("duration")
    val duration: Int,
    @SerializedName("endTime")
    val endTime: String,
    @SerializedName("hosts")
    val hosts: List<Host>,
    @SerializedName("id")
    val id: String,
    @SerializedName("metaData")
    val metaData: MetaData,
    @SerializedName("owner")
    val owner: Owner,
    @SerializedName("ownerId")
    val ownerId: String,
    @SerializedName("products")
    val products: List<Product>,
    @SerializedName("published")
    val published: Boolean,
    @SerializedName("reactionsCount")
    val reactionsCount: ReactionsCount,
    @SerializedName("recording")
    val recording: Boolean,
    @SerializedName("startTime")
    val startTime: String,
    @SerializedName("status")
    var status: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("updatedAt")
    val updatedAt: String,
    @SerializedName("viewersCount")
    val viewersCount: Int,
    @SerializedName("watchersCount")
    val watchersCount: Int
)