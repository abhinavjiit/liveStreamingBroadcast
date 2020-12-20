
package com.livestreaming.channelize.io.model.lscDetailsModel


import com.google.gson.annotations.SerializedName

data class LSCBroadCastLiveUpdateDetailsResponse(

    @SerializedName("reactionsCount")
    var reactionsCount: ReactionsCount,

    @SerializedName("viewersCount")
    var viewersCount: Int,

    @SerializedName("count")
    var messageCount: Int
)
