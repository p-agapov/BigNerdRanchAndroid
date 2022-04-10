package com.agapovp.bignerdranch.android.criminalintent

import androidx.lifecycle.ViewModel

class CrimeListViewModel : ViewModel() {

    val crimes = MutableList(100) {
        Crime().apply {
            title = "Crime #${it}"
            isSolved = it % 2 == 0
        }
    }
}
