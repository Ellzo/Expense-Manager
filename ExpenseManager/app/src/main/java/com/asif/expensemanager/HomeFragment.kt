package com.asif.expensemanager

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.asif.expensemanager.db.TransactionViewModel
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.navigation_drawer_header.view.*
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

class HomeFragment : Fragment() {
    interface HomeFragmentListener {
        fun onAddTransactionClicked()
        fun onShowAllTransactions()
        fun onReportSelected()
    }

    private var listener: HomeFragmentListener? = null

    override fun onAttach(context: Context) {
        if (context is HomeFragmentListener) {
            listener = context
        }

        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        root.bottomNav.selectedItemId = R.id.home

        root.toolbar.title = resources.getString(R.string.app_name)

        if (context != null)
            root.navView.getHeaderView(0).tvUserName.text = Utils.getUserName(context!!)

        val toggle = ActionBarDrawerToggle(
            activity,
            root.drawer,
            root.toolbar,
            R.string.open_drawer,
            R.string.close_drawer
        )
        root.drawer.addDrawerListener(toggle)
        toggle.syncState()

        root.progress.visibility = View.VISIBLE
        root.homeContainer.visibility = View.GONE

        val viewModel = ViewModelProvider(this).get(TransactionViewModel::class.java)

        val adapter = TransactionsAdapter()
        root.recyclerUpcoming.adapter = adapter
        root.recyclerUpcoming.layoutManager = LinearLayoutManager(context)
        if (context != null) {
            val dividerItemDecoration =
                DividerItemDecoration(context!!, LinearLayoutManager.VERTICAL)
            root.recyclerUpcoming.addItemDecoration(dividerItemDecoration)
        }

        viewModel.getUpcomingTransactions(4, Utils.getCurrentTimestamp())
            .observe(viewLifecycleOwner, Observer {
                root.progress.visibility = View.VISIBLE
                root.homeContainer.visibility = View.GONE
                adapter.updateItems(it)
                root.progress.visibility = View.GONE
                root.homeContainer.visibility = View.VISIBLE
            })

        var cash: Float
        var credit: Float
        var cheque: Float
        var other: Float
        var balance: Float
        val stepSize = 1000 * 60 * 60 * 24L
        viewModel.getPastTransactions(Utils.getCurrentTimestamp())
            .observe(viewLifecycleOwner, Observer {
                root.progress.visibility = View.VISIBLE
                root.homeContainer.visibility = View.GONE

                cash = 0f
                credit = 0f
                cheque = 0f
                other = 0f
                balance = 0f

                for (transaction in it) {
                    for (i in transaction.startDate..transaction.endDate step stepSize) {
                        if (i <= Utils.getCurrentTimestamp()) {
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

                root.tvCash.text = "${resources.getString(R.string.cash)} $$cash"
                root.tvCredit.text = "${resources.getString(R.string.credit)} $$credit"
                root.tvCheque.text = "${resources.getString(R.string.cheque)} $$cheque"
                root.tvOther.text = "${resources.getString(R.string.other_pays)} $$other"

                root.tvNetBalanceVal.text = "$$balance"

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
                root.pie.colorsValuesMap = map
                root.pie.postInvalidate()

                root.progress.visibility = View.GONE
                root.homeContainer.visibility = View.VISIBLE
            })


        if (listener != null) {
            root.btnAdd.setOnClickListener {
                listener!!.onAddTransactionClicked()
            }
        }

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

        root.navView.setNavigationItemSelectedListener {
            if (listener != null) {
                when (it.itemId) {
                    R.id.all_transaction -> {
                        root.drawer.closeDrawer(GravityCompat.START)
                        listener!!.onShowAllTransactions()
                        true
                    }
                    R.id.report -> {
                        root.drawer.closeDrawer(GravityCompat.START)
                        listener!!.onReportSelected()
                        true
                    }
                    else -> false
                }
            } else {
                false
            }
        }

        root.btnViewAll.setOnClickListener {
            listener!!.onShowAllTransactions()
        }

        return root
    }
}