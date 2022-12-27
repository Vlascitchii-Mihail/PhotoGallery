package com.bignerdrunch.photogallery.repository

import com.bignerdrunch.photogallery.api.FlickrApi
import com.bignerdrunch.photogallery.api.GalleryItem
import com.bignerdrunch.photogallery.api.PhotoInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create

/**
 * The repository for the API request
 */
class PhotoRepository {
    private val flickrApi: FlickrApi

    init {

        val okHttpClient = OkHttpClient.Builder()

                //add the interceptor
            .addInterceptor(PhotoInterceptor())
            .build()

        /**
         * Retrofit's exemplar for creating the instances of the FlickrApi interface
         */
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://api.flickr.com/")
            .addConverterFactory(MoshiConverterFactory.create())

                //add a new client
            .client(okHttpClient)
            .build()

        flickrApi = retrofit.create()
    }

    /**
     * Getting some information from a server
     */
    suspend fun fetchPhotos(): List<GalleryItem> = flickrApi.fetchPhotos().photos.galleryItems

    suspend fun searchPhotos(query: String): List<GalleryItem> =
        flickrApi.searchPhotos(query).photos.galleryItems
}