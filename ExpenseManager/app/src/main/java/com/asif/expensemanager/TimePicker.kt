package com.asif.expensemanager

import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment

class TimePicker : DialogFragment(), TimePickerDialog.OnTimeSetListener {
    interface OnTimeSetListener {
        fun onTimeSet(hour: Int, minute: Int)
    }

    private var listener: OnTimeSetListener? = null

    override fun onAttach(context: Context) {
        if (context is OnTimeSetListener) {
            listener = context
        }

        super.onAttach(context)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return if (arguments != null) {
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

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        if (listener != null) {
            listener!!.onTimeSet(hourOfDay, minute)
        }
    }

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