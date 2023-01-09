package com.bignerdrunch.photogallery.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Moshi automatically will create List<GalleryItem> with name "photo"
 */
@JsonClass(generateAdapter = true)
data class PhotoResponse(
    @Json(name = "photo") val galleryItems: List<GalleryItem>
)
