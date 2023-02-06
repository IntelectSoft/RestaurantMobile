package md.edi.mobilewaiter.common.styles

import androidx.annotation.DrawableRes

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

