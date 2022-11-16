package com.example.igor.restaurantmobile.common.styles

import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources

sealed class Image {
    data class Resource(@DrawableRes val resource: Int) : Image()
    data class Network(val url: String) : Image()
}
//fun Image.loadImage(imageView: ImageView) {
//    when (this) {
//        is Image.Resource -> {
//            imageView.setImageDrawable(
//                AppCompatResources.getDrawable(
//                    imageView.context,
//                    this.resource
//                )
//            )
//        }
//
//        is Image.Network -> {
//            GlideImageLoad.Builder()
//                .context(imageView.context)
//                .imageUrl(this.url)
//                .build().into(imageView)
//        }
//    }
//}

