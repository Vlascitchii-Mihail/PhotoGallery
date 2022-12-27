package com.bignerdrunch.photogallery

import android.app.Application
import com.bignerdrunch.photogallery.repository.PreferencesRepository

class PhotoGalleryApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        PreferencesRepository.initialize(this)
    }
}