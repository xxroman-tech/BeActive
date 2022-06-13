package com.romanlojko.beactive

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.romanlojko.beactive.Objects.UserActivity

/**
 * Adapter pre recyclerView
 * @author Roman Lojko
 */
class RecycleViewAdapter(private val activityList: ArrayList<UserActivity>):
    RecyclerView.Adapter<RecycleViewAdapter.MyViewHolder>() {

    /**
     * Lyfecycle metoda inicializuje layout, carditem v recyclerViewe
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.activity_item,
        parent, false)
        return MyViewHolder(itemView)

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val currentItem = activityList[position]

        holder.typeOfActivity.text = currentItem.getTypeOfActivity()
        holder.caloriesBurned.text = currentItem.getCaloriesBurned().toString()
        holder.timeofActivity.text = currentItem.getTimeOfActivity().toString()
        holder.totalSteps.text = currentItem.getTotalSteps().toString()

    }

    /**
     * Metoda vrati pocet itemov ktore chcem vlozit do recyclerViewu
     * @return activityList.size
     */
    override fun getItemCount(): Int {
        return activityList.size
    }


    /**
     * Trieda predstavuje udaje ktore budu jednotlive karty obsahovat v samotnom
     * recylcerViewe
     */
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val caloriesBurned: TextView = itemView.findViewById(R.id.textViewBurnedCalories)
        val totalSteps: TextView = itemView.findViewById(R.id.textViewSteps)
        val timeofActivity: TextView = itemView.findViewById(R.id.textViewTime)
        val typeOfActivity: TextView = itemView.findViewById(R.id.textViewTypeOfActivity)

    }

}