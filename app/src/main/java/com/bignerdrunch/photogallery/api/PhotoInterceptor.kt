package com.bignerdrunch.photogallery.api

import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

private const val API_KEY = "0de99c04241cebbb57e0571d117e351b"

/**
 * Create a request's template
 */
class PhotoInterceptor: Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {

        /**
         * access to the initial query
         */
        val originalRequest: Request = chain.request()

        /**
         * Building the request's template
         */
        val newUrl: HttpUrl = originalRequest.url.newBuilder()
            .addQueryParameter("api_key", API_KEY)
            .addQueryParameter("format", "json")
            .addQueryParameter("nojsoncallback", "1")
            .addQueryParameter("extras", "url_s")
            .addQueryParameter("safesearch", "1")
            .build()

        val newRequest: Request = originalRequest.newBuilder()
            .url(newUrl)
            .build()

        //return an answer from the server
        return chain.proceed(newRequest)
    }
}