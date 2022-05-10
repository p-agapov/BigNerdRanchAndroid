package com.agapovp.bignerdranch.android.criminalintent.utils

import android.view.View
import java.text.SimpleDateFormat
import java.util.*

fun View.setSafeOnClickListener(onSafeClick: (View) -> Unit) {

    val safeClickListener = SafeClickListener {
        onSafeClick(it)
    }

    setOnClickListener(safeClickListener)
}

fun Date.formatToLocalizeDate(): String =
    SimpleDateFormat.getDateTimeInstance().format(this)

fun Date.formatToLocalizePrettyDate(): String =
    SimpleDateFormat("HH:mm EEEE, MMM dd, yyyy", Locale.getDefault()).format(this)
