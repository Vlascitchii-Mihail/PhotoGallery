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
 * Checking for new photo in background
 */
class PollWorker(
    private val context: Context,
    workerParameters: WorkerParameters
) : CoroutineWorker(context, workerParameters) {
    override suspend fun doWork(): Result {
//        Log.i(TAG, "Work request triggered")

        val preferenceRepository = PreferencesRepository.get()
        val photoRepository = PhotoRepository()

        val query = preferenceRepository.storedQuery.first()
        val lastId = preferenceRepository.lastResultId.first()

        if (query.isEmpty()) {
            Log.i(TAG, "No saved query, finishing early")
            return  Result.success()
        }

        //the final state of the operation
        return try {
            val items = photoRepository.searchPhotos(query)

            if (items.isNotEmpty()) {
                val newResultId = items.first().id

                if (newResultId == lastId) {
                    Log.i(TAG, "Still have the same result")
                } else {
                    Log.i(TAG, "Got a new result: $newResultId")
                    preferenceRepository.setLastResultId(newResultId)
                    notifyUser()
                }
            }

            Result.success()
        } catch (ex: Exception) {
            Log.e(TAG, "Background update failed", ex)
            Result.failure()
        }
    }

    private fun notifyUser() {
        val intent = MainActivity.newIntent(context)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val resources = context.resources

        val notification = NotificationCompat

                //set the notification channel
            .Builder(context, NOTIFICATION_CHANNEL_ID)
            .setTicker(resources.getString(R.string.new_picture_ticker))
            .setSmallIcon(android.R.drawable.ic_menu_report_image)
            .setContentTitle(resources.getString(R.string.new_picture_title))
            .setContentText(resources.getString(R.string.new_picture_text))
            .setContentIntent(pendingIntent)

                //The notification is automatically canceled when the user clicks it in the panel.
            .setAutoCancel(true)
            .build()

        //0 - notification ID
        NotificationManagerCompat.from(context).notify(0, notification)
    }
}