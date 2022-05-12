package com.agapovp.bignerdranch.android.beatbox

import android.content.res.AssetManager
import android.util.Log
import java.io.IOException

class BeatBox(private val assets: AssetManager) {

    val sounds: List<Sound>

    init {
        sounds = loadSounds()
    }

    private fun loadSounds(): List<Sound> {
        return try {
            assets.list(SOUNDS_FOLDER)?.map { soundName ->
                Sound("$SOUNDS_FOLDER/$soundName")
            } ?: throw IOException()
        } catch (e: Exception) {
            Log.e(TAG, "Could not list assets.", e)
            return emptyList()
        }
    }

    companion object {

        private const val TAG = "BeatBox"

        private const val SOUNDS_FOLDER = "sounds"
    }
}
