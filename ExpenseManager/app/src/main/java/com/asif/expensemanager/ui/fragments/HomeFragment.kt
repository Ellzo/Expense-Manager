package com.asif.expensemanager.ui.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.asif.expensemanager.R
import com.asif.expensemanager.background.db.TransactionViewModel
import com.asif.expensemanager.background.utils.DateUtils
import com.asif.expensemanager.ui.adapters.TransactionsAdapter
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

/**
 * Fragment the shows the Home screen
 */
class HomeFragment : Fragment() {
    /**
     * A listener for this fragment
     */
    interface HomeFragmentListener {
        fun onAddTransactionClicked()
        fun onShowAllTransactions()
        fun onReportSelected()
    }

    //Listener instance
    private var listener: HomeFragmentListener? = null

    override fun onAttach(context: Context) {
        //Initialize the listener
        if (context is HomeFragmentListener) {
            listener = context
        }

        super.onAttach(context)
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        //Set the bottom navigation view selected item
        root.bottomNav.selectedItemId = R.id.home

        //Show the progressbar, and hide everything else temporarily
        root.progress.visibility = View.VISIBLE
        root.linearBalance.visibility = View.GONE
        root.card.visibility = View.GONE
        root.pie.visibility = View.GONE
        root.btnAdd.visibility = View.GONE
        root.tvUpcoming.visibility = View.GONE
        root.btnViewAll.visibility = View.GONE
        root.recyclerUpcoming.visibility = View.GONE

        //Get the TransactionViewModel
        val viewModel = ViewModelProvider(this).get(TransactionViewModel::class.java)


        //Setup the Upcoming Transactions RecyclerView, and its adapter
        val adapter = TransactionsAdapter(context)
        root.recyclerUpcoming.adapter = adapter
        root.recyclerUpcoming.layoutManager = LinearLayoutManager(context)

        //Get 3 upcoming transactions from the database, and observe when data changes
        viewModel.getUpcomingTransactions(3, DateUtils.getCurrentTimestamp())
            .observe(viewLifecycleOwner, Observer {
                //Update the adapter's items to the new data
                adapter.setItems(it)

                //Show the RecyclerView if the data is not empty
                if (it.isEmpty()) {
                    root.tvUpcoming.visibility = View.GONE
                    root.btnViewAll.visibility = View.GONE
                    root.recyclerUpcoming.visibility = View.GONE
                } else {
                    root.tvUpcoming.visibility = View.VISIBLE
                    root.btnViewAll.visibility = View.VISIBLE
                    root.recyclerUpcoming.visibility = View.VISIBLE
                }
            })

        //Floats representing the balance of each payment type
        var cash: Float
        var credit: Float
        var cheque: Float
        var other: Float
        var balance: Float

        //Set the jump step size to a single day
        val stepSize = 1000 * 60 * 60 * 24L

        //Get the past transactions from the database, and observe when data changes
        viewModel.getPastTransactions(DateUtils.getCurrentTimestamp())
            .observe(viewLifecycleOwner, Observer {

                //Show the progress bar
                root.progress.visibility = View.VISIBLE
                root.linearBalance.visibility = View.GONE
                root.card.visibility = View.GONE
                root.pie.visibility = View.GONE
                root.btnAdd.visibility = View.GONE

                //Set payment types balances initial values to 0
                cash = 0f
                credit = 0f
                cheque = 0f
                other = 0f
                balance = 0f

                //Loop on the past transactions list, to calculate the balance
                for (transaction in it) {
                    for (i in transaction.startDate..transaction.endDate step stepSize) {
                        if (i <= DateUtils.getCurrentTimestamp()) {
                            balance += transaction.amount
                            when (transaction.type) {
                                resources.getString(R.string.cash) -> cash += transaction.amount
                                resources.getString(R.string.credit) -> credit += transaction.amount
                                resources.getString(R.string.cheque) -> cheque += transaction.amount
                                resources.getString(R.string.other_pays) -> other += transaction.amount
                            }
                        } else {
                            break
                        }
                    }
                }

                //Set payment types balances TextViews text
                root.tvCash.text = "${resources.getString(R.string.cash)} $$cash"
                root.tvCredit.text = "${resources.getString(R.string.credit)} $$credit"
                root.tvCheque.text = "${resources.getString(R.string.cheque)} $$cheque"
                root.tvOther.text = "${resources.getString(R.string.other_pays)} $$other"

                //Set the balance text
                root.tvNetBalanceVal.text = "$$balance"

                //Create a Map, that links each payment type balance color with its value
                val map = mapOf(
                    ContextCompat.getColor(
                        context!!,
                        R.color.colorCash
                    ) to cash.absoluteValue.roundToInt(),
                    ContextCompat.getColor(
                        context!!,
                        R.color.colorCredit
                    ) to credit.absoluteValue.roundToInt(),
                    ContextCompat.getColor(
                        context!!,
                        R.color.colorCheque
                    ) to cheque.absoluteValue.roundToInt(),
                    ContextCompat.getColor(
                        context!!,
                        R.color.colorOtherPays
                    ) to other.absoluteValue.roundToInt()
                )

                //Send the map to the circular graph
                root.pie.colorsValuesMap = map

                //Invalidate the graph, to draw it again with new data
                root.pie.postInvalidate()

                //Hide the progress bar, and show the data
                root.progress.visibility = View.GONE
                root.linearBalance.visibility = View.VISIBLE
                root.card.visibility = View.VISIBLE
                root.btnAdd.visibility = View.VISIBLE
                if (cash != 0f || credit != 0f || cheque != 0f || other != 0f)
                    root.pie.visibility = View.VISIBLE
            })


        if (listener != null) {

            //Set "Add Transaction" button listener
            root.btnAdd.setOnClickListener {
                listener!!.onAddTransactionClicked()
            }

            //Set a listener for "View All" button
            root.btnViewAll.setOnClickListener {
                listener!!.onShowAllTransactions()
            }

        }

        if (activity != null) {
            //Set settings button listener
            root.btnSettings.setOnClickListener {
                activity!!.supportFragmentManager.beginTransaction()
                    .replace(R.id.container, SettingsFragment())
                    .addToBackStack(null)
                    .commit()
            }
        }

        //Set a listener for the bottom navigation view
        root.bottomNav.setOnNavigationItemSelectedListener {
            if (listener != null) {
                when (it.itemId) {
                    R.id.all_transaction ->
                        listener!!.onShowAllTransactions()
                    R.id.report ->
                        listener!!.onReportSelected()
                }
            }
            false
        }

        //Finally, return the root view
        return root
    }
}