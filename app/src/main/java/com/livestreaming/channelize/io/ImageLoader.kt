package com.livestreaming.channelize.io

import android.widget.ImageView
import com.squareup.picasso.Picasso

class ImageLoader private constructor() {

    companion object {
        fun showImage(imageUrl: String?, viewId: ImageView) {
            try {
                imageUrl?.let { url ->
                    Picasso.get().load(url.trim()).into(viewId)
                }
            } catch (e: Exception) {
                TODO("Can show default image for null value")
            }
        }
    }

}