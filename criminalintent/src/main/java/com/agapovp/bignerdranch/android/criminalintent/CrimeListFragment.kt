package com.agapovp.bignerdranch.android.criminalintent

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class CrimeListFragment : Fragment() {

    private var callbacks: Callbacks? = null
    private lateinit var recycledViewCrimes: RecyclerView
    private lateinit var viewEmpty: LinearLayout
    private lateinit var buttonEmpty: Button

    private val viewModel: CrimeListViewModel by lazy {
        ViewModelProvider(this).get(CrimeListViewModel::class.java)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks?
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
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
        viewEmpty = view.findViewById<LinearLayout?>(R.id.fragment_crime_list_view_empty)
        buttonEmpty = view.findViewById<Button?>(R.id.fragment_crime_list_button_empty).apply {
            setOnClickListener {
                addCrime(Crime())
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val crimeItemAdapter = CrimeItemAdapter()
        recycledViewCrimes.adapter = crimeItemAdapter
        viewModel.crimes.observe(viewLifecycleOwner) { list ->
            list?.let { crimes ->
                Log.i(TAG, "Got crimes ${crimes.size}")
                crimes.isEmpty().let {
                    viewEmpty.isVisible = it
                    recycledViewCrimes.isVisible = !it
                }
                crimeItemAdapter.submitList(crimes)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_fragment_crime_list, menu)
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        when (item.itemId) {
            R.id.menu_fragment_crime_list_item_new_crime -> {
                addCrime(Crime())
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    private fun addCrime(crime: Crime) {
        callbacks?.onCrimeSelected(crime.id)
        viewModel.addCrime(crime)
    }

    interface Callbacks {
        fun onCrimeSelected(crimeId: UUID)
    }

    private inner class CrimeItemHolder(view: View) : BaseCrimeItemHolder(view),
        View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        private val textTitle: TextView = itemView.findViewById(R.id.item_crime_text_title)
        private val textDate: TextView = itemView.findViewById(R.id.item_crime_text_date)
        private val imageSolved: ImageView = itemView.findViewById(R.id.item_crime_image_solved)
        private lateinit var crime: Crime

        override fun bind(crime: Crime) {
            this.crime = crime
            textTitle.text = crime.title
            textDate.text = crime.date.toString()
            imageSolved.visibility = if (crime.isSolved) View.VISIBLE else View.GONE
        }

        override fun onClick(view: View) {
            callbacks?.onCrimeSelected(crime.id)
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
        private val imageSolved: ImageView = itemView.findViewById(R.id.item_crime_image_solved)
        private lateinit var crime: Crime

        override fun bind(crime: Crime) {
            this.crime = crime
            textTitle.text = crime.title
            textDate.text = crime.date.toString()
            buttonPolice.run {
                isEnabled = !crime.isSolved
                setOnClickListener {
                    Toast.makeText(context, "${crime.title} sent to police!", Toast.LENGTH_LONG)
                        .show()
                }
            }
            imageSolved.visibility = if (crime.isSolved) View.VISIBLE else View.GONE
        }

        override fun onClick(view: View) {
            callbacks?.onCrimeSelected(crime.id)
        }
    }

    private inner class CrimeItemAdapter :
        ListAdapter<Crime, BaseCrimeItemHolder>(CRIME_ITEM_CALLBACK) {

        override fun getItemViewType(position: Int) = when {
            getItem(position).requiresPolice -> 1
            else -> 0
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseCrimeItemHolder =
            when (viewType) {
                1 -> CrimePoliceItemHolder(
                    layoutInflater.inflate(
                        R.layout.item_crime_police,
                        parent,
                        false
                    )
                )
                else -> CrimeItemHolder(
                    layoutInflater.inflate(
                        R.layout.item_crime,
                        parent,
                        false
                    )
                )
            }

        override fun onBindViewHolder(holder: BaseCrimeItemHolder, position: Int) {
            holder.bind(getItem(position))
        }
    }

    private abstract inner class BaseCrimeItemHolder(view: View) : RecyclerView.ViewHolder(view) {
        abstract fun bind(crime: Crime)
    }

    companion object {

        private const val TAG = "CrimeListFragment"

        private val CRIME_ITEM_CALLBACK: DiffUtil.ItemCallback<Crime> =
            object : DiffUtil.ItemCallback<Crime>() {
                override fun areItemsTheSame(oldItem: Crime, newItem: Crime): Boolean =
                    oldItem.id == newItem.id

                override fun areContentsTheSame(oldItem: Crime, newItem: Crime): Boolean =
                    oldItem == newItem
            }

        fun newInstance(): CrimeListFragment = CrimeListFragment()
    }
}
