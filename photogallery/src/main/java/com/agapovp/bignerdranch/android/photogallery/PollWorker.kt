package com.agapovp.bignerdranch.android.photogallery

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.os.bundleOf
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

const val ACTION_SHOW_NOTIFICATION =
    "com.agapovp.bignerdranch.android.photogallery.SHOW_NOTIFICATION"
const val PERMISSION_PRIVATE =
    "com.agapovp.bignerdranch.android.photogallery.PRIVATE"

const val REQUEST_CODE = "REQUEST_CODE"
const val NOTIFICATION = "NOTIFICATION"

class PollWorker(
    appContext: Context, params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val query = QueryPreferences.getStoredQuery(applicationContext)
        val lastResultId = QueryPreferences.getLastResultId(applicationContext)
        val items: List<GalleryItem> = if (query.isEmpty()) {
            FlickrRepository().fetchPhotos(DEFAULT_PAGE_NUMBER)
        } else {
            FlickrRepository().searchPhotos(query, DEFAULT_PAGE_NUMBER)
        }

        if (items.isEmpty()) return Result.success()

        val resultId = items.first().id
        if (resultId == lastResultId) {
            Log.i(TAG, "Got an old result: $resultId")
        } else {
            Log.i(TAG, "Got a new result: $resultId")
            QueryPreferences.setLastResultId(applicationContext, resultId)

            val resources = applicationContext.resources
            val notification =
                NotificationCompat.Builder(applicationContext, NOTIFICATION_CHANNEL_ID)
                    .setTicker(resources.getString(R.string.application_notification_title))
                    .setSmallIcon(android.R.drawable.ic_menu_report_image)
                    .setContentTitle(resources.getString(R.string.application_notification_title))
                    .setContentText(resources.getString(R.string.application_notification_message))
                    .setContentIntent(
                        PendingIntent.getActivity(
                            applicationContext,
                            0,
                            PhotoGalleryActivity.newIntent(applicationContext),
                            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE
                            else 0
                        )
                    )
                    .setAutoCancel(true)
                    .build()

            showBackgroundNotification(0, notification)
        }
        return Result.success()
    }

    private fun showBackgroundNotification(requestCode: Int, notification: Notification) {
        applicationContext.sendOrderedBroadcast(
            Intent(ACTION_SHOW_NOTIFICATION).apply {
                putExtras(
                    bundleOf(
                        REQUEST_CODE to requestCode,
                        NOTIFICATION to notification
                    )
                )
            },
            PERMISSION_PRIVATE
        )
    }

    companion object {
        private const val TAG = "PollWorker"

        private const val DEFAULT_PAGE_NUMBER = 1
    }
}
