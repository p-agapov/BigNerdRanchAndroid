package com.agapovp.bignerdranch.android.photogallery.api

import com.agapovp.bignerdranch.android.photogallery.GalleryItem
import com.google.gson.annotations.SerializedName

class PhotoResponse {

    @SerializedName("photo")
    lateinit var galleryItems: List<GalleryItem>
}
