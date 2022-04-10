package com.agapovp.bignerdranch.android.criminalintent

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.findFragmentById(R.id.activity_main_container)
            ?: supportFragmentManager
                .beginTransaction()
                .add(R.id.activity_main_container, CrimeFragment())
                .commit()
    }
}
