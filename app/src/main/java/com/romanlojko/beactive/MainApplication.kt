package com.romanlojko.beactive

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.CalendarView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.romanlojko.beactive.Objects.DataHolder
import com.romanlojko.beactive.Objects.PersonDataLoader
import com.romanlojko.beactive.Objects.UserActivity
import com.romanlojko.beactive.databinding.FragmentMainApplicationBinding
import kotlinx.android.synthetic.main.fragment_main_application.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MainApplication : Fragment() {

    private lateinit var binding: FragmentMainApplicationBinding
    private lateinit var myAuthorization: FirebaseAuth
    private lateinit var dbRef: DatabaseReference
    private lateinit var activitiesRecycleView: RecyclerView
    private lateinit var activityList: ArrayList<UserActivity>

    // Nacitanie animacii pre menu
    private val rotateOpen: Animation by lazy { AnimationUtils.loadAnimation(this.context, R.anim.rotate_open_anim) }
    private val rotateClose: Animation by lazy { AnimationUtils.loadAnimation(this.context, R.anim.rotate_close_anim) }
    private val fromBottom: Animation by lazy { AnimationUtils.loadAnimation(this.context, R.anim.from_bottom_anim) }
    private val toBottom: Animation by lazy { AnimationUtils.loadAnimation(this.context, R.anim.to_bottom_anim) }

    private var clickedMenuButton = false

    private var date: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        PersonDataLoader.loadDataToPerson()
    }

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
            getDateFromCalendarView()
            DataHolder.setDate(date)
            onMenuButtonClick()
            view.findNavController().navigate(R.id.action_mainApplication_to_timePickerDialog2)
        }

        binding.buttonProfile!!.setOnClickListener{view : View ->
            onMenuButtonClick()
            view.findNavController().navigate(R.id.action_mainApplication_to_profile)
        }

        binding.buttonMenu!!.setOnClickListener{
            onMenuButtonClick()
        }

        binding.CalendarView.setOnDateChangeListener{
                calendarView, year, month , dayOfMonth ->

            activityList.clear()
            activitiesRecycleView.adapter?.notifyDataSetChanged()


            date = (dayOfMonth.toString() + "-" +
                    (month + 1) + "-" + year)
            getUserData()
        }

        getDateFromCalendarView()
        getUserData()

        return binding.root
    }

    /**
     * Metoda ktora handluje menu button click
     */
    private fun onMenuButtonClick() {
        setVisibility(clickedMenuButton)
        setAnimation(clickedMenuButton)
        clickedMenuButton = !clickedMenuButton //if (!clickedMenuButton) clickedMenuButton = true else clickedMenuButton = false - skratene
    }

    /**
     * Metoda ktora nastavuje animacie pri kliknuti na menu button
     */
    private fun setAnimation(clicked: Boolean) {
        if (!clicked) {
            button_add_activity.startAnimation(fromBottom)
            button_profile.startAnimation(fromBottom)
            button_Menu.startAnimation(rotateOpen)
        } else {
            button_add_activity.startAnimation(toBottom)
            button_profile.startAnimation(toBottom)
            button_Menu.startAnimation(rotateClose)
        }
    }

    /**
     * Metoda ktora schovava a odokryva buttony v menu
     */
    private fun setVisibility(clicked: Boolean) {
        if(!clicked) {
            button_add_activity.visibility = View.VISIBLE
            button_profile.visibility = View.VISIBLE
        } else {
            button_add_activity.visibility = View.INVISIBLE
            button_profile.visibility = View.INVISIBLE
        }
    }

    /**
     * Metoda spristupnuje data o aktivitach v dani den pre usera
     * a uklada ich do listu activityList, ktory sa nasledne zobrazi v recyclerviewe
     */
    private fun getUserData() {

        dbRef = FirebaseDatabase.getInstance("https://vamzapp-5939a-default-rtdb.europe-west1.firebasedatabase.app").getReference("/activityData/" + myAuthorization.currentUser?.uid + "/$date")

        dbRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                // Resetuje pocet aktivity v dni
                DataHolder.resetNumberOfActivity()

                // Ideme prechadzat Realtime databazu
                if(snapshot.exists()) {

                    for (activitySnapshot in snapshot.children) {
                        val activity: UserActivity? = activitySnapshot.getValue(UserActivity::class.java)

                        Log.w(TAG,activity?.getTypeOfActivity() + " " + activity?.getTimeOfActivity() + " "
                                + activity?.getTotalSteps() + " " + activity?.getCaloriesBurned())

                        if (activity != null) {
                            activityList.add(activity)
                        }

                        DataHolder.incNumberOfActivity()
                    }

                    activitiesRecycleView.adapter = RecycleViewAdapter(activityList)
                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }


        })
    }

    /**
     * Metoda nastavi atribut date na datum zo zakliknuteho datumu v calendarViewe
     */
    private fun getDateFromCalendarView() {
        val dateFormat = SimpleDateFormat("dd-M-yyyy")
        date = dateFormat.format(Date(binding.CalendarView.date))
    }

    /**
     * Ak nieje user prihlaseeny, tak ho poslem na login screen
     */
    override fun onStart() {
        super.onStart()

        val user = myAuthorization.currentUser
        if (user == null) {
            view?.findNavController()?.navigate(R.id.action_mainApplication_to_loginFragment2)
        }
    }

}