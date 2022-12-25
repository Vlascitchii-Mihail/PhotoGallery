package com.bignerdrunch.photogallery.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * JsonClass will be automatically filled with data from JSON response
 */
@JsonClass(generateAdapter = true)
data class GalleryItem(
    val title: String,
    val id: String,
    @Json(name = "url_s")val url: String
)
