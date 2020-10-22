package com.asif.expensemanager

import android.content.Context
import java.util.*

object Utils {
    //Shared Preferences Name
    private const val PREFS_NAME = "com.asif.expensemanager.preferences_name"

    //Data keys
    private const val NAME_KEY = "com.asif.expensemanager.user_name"
    private const val BUDGET_KEY = "com.asif.expensemanager.budget_name"
    private const val INCOME_KEY = "com.asif.expensemanager.income_name"

    fun saveUserName(context: Context, name: String) {
        //Getting a SharedPreferences Editor object
        val editor = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()

        //Put the name on SharedPreferences
        editor.putString(NAME_KEY, name)

        //Call apply method on the editor to save the changes
        editor.apply()
    }

    fun getUserName(context: Context): String? {
        //Getting an instance of SharedPreferences
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        //Return the name from the SharedPreferences, or null if there isn't anyone
        return prefs.getString(NAME_KEY, null)
    }

    fun saveBudget(context: Context, budget: Float) {
        //Getting a SharedPreferences Editor object
        val editor = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()

        //Put the budget on SharedPreferences
        editor.putFloat(BUDGET_KEY, budget)

        //Call apply method on the editor to save the changes
        editor.apply()
    }

    fun getBudget(context: Context): Float {
        //Getting an instance of SharedPreferences
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        //Return the budget from the SharedPreferences, or 0 if there isn't.
        return prefs.getFloat(BUDGET_KEY, 0f)
    }

    fun saveIncome(context: Context, income: Float) {
        //Getting a SharedPreferences Editor object
        val editor = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()

        //Put the income on SharedPreferences
        editor.putFloat(INCOME_KEY, income)

        //Call apply method on the editor to save the changes
        editor.apply()
    }

    fun getIncome(context: Context): Float {
        //Getting an instance of SharedPreferences
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        //Return the income from the SharedPreferences, or 0 if there isn't.
        return prefs.getFloat(INCOME_KEY, 0f)
    }

    fun getCurrentTimestamp(): Long = System.currentTimeMillis()

    fun getTimestamp(date: Long, hour: Int, minute: Int): Long {
        val c = Calendar.getInstance()
        c.timeInMillis = date
        c.set(c[Calendar.YEAR], c[Calendar.MONTH], c[Calendar.DAY_OF_MONTH], hour, minute)
        return c.timeInMillis
    }

    fun getTimestamp(year: Int, month: Int, day: Int): Long {
        val c = Calendar.getInstance()
        c.set(year, month, day)
        return c.timeInMillis
    }

    fun getTimestamp(year: Int, month: Int, day: Int, hour: Int, minute: Int): Long {
        val c = Calendar.getInstance()
        c.set(1900 + year, month, day, hour, minute)
        return c.timeInMillis
    }

    fun getMonthEnd(start: Long): Long {
        val c = Calendar.getInstance()
        c.timeInMillis = start
        val month = c[Calendar.MONTH]
        if (month == 11) {
            c.set(c[Calendar.YEAR] + 1, 0, 1, 0, 0)
        } else {
            c.set(c[Calendar.YEAR], month + 1, 1, 0, 0)
        }
        return c.timeInMillis
    }

    fun getYearEnd(start: Long): Long {
        val c = Calendar.getInstance()
        c.timeInMillis = start
        c.set(c[Calendar.YEAR] + 1, 0, 1, 0, 0)
        return c.timeInMillis
    }

    fun getMonthName(context: Context, dateInMonth: Long): String {
        val c = Calendar.getInstance()
        c.timeInMillis = dateInMonth
        return context.resources.getStringArray(R.array.months)[c[Calendar.MONTH]]
    }
}