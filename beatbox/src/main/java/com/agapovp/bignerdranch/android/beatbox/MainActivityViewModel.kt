package com.agapovp.bignerdranch.android.beatbox

import android.content.res.AssetManager
import androidx.lifecycle.ViewModel

class MainActivityViewModel(assets: AssetManager, rate: Float) : ViewModel() {

    val beatBox: BeatBox = BeatBox(assets).apply {
        this.rate = rate
    }

    override fun onCleared() {
        super.onCleared()
        beatBox.clear()
    }
}
