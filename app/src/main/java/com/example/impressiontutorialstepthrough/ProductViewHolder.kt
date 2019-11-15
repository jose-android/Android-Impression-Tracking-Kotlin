package com.example.impressiontutorialstepthrough

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.TextView

class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val mTitleTextView: TextView

    init {
        mTitleTextView = itemView.findViewById(R.id.title_textview) as TextView
    }
}
