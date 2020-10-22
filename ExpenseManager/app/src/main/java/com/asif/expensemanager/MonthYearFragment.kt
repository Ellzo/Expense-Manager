package com.asif.expensemanager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.asif.expensemanager.db.Transaction
import kotlinx.android.synthetic.main.card_month_year.view.*
import kotlinx.android.synthetic.main.fragment_report.view.*

private const val ARG_BALANCE = "com.asif.expensemanager.balance"
private const val ARG_EXPENSES = "com.asif.expensemanager.expenses"
private const val ARG_TRANSACTIONS = "com.asif.expensemanager.transactions"

class MonthYearFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return if (arguments != null && context != null) {
            // Inflate the layout for this fragment
            val root = inflater.inflate(R.layout.fragment_month_year, container, false)
            root.tvBalanceVal.text = arguments!!.getFloat(ARG_BALANCE, 0f).toString()
            val adapter = TransactionsAdapter()
            root.recycler.adapter = adapter
            root.recycler.layoutManager = LinearLayoutManager(context!!)
            adapter.updateItems(arguments!!.getParcelableArrayList(ARG_TRANSACTIONS))
            root
        } else {
            super.onCreateView(inflater, container, savedInstanceState)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(balance: Float, expenses: Float, transactions: ArrayList<Transaction>) =
            MonthYearFragment().apply {
                arguments = Bundle().apply {
                    putFloat(ARG_BALANCE, balance)
                    putFloat(ARG_EXPENSES, expenses)
                    putParcelableArrayList(ARG_TRANSACTIONS, transactions)
                }
            }
    }
}