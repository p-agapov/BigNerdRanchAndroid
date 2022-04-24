package com.agapovp.bignerdranch.android.criminalintent

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.core.os.bundleOf
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import java.text.SimpleDateFormat
import java.util.*

class CrimeFragment : Fragment() {

    private lateinit var crime: Crime

    private lateinit var editTextTitle: EditText
    private lateinit var buttonDate: Button
    private lateinit var checkBoxSolved: CheckBox
    private lateinit var checkBoxRequiresPolice: CheckBox

    private val viewModel: CrimeViewModel by lazy {
        ViewModelProvider(this).get(CrimeViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crime = Crime()
        viewModel.loadCrime(arguments?.getSerializable(ARG_CRIME_ID) as UUID)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_crime, container, false).also { view ->
        editTextTitle = view.findViewById(R.id.fragment_crime_edittext_title)
        buttonDate = view.findViewById<Button>(R.id.fragment_crime_button_date).apply {
            text = dateFormatter.format(crime.date)
            isEnabled = false
        }
        checkBoxSolved = view.findViewById(R.id.fragment_crime_checkbox_solved)
        checkBoxRequiresPolice = view.findViewById(R.id.fragment_crime_checkbox_requires_police)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.crime.observe(viewLifecycleOwner) { crime ->
            crime?.let {
                this.crime = crime
                updateUI()
            }
        }
    }

    override fun onStart() {
        super.onStart()

        editTextTitle.doOnTextChanged { text, _, _, _ ->
            crime.title = text.toString()
        }
        checkBoxSolved.setOnCheckedChangeListener { _, isChecked ->
            crime.isSolved = isChecked
        }

        checkBoxRequiresPolice.setOnCheckedChangeListener { _, isChecked ->
            crime.requiresPolice = isChecked
        }
    }

    override fun onStop() {
        super.onStop()
        viewModel.saveCrime(crime)
    }

    private fun updateUI() {
        editTextTitle.setText(crime.title)
        buttonDate.text = dateFormatter.format(crime.date)
        checkBoxSolved.run {
            isChecked = crime.isSolved
            jumpDrawablesToCurrentState()
        }
        checkBoxRequiresPolice.run {
            isChecked = crime.requiresPolice
            jumpDrawablesToCurrentState()
        }
    }

    companion object {

        private const val TAG = "CrimeFragment"
        private const val ARG_CRIME_ID = "${TAG}_ARG_CRIME_ID"

        private val dateFormatter = SimpleDateFormat("EEEE, MMM dd, yyyy", Locale.US)

        fun newInstance(crimeId: UUID): CrimeFragment =
            CrimeFragment().apply {
                arguments = bundleOf(
                    ARG_CRIME_ID to crimeId
                )
            }
    }
}
