package com.livestreaming.channelize.io.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.livestreaming.channelize.io.R
import com.livestreaming.channelize.io.model.productdetailModel.PresentmentPrices
import com.livestreaming.channelize.io.model.productdetailModel.ProductDetailResponse
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.adapter_products_list_item_layout.view.*
import java.util.*

class LSCBroadcastProductsListingAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var listData: List<ProductDetailResponse>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.adapter_products_list_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return if (listData.isNullOrEmpty())
            0
        else
            listData?.size!!
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            holder.apply {
                try {
                    when {
                        listData?.get(position)?.image != null -> {
                            Picasso.get().load(listData?.get(position)?.image?.src?.trim())
                                .into(itemImageView)
                        }
                        listData?.get(position)?.images.isNullOrEmpty() -> {
                            Picasso.get().load(listData?.get(position)?.images?.get(0)?.src?.trim())
                                .into(itemImageView)
                        }
                        else -> {

                            itemImageView.visibility = View.INVISIBLE
                        }
                    }
                } catch (e: Exception) {

                }
                productTitleTextView.text = listData?.get(position)?.title?.trim()
                val symbolWithPrice =
                    listData?.get(position)?.variants?.get(0)?.presentmentPrices?.getCurrencySymbolAndPrice(

                    )
                price.text = symbolWithPrice
            }
        }
    }


    fun setListData(list: List<ProductDetailResponse>?) {
        this.listData = list
    }

    class ViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
        internal val itemImageView: ImageView = mView.itemImageView
        internal val productTitleTextView: TextView = mView.productTitleTextView
        internal val price: TextView = mView.price

    }

}

fun List<PresentmentPrices>.getCurrencySymbolAndPrice(): String {
    val localCurrency = Currency.getInstance(Locale.getDefault())
    this.forEach {
        if (it.price.currency_code == "INR") {
            return Currency.getInstance("INR").symbol.plus(" " + it.price.amount)
        }
    }
    this.forEach {
        if (it.price.currency_code == "USD")
            return Currency.getInstance(Locale.US).getSymbol(Locale.US).plus(" " + it.price.amount)
    }
    return (this[0].price.amount).toString()
}