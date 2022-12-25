package com.bignerdrunch.photogallery.api

import retrofit2.http.GET

private const val API_KEY = "0de99c04241cebbb57e0571d117e351b"

interface FlickrApi {

    /**
     * Requests to a server  using the Retrofit's reference
     * nojsoncallback - without any parentheses
     * extras - URL-address of each photo
     */
    @GET(
        "services/rest/?method=flickr.interestingness.getList"+
                "&api_key=$API_KEY"+
                "&format=json"+
                "&nojsoncallback=1"+
                "&extras=url_s"
    )
    suspend fun fetchPhotos() : FlickrResponse
}