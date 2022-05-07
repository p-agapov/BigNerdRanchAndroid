package com.agapovp.bignerdranch.android.criminalintent

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import java.util.*

class DatePickerFragment : DialogFragment() {

    private lateinit var requestKey: String

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        isCancelable = false

        requestKey = arguments?.getString(ARG_REQUEST_KEY) as String
        val calendar: Calendar = Calendar.getInstance().apply {
            time = arguments?.getSerializable(ARG_DATE) as Date
        }

        val initialYear: Int = calendar.get(Calendar.YEAR)
        val initialMonth: Int = calendar.get(Calendar.MONTH)
        val initialDay: Int = calendar.get(Calendar.DAY_OF_MONTH)
        val initialHour: Int = calendar.get(Calendar.HOUR_OF_DAY)
        val initialMinute: Int = calendar.get(Calendar.MINUTE)

        val onDateSetListener: DatePickerDialog.OnDateSetListener =
            DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                val selectedDate: Date =
                    GregorianCalendar(year, month, dayOfMonth, initialHour, initialMinute).time
                setFragmentResult(
                    requestKey,
                    bundleOf(requestKey to selectedDate)
                )
            }

        return DatePickerDialog(
            requireContext(),
            onDateSetListener,
            initialYear,
            initialMonth,
            initialDay
        )
    }

    companion object {

        private const val TAG = "DatePickerFragment"
        private const val ARG_DATE = "${TAG}_ARG_DATE"
        private const val ARG_REQUEST_KEY = "${TAG}_ARG_REQUEST_KEY"

        fun newInstance(date: Date, requestKey: String): DatePickerFragment =
            DatePickerFragment().apply {
                arguments = bundleOf(
                    ARG_DATE to date,
                    ARG_REQUEST_KEY to requestKey
                )
            }
    }
}
