package com.asif.expensemanager.ui.fragments

import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.asif.expensemanager.R
import com.asif.expensemanager.background.db.Transaction
import com.asif.expensemanager.background.utils.DateUtils
import kotlinx.android.synthetic.main.fragment_calendar.view.*
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

/*
* Constants for the arguments passed to this fragment
 */
private const val ARG_MONTH_NAME = "com.asif.expensemanager.month"
private const val ARG_MONTH_TRANSACTIONS = "com.asif.expensemanager.transactions"

/**
 * Fragment the shows a Calendar on the screen
 */
class CalendarFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (context != null && arguments != null) {
            //Get the month name from arguments
            val title = arguments!!.getString(ARG_MONTH_NAME)

            if (title != null) {
                // Inflate the layout for this fragment
                val root = inflater.inflate(R.layout.fragment_calendar, container, false)

                //Setup the toolbar
                root.toolbar.title = title
                root.toolbar.setNavigationIcon(R.drawable.ic_clear)
                root.toolbar.setNavigationOnClickListener {
                    if (activity != null) {
                        activity!!.supportFragmentManager.popBackStackImmediate()
                    }
                }

                //Setup the first row, that shows the weekdays
                val firstRow = TableRow(context!!)
                firstRow.layoutParams =
                    TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, 0, 1f)
                for (weekDay in 0..6) {
                    val tv = TextView(context!!)
                    tv.layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT)
                    tv.gravity = Gravity.CENTER
                    tv.text = context!!.resources.getStringArray(R.array.week_days)[weekDay]
                    tv.setTextColor(ContextCompat.getColor(context!!, R.color.colorPrimary))
                    firstRow.addView(tv)
                }
                root.tableCalendar.addView(firstRow)

                //Get the transactions argument
                val transactions: ArrayList<Transaction>? =
                    arguments!!.getParcelableArrayList(ARG_MONTH_TRANSACTIONS)

                //Create a StringBuilder, to use it later to build Calendar cells text
                val txtBuilder = StringBuilder()

                if (transactions != null) {

                    //Set the month's day to be 0 initially
                    var monthDay = 0

                    //Set the jump step to a single day
                    val step: Long = 1000 * 60 * 60 * 24

                    //Create 6 rows
                    for (row in 1..6) {
                        //Create  a table row and sit its parameters
                        val tableRow = TableRow(context!!)
                        tableRow.layoutParams =
                            TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, 0, 1f)

                        //Create 7 columns on each row
                        for (column in 1..7) {
                            //Create a TextView and set its attributes
                            val tv = TextView(context!!)
                            tv.layoutParams =
                                TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT)
                            tv.gravity = Gravity.CENTER
                            if (monthDay <= 0) {
                                //If the selected column equals the index of the weekday of the first day in the month,
                                // increase the month's day counter, and set its text and attributes
                                if (column == DateUtils.getFirstDayOfWeekInMonth(
                                        DateUtils.getMonthStartByName(context!!, title)
                                    )
                                ) {
                                    monthDay++

                                    txtBuilder.clear()
                                    txtBuilder.append("<font color=#002C96>$monthDay</font>")

                                    //Get the expenses and income of the first month's day
                                    var expenses = 0f
                                    var income = 0f
                                    for (transaction in transactions) {
                                        if (!(transaction.startDate > min(
                                                transactions[0].startDate,
                                                DateUtils.getMonthStartByName(context!!, title)
                                            ) + step || transaction.endDate <= max(
                                                transactions[0].startDate,
                                                DateUtils.getMonthStartByName(context!!, title)
                                            ))
                                        ) {
                                            if (transaction.amount < 0) {
                                                expenses -= transaction.amount
                                            } else {
                                                income += transaction.amount
                                            }
                                        }
                                    }

                                    txtBuilder.append("<br/>")
                                    txtBuilder.append("<br/>")

                                    //Show the expenses and income of the first month's day
                                    if (income > 0) {
                                        txtBuilder.append("<font color=#00FF00>+${income.roundToInt()}</font>")
                                    }
                                    txtBuilder.append("<br/>")
                                    if (expenses > 0) {
                                        txtBuilder.append("<font color=#FF0000>-${expenses.roundToInt()}</font>")
                                    }
                                    txtBuilder.append("<br/>")

                                    //Set the TextView's text
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                        tv.text = Html.fromHtml(txtBuilder.toString(), 0)
                                    } else {
                                        tv.text = Html.fromHtml(txtBuilder.toString())
                                    }
                                }
                            } else {
                                if (monthDay < DateUtils.getLastMonthDay(
                                        DateUtils.getMonthStartByName(
                                            context!!,
                                            title
                                        )
                                    )
                                ) {
                                    monthDay++

                                    //Clear the StringBuilder before performing any operations
                                    txtBuilder.clear()

                                    //Set the initial text of the Builder, to the month day
                                    txtBuilder.append("<font color=#002C96>$monthDay</font>")

                                    //Get the income & expenses of that month day
                                    var expenses = 0f
                                    var income = 0f
                                    for (transaction in transactions) {
                                        if (!(transaction.startDate > DateUtils.getMonthStartByName(
                                                context!!,
                                                title
                                            ) + step * monthDay || transaction.endDate <= DateUtils.getMonthStartByName(
                                                context!!,
                                                title
                                            ) + step * (monthDay - 1))
                                        ) {
                                            if (transaction.amount < 0) {
                                                expenses -= transaction.amount
                                            } else {
                                                income += transaction.amount
                                            }
                                        }
                                    }

                                    txtBuilder.append("<br/>")
                                    txtBuilder.append("<br/>")

                                    //Set the income text, & color
                                    if (income > 0) {
                                        txtBuilder.append("<font color=#00FF00>+${income.roundToInt()}</font>")
                                    }

                                    txtBuilder.append("<br/>")

                                    //Set the expenses text, & color
                                    if (expenses > 0) {
                                        txtBuilder.append("<font color=#FF0000>-${expenses.roundToInt()}</font>")
                                    }

                                    txtBuilder.append("<br/>")

                                    //Finally set the text
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                        tv.text = Html.fromHtml(txtBuilder.toString(), 0)
                                    } else {
                                        tv.text = Html.fromHtml(txtBuilder.toString())
                                    }
                                }
                            }

                            //Add it to the table row
                            tableRow.addView(tv)
                        }

                        //Finally, add the row to the Calendar table
                        root.tableCalendar.addView(tableRow)
                    }
                }

                //Return the root view
                return root
            }
        }

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    /**
     * Companion object, used to create an instance of this Fragment
     */
    companion object {
        @JvmStatic
        fun newInstance(monthName: String, transactions: ArrayList<Transaction>) =
            CalendarFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_MONTH_NAME, monthName)
                    putParcelableArrayList(ARG_MONTH_TRANSACTIONS, transactions)
                }
            }
    }
}