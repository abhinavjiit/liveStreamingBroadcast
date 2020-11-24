package com.livestreaming.channelize.io.model.productdetailModel

import com.google.gson.annotations.SerializedName

data class Images (

    @SerializedName("src") val src : String,
    @SerializedName("variant_ids") val variant_ids : List<String>
)