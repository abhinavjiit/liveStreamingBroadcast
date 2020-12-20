package com.livestreaming.channelize.io.model

import com.google.gson.annotations.SerializedName

data class StartBroadcastRequiredResponse(

    @SerializedName("rtcUserId")
    var rtcUserId: Int = -1,

    @SerializedName("recordingParams")
    var recordingParams: RecordingParams? = null
)

data class RecordingParams(

    @SerializedName("width")
    var width: Int,

    @SerializedName("height")
    var height: Int,

    @SerializedName("fps")
    var fps: Int = 30,

    @SerializedName("bitrate")
    var bitrate: Int = 1710
)
