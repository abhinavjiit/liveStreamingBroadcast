
package com.livestreaming.channelize.io.model.productdetailModel

import com.google.gson.annotations.SerializedName
import java.math.BigInteger

data class ProductDetailResponse(
    @SerializedName("title") val title: String,
    @SerializedName("variants") val variants: List<Variants>,
    @SerializedName("images") val images: List<Images>,
    @SerializedName("image") val image: Image
)

data class ProductItemsResponse(
    val statusCode: Int,
    val success: String,
    val data: Data
)

data class Data(val products: List<ProductDetailResponse>?)