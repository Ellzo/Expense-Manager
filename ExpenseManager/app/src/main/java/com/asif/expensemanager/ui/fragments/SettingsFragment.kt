package com.asif.expensemanager.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.asif.expensemanager.R
import com.asif.expensemanager.background.utils.SharedPrefsUtils
import kotlinx.android.synthetic.main.fragment_settings.view.*

class SettingsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_settings, container, false)

        if (activity != null) {
            //Get user's basic info from SharedPreferences
            val name = SharedPrefsUtils.getUserName(activity!!)
            val budget = SharedPrefsUtils.getBudget(activity!!)
            val income = SharedPrefsUtils.getIncome(activity!!)

            //Set the texts displayed on the screen
            root.tvNameVal.text = name
            root.tvBudgetVal.text = budget.toString()
            root.tvIncomeVal.text = income.toString()

            //Remove the fragment from the screen when user clicks the "Clear" button in the toolbar
            root.toolbar.setNavigationOnClickListener {
                activity!!.supportFragmentManager
                    .popBackStackImmediate()
            }

            //Show "OnBoarding" Fragment, when users clicks "Edit" button
            root.btnEdit.setOnClickListener {
                activity!!.supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.container, OnBoardingFragment())
                    .addToBackStack(null)
                    .commit()
            }

            //Show CategoriesFragment when users clicks "Edit Categories"
            root.btnEditCats.setOnClickListener {
                activity!!.supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.container, CategoriesFragment())
                    .addToBackStack(null)
                    .commit()
            }
        }

        //Return root
        return root
    }
}