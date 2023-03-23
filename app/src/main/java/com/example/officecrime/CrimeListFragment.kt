@file:Suppress("DEPRECATION")

package com.example.officecrime

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import java.util.UUID

private const val TAG = "CrimeListFragment"

class CrimeListFragment : Fragment() {

    interface Callbacks{
        fun onCrimeSelected(crimeId:UUID)
    }

    private var callbacks:Callbacks? = null
    private lateinit var crimeRecyclerView: RecyclerView
    private var adapter: CrimeAdapter? = CrimeAdapter()

    private val crimeListViewModel: CrimeListViewModel by lazy {
        ViewModelProviders
            .of(this)[CrimeListViewModel::class.java]
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
    ): View? {
        val view = inflater.inflate(
            R.layout.fragment_crime_list,
            container,
            false
        )
        crimeRecyclerView =
            view.findViewById(R.id.crime_recycler_view) as RecyclerView
        crimeRecyclerView.layoutManager = LinearLayoutManager(context)
        crimeRecyclerView.adapter = adapter
//        crimeListViewModel.crimeListLiveData.observe(this,
//            Observer {
//                adapter.submitList(List<Crime>)  }
//
//        )
//        crimeRecyclerView.adapter
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    crimeListViewModel.crimeListLiveData.observe(
        viewLifecycleOwner,
        Observer { crimes ->
            crimes?.let{
                Log.i(TAG,"Got crimes ${crimes.size}")
                updateUI(crimes)
            }
        }
    )
    }



    private fun updateUI(crimes:List<Crime>) {
        (crimeRecyclerView.adapter as CrimeAdapter).submitList(crimes)
    }

    private inner class CrimeHolder(view: View)
        : RecyclerView.ViewHolder(view),View.OnClickListener {

        private lateinit var crime: Crime

        val titleTextView: TextView = itemView.findViewById(R.id.crime_title)
        val dateTextView: TextView = itemView.findViewById(R.id.crime_date)
        private val solvedImageView: ImageView = itemView.findViewById(R.id.crime_solved)

        init{
            itemView.setOnClickListener(this)
        }

        override fun onClick(v:View){
          callbacks?.onCrimeSelected(crime.id)
        }

        fun bind(crime: Crime) {
            this.crime = crime
            titleTextView.text = this.crime.title
            dateTextView.text = this.crime.date.toString()
            solvedImageView.visibility = if (crime.isSolved) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }

    }


    private inner class CrimeAdapter:
        ListAdapter<Crime,CrimeHolder>(ItemComparator()) {
        //Лучшая часть использования ListAdapter заключается в том, что вам не нужно предоставлять список данных
        override fun onCreateViewHolder(
            parent: ViewGroup, viewType: Int
        )
                : CrimeHolder {
            val view = layoutInflater.inflate(
                R.layout.list_item_crime,
                parent,
                false
            )
            return CrimeHolder(view)
        }

        override fun onBindViewHolder(
            holder: CrimeHolder,
            position: Int
        ) {
            holder.bind(getItem(position))
        }
        }
    class ItemComparator:DiffUtil.ItemCallback<Crime>(){
        override fun areItemsTheSame(oldItem: Crime, newItem: Crime): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Crime, newItem: Crime): Boolean {
            return oldItem == newItem
        }
    }


    companion object {
        fun newInstance(): CrimeListFragment {
            return CrimeListFragment()
        }
    }
    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_crime_list,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            R.id.new_crime -> {
                val crime = Crime()
                crimeListViewModel.addCrime(crime)
                callbacks?.onCrimeSelected(crime.id)
                true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
}
