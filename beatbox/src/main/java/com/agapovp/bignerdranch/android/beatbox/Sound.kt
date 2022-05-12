package com.agapovp.bignerdranch.android.beatbox

class Sound(path: String) {

    val name = path.split('/').last().removeSuffix(EXTENSION_WAV)

    companion object {

        private const val EXTENSION_WAV = ".wav"
    }
}
