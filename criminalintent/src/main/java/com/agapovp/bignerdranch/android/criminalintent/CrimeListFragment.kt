package com.agapovp.bignerdranch.android.criminalintent

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

private const val TAG = "CrimeListFragment"

class CrimeListFragment : Fragment() {

    private lateinit var recycledViewCrimes: RecyclerView

    private val viewModel: CrimeListViewModel by lazy {
        ViewModelProvider(this).get(CrimeListViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "Total crimes: ${viewModel.crimes.size}")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_crime_list, container, false).also { view ->
        recycledViewCrimes =
            view.findViewById<RecyclerView?>(R.id.fragment_crime_list_recyclerview_crimes).apply {
                layoutManager = LinearLayoutManager(context)
            }
        updateUI()
    }

    private inner class CrimeItemHolder(view: View) : BaseCrimeItemHolder(view),
        View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        private val textTitle: TextView = itemView.findViewById(R.id.item_crime_text_title)
        private val textDate: TextView = itemView.findViewById(R.id.item_crime_text_date)

        override fun bind(crime: Crime) {
            textTitle.text = crime.title
            textDate.text = crime.date.toString()
        }

        override fun onClick(view: View) {
            Toast.makeText(context, "${textTitle.text} pressed!", Toast.LENGTH_SHORT).show()
        }
    }

    private inner class CrimePoliceItemHolder(view: View) : BaseCrimeItemHolder(view),
        View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        private val textTitle: TextView = itemView.findViewById(R.id.item_crime_text_title)
        private val textDate: TextView = itemView.findViewById(R.id.item_crime_text_date)
        private val buttonPolice: Button =
            itemView.findViewById(R.id.item_crime_police_button_police)

        override fun bind(crime: Crime) {
            textTitle.text = crime.title
            textDate.text = crime.date.toString()
            buttonPolice.setOnClickListener {
                Toast.makeText(context, "${crime.title} sent to police!", Toast.LENGTH_LONG).show()
            }
        }

        override fun onClick(view: View) {
            Toast.makeText(context, "${textTitle.text} pressed!", Toast.LENGTH_SHORT).show()
        }
    }

    private inner class CrimeItemAdapter(val crimes: List<Crime>) :
        RecyclerView.Adapter<BaseCrimeItemHolder>() {

        override fun getItemViewType(position: Int) = when {
            crimes[position].requiresPolice -> 1
            else -> 0
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseCrimeItemHolder =
            when (viewType) {
                1 -> CrimeItemHolder(layoutInflater.inflate(R.layout.item_crime, parent, false))
                else -> CrimePoliceItemHolder(
                    layoutInflater.inflate(
                        R.layout.item_crime_police,
                        parent,
                        false
                    )
                )
            }

        override fun onBindViewHolder(holder: BaseCrimeItemHolder, position: Int) {
            holder.bind(crimes[position])
        }

        override fun getItemCount(): Int = crimes.size
    }

    private fun updateUI() {
        recycledViewCrimes.adapter = CrimeItemAdapter(viewModel.crimes)
    }

    private abstract inner class BaseCrimeItemHolder(view: View) : RecyclerView.ViewHolder(view) {
        abstract fun bind(crime: Crime)
    }

    companion object {
        fun newInstance() = CrimeListFragment()
    }
}
