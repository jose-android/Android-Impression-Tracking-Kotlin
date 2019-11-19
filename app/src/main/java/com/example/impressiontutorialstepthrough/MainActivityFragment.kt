package com.example.impressiontutorialstepthrough

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.impressiontutorialstepthrough.analytic.TrackingData
import com.example.impressiontutorialstepthrough.analytic.ViewTracker
import com.example.impressiontutorialstepthrough.model.DataModel

class MainActivityFragment : Fragment(), ViewTracker.Listener {
    private var mRecyclerView: RecyclerView? = null
    private var mDataSet = ArrayList<DataModel>()
    private var viewTracker = ViewTracker()

    private val mockData: ArrayList<DataModel>
        get() {
            val data = ArrayList<DataModel>()
            for (i in 0..249) {
                val item = "Title $i"
                data.add(DataModel(item))

            }
            return data
        }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_main, container, false)

        mRecyclerView = rootView.findViewById(R.id.recycler_view) as RecyclerView
        mDataSet = mockData

        // mRecyclerView?.layoutManager = GridLayoutManager(context, 2)
        mRecyclerView?.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        mRecyclerView?.adapter = ImpressionAdapter(requireActivity(), mDataSet)
        mRecyclerView?.isNestedScrollingEnabled = false

        viewTracker.recyclerView = mRecyclerView
        viewTracker.listener = this
        viewTracker.startTracking()

        return rootView
    }

    override fun viewsViewed(viewsViewed: ArrayList<Int>) {
        Log.i("MainActivityFragment", "viewId: $viewsViewed")
    }

    override fun onDestroy() {
        super.onDestroy()
        viewTracker.stopTracking()
    }
}