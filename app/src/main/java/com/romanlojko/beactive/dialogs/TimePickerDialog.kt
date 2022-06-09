package com.romanlojko.beactive.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.romanlojko.beactive.Objects.DataHolder
import com.romanlojko.beactive.R
import kotlinx.android.synthetic.main.float_number_picker.*

/**
 * Trieda pre timepickerDialog
 * Pouziva sa na vyberanie casu novej aktivity.
 */
class TimePickerDialog() : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.float_number_picker, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setNumbersInTimePicker()

        button_open_activity_counter.setOnClickListener{
            DataHolder.setTimeOfActivity(getTimeSelected())
            findNavController().navigate(R.id.action_timePickerDialog2_to_activityCounter)
        }
    }

    /**
     * Metoda nastavi rozsah hodnotu pre TimePicker
     */
    private fun setNumbersInTimePicker() {
        if (time_picker!=null) {
            time_picker.minValue = 1
            time_picker.maxValue = 120
        }
    }

    /**
     * Getter pre zakliknuty cas
     */
    fun getTimeSelected(): Int {
        return if (time_picker != null) time_picker.value else 0
    }

}