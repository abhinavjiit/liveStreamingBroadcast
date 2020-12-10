
package com.livestreaming.channelize.io.model


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MetaData(
    @SerializedName("platform")
    val platform: String,
    @SerializedName("conversationId")
    val conversationId: String
) : Parcelable {

}


