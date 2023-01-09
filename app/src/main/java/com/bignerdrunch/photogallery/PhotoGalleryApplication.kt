package com.bignerdrunch.photogallery

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.bignerdrunch.photogallery.repository.PreferencesRepository

const val NOTIFICATION_CHANNEL_ID = "flickr_poll"

class PhotoGalleryApplication: Application() {
    override fun onCreate() {
        super.onCreate()

        /**
         * initializing a new PreferencesRepository's instance
         */
        PreferencesRepository.initialize(this)

        /**
         * create a new notification channel
         */
        //notification chanel's name
        val name = getString(R.string.notification_channel_name)
        val importance = NotificationManager.IMPORTANCE_DEFAULT

        //create a channel
        val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance)

        //creates a notification chanel in ApplicationManagerClass
        val notificationManager: NotificationManager =
            getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }
}