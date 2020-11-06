package com.asif.expensemanager.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.asif.expensemanager.R
import com.asif.expensemanager.background.db.Transaction
import com.asif.expensemanager.ui.adapters.TransactionsAdapter
import kotlinx.android.synthetic.main.fragment_month_year.view.*
import kotlinx.android.synthetic.main.fragment_report.view.recycler

/*
* Constants for the arguments passed to this fragment
 */
private const val ARG_NAME = "com.asif.expensemanager.month_year"
private const val ARG_BALANCE = "com.asif.expensemanager.balance"
private const val ARG_EXPENSES = "com.asif.expensemanager.expenses"
private const val ARG_TRANSACTIONS = "com.asif.expensemanager.transactions"

/**
 * Fragment to show the data of specific month/year
 */
class MonthYearFragment : Fragment() {
    /**
     * A listener for this fragment
     */
    interface MonthYearFragListener {
        fun showCalendar(monthName: String, transactions: ArrayList<Transaction>?)
    }

    //Instance of the listener
    private var listener: MonthYearFragListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return if (context != null && arguments != null) {
            // Inflate the layout for this fragment
            val root = inflater.inflate(R.layout.fragment_month_year, container, false)

            //Get month/year name from the arguments
            val name = arguments!!.getString(ARG_NAME)

            //Get the transactions from the arguments object
            val transactions: ArrayList<Transaction>? = arguments!!.getParcelableArrayList(
                ARG_TRANSACTIONS
            )

            if (name != null) {
                //Set toolbar's title
                root.toolbarTitle.text = name

                //Show or hide Calendar button, depending on wither it is a year, or a month
                if (name.toIntOrNull() == null && context is MonthYearFragListener) {
                    root.imgCalendar.visibility = View.VISIBLE
                    listener = context as MonthYearFragListener
                    root.imgCalendar.setOnClickListener {
                        listener!!.showCalendar(name, transactions)
                    }
                } else {
                    root.imgCalendar.visibility = View.GONE
                }
            }

            //Set toolbar's navigation icon, and its OnClickListener
            root.toolbar.setNavigationIcon(R.drawable.ic_clear)
            root.toolbar.setNavigationOnClickListener {
                if (activity != null) {
                    activity!!.supportFragmentManager.popBackStackImmediate()
                }
            }

            //Create an adapter from the TextView
            val adapter = TransactionsAdapter(context, true)

            //Set adapter's items
            adapter.setItems(transactions)

            //Set adapter's balance & expenses
            adapter.setBalance(arguments!!.getFloat(ARG_BALANCE, 0f))
            adapter.setExpenses(arguments!!.getFloat(ARG_EXPENSES, 0f))

            //Set RecyclerView's adapter & layout manager
            root.recycler.adapter = adapter
            root.recycler.layoutManager = LinearLayoutManager(context!!)

            //Finally, return the root view
            root
        } else {
            super.onCreateView(inflater, container, savedInstanceState)
        }
    }

    /**
     * Companion object, to create a new instance of this fragment
     */
    companion object {
        @JvmStatic
        fun newInstance(
            monthYearName: String,
            balance: Float,
            expenses: Float,
            transactions: ArrayList<Transaction>
        ) =
            MonthYearFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_NAME, monthYearName)
                    putFloat(ARG_BALANCE, balance)
                    putFloat(ARG_EXPENSES, expenses)
                    putParcelableArrayList(ARG_TRANSACTIONS, transactions)
                }
            }
    }
}