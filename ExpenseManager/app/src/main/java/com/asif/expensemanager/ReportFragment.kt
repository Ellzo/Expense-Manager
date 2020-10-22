package com.asif.expensemanager

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.asif.expensemanager.db.Transaction
import com.asif.expensemanager.db.TransactionViewModel
import kotlinx.android.synthetic.main.fragment_report.view.*

class ReportFragment : Fragment() {
    interface ReportListener {
        fun onHomeSelected()
        fun onShowAllTransactions()
    }

    private var listener: ReportListener? = null

    override fun onAttach(context: Context) {
        if (context is ReportListener) {
            listener = context
        }

        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_report, container, false)

        root.bottomNav.selectedItemId = R.id.report
        root.bottomNav.setOnNavigationItemSelectedListener {
            if (listener != null) {
                when (it.itemId) {
                    R.id.home -> listener!!.onHomeSelected()
                    R.id.all_transaction -> listener!!.onShowAllTransactions()
                }
            }
            false
        }

        val adapter = MonthYearAdapter(context!!)
        root.recycler.adapter = adapter
        root.recycler.layoutManager = LinearLayoutManager(context!!)

        val viewModel = ViewModelProvider(this).get(TransactionViewModel::class.java)
        val transactions = ArrayList<ArrayList<Transaction>>()
        val months = ArrayList<String>()
        /*
        viewModel.getFirst(1).observe(viewLifecycleOwner, Observer { first ->
            val c = Calendar.getInstance()
            c.timeInMillis = first[0].startDate
            viewModel.getLast(1).observe(viewLifecycleOwner, Observer { last ->
                Log.e("onCreateView: ", "Last: ${last[0].name} End: ${Utils.getMonthName(context!!, last[0].endDate)}")
                var start = first[0].startDate
                while (start <= last[0].endDate) {
                    val monthEnd = Utils.getMonthEnd(start)
                    viewModel.getInRange(start, monthEnd)
                        .observe(viewLifecycleOwner, Observer {
                            if (it.isNotEmpty() && context != null) {
                                Log.e( "onCreateView: ", "Month: ${Utils.getMonthName(context!!, start)}")
                                months.add(Utils.getMonthName(context!!, start))
                                transactions.add(it as ArrayList<Transaction>)
                            }
                        })
                    start = monthEnd
                }
                adapter.setData(months, transactions)
            })
        })*/
        viewModel.getAllTransactions().observe(viewLifecycleOwner, Observer {
            // TODO: 10/21/20 : Move all the logic to a background thread
            if (it.isNotEmpty()) {
                var leftBound = it[0].startDate
                var rightBound = it[0].endDate
                for (transaction in it)
                    if (transaction.startDate < leftBound)
                        leftBound = transaction.startDate
                while (leftBound <= rightBound) {
                    val monthEnd = Utils.getMonthEnd(leftBound)
                    val monthTransactions = ArrayList<Transaction>()
                    for (transaction in it) {
                        if (!(transaction.startDate >= monthEnd || transaction.endDate < leftBound)) {
                            monthTransactions.add(transaction)
                        }
                    }
                    if (monthTransactions.isNotEmpty()) {
                        months.add(Utils.getMonthName(context!!, leftBound))
                        transactions.add(monthTransactions)
                    }
                    leftBound = monthEnd
                }
                adapter.setData(months, transactions)
            }
        })

        return root
    }
}