package com.asif.expensemanager.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.asif.expensemanager.R
import com.asif.expensemanager.background.db.Transaction
import com.asif.expensemanager.background.db.TransactionViewModel
import com.asif.expensemanager.background.utils.DateUtils
import com.asif.expensemanager.ui.adapters.MonthYearAdapter
import kotlinx.android.synthetic.main.fragment_report.view.*

/**
 * Fragment that shows a report on user's transactions
 */
class ReportFragment : Fragment() {
    /**
     * Listener for this fragment
     */
    interface ReportListener {
        fun onHomeSelected()
        fun onShowAllTransactions()
    }

    //Instance of the listener
    private var listener: ReportListener? = null

    override fun onAttach(context: Context) {
        //Initialize the listener
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
        //Inflate the layout
        val root = inflater.inflate(R.layout.fragment_report, container, false)

        //Set bottom navigation view selected item, and a selection listener
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

        //Show the progress bar
        root.recycler.visibility = View.GONE
        root.tvEmpty.visibility = View.GONE
        root.progress.visibility = View.VISIBLE

        if (context != null) {
            //Setup the report types spinner
            val spinnerAdapter = ArrayAdapter.createFromResource(
                context!!,
                R.array.report_types,
                android.R.layout.simple_spinner_item
            )
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            root.spinnerReportType.adapter = spinnerAdapter

            //Setup the recycler view, and its adapter
            val recyclerAdapter = MonthYearAdapter(context!!)
            root.recycler.adapter = recyclerAdapter
            root.recycler.layoutManager = LinearLayoutManager(context!!)

            //Set a listener on report type spinner, to refresh the data when the user selects a new type
            root.spinnerReportType.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        //Call setupPeriodicReport method, to setup the report, according to the selected item
                        when (context!!.resources.getStringArray(R.array.report_types)[position]) {
                            context!!.resources.getString(R.string.monthly) -> setupPeriodicReport(
                                root,
                                recyclerAdapter,
                                true
                            )
                            context!!.resources.getString(R.string.annually) -> setupPeriodicReport(
                                root,
                                recyclerAdapter,
                                false
                            )
                        }
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                    }

                }

        }

        //Return the root
        return root
    }

    /**
     * Method to setup the report
     */
    fun setupPeriodicReport(root: View, adapter: MonthYearAdapter, isMonthly: Boolean) {
        //Create a transactions ArrayList
        val transactions = ArrayList<ArrayList<Transaction>>()

        //Create a String ArrayList, that holds the months/years
        val timeUnits = ArrayList<String>()

        //Get the ViewModel
        val viewModel = ViewModelProvider(this).get(TransactionViewModel::class.java)

        //Get past transactions from the ViewModel, and observe when data changes
        viewModel.getPastTransactions(DateUtils.getCurrentTimestamp())
            .observe(viewLifecycleOwner, Observer {
                //Show the progress bar
                root.recycler.visibility = View.GONE
                root.tvEmpty.visibility = View.GONE
                root.progress.visibility = View.VISIBLE

                //If there are past transactions, process their data to produce the report
                if (it.isNotEmpty()) {

                    //Set the initial left bound to first item's start date,
                    // since the items are ordered according to their start date
                    var leftBound = it[0].startDate

                    //Set the right bound to the current time,
                    // because we don't want to show the user the transactions that didn't happen yet
                    val rightBound = DateUtils.getCurrentTimestamp()

                    //Keep looping while the left bound didn't exceed the right bound yet
                    while (leftBound <= rightBound) {
                        //Get month/year end
                        val unitEnd = if (isMonthly)
                            DateUtils.getMonthEnd(leftBound)
                        else
                            DateUtils.getYearEnd(leftBound)

                        //Create an ArrayList for this year/month
                        val unitTransactions = ArrayList<Transaction>()

                        //Set the transactions of this year/month
                        for (transaction in it) {
                            if (!(transaction.startDate >= unitEnd || transaction.endDate < leftBound)) {
                                unitTransactions.add(transaction)
                            }
                        }

                        //Add the year/month's transactions ArrayList, if it is not empty
                        if (unitTransactions.isNotEmpty()) {
                            timeUnits.add(
                                if (isMonthly)
                                    DateUtils.getMonthName(context!!, leftBound)
                                else
                                    DateUtils.getYearName(leftBound)

                            )
                            transactions.add(unitTransactions)
                        }

                        //Increase the left bound to be the month/year ebd
                        leftBound = unitEnd
                    }

                    //Set the adapter's data
                    adapter.setData(timeUnits, transactions, isMonthly)

                    //Show the recycler view
                    root.recycler.visibility = View.VISIBLE

                } else {
                    //Else; Show the empty TextView, to tell the user that no data is available at the moment
                    root.tvEmpty.visibility = View.VISIBLE
                }

                //Hide the progress bar
                root.progress.visibility = View.GONE
            })
    }
}