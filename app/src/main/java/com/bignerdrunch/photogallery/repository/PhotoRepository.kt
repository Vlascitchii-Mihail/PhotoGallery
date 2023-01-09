package com.bignerdrunch.photogallery.repository

import com.bignerdrunch.photogallery.api.FlickrApi
import com.bignerdrunch.photogallery.api.GalleryItem
import com.bignerdrunch.photogallery.api.PhotoInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create

/**
 * The repository for the API requests
 */
class PhotoRepository {
    private val flickrApi: FlickrApi

    init {

        /**
         * add the client
         */
        val okHttpClient = OkHttpClient.Builder()

                //add the interceptor to any request
            .addInterceptor(PhotoInterceptor())
            .build()

        /**
         * Retrofit's exemplar for creating the instances of the FlickrApi interface
         */
        val retrofit: Retrofit = Retrofit.Builder()

                //base URL for our request
            .baseUrl("https://api.flickr.com/")

            /**
             * add the converter which convert the API's response to String (indicated in the
             */
            .addConverterFactory(MoshiConverterFactory.create())

                //add a new client, adding URL template with any request
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