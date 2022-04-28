package com.romanlojko.beactive

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.romanlojko.beactive.databinding.FragmentActivityCounterBinding

class activityCounter : Fragment() {

    // Enum pre tlacidla
    enum class TimerState{
        Stopped, Paused, Running
    }

    lateinit var binding: FragmentActivityCounterBinding

    private lateinit var timer: CountDownTimer
    private var timerState = TimerState.Stopped

    private var timerLengthSeconds: Long = 0
    private var secondsRemaining: Long = 0

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
            timer.cancel()
            timerState = TimerState.Stopped
            updateButtons()
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

        timer.cancel()

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
        timerLengthSeconds = (10 * 60L)
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
        binding.progressCasovac.progress = (timerLengthSeconds - secondsRemaining).toInt()
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

}