package com.agapovp.bignerdranch.android.photogallery

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

class PhotoGalleryApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getSystemService(NotificationManager::class.java)
                .createNotificationChannel(
                    NotificationChannel(
                        NOTIFICATION_CHANNEL_ID,
                        getString(R.string.application_notification_chanel_name),
                        NotificationManager.IMPORTANCE_DEFAULT
                    )
                )
        }
    }

    companion object {
        const val NOTIFICATION_CHANNEL_ID = "new_pictures_poll"
    }
}
