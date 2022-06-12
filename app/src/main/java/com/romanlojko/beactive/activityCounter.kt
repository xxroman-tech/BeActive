package com.romanlojko.beactive

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.ContactsContract
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.inflate
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.annotation.Nullable
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorDestinationBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.romanlojko.beactive.databinding.FragmentActivityCounterBinding
import com.romanlojko.beactive.Objects.DataHolder
import com.romanlojko.beactive.databinding.ActivityItemBinding.inflate
import kotlinx.android.synthetic.main.typeofactivity_dialog.view.*

class activityCounter : Fragment() , SensorEventListener {

    // Enum pre tlacidla
    enum class TimerState{
        Stopped, Paused, Running
    }

    lateinit var binding: FragmentActivityCounterBinding

    private var timer: CountDownTimer? = null
    private var timerState = TimerState.Stopped

    private var timerLengthSeconds: Long = DataHolder.getTimeOfActivity() * 60L
    private var secondsRemaining: Long = 0
    // uchovanie pociatocneho casu na zaciatku aktivity
    private var firstTime: Long = timerLengthSeconds

    // Pre stepCounter
    private var sensorManager: SensorManager? = null
    private var totalSteps = 0f
    private var prevTotalSteps = 0f
    private var running = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentActivityCounterBinding.inflate(layoutInflater)

        binding.textVievKrokomer.text = "0"

        binding.flaotActionButtonPlay.setOnClickListener { view: View ->
            running = true
            startTimer()
            timerState = TimerState.Running
            updateButtons()
        }

//        binding.flaotActionButtonPause.setOnClickListener { view: View ->
//            running = false
//            timer?.cancel()
//            timerState = TimerState.Stopped
//            updateButtons()
//        }

        binding.flaotActionButtonClose.setOnClickListener{ view: View ->
            StopActivityDialog().show(childFragmentManager, StopActivityDialog.TAG)
            prevTotalSteps = totalSteps
            saveStepsData()
        }

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //nacita stepcounter data zo shared preferences
        lodaData()

        running = true
        sensorManager = requireContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    /**
     * Spustenie zaznamenavania senzoru a inicializacia timeru
     */
    override fun onResume() {
        super.onResume()

        initTimer()

        val stepSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        if (stepSensor == null) {
            Toast.makeText(activity, "Vas telefon neobsahuje senzor na detekovanie krokov", Toast.LENGTH_SHORT).show()
        } else {
            sensorManager?.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI)
        }

        //TODO: remove background timer, hide notification
    }

    override fun onPause() {
        super.onPause()
//        if (timerState == TimerState.Running){
//            timer.cancel()
//            //TODO: start background timer and show notification
//        }
//        else if (timerState == TimerState.Paused){
//            //TODO: show notification
//        }
    }

    override fun onDestroy() {
        super.onDestroy()

        val stepSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        sensorManager?.unregisterListener(this, stepSensor)
    }

    /**
     * Inicializacia timeru
     */
    fun initTimer() {
        setNewTimerLength()
        secondsRemaining = timerLengthSeconds
        updateButtons()
        updateCountdownUI()
    }

    private fun setNewTimerLength(){
        binding.progressCasovac.max = timerLengthSeconds.toInt()
    }

    /**
     * Metoda ktora sa pouziva na spustenie timeru
     */
    private fun startTimer() {
        timer = object : CountDownTimer(secondsRemaining * 1000, 1000) {
            override fun onFinish() = onTimerFinished()

            override fun onTick(millisUntilFinished: Long) {
                secondsRemaining = millisUntilFinished / 1000
                updateCountdownUI()
            }
        }.start()
    }

    /**
     * Medtoda ktora sa zavola pri skonceni odpocitavanie timeru
     */
    private fun onTimerFinished() {
        secondsRemaining = timerLengthSeconds

        binding.progressCasovac.progress = 0

        updateButtons()
        updateCountdownUI()

        sendDataToFirebase()

        prevTotalSteps = totalSteps
        saveStepsData()
    }

    /**
     * Metoda ktora posle data na firebase po ukonceni aktivity
     */
    private fun sendDataToFirebase() {
        DataHolder.incNumberOfActivity()
        DataHolder.setTotalSteps((totalSteps - prevTotalSteps).toInt())
        calcTotalBurnedCalories()
        TypeOfActivityDialog().show(childFragmentManager, TypeOfActivityDialog.TAG)
    }

    /**
     * Vypocet spalenych kalorii
     */
    private fun calcTotalBurnedCalories() {
        var burnedCalories: String = ""

        burnedCalories = ((totalSteps - prevTotalSteps) / 20).toString()
        DataHolder.setCaloriesBurned(burnedCalories)
    }

    /**
     * Updatuje UI na obrazovke activityCounter
     */
    private fun updateCountdownUI(){
        val minutesUntilFinished = secondsRemaining / 60
        val secondsInMinuteUntilFinished = secondsRemaining - minutesUntilFinished * 60
        val secondsStr = secondsInMinuteUntilFinished.toString()
        binding.textViewCasovac.text = "$minutesUntilFinished:${if (secondsStr.length == 2) secondsStr else "0" + secondsStr}"
        binding.progressCasovac.progress = (firstTime - secondsRemaining).toInt()
    }

    /**
     * Updatuje stav button, ci sa daju zakliknut alebo nie
     */
    private fun updateButtons() {
        when (timerState) {
            TimerState.Running ->{
                binding.flaotActionButtonPlay.isEnabled = false
//                binding.flaotActionButtonPause.isEnabled = true
            }
            TimerState.Stopped -> {
                binding.flaotActionButtonPlay.isEnabled = true
//                binding.flaotActionButtonPause.isEnabled = false
            }
        }
    }

    /**
     * Trieda zdedena od DialogFragmentu
     * Obsahuje alertdialog ktory sa zobrazi pred ukoncenim novej aktivity a teda nove data sa
     * neulozia do firebase DB
     */
    class StopActivityDialog : DialogFragment() {

        override fun onCreateDialog(@Nullable savedInstanceState: Bundle?): Dialog {
            return activity?.let {
                // Use the Builder class for convenient dialog construction
                val builder = AlertDialog.Builder(it)
                builder.setMessage(R.string.wantCloseActivityCounter)
                    .setPositiveButton(R.string.answearYes,
                        DialogInterface.OnClickListener { dialog, id ->
                            activity?.onBackPressed()
                        })
                    .setNegativeButton(R.string.answearNo,
                        DialogInterface.OnClickListener { dialog, id ->
                        })
                // Create the AlertDialog object and return it
                builder.create()
            } ?: throw IllegalStateException("Activity cannot be null")
        }

        companion object {
            const val TAG = "StopActivityDialog"
        }

    }

    /**
     * Trieda zdedena od DialogFragmentu
     * Zobrazuje Dialog so vstupnym oknom kam sa zadava typ aktivity pred finalym
     * ulozenim do firebase DB
     */
    class TypeOfActivityDialog: DialogFragment() {

        //Firebase
        private val myAuthorization: FirebaseAuth = FirebaseAuth.getInstance()
        private lateinit var dbRef: DatabaseReference

        override fun onCreateDialog(@Nullable savedInstanceState: Bundle?) : Dialog {
            return (activity?.let{
                val builder = AlertDialog.Builder(it)
                val input = EditText(activity)

                input.setHint(R.string.choose_activity_hint_typ)
                input.inputType = InputType.TYPE_CLASS_TEXT
                builder.setView(input)

                builder.setTitle("Super výkon!")
                    .setPositiveButton(R.string.answerSave,
                        DialogInterface.OnClickListener { dialog, id ->
                            DataHolder.setTypeOfActivity(input.text.toString())
                            pushToFirebase()
                            activity?.onBackPressed()
                        })
                // Create the AlertDialog object and return it
                builder.create()
            } ?: throw IllegalStateException("Activity cannot be null"))
        }

        /**
         * Metoda kotra pushne novu aktivitu do firebase
         */
        private fun pushToFirebase() {
            dbRef = FirebaseDatabase.getInstance("https://vamzapp-5939a-default-rtdb.europe-west1.firebasedatabase.app").getReference("/activityData/" + myAuthorization.currentUser?.uid + "/${DataHolder.getDate()}" + "/${DataHolder.getNumberOfActivity()}")

            var list: HashMap<String, Any> = HashMap()

            list.put("caloriesBurned", DataHolder.getCaloriesBurned())
            list.put("timeOfActivity", DataHolder.getTimeOfActivity())
            list.put("totalSteps", DataHolder.getTotalSteps())
            list.put("typeOfActivity", DataHolder.getTypeOfActivity())

            dbRef.updateChildren(list as Map<String, Any>)
        }

        companion object {
            const val TAG = "TypeOfActivityDialog"
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putLong("secondsLeft", secondsRemaining)
        if (timerState == TimerState.Running)
            outState.putBoolean("timerRunning", true)
        else
            outState.putBoolean("timerRunning", false)
        val currentSteps = totalSteps - prevTotalSteps
        outState.putFloat("steps", currentSteps)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState != null) {
            secondsRemaining = savedInstanceState.getLong("secondsLeft")
            if (savedInstanceState.getBoolean("timerRunning")) {
                timerState = TimerState.Running
                startTimer()
            }
            else {
                timerState = TimerState.Stopped
                timerLengthSeconds = savedInstanceState.getLong("secondsLeft")
            }
        }

    }

    /**
     * Metoda ktora handule step sensor
     * @param event
     */
    override fun onSensorChanged(event: SensorEvent?) {
        if (running) {
            totalSteps = event!!.values[0]
            val currentSteps = totalSteps.toInt() - prevTotalSteps.toInt()
            binding.textVievKrokomer.text = ("$currentSteps")
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) { }

    /**
     * Metoda uklada data do sharedPreferences pre zachovanie v pamati
     */
    private fun saveStepsData() {
        val sharedPreferences: SharedPreferences = requireActivity().getSharedPreferences("myPref", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putFloat("prevTotalSteps", prevTotalSteps)
        editor.apply()
    }

    /**
     * Nacitavanie dat zo sharedPreferences
     */
    private fun lodaData() {
        val sharedPreferences: SharedPreferences = requireActivity().getSharedPreferences("myPref", Context.MODE_PRIVATE)
        val savedNumber = sharedPreferences.getFloat("prevTotalSteps", 0f)
        Log.d("StepCounterData", "$savedNumber")
        prevTotalSteps = savedNumber
    }
}

