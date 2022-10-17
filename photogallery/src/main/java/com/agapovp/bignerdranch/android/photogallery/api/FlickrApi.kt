package com.agapovp.bignerdranch.android.photogallery.api

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface FlickrApi {

    @GET("services/rest/?method=flickr.photos.search")
    suspend fun searchPhotos(
        @Query(value = "text") query: String,
        @Query(value = "page") page: Int
    ): PhotoResponse

    @GET("services/rest/?method=flickr.interestingness.getlist")
    suspend fun fetchPhotos(
        @Query(value = "page") page: Int
    ): PhotoResponse

    @GET
    fun fetchUrlBites(@Url url: String): Call<ResponseBody>
}
