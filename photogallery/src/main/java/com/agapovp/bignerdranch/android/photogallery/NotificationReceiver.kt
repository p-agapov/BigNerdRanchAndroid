package com.agapovp.bignerdranch.android.photogallery

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationManagerCompat

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.i(TAG, "Received result: $resultCode")
        if (resultCode != Activity.RESULT_OK) return

        NotificationManagerCompat.from(context)
            .notify(
                intent.getIntExtra(REQUEST_CODE, 0),
                intent.getParcelableExtra(NOTIFICATION) ?: return
            )
    }

    companion object {
        private const val TAG = "NotificationReceiver"
    }
}
