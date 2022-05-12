package com.agapovp.bignerdranch.android.beatbox

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class SoundItemViewModel(private val beatBox: BeatBox) {

    val title: LiveData<String?>
        get() = _title

    var sound: Sound? = null
        set(sound) {
            field = sound
            _title.postValue(sound?.name)
        }

    private val _title: MutableLiveData<String?> = MutableLiveData()

    fun onButtonClicked() {
        sound?.let { beatBox.play(it) }
    }
}
