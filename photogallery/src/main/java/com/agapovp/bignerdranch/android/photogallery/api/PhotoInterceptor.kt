package com.agapovp.bignerdranch.android.photogallery.api

import com.agapovp.bignerdranch.android.photogallery.BuildConfig
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class PhotoInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {

        val originalRequest: Request = chain.request()

        if (originalRequest.url().host() == URL_DOWNLOAD) {
            return chain.proceed(originalRequest)
        }

        val newUrl: HttpUrl = originalRequest.url().newBuilder()
            .addQueryParameter(API_KEY_NAME, BuildConfig.API_KEY)
            .addQueryParameter(FORMAT_NAME, FORMAT_VALUE)
            .addQueryParameter(NO_JSON_CALLBACK_NAME, NO_JSON_CALLBACK_VALUE)
            .addQueryParameter(EXTRAS_NAME, EXTRAS_VALUE)
            .addQueryParameter(SAFE_SEARCH_NAME, SAFE_SEARCH_VALUE)
            .build()

        val newRequest: Request = originalRequest.newBuilder()
            .url(newUrl)
            .build()

        return chain.proceed(newRequest)
    }

    companion object {
        private const val API_KEY_NAME = "api_key"
        private const val FORMAT_NAME = "format"
        private const val FORMAT_VALUE = "json"
        private const val NO_JSON_CALLBACK_NAME = "nojsoncallback"
        private const val NO_JSON_CALLBACK_VALUE = "1"
        private const val EXTRAS_NAME = "extras"
        private const val EXTRAS_VALUE = "url_s"
        private const val SAFE_SEARCH_NAME = "safesearch"
        private const val SAFE_SEARCH_VALUE = "1"

        private const val URL_DOWNLOAD = "live.staticflickr.com"
    }
}
