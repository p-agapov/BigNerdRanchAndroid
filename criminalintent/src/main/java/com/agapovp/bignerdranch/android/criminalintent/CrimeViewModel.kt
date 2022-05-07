package com.agapovp.bignerdranch.android.criminalintent

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import java.io.File
import java.util.*

class CrimeViewModel : ViewModel() {

    private val crimeRepository: CrimeRepository = CrimeRepository.get()

    private val crimeId: MutableLiveData<UUID> = MutableLiveData<UUID>()

    val crime: LiveData<Crime?> = Transformations.switchMap(crimeId) { crimeId ->
        crimeRepository.getCrime(crimeId)
    }

    fun loadCrime(crimeId: UUID) {
        this.crimeId.value = crimeId
    }

    fun saveCrime(crime: Crime) = crimeRepository.updateCrime(crime)

    fun getPhotoFile(crime: Crime): File = crimeRepository.getPhotoFile(crime)
}
