    package com.asif.expensemanager

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment

class DatePicker : DialogFragment(), DatePickerDialog.OnDateSetListener {
    interface OnDateSetListener {
        fun onDateSet(isStartDate: Boolean, year: Int, month: Int, day: Int)
    }

    private var listener: OnDateSetListener? = null

    override fun onAttach(context: Context) {
        if (context is OnDateSetListener) {
            listener = context
        }

        super.onAttach(context)
    }

    private var isStartDate = false

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        return if (context != null && arguments != null) {
            isStartDate = arguments!!.getBoolean(START_DAY_KEY)

            // Create a new instance of DatePickerDialog and return it.
            DatePickerDialog(
                context!!,
                this,
                arguments!!.getInt(YEAR_KEY),
                arguments!!.getInt(MONTH_KEY),
                arguments!!.getInt(DAY_KEY)
            )
        } else {
            super.onCreateDialog(savedInstanceState)
        }
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        if (listener != null) {
            listener!!.onDateSet(isStartDate, year, month, dayOfMonth)
        }
    }

    companion object {
        const val START_DAY_KEY = "com.asif.expensemanager.is_start_day"
        const val YEAR_KEY = "com.asif.expensemanager.old_year"
        const val MONTH_KEY = "com.asif.expensemanager.old_month"
        const val DAY_KEY = "com.asif.expensemanager.old_day"

        @JvmStatic
        fun newInstance(
            isStartDate: Boolean,
            initYear: Int,
            initMonth: Int,
            initDay: Int
        ) =
            DatePicker().apply {
                arguments = Bundle().apply {
                    putBoolean(START_DAY_KEY, isStartDate)
                    putInt(YEAR_KEY, initYear)
                    putInt(MONTH_KEY, initMonth)
                    putInt(DAY_KEY, initDay)
                }
            }
    }

}