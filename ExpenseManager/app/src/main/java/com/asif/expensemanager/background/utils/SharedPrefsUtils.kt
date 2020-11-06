package com.asif.expensemanager.background.utils

import android.content.Context
import com.asif.expensemanager.R

/**
 * A Utilities object to save user's data & settings in SharedPreferences.
 */
object SharedPrefsUtils {
    //Shared Preferences Name
    private const val PREFS_NAME = "com.asif.expensemanager.preferences_name"

    //Data keys
    private const val NAME_KEY = "com.asif.expensemanager.user_name"
    private const val BUDGET_KEY = "com.asif.expensemanager.budget_name"
    private const val INCOME_KEY = "com.asif.expensemanager.income_name"
    private const val CATEGORIES_KEY = "com.asif.expensemanager.categories"

    /*
    * Method to save the user's name
     */
    fun saveUserName(context: Context, name: String) {
        //Getting a SharedPreferences Editor object
        val editor = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()

        //Put the name on SharedPreferences
        editor.putString(NAME_KEY, name)

        //Call apply method on the editor to save the changes
        editor.apply()
    }

    /*
    * Method to get the user's name.
     */
    fun getUserName(context: Context): String? {
        //Getting an instance of SharedPreferences
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        //Return the name from the SharedPreferences, or null if there isn't anyone
        return prefs.getString(NAME_KEY, null)
    }

    /*
    * Method to save user's budget.
     */
    fun saveBudget(context: Context, budget: Float?) {
        //Getting a SharedPreferences Editor object
        val editor = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()

        //Put the budget on SharedPreferences
        if (budget != null && budget > 0) {
            editor.putFloat(BUDGET_KEY, budget)
        } else {
            editor.putFloat(BUDGET_KEY, 0f)
        }

        //Call apply method on the editor to save the changes
        editor.apply()
    }

    /*
    * Method to get user's budget.
     */
    fun getBudget(context: Context): Float {
        //Getting an instance of SharedPreferences
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        //Return the budget from the SharedPreferences, or 0 if there isn't.
        return prefs.getFloat(BUDGET_KEY, 0f)
    }

    /*
    * Method to save user's income.
     */
    fun saveIncome(context: Context, income: Float?) {
        //Getting a SharedPreferences Editor object
        val editor = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()

        //Put the income on SharedPreferences
        if (income != null && income > 0) {
            editor.putFloat(INCOME_KEY, income)
        } else {
            editor.putFloat(INCOME_KEY, 0f)
        }

        //Call apply method on the editor to save the changes
        editor.apply()
    }

    /*
    * Method to get user's income.
     */
    fun getIncome(context: Context): Float {
        //Getting an instance of SharedPreferences
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        //Return the income from the SharedPreferences, or 0 if there isn't.
        return prefs.getFloat(INCOME_KEY, 0f)
    }

    /*
    * Method to save the categories.
     */
    fun saveCategories(context: Context, categories: Set<String>) {
        //Getting a SharedPreferences Editor object
        val editor = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()

        //Save the categories
        editor.putStringSet(CATEGORIES_KEY, categories)

        //Call apply method on the editor to save the changes
        editor.apply()
    }

    /*
    * Method to get the categories
     */
    fun getCategories(context: Context): List<String> {
        //Getting an instance of SharedPreferences
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        //Get the categories from SharedPreferences
        var categoriesSet = prefs.getStringSet(CATEGORIES_KEY, null)

        //Set the categories to the ones defined in the resources, if categories variable is null
        if (categoriesSet == null) {
            categoriesSet = context.resources.getStringArray(R.array.categories).toSet()
        }

        //Convert categories set into a list
        val categoriesList = categoriesSet.toMutableList()

        //Swap first and "Select Category" elements, if the list contains "Select Category element"
        val selectCategory = context.getString(R.string.select_category)
        if (categoriesList.contains(selectCategory)) {
            categoriesList[categoriesList.indexOf(selectCategory)] =
                categoriesList[0]
            categoriesList[0] = selectCategory
        }

        //Swap last & "Other" elements, if the list contains "Other" element
        val other = context.getString(R.string.other)
        if (categoriesList.contains(other)) {
            categoriesList[categoriesList.indexOf(other)] =
                categoriesList[categoriesList.size - 1]
            categoriesList[categoriesList.size - 1] = other
        }

        //Return the categories
        return categoriesList
    }
}