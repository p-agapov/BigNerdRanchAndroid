package com.agapovp.bignerdranch.android.photogallery

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import java.util.concurrent.ConcurrentHashMap

class ThumbnailDownloader<in T>(
    private val responseHandler: Handler,
    private val onThumbnailDownloaded: (T, Bitmap) -> Unit
) : HandlerThread(TAG) {

    var fragmentLifecycleObserver: LifecycleObserver = object : DefaultLifecycleObserver {
        override fun onCreate(owner: LifecycleOwner) {
            setup()
        }

        override fun onDestroy(owner: LifecycleOwner) {
            tearDown()
        }
    }

//    var viewLifecycleObserver = object : DefaultLifecycleObserver {
//        override fun onDestroy(owner: LifecycleOwner) {
//            Log.d(TAG, "Clearing all requests from queue")
//            requestHandler.removeMessages(MESSAGE_DOWNLOAD)
//            requestMap.clear()
//        }
//    }

    private val requestMap = ConcurrentHashMap<T, String>()
    private val flickrRepository = FlickrRepository()

    private var hasQuit = false

    private lateinit var requestHandler: Handler

    @Suppress("UNCHECKED_CAST")
    @SuppressLint("HandlerLeak")
    override fun onLooperPrepared() {
        requestHandler = object : Handler() {
            override fun handleMessage(msg: Message) {
                if (msg.what == MESSAGE_DOWNLOAD) {
                    val target = msg.obj as T
                    Log.d(TAG, "Got a request for URL: ${requestMap[target]}")
                    handleRequest(target)
                }
            }
        }
    }

    override fun quit(): Boolean {
        hasQuit = true
        return super.quit()
    }

    fun queueThumbnail(target: T, url: String?) {
        Log.d(TAG, "Got a URL: $url")
        url?.let {
            requestMap[target] = it
            requestHandler.obtainMessage(MESSAGE_DOWNLOAD, target).sendToTarget()
        }
    }

    fun clearQueue() {
        Log.d(TAG, "Clearing all requests from queue")
        requestHandler.removeMessages(MESSAGE_DOWNLOAD)
        requestMap.clear()
    }

    private fun setup() {
        Log.d(TAG, "Starting background thread")
        start()
        looper
    }

    private fun tearDown() {
        Log.d(TAG, "Destroying background thread")
        quit()
    }

    private fun handleRequest(target: T) {
        val url = requestMap[target] ?: return
        val bitmap = flickrRepository.fetchPhoto(url) ?: return
        responseHandler.post(Runnable {
            if (requestMap[target] != url || hasQuit) {
                return@Runnable
            }
            requestMap.remove(target)
            onThumbnailDownloaded(target, bitmap)
        })
    }

    companion object {

        private const val TAG = "ThumbnailDownloader"

        private const val MESSAGE_DOWNLOAD = 0
    }
}
