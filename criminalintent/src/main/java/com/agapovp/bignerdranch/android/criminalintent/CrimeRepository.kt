package com.agapovp.bignerdranch.android.criminalintent

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.agapovp.bignerdranch.android.criminalintent.database.CrimeDao
import com.agapovp.bignerdranch.android.criminalintent.database.CrimeDatabase
import com.agapovp.bignerdranch.android.criminalintent.database.migration_1_2
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CrimeRepository private constructor(context: Context) {

    private val database: CrimeDatabase = Room.databaseBuilder(
        context.applicationContext,
        CrimeDatabase::class.java,
        DATABASE_NAME
    ).addMigrations(migration_1_2)
        .build()

    private val crimeDao: CrimeDao = database.crimeDao()
    private val executor: ExecutorService = Executors.newSingleThreadExecutor()

    fun getCrime(id: UUID): LiveData<Crime?> = crimeDao.getCrime(id)

    fun getCrimes(): LiveData<List<Crime>> = crimeDao.getCrimes()

    fun addCrime(crime: Crime) {
        executor.execute {
            crimeDao.addCrime(crime)
        }
    }

    fun updateCrime(crime: Crime) {
        executor.execute {
            crimeDao.updateCrime(crime)
        }
    }

    companion object {

        private const val DATABASE_NAME = "crime-database"

        private var INSTANCE: CrimeRepository? = null

        fun initialize(context: Context) {
            if (INSTANCE == null) INSTANCE = CrimeRepository(context)
        }

        fun get(): CrimeRepository =
            INSTANCE ?: throw IllegalStateException("CrimeRepository must be initialized")
    }
}
