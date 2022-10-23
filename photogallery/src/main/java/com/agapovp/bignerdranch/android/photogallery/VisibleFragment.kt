package com.agapovp.bignerdranch.android.photogallery

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import androidx.fragment.app.Fragment

abstract class VisibleFragment : Fragment() {

    private val onShowNotificationReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent) {
            Log.i(TAG, "Canceling notification")
            resultCode = Activity.RESULT_CANCELED
        }
    }

    override fun onStart() {
        super.onStart()

        requireActivity().registerReceiver(
            onShowNotificationReceiver,
            IntentFilter(ACTION_SHOW_NOTIFICATION),
            PERMISSION_PRIVATE,
            null
        )
    }

    override fun onStop() {
        super.onStop()

        requireActivity().unregisterReceiver(onShowNotificationReceiver)
    }

    companion object {
        private const val TAG = "VisibleFragment"
    }
}
