package com.asif.expensemanager.background.utils

import android.content.Context
import com.asif.expensemanager.R
import java.util.*

/**
 * Utilities object, to perform static date operations, that repeat across the app.
 */
object DateUtils {

    /*
    * Method to get the current time in millis.
     */
    fun getCurrentTimestamp(): Long = System.currentTimeMillis()

    /*
    * Method to get a specific time in millis
     */
    fun getTimestamp(date: Long, hour: Int, minute: Int): Long {
        //Get a Calendar instance
        val c = Calendar.getInstance()

        //Set the timestamp of the Calendar
        c.timeInMillis = date

        //Set the date of the Calendar
        c.set(c[Calendar.YEAR], c[Calendar.MONTH], c[Calendar.DAY_OF_MONTH], hour, minute)

        //Return the new timestamp of the Calendar
        return c.timeInMillis
    }

    /*
    * Method to get time in millis of a specific date.
     */
    fun getTimestamp(year: Int, month: Int, day: Int): Long {
        //Get a Calendar instance
        val c = Calendar.getInstance()

        //Set the date of the Calendar
        c.set(year, month, day)

        //Return the timestamp of the Calendar
        return c.timeInMillis
    }

    /*
    * Method to get the time in millis, in which the month ends.
     */
    fun getMonthEnd(date: Long): Long {
        //Get an instance of the Calendar
        val c = Calendar.getInstance()

        //Set the Calendar timestamp to the argument passed to this method.
        c.timeInMillis = date

        //Get the month
        val month = c[Calendar.MONTH]

        //If it is the last month, increase the year, else, only increase the month
        if (month == 11) {
            c.set(c[Calendar.YEAR] + 1, 0, 1, 0, 0)
        } else {
            c.set(c[Calendar.YEAR], month + 1, 1, 0, 0)
        }

        //Return the new timestamp
        return c.timeInMillis
    }

    /*
    * Method to get the time in millis, in which the year ends.
     */
    fun getYearEnd(date: Long): Long {
        //Get an instance of the Calendar
        val c = Calendar.getInstance()

        //Set timestamp
        c.timeInMillis = date

        //Increase the year
        c.set(c[Calendar.YEAR] + 1, 0, 1, 0, 0)

        //Return new timestamp
        return c.timeInMillis
    }

    /*
    * Method to get the name of the month.
     */
    fun getMonthName(context: Context, date: Long): String {
        //Get the Calendar instance
        val c = Calendar.getInstance()

        //Set Calendar's timestamp
        c.timeInMillis = date

        //Return a String, containing the month name, gotten from the resources using the month's index, and the year number.
        return context.resources.getStringArray(R.array.months)[c[Calendar.MONTH]] + " " + c[Calendar.YEAR].toString()
    }

    /*
    * Method to get the number of the year as a String.
     */
    fun getYearName(date: Long): String {
        //Instance of the Calendar
        val c = Calendar.getInstance()

        //Set timestamp
        c.timeInMillis = date

        //Return a String containing the number of the year.
        return c[Calendar.YEAR].toString()
    }

    /*
    * Method to get month start using its name
     */
    fun getMonthStartByName(context: Context, name: String): Long {
        //Instance of the Calendar
        val c = Calendar.getInstance()

        //Split the name to month and year
        val nameSplit = name.split(" ")

        //Set calendar's date
        c.set(
            nameSplit[1].toInt(),
            context.resources.getStringArray(R.array.months).indexOf(nameSplit[0]),
            1,
            0,
            0,
            0
        )

        //Return the timestamp
        return c.timeInMillis
    }

    /*
    * Method to get the weekday of the first day pf a specific month.
     */
    fun getFirstDayOfWeekInMonth(date: Long): Int {
        //Get an instance of the Calendar
        val c = Calendar.getInstance()

        //Set the Calendar's timestamp
        c.timeInMillis = date

        //Set the day on month to be the first day.
        c[Calendar.DAY_OF_MONTH] = 1

        //Return the index of the day of the week.
        return c[Calendar.DAY_OF_WEEK]
    }

    /*
    * Method to get the count of days of a month, i.e., the number of the last month day.
     */
    fun getLastMonthDay(date: Long): Int {
        //Get an instance of the Calendar
        val c = Calendar.getInstance()

        //Set timestamp
        c.timeInMillis = date

        //Get the Month
        val month = c[Calendar.MONTH]

        //If it is the last month, increase the year, else, only increase the month
        if (month == 11) {
            c.set(c[Calendar.YEAR] + 1, 0, 1, 0, 0)
        } else {
            c.set(c[Calendar.YEAR], month + 1, 1, 0, 0)
        }

        //Subtract a day from the Calendar, so that the Calendar holds the last day of month
        c.timeInMillis -= 60 * 60 * 24

        //Return the day of the month.
        return c[Calendar.DAY_OF_MONTH]
    }
}