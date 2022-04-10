package com.agapovp.bignerdranch.android.criminalintent

import java.util.*

data class Crime(
    val id: UUID = UUID.randomUUID(),
    var title: String = "",
    var date: Date = Date(),
    var isSolved: Boolean = false,
    var requiresPolice: Boolean = Random().nextBoolean()
)
