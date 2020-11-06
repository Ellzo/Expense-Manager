package com.asif.expensemanager.ui.fragments

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment

/**
 * DatePicker to let the user pick a date
 */
class DatePicker : DialogFragment(), DatePickerDialog.OnDateSetListener {
    /**
     * A listener for the picker
     */
    interface OnDateSetListener {
        fun onDateSet(isStartDate: Boolean, year: Int, month: Int, day: Int)
    }

    //Instance of the listener's interface
    private var listener: OnDateSetListener? = null

    //Boolean to indicate wither it is the start date, or the end date
    private var isStartDate = false

    override fun onAttach(context: Context) {
        //Initialize the listener
        if (context is OnDateSetListener) {
            listener = context
        }

        super.onAttach(context)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return if (context != null && arguments != null) {
            //Get the start date from the arguments
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

    /*
     * Method called when date is picked
     */
    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        if (listener != null) {
            listener!!.onDateSet(isStartDate, year, month, dayOfMonth)
        }
    }

    /**
     * Companion object, used to create a new instance, and to set its arguments
     */
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