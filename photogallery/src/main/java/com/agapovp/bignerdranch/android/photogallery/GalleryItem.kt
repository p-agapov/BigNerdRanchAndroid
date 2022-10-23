package com.agapovp.bignerdranch.android.photogallery

import android.net.Uri
import com.google.gson.annotations.SerializedName

data class GalleryItem(
    @SerializedName("id")
    var id: String = "",
    @SerializedName("owner")
    var owner: String = "",
    @SerializedName("title")
    var title: String = "",
    @SerializedName("url_s")
    var url: String = ""
) {
    val photoPageUri: Uri
        get() = Uri.parse(BASE_URI)
            .buildUpon()
            .appendPath(owner)
            .appendPath(id)
            .build()

    companion object {
        private const val BASE_URI = "https://www.flickr.com/photos/"
    }
}
