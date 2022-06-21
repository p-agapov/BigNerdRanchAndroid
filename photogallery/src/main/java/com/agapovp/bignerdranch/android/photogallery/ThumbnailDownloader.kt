package com.agapovp.bignerdranch.android.photogallery

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.os.Message
import android.util.Log
import android.util.LruCache
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentSkipListSet

class ThumbnailDownloader<in T : Any>(
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
    private val preLoadSet = ConcurrentSkipListSet<String>()
    private val flickrRepository = FlickrRepository()
    private val cache = object : LruCache<String, Bitmap>(DEFAULT_CACHE_SIZE) {
        override fun sizeOf(key: String, value: Bitmap): Int =
            super.sizeOf(key, value)//value.byteCount
    }

    private var hasQuit = false

    private lateinit var requestHandler: Handler

    @Suppress("UNCHECKED_CAST")
    @SuppressLint("HandlerLeak")
    override fun onLooperPrepared() {
        val looper = Looper.myLooper()
            ?: throw IllegalStateException("Current thread is not associated with a Looper")
        requestHandler = object : Handler(looper) {
            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    MESSAGE_DOWNLOAD -> {
                        val target = msg.obj as T
                        Log.d(TAG, "Got a request for URL: ${requestMap[target]}")
                        handleRequest(target)
                    }
                    MESSAGE_PRELOAD -> {
                        val target = msg.obj as String
                        Log.d(TAG, "Got a preload request for URL: $target")
                        handlePreload(target)
                    }
                }
            }
        }
    }

    override fun quit(): Boolean {
        hasQuit = true
        return super.quit()
    }

    fun queueThumbnail(target: T, url: String?) {
        url?.let {
            requestMap[target] = it
            requestHandler.obtainMessage(MESSAGE_DOWNLOAD, target).sendToTarget()
        }
    }

    fun preloadThumbnail(url: String?) {
        url?.let {
            if (!preLoadSet.contains(it)) {
                preLoadSet.add(url)
                requestHandler.obtainMessage(MESSAGE_PRELOAD, url).sendToTarget()
            }
        }
    }

    fun clearQueue() {
        Log.d(TAG, "Clearing all requests from queue")
        requestHandler.removeMessages(MESSAGE_DOWNLOAD)
        requestHandler.removeMessages(MESSAGE_PRELOAD)
        requestMap.clear()
        preLoadSet.clear()
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
        var bitmap = cache[url]
        if (bitmap == null) {
            bitmap = flickrRepository.fetchPhoto(url) ?: return
            cache.put(url, bitmap)
            Log.d(TAG, "Cached: $url, total size: ${cache.size()} of ${cache.maxSize()}")
        } else {
            Log.d(TAG, "Loaded from cache: $url, total size: ${cache.size()} of ${cache.maxSize()}")
        }
        responseHandler.post(Runnable {
            if (hasQuit || requestMap[target] != url) {
                return@Runnable
            }
            requestMap.remove(target)
            onThumbnailDownloaded(target, bitmap)
        })
    }

    private fun handlePreload(url: String) {
        if (hasQuit || cache[url] != null) {
            Log.d(TAG, "Already in cache: $url")
            return
        }
        val bitmap = flickrRepository.fetchPhoto(url) ?: return
        cache.put(url, bitmap)
        preLoadSet.remove(url)
        Log.d(TAG, "Preloaded in cache: $url")
    }

    companion object {

        private const val TAG = "ThumbnailDownloader"

        private const val MESSAGE_DOWNLOAD = 0
        private const val MESSAGE_PRELOAD = 1

        private const val DEFAULT_CACHE_SIZE = 200
    }
}
