package com.romanlojko.beactive

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Nullable
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.romanlojko.beactive.databinding.FragmentActivityCounterBinding
import com.romanlojko.beactive.Objects.DataHolder

class activityCounter : Fragment() {

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentActivityCounterBinding.inflate(layoutInflater)

        binding.flaotActionButtonPlay.setOnClickListener { view: View ->
            startTimer()
            timerState = TimerState.Running
            updateButtons()
        }

        binding.flaotActionButtonPause.setOnClickListener { view: View ->
            timer?.cancel()
            timerState = TimerState.Stopped
            updateButtons()
        }

        binding.flaotActionButtonClose.setOnClickListener{ view: View ->
            StopActivityDialog().show(childFragmentManager, StopActivityDialog.TAG)
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        initTimer()

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

    fun initTimer() {
        setNewTimerLength()
        secondsRemaining = timerLengthSeconds
        updateButtons()
        updateCountdownUI()
    }

    private fun setNewTimerLength(){
        binding.progressCasovac.max = timerLengthSeconds.toInt()
    }

    private fun startTimer() {
        timer = object : CountDownTimer(secondsRemaining * 1000, 1000) {
            override fun onFinish() = onTimerFinished()

            override fun onTick(millisUntilFinished: Long) {
                secondsRemaining = millisUntilFinished / 1000
                updateCountdownUI()
            }
        }.start()
    }

    private fun onTimerFinished() {
        secondsRemaining = timerLengthSeconds

        binding.progressCasovac.progress = 0

        updateButtons()
        updateCountdownUI()
    }

    private fun updateCountdownUI(){
        val minutesUntilFinished = secondsRemaining / 60
        val secondsInMinuteUntilFinished = secondsRemaining - minutesUntilFinished * 60
        val secondsStr = secondsInMinuteUntilFinished.toString()
        binding.textViewCasovac.text = "$minutesUntilFinished:${if (secondsStr.length == 2) secondsStr else "0" + secondsStr}"
        binding.progressCasovac.progress = (firstTime - secondsRemaining).toInt()
    }

    private fun updateButtons() {
        when (timerState) {
            TimerState.Running ->{
                binding.flaotActionButtonPlay.isEnabled = false
                binding.flaotActionButtonPause.isEnabled = true
            }
            TimerState.Stopped -> {
                binding.flaotActionButtonPlay.isEnabled = true
                binding.flaotActionButtonPause.isEnabled = false
            }
        }
    }

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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putLong("secondsLeft", secondsRemaining)
        if (timerState == TimerState.Running)
            outState.putBoolean("timerRunning", true)
        else
            outState.putBoolean("timerRunning", false)
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

}

