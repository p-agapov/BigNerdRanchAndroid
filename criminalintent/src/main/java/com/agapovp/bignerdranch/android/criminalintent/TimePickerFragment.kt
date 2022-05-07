package com.agapovp.bignerdranch.android.criminalintent

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import java.util.*

class TimePickerFragment : DialogFragment() {

    private lateinit var requestKey: String

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        isCancelable = false

        requestKey = arguments?.getString(ARG_REQUEST_KEY) as String

        val calendar: Calendar = Calendar.getInstance()
        val currentHour: Int = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute: Int = calendar.get(Calendar.MINUTE)

        calendar.time = arguments?.getSerializable(ARG_TIME) as Date

        val initialYear: Int = calendar.get(Calendar.YEAR)
        val initialMonth: Int = calendar.get(Calendar.MONTH)
        val initialDay: Int = calendar.get(Calendar.DAY_OF_MONTH)

        val onDateSetListener: TimePickerDialog.OnTimeSetListener =
            TimePickerDialog.OnTimeSetListener { _, hourOfDay, minuteOfHour ->
                val selectedDate: Date = GregorianCalendar(
                    initialYear,
                    initialMonth,
                    initialDay,
                    hourOfDay,
                    minuteOfHour
                ).time
                setFragmentResult(
                    requestKey,
                    bundleOf(requestKey to selectedDate)
                )
            }

        return TimePickerDialog(
            requireContext(),
            onDateSetListener,
            currentHour,
            currentMinute,
            true
        )
    }

    companion object {

        private const val TAG = "TimePickerFragment"
        private const val ARG_TIME = "${TAG}_ARG_TIME"
        private const val ARG_REQUEST_KEY = "${TAG}_ARG_REQUEST_KEY"

        fun newInstance(date: Date, requestKey: String): TimePickerFragment =
            TimePickerFragment().apply {
                arguments = bundleOf(
                    ARG_TIME to date,
                    ARG_REQUEST_KEY to requestKey
                )
            }
    }
}
