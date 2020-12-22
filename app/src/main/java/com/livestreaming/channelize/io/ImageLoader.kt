package com.livestreaming.channelize.io

import android.util.Log
import android.widget.ImageView
import com.squareup.picasso.Picasso

class ImageLoader private constructor() {

    companion object {

        fun showImage(imageUrl: String?, viewId: ImageView, width: Int, height: Int) {
            try {
                imageUrl?.let { url ->
                    Picasso.get().load(url.trim()).placeholder(R.drawable.app_logo_svg).resize(width, height)
                        .error(R.drawable.app_logo_svg).into(viewId)
                }
            } catch (e: Exception) {
                Log.d("ImageShowEx", e.toString())
                TODO("Can show default image for null value")
            }
        }
    }

}