package com.bignerdrunch.photogallery.backend

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.bignerdrunch.photogallery.MainActivity
import com.bignerdrunch.photogallery.NOTIFICATION_CHANNEL_ID
import com.bignerdrunch.photogallery.R
import com.bignerdrunch.photogallery.repository.PhotoRepository
import com.bignerdrunch.photogallery.repository.PreferencesRepository
import kotlinx.coroutines.flow.first
import okhttp3.internal.notify

private const val TAG = "PollWorker"

/**
 * Provides to set background processes
 */
class PollWorker(
    private val context: Context,
    workerParameters: WorkerParameters
) : CoroutineWorker(context, workerParameters) {

    //perform background work
    //is calling from background
    override suspend fun doWork(): Result {
//        Log.i(TAG, "Work request triggered")

        /**
         * get the PreferencesRepository's object to get the last photo's ID
         */
        val preferenceRepository = PreferencesRepository.get()

        //for checking a new photos
        val photoRepository = PhotoRepository()

        val query = preferenceRepository.storedQuery.first()
        val lastId = preferenceRepository.lastResultId.first()

        if (query.isEmpty()) {
            Log.i(TAG, "No saved query, finishing early")
            return  Result.success()
        }

        /**
         * comparison the last photo's ID and ID from a new request
         * catching the error if there is no connection
         */
        return try {
            val items = photoRepository.searchPhotos(query)

            if (items.isNotEmpty()) {
                val newResultId = items.first().id

                if (newResultId == lastId) {
                    Log.i(TAG, "Still have the same result")
                } else {
                    Log.i(TAG, "Got a new result: $newResultId")

                    //save a new photo's ID in file
                    preferenceRepository.setLastResultId(newResultId)

                    //notify a user
                    notifyUser()
                }
            }

            Result.success()
        } catch (ex: Exception) {
            Log.e(TAG, "Background update failed", ex)
            Result.failure()
        }
    }

    /**
     * create a notification and notify a user
     */
    private fun notifyUser() {
        val intent = MainActivity.newIntent(context)

        /**
         * create a pending intent for launching thr app from NotificationBar
         */
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val resources = context.resources

        /**
         * build a notification
         */
        val notification = NotificationCompat

                //set the notification channel
            .Builder(context, NOTIFICATION_CHANNEL_ID)
            .setTicker(resources.getString(R.string.new_picture_ticker))
            .setSmallIcon(android.R.drawable.ic_menu_report_image)
            .setContentTitle(resources.getString(R.string.new_picture_title))
            .setContentText(resources.getString(R.string.new_picture_text))
            .setContentIntent(pendingIntent)

                //The notification is automatically deleted from the appBar when
            // the user clicks it the appBar's panel.
            .setAutoCancel(true)
            .build()

        //0 - notification ID
        //notify - send the notification
        NotificationManagerCompat.from(context).notify(0, notification)
    }
}