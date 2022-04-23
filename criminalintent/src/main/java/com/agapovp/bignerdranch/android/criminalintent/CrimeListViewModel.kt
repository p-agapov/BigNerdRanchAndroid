package com.agapovp.bignerdranch.android.criminalintent

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel

class CrimeListViewModel : ViewModel() {

    val crimes: LiveData<List<Crime>> = CrimeRepository.get().getCrimes()
}
