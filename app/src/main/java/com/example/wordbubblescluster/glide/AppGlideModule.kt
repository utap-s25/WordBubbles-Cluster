package com.example.wordbubblescluster.glide

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions
import com.firebase.ui.storage.images.FirebaseImageLoader
import com.google.firebase.storage.StorageReference
import java.io.InputStream

@GlideModule
class AppGlideModule: AppGlideModule() {
    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        // Register FirebaseImageLoader to handle StorageReference
        registry.append(
            StorageReference::class.java, InputStream::class.java,
            FirebaseImageLoader.Factory()
        )
    }
}
object Glide {
    private var glideOptions = RequestOptions ()
        .fitCenter()
        .circleCrop()

    fun fetch(storageReference: StorageReference, imageView: ImageView) {
        val width = imageView.width
        val height = imageView.height
        GlideApp.with(imageView.context)
            .asBitmap()
            .load(storageReference)
            .apply(glideOptions)
            .override(width, height)
            .into(imageView)
    }
}