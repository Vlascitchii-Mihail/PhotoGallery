package com.bignerdrunch.photogallery.api

import com.squareup.moshi.JsonClass

/**
 * Will be compared with external JSON's object
 */
@JsonClass(generateAdapter = true)
data class FlickrResponse (val photos: PhotoResponse)