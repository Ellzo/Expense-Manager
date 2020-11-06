package com.asif.expensemanager.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.asif.expensemanager.R
import com.asif.expensemanager.background.utils.SharedPrefsUtils
import kotlinx.android.synthetic.main.fragment_on_boarding.view.*

/**
 * Fragment that appears on the screen when the user first uses the app,
 * it gets some initial data from the user,
 * or when the user edits those data
 */
class OnBoardingFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //Inflate the layout
        val root = inflater.inflate(R.layout.fragment_on_boarding, container, false)

        if (context != null) {

            //Check if there is already a saved name, i.e., we're editing the user's settings
            val initName = SharedPrefsUtils.getUserName(context!!)
            if (!initName.isNullOrEmpty()) {
                //Show user's name on the editText
                root.inputName.editText?.setText(initName)

                //Get the budget
                val initBudget = SharedPrefsUtils.getBudget(context!!)

                //Show monthly budget if it is larger than 0
                if (initBudget > 0f) {
                    root.inputBudget.editText?.setText(initBudget.toString())
                }

                //Get the income
                val initIncome = SharedPrefsUtils.getIncome(context!!)

                //Show user's income if it is larger than 0
                if (initIncome > 0f) {
                    root.inputIncome.editText?.setText(initIncome.toString())
                }

            }

            //Check data validation gotten from the EditTexts when the user clicks "Save" button,
            // if valid, save to SharedPreferences, using SharedPrefsUtils static methods.
            root.btnSave.setOnClickListener {

                val name = root.inputName.editText?.text.toString().trim()

                if (name.isEmpty()) {
                    root.inputName.error = resources.getString(R.string.name_error)

                } else {

                    root.inputName.error = null

                    val budget = root.inputBudget.editText?.text.toString().toFloatOrNull()
                    val income = root.inputIncome.editText?.text.toString().toFloatOrNull()

                    SharedPrefsUtils.saveBudget(context!!, budget)

                    SharedPrefsUtils.saveIncome(context!!, income)

                    SharedPrefsUtils.saveUserName(context!!, name)

                    if (activity != null) {
                        activity!!.supportFragmentManager.beginTransaction()
                            .replace(R.id.container, HomeFragment())
                            .commit()
                    }
                }
            }

        }

        //Return the root view
        return root
    }
}