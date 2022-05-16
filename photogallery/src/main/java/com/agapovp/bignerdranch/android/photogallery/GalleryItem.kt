package com.agapovp.bignerdranch.android.photogallery

import com.google.gson.annotations.SerializedName

data class GalleryItem(
    @SerializedName("id")
    var id: String = "",
    @SerializedName("title")
    var title: String = "",
    @SerializedName("url_s")
    var url: String = ""
)
