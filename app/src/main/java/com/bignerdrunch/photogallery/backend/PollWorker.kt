package com.bignerdrunch.photogallery.backend

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
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
                }
            }

            Result.success()
        } catch (ex: Exception) {
            Log.e(TAG, "Background update failed", ex)
            Result.failure()
        }
    }
}