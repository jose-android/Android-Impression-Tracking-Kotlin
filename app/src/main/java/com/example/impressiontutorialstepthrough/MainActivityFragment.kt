package com.example.impressiontutorialstepthrough

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class MainActivityFragment : Fragment() {
    private var mRecyclerView: RecyclerView? = null
    private var mDataSet = ArrayList<String>()

    private val mockData: ArrayList<String>
        get() {
            val data = ArrayList<String>()
            for (i in 0..9) {
                val item = "Title $i"
                data.add(item)

            }
            return data
        }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_main, container, false)

        mRecyclerView = rootView.findViewById(R.id.recycler_view) as RecyclerView
        mDataSet = mockData

        mRecyclerView!!.setLayoutManager(GridLayoutManager(context, 2))
        mRecyclerView!!.adapter = ImpressionAdapter(requireActivity(), mDataSet)

        return rootView
    }
}