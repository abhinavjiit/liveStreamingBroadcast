
package com.livestreaming.channelize.io.model.productdetailModel

import com.google.gson.annotations.SerializedName

data class Variants(
    @SerializedName("presentment_prices")
    val presentmentPrices: List<PresentmentPrices>
)

data class PresentmentPrices(
    @SerializedName("price")
    val price: Price,
    @SerializedName("compare_at_price")
    val compare_at_price: String
)

data class Price(
    @SerializedName("currency_code")
    val currency_code: String,
    @SerializedName("amount")
    val amount: Double
)