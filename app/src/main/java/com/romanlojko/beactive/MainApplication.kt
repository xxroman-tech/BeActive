package com.romanlojko.beactive

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.romanlojko.beactive.Objects.DataHolder
import com.romanlojko.beactive.Objects.UserActivity
import com.romanlojko.beactive.databinding.FragmentMainApplicationBinding
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MainApplication : Fragment() {

    private lateinit var binding: FragmentMainApplicationBinding
    private lateinit var myAuthorization: FirebaseAuth
    private lateinit var dbRef: DatabaseReference
    private lateinit var activitiesRecycleView: RecyclerView
    private lateinit var activityList: ArrayList<UserActivity>

    private var date: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentMainApplicationBinding.inflate(layoutInflater)

        myAuthorization = FirebaseAuth.getInstance()

        activitiesRecycleView = binding.activityZoznam
        activitiesRecycleView.layoutManager = LinearLayoutManager(this.context)
        activitiesRecycleView.setHasFixedSize(true)

        activityList = arrayListOf<UserActivity>()

        binding.buttonAddActivity.setOnClickListener{view : View ->
            // pridanie date do DataHolder triedy
            DataHolder.setDate(date)
            view.findNavController().navigate(R.id.action_mainApplication_to_timePickerDialog2)
        }

        binding.CalendarView.setOnDateChangeListener(CalendarView.OnDateChangeListener {
                calendarView, year, month , dayOfMonth ->

            activityList.clear()
            activitiesRecycleView.adapter?.notifyDataSetChanged()


            date = (dayOfMonth.toString() + "-" +
                    (month + 1) + "-" + year)
            getUserData()
        })

        getDateFromCalendarView()
        getUserData()

        return binding.root
    }

    private fun getUserData() {

        dbRef = FirebaseDatabase.getInstance("https://vamzapp-5939a-default-rtdb.europe-west1.firebasedatabase.app").getReference("/activityData/" + myAuthorization.currentUser?.uid + "/$date")

        dbRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                // Ideme prechadzat Realtime databazu
                if(snapshot.exists()) {

                    for (activitySnapshot in snapshot.children) {
                        val activity: UserActivity? = activitySnapshot.getValue(UserActivity::class.java)

                        Log.w(TAG,activity?.getTypeOfActivity() + " " + activity?.getTimeOfActivity() + " "
                                + activity?.getTotalSteps() + " " + activity?.getCaloriesBurned())

                        if (activity != null) {
                            activityList.add(activity)
                        }
                    }

                    activitiesRecycleView.adapter = RecycleViewAdapter(activityList)
                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }


        })
    }

    private fun getDateFromCalendarView() {
        val dateFormat = SimpleDateFormat("dd-MM-yyyy")
        date = dateFormat.format(Date(binding.CalendarView.date))
    }

    override fun onStart() {
        super.onStart()

        val user = myAuthorization.currentUser
        if (user == null) {
            view?.findNavController()?.navigate(R.id.action_mainApplication_to_loginFragment2)
        }
    }

}