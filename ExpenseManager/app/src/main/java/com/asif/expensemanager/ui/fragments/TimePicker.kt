package com.asif.expensemanager.ui.fragments

import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment

/**
 * TimePicker fragment to let the user pick a time
 */
class TimePicker : DialogFragment(), TimePickerDialog.OnTimeSetListener {
    /**
     * A listener for the picker
     */
    interface OnTimeSetListener {
        fun onTimeSet(hour: Int, minute: Int)
    }

    //Instance of the listener's interface
    private var listener: OnTimeSetListener? = null

    override fun onAttach(context: Context) {
        //Initialize the listener if the context is an instance of OnTimeSetListener
        if (context is OnTimeSetListener) {
            listener = context
        }

        super.onAttach(context)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return if (arguments != null) {
            // Create a new instance of TimePickerDialog and return it.
            TimePickerDialog(
                context,
                this,
                arguments!!.getInt(HOUR_KEY),
                arguments!!.getInt(MINUTE_KEY),
                true
            )
        } else {
            super.onCreateDialog(savedInstanceState)
        }
    }

    /*
     * Method called when time is picked
     */
    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        if (listener != null) {
            listener!!.onTimeSet(hourOfDay, minute)
        }
    }

    /**
     * Companion object, used to create a new instance, and to set its arguments
     */
    companion object {
        const val HOUR_KEY = "com.asif.expensemanager.old_hour"
        const val MINUTE_KEY = "com.asif.expensemanager.old_minute"

        @JvmStatic
        fun newInstance(
            initHour: Int,
            initMinute: Int
        ) =
            TimePicker().apply {
                arguments = Bundle().apply {
                    putInt(HOUR_KEY, initHour)
                    putInt(MINUTE_KEY, initMinute)
                }
            }
    }

}