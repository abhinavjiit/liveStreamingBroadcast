package com.livestreaming.channelize.io.model.lscDetailsModel

import com.google.gson.annotations.SerializedName


class ReactionsCount(
    @SerializedName("wow")
    var wow: Int = 0,
    @SerializedName("angry")

    var angry: Int = 0,
    @SerializedName("clap")

    var clap: Int = 0,
    @SerializedName("dislike")

    var dislike: Int = 0,
    @SerializedName("heart")

    var heart: Int = 0,
    @SerializedName("insightfull")

    var insightfull: Int = 0,
    @SerializedName("laugh")

    var laugh: Int = 0,
    @SerializedName("like")

    var like: Int = 0,
    @SerializedName("sad")

    var sad: Int = 0,
    @SerializedName("smiley")

    var smiley: Int = 0,
    @SerializedName("thankyou")

    var thankyou: Int = 0


)