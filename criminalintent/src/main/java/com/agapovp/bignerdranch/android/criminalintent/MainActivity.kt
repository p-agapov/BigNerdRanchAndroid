package com.agapovp.bignerdranch.android.criminalintent

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class MainActivity : AppCompatActivity(), CrimeListFragment.Callbacks {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.findFragmentById(R.id.activity_main_container)
            ?: supportFragmentManager
                .beginTransaction()
                .add(R.id.activity_main_container, CrimeListFragment.newInstance())
                .commit()
    }

    override fun onCrimeSelected(crimeId: UUID) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.activity_main_container, CrimeFragment.newInstance(crimeId))
            .addToBackStack(null)
            .commit()
    }

    companion object {

        private const val TAG = "MainActivity"
    }
}
