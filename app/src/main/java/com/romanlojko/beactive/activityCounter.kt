package com.romanlojko.beactive

import android.app.*
import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.findNavController
import com.google.firebase.database.*
import com.romanlojko.beactive.Objects.DataHolder
import com.romanlojko.beactive.Objects.Person
import com.romanlojko.beactive.databinding.FragmentActivityCounterBinding
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.typeofactivity_dialog.view.*
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * ActivityCounter je trieda ktora dedi Fragment a sensoreventListener
 * predstavuje cast aplikacie, kde meriame novu aktivity ktoru
 * si chce uzivatel zaznamenat, po ubehnuti casu sa data odoslu na Firebase RDB
 * @author Roman Lojko
 */
class activityCounter : Fragment() , SensorEventListener {

    /**
     * Enum pre tlacidla
     */
    enum class TimerState{
        Stopped, Paused, Running, ENDED
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

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    /**
     * Lifecycle metoda ktora sa zavola pri otvoreni aplikacie alebo pri zmene rotacie
     * @return binding.root
     */
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



    /**
     * Lifecycle metoda, loadujem data zo sharedpreferences a spusta sensor managera
     * pre detekovanie krokov
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = this.activity!!.getSharedPreferences("myPref", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()

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

    /**
     * Lifecycle metoda, zobrazuje notifikacie pri shovani aplikacie
     */
    override fun onPause() {
        super.onPause()
//        if (timerState == TimerState.Running){
//            timer.cancel()
//            //TODO: start background timer and show notification
//        }
//        if (timerState == TimerState.ENDED){
//            //TODO: show notification
//        }
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
            override fun onFinish() {
                onTimerFinished()
                view?.findNavController()?.navigate(R.id.action_activityCounter_to_typeOfActivityDialog2)
            }

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
        timerState = TimerState.ENDED
        secondsRemaining = timerLengthSeconds

        binding.progressCasovac.progress = 0

        updateButtons()
        updateCountdownUI()

        sendDataToFirebase()
    }

    /**
     * Metoda ktora posle data na firebase po ukonceni aktivity
     */
    private fun sendDataToFirebase() {
        DataHolder.incNumberOfActivity()
        DataHolder.setTotalSteps((totalSteps - prevTotalSteps).toInt())
        calcTotalBurnedCalories()


        // ulozenie dat pre dalsie pocitanie krokov
        prevTotalSteps = totalSteps
        saveStepsData()

    }

    /**
     * Vypocet spalenych kalorii
     */
    private fun calcTotalBurnedCalories() {
        var burnedCalories: String = ""
//        val stepsToKmConst = 1312.33595801 // Konstata ktoru vypocitam z krokov vzdialenost

        if ((totalSteps - prevTotalSteps) > 0)
            burnedCalories = BigDecimal((((Person.getAge() * 0.2017) + (Person.getWeight() * 0.09036) + (getAverageHearthRate() * 0.6309) - 55.0969) * (timerLengthSeconds / 60)) / 4.184).setScale(2, RoundingMode.HALF_DOWN).toString()
//            burnedCalories = BigDecimal(((8.3 * Person.getWeight() * 3.5) / 200) * (timerLengthSeconds / 60)).setScale(2, RoundingMode.HALF_DOWN).toString()
        else
            burnedCalories = "0"
//        burnedCalories = ((totalSteps - prevTotalSteps) / 20).toString()
        DataHolder.setCaloriesBurned(burnedCalories)
    }

    /**
     * Vrati primernu tep srdca v tepoch za minutu
     * pre vypocet kalorii, pouzivame priemer pretoze nemame dosah na
     * meranie tepu
     */
    private fun getAverageHearthRate(): Int {
        return 130
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
     * Metoda ulozi data do Bundle pred ukoncenim alebo ototcenim displeja
     * alebo ukoncenim, minimalizovanim aplikacie
     * @param outState
     */
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

    /**
     * Nacita data z Bundle pri nacitani viewu nanovo
     * pouziva sa hlavne pri zmene orientacie obrazovky
     * @param savedInstanceState
     */
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
        editor.putFloat("prevTotalSteps", prevTotalSteps)
        editor.apply()
    }

    /**
     * Nacitavanie dat zo sharedPreferences
     */
    private fun lodaData() {
        val savedNumber = sharedPreferences.getFloat("prevTotalSteps", 0f)
        Log.d("StepCounterData", "$savedNumber")
        prevTotalSteps = savedNumber
    }
}

