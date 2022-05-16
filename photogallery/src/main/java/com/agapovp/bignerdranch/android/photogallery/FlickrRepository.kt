package com.agapovp.bignerdranch.android.photogallery

import android.util.Log
import com.agapovp.bignerdranch.android.photogallery.api.FlickrApi
import com.agapovp.bignerdranch.android.photogallery.api.PhotoResponse
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type

class FlickrRepository {

    private val flickrApi: FlickrApi = Retrofit.Builder()
        .baseUrl("https://api.flickr.com/")
        .addConverterFactory(
            GsonConverterFactory.create(
                GsonBuilder().registerTypeAdapter(
                    PhotoResponse::class.java,
                    PhotoDeserializer()
                ).create()
            )
        )
        .build()
        .create(FlickrApi::class.java)

    private var photosCall: Call<PhotoResponse>? = null

    suspend fun fetchPhotos(page: Int): List<GalleryItem> {
        flickrApi.fetchPhotos(BuildConfig.API_KEY, page).apply {
            Log.d(TAG, "fetchPhotos: fetched page: $page, with ${galleryItems.size} items")
            return galleryItems
        }
    }

    fun cancelPhotos() {
        photosCall?.apply { cancel() }
    }

    private class PhotoDeserializer : JsonDeserializer<PhotoResponse> {

        override fun deserialize(
            json: JsonElement,
            typeOfT: Type,
            context: JsonDeserializationContext
        ): PhotoResponse {

            val type = object : TypeToken<List<GalleryItem>>() {}.type
            val photos = json
                .asJsonObject.get("photos")
                .asJsonObject.get("photo")

            return PhotoResponse().apply {
                galleryItems = context.deserialize(photos, type)
            }
        }
    }

    companion object {

        private const val TAG = "FlickrRepository"
    }
}
