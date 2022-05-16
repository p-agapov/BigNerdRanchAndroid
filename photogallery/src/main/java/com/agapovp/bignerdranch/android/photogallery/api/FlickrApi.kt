package com.agapovp.bignerdranch.android.photogallery.api

import retrofit2.http.GET
import retrofit2.http.Query

interface FlickrApi {

    @GET(
        "services/rest/?method=flickr.interestingness.getlist" +
                "&format=json" +
                "&nojsoncallback=1" +
                "&extras=url_s"
    )
    suspend fun fetchPhotos(
        @Query(value = "api_key") apiKey: String,
        @Query(value = "page") page: Int
    ): PhotoResponse
}
