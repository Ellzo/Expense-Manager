package com.asif.expensemanager.ui.fragments

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
import androidx.recyclerview.widget.LinearLayoutManager
import com.asif.expensemanager.R
import com.asif.expensemanager.background.db.TransactionViewModel
import com.asif.expensemanager.background.utils.SharedPrefsUtils
import com.asif.expensemanager.ui.adapters.TransactionsAdapter
import kotlinx.android.synthetic.main.fragment_all_transactions.view.*

/**
 * Fragment that represents the screen that shows AllTransactions to the user
 */
class AllTransactionsFragment : Fragment() {
    /**
     * A Listener for this Fragment
     */
    interface AllTransactionsListener {
        fun onHomeSelected()
        fun onReportSelected()
    }

    //An instance of the listener
    private var listener: AllTransactionsListener? = null

    override fun onAttach(context: Context) {
        //Initialize the listener, if the context is an instance of AllTransactionsListener
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
        //Inflate the layout
        val root = inflater.inflate(R.layout.fragment_all_transactions, container, false)

        //Show progressbar
        root.progress.visibility = View.VISIBLE
        root.recycler.visibility = View.GONE

        //Setup the categories spinner
        setupSpinner(root)

        //Setup the recyclerview, and it's adapter
        val transactionsAdapter = TransactionsAdapter(context)
        root.recycler.adapter = transactionsAdapter
        root.recycler.layoutManager = LinearLayoutManager(context)

        //Get the TransactionViewModel
        val viewModel = ViewModelProvider(this).get(TransactionViewModel::class.java)

        //Get all transactions from the ViewModel, and notify the adapter when data changes
        viewModel.getAllTransactions().observe(viewLifecycleOwner, Observer {
            transactionsAdapter.setItems(it)
            root.progress.visibility = View.GONE
            root.recycler.visibility = View.VISIBLE
        })

        //Set a listener to the searchEditText, to listener when input text changes, to do search the database
        root.inputSearch.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            /**
             * This overridden method will search the database for the entered keyword,
             *  depending also on the chosen category.
             */
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                root.progress.visibility = View.VISIBLE
                root.recycler.visibility = View.GONE

                if (s == null) {
                    viewModel.getAllTransactions().observe(viewLifecycleOwner, Observer {
                        transactionsAdapter.setItems(it)
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
                                transactionsAdapter.setItems(it)
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
                                transactionsAdapter.setItems(it)
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

        //Set a listener for listen for category selection
        root.spinnerCat?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            /**
             * This overridden method will search in the selected category transactions for the entered keyword,
             */
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                root.progress.visibility = View.VISIBLE
                root.recycler.visibility = View.GONE

                if (root.spinnerCat.selectedItemPosition == 0) {
                    viewModel.searchTransaction(root.inputSearch.editText?.text.toString())
                        .observe(viewLifecycleOwner, Observer {
                            transactionsAdapter.setItems(it)
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
                        root.inputSearch.editText?.text.toString()
                    )
                        .observe(viewLifecycleOwner, Observer {
                            transactionsAdapter.setItems(it)
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

        //Set a listener for the Bottom Navigation View
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

        //Return the root view
        return root
    }

    /*
    * Method to setup the categories spinner
     */
    private fun setupSpinner(root: View) {
        if (context != null) {
            //Create an adapter for the spinner
            val adapter = ArrayAdapter(
                context!!,
                android.R.layout.simple_spinner_item,
                SharedPrefsUtils.getCategories(context!!).toList()
            )

            //Set the adapter's DropDownView
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            //Set the spinner's adapter
            root.spinnerCat.adapter = adapter
        }
    }
}