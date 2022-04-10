package com.agapovp.bignerdranch.android.criminalintent

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment

class CrimeFragment : Fragment() {

    private lateinit var crime: Crime

    private lateinit var editTextTitle: EditText
    private lateinit var buttonDate: Button
    private lateinit var checkBoxSolved: CheckBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crime = Crime()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_crime, container, false).also { view ->
        editTextTitle = view.findViewById(R.id.fragment_crime_edittext_title)
        buttonDate = view.findViewById<Button>(R.id.fragment_crime_button_date).apply {
            text = crime.date.toString()
            isEnabled = false
        }
        checkBoxSolved = view.findViewById(R.id.fragment_crime_checkbox_solved)
    }

    override fun onStart() {
        super.onStart()

        editTextTitle.doOnTextChanged { text, _, _, _ ->
            crime.title = text.toString()
        }
        checkBoxSolved.setOnCheckedChangeListener { _, isChecked ->
            crime.isSolved = isChecked
        }
    }
}
