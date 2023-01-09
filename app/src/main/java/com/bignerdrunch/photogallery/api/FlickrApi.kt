package com.bignerdrunch.photogallery.api

import retrofit2.http.GET
import retrofit2.http.Query

interface FlickrApi {

    /**
     * Requests to a server using the Retrofit's reference
     */
    @GET("services/rest/?method=flickr.interestingness.getList")
    suspend fun fetchPhotos() : FlickrResponse

    /**
     * @Query("text") - Query parameter appended after Interceptor's URL.
     */
    @GET("services/rest/?method=flickr.photos.search")
    suspend fun searchPhotos(@Query("text") query: String): FlickrResponse
}