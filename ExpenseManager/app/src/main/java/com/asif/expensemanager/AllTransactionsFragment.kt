package com.asif.expensemanager

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.asif.expensemanager.db.TransactionViewModel
import kotlinx.android.synthetic.main.fragment_all_transactions.view.*

class AllTransactionsFragment : Fragment() {
    interface AllTransactionsListener {
        fun onHomeSelected()
        fun onReportSelected()
    }

    private var listener: AllTransactionsListener? = null

    override fun onAttach(context: Context) {
        if (context is AllTransactionsListener) {
            listener = context
        }

        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_all_transactions, container, false)

        root.progress.visibility = View.VISIBLE
        root.recycler.visibility = View.GONE

        setupSpinner(root)

        val transactionsAdapter = TransactionsAdapter()
        root.recycler.adapter = transactionsAdapter
        root.recycler.layoutManager = LinearLayoutManager(context)
        if (context != null) {
            val dividerItemDecoration =
                DividerItemDecoration(context!!, LinearLayoutManager.VERTICAL)
            root.recycler.addItemDecoration(dividerItemDecoration)
        }

        val viewModel = ViewModelProvider(this).get(TransactionViewModel::class.java)

        viewModel.getAllTransactions().observe(viewLifecycleOwner, Observer {
            transactionsAdapter.updateItems(it)
        })

        root.progress.visibility = View.GONE
        root.recycler.visibility = View.VISIBLE

        root.editSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                root.progress.visibility = View.VISIBLE
                root.recycler.visibility = View.GONE
                if (s == null) {
                    viewModel.getAllTransactions().observe(viewLifecycleOwner, Observer {
                        transactionsAdapter.updateItems(it)
                        if (it.isEmpty()) {
                            root.tvEmpty.visibility = View.VISIBLE
                            root.progress.visibility = View.GONE
                            root.recycler.visibility = View.GONE
                        } else {
                            root.tvEmpty.visibility = View.GONE
                            root.progress.visibility = View.GONE
                            root.recycler.visibility = View.VISIBLE
                        }
                    })
                } else {
                    if (root.spinnerCat.selectedItemPosition == 0) {
                        viewModel.searchTransaction(s.toString())
                            .observe(viewLifecycleOwner, Observer {
                                transactionsAdapter.updateItems(it)
                                if (it.isEmpty()) {
                                    root.tvEmpty.visibility = View.VISIBLE
                                    root.progress.visibility = View.GONE
                                    root.recycler.visibility = View.GONE
                                } else {
                                    root.tvEmpty.visibility = View.GONE
                                    root.progress.visibility = View.GONE
                                    root.recycler.visibility = View.VISIBLE
                                }
                            })
                    } else {
                        viewModel.searchTransactionByCategory(
                            root.spinnerCat.selectedItem.toString(),
                            s.toString()
                        )
                            .observe(viewLifecycleOwner, Observer {
                                transactionsAdapter.updateItems(it)
                                if (it.isEmpty()) {
                                    root.tvEmpty.visibility = View.VISIBLE
                                    root.progress.visibility = View.GONE
                                    root.recycler.visibility = View.GONE
                                } else {
                                    root.tvEmpty.visibility = View.GONE
                                    root.progress.visibility = View.GONE
                                    root.recycler.visibility = View.VISIBLE
                                }
                            })
                    }
                }
                root.progress.visibility = View.GONE
                root.recycler.visibility = View.VISIBLE
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })

        root.spinnerCat?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                root.progress.visibility = View.VISIBLE
                root.recycler.visibility = View.GONE
                if (root.spinnerCat.selectedItemPosition == 0) {
                    viewModel.searchTransaction(root.editSearch.text.toString())
                        .observe(viewLifecycleOwner, Observer {
                            transactionsAdapter.updateItems(it)
                            if (it.isEmpty()) {
                                root.tvEmpty.visibility = View.VISIBLE
                                root.progress.visibility = View.GONE
                                root.recycler.visibility = View.GONE
                            } else {
                                root.tvEmpty.visibility = View.GONE
                                root.progress.visibility = View.GONE
                                root.recycler.visibility = View.VISIBLE
                            }
                        })
                } else {
                    viewModel.searchTransactionByCategory(
                        root.spinnerCat.selectedItem.toString(),
                        root.editSearch.text.toString()
                    )
                        .observe(viewLifecycleOwner, Observer {
                            transactionsAdapter.updateItems(it)
                            if (it.isEmpty()) {
                                root.tvEmpty.visibility = View.VISIBLE
                                root.progress.visibility = View.GONE
                                root.recycler.visibility = View.GONE
                            } else {
                                root.tvEmpty.visibility = View.GONE
                                root.progress.visibility = View.GONE
                                root.recycler.visibility = View.VISIBLE
                            }
                        })
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }

        root.bottomNav.setOnNavigationItemSelectedListener {
            if (listener != null) {
                when (it.itemId) {
                    R.id.home ->
                        listener!!.onHomeSelected()
                    R.id.report ->
                        listener!!.onReportSelected()
                }
            }
            false
        }

        return root
    }

    private fun setupSpinner(root: View) {
        if (context != null) {
            val adapter = ArrayAdapter.createFromResource(
                context!!,
                R.array.categories,
                android.R.layout.simple_spinner_item
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            root.spinnerCat.adapter = adapter
        }
    }
}