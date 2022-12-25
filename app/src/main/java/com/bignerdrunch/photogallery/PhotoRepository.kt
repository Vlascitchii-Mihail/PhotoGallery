package com.bignerdrunch.photogallery

import com.bignerdrunch.photogallery.api.FlickrApi
import com.bignerdrunch.photogallery.api.GalleryItem
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create

/**
 * The repository for the API request
 */
class PhotoRepository {
    private val flickrApi: FlickrApi

    init {

        /**
         * Retrofit's exemplar for creating the instances of the FlickrApi interface
         */
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://api.flickr.com/")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()

        flickrApi = retrofit.create()
    }

    /**
     * Getting some information from a server
     */
    suspend fun fetchPhotos(): List<GalleryItem> = flickrApi.fetchPhotos().photos.galleryItems
}