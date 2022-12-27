package com.bignerdrunch.photogallery.api

import retrofit2.http.GET
import retrofit2.http.Query

private const val API_KEY = "0de99c04241cebbb57e0571d117e351b"

interface FlickrApi {

    /**
     * Requests to a server  using the Retrofit's reference
     * nojsoncallback - without any parentheses
     * extras - URL-address of each photo
     */
    @GET("services/rest/?method=flickr.interestingness.getList")
    suspend fun fetchPhotos() : FlickrResponse

    //@Query("text") - Query parameter appended to the URL.
    @GET("services/rest/?method=flickr.photos.search")
    suspend fun searchPhotos(@Query("text") query: String): FlickrResponse
}