package com.romanlojko.beactive

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RecycleViewAdapter(private val activityList: ArrayList<UserActivity>): RecyclerView.Adapter<RecycleViewAdapter.MyViewHolder> {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        TODO()
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        TODO()
    }

    override fun getItemCount(): Int {
        return activityList.size
    }


    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val caloriesBurned: TextView = itemView.findViewById(R.id.textViewBurnedCalories)
        val totalSteps: TextView = itemView.findViewById(R.id.textViewSteps)
        val

    }

}