package com.agapovp.bignerdranch.android.beatbox

import android.content.res.AssetFileDescriptor
import android.content.res.AssetManager
import android.media.SoundPool
import android.util.Log
import java.io.IOException

class BeatBox(private val assets: AssetManager) {

    val sounds: List<Sound>

    var rate: Float = 1.0f

    private val soundPool: SoundPool = SoundPool.Builder()
        .setMaxStreams(MAX_STREAMS)
        .build()

    init {
        sounds = loadSounds()
    }

    fun play(sound: Sound) {
        sound.soundId?.let {
            soundPool.play(it, 1.0F, 1.0F, 1, 0, rate)
        }
    }

    fun clear() {
        soundPool.release()
    }

    private fun loadSounds(): List<Sound> {
        return try {
            assets.list(SOUNDS_FOLDER)?.map { soundName ->
                Sound("$SOUNDS_FOLDER/$soundName").apply {
                    load(this)
                }
            } ?: throw IOException()
        } catch (e: IOException) {
            Log.e(TAG, "Could not load sound.", e)
            return emptyList()
        }
    }

    private fun load(sound: Sound) {
        val aFileDescriptor: AssetFileDescriptor = assets.openFd(sound.path)
        sound.soundId = soundPool.load(aFileDescriptor, 1)
    }

    companion object {

        private const val TAG = "BeatBox"

        private const val MAX_STREAMS = 5
        private const val SOUNDS_FOLDER = "sounds"
    }
}
