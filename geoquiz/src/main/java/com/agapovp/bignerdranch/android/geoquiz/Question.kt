package com.agapovp.bignerdranch.android.geoquiz

import androidx.annotation.StringRes

data class Question(
    @StringRes
    val textResId: Int,
    val answer: Boolean,
    var isActive: Boolean = true,
    var isCheated: Boolean = false
)
