package com.asif.expensemanager.ui.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.asif.expensemanager.R
import com.asif.expensemanager.background.db.TransactionViewModel
import com.asif.expensemanager.background.utils.SharedPrefsUtils
import kotlinx.android.synthetic.main.edit_category_layout.view.*
import kotlinx.android.synthetic.main.fragment_categories.view.*

/**
 * Fragment displayed on the screen to show & edit Transactions categories
 */
class CategoriesFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //Inflate the layout
        val root = inflater.inflate(R.layout.fragment_categories, container, false)

        if (activity != null) {
            //Get the categories list
            val categories = SharedPrefsUtils.getCategories(activity!!).toMutableList()

            //Remove "Selected Category" element, because we don't want the user to edit it
            categories.remove(activity!!.getString(R.string.select_category))

            //Initialize the adapter
            val adapter = ArrayAdapter(
                activity!!,
                android.R.layout.simple_list_item_1,
                categories
            )

            //Set categories' ListView adapter
            root.listCategories.adapter = adapter

            //Get the ViewModel, since we may need when the user makes any changes
            val viewModel = ViewModelProvider(this).get(TransactionViewModel::class.java)

            //Set a click listener for the "Add Transaction" button
            root.btnAddCat.setOnClickListener {
                //Inflate Edit Category Dialog layout
                val dialogView = LayoutInflater.from(activity!!)
                    .inflate(R.layout.edit_category_layout, null, false)

                //Create the dialog build, and set the view
                val builder = AlertDialog.Builder(activity)
                    .setView(dialogView)

                //Edit the dialog title
                dialogView.tvTitle.text = activity!!.getString(R.string.add_category)

                //Setup dialog's buttons
                builder.setPositiveButton(R.string.save) { _, _ ->
                    //Get entered text
                    val inputCategory = dialogView.editCategory.text.toString().trim()

                    //Do the required changes, if input category is not empty
                    if (inputCategory.isNotEmpty() && !categories.contains(inputCategory)) {
                        //Add the entered category to the list
                        categories.add(inputCategory)

                        //Add "Select Category" item at the beginning of the list, since it is deleted earlier
                        categories.add(0, activity!!.getString(R.string.select_category))

                        //Save the updated categories list
                        SharedPrefsUtils.saveCategories(activity!!, categories.toSet())

                        //Remove "Select Category" item again
                        categories.removeAt(0)

                        //Notify the adapter of data change
                        adapter.notifyDataSetChanged()
                    }

                }.setNegativeButton(R.string.cancel) { _, _ ->
                    //Do nothing, only dismiss the dialog
                }

                //Create and show the dialog
                builder.create().show()
            }

            //Set a click listener for the ListView items
            root.listCategories.setOnItemClickListener { _, _, position, _ ->
                //Inflate Edit Category Dialog layout
                val dialogView = LayoutInflater.from(activity!!)
                    .inflate(R.layout.edit_category_layout, null, false)

                //Create the dialog build, and set the view
                val builder = AlertDialog.Builder(activity)
                    .setView(dialogView)

                //Set Category EditText's text
                dialogView.editCategory.setText(categories[position])

                //Setup dialog's buttons
                builder.setPositiveButton(R.string.save) { _, _ ->
                    //Get entered text
                    val inputCategory = dialogView.editCategory.text.toString().trim()

                    //Do the required changes, if input category is not empty
                    if (inputCategory.isNotEmpty() && categories[position] != inputCategory) {
                        //Replace the old category in the database, with the new one
                        viewModel.searchTransactionByCategory(categories[position], "")
                            .observe(viewLifecycleOwner, Observer {
                                for (transaction in it) {
                                    transaction.category = inputCategory
                                    viewModel.updateTransaction(transaction)
                                }
                            })

                        //Update the category list
                        categories[position] = inputCategory

                        //Add "Select Category" item at the beginning of the list, since it is deleted earlier
                        categories.add(0, activity!!.getString(R.string.select_category))

                        //Save the updated categories list
                        SharedPrefsUtils.saveCategories(activity!!, categories.toSet())

                        //Remove "Select Category" item again
                        categories.removeAt(0)

                        //Notify the adapter of data change
                        adapter.notifyDataSetChanged()
                    }

                }.setNegativeButton(R.string.cancel) { _, _ ->
                    //Do nothing, only dismiss the dialog
                }

                //Create and show the dialog
                builder.create().show()

            }

            //Set a long click listener for ListView items
            root.listCategories.setOnItemLongClickListener { _, _, position, _ ->
                //Show a dialog to delete the category, when the users perform a long click on it
                AlertDialog.Builder(activity!!)
                    .setTitle(R.string.delete_category)
                    .setMessage(
                        activity!!.getString(R.string.delete_question) + " " + categories[position] + activity!!.getString(
                            R.string.question_mark
                        )
                    )
                    .setPositiveButton(R.string.yes) { _, _ ->
                        //Get the transactions of long-clicked category from the database
                        viewModel.searchTransactionByCategory(categories[position], "")
                            .observe(viewLifecycleOwner, Observer {
                                //Remove the category only if it doesn't have any transactions
                                if (it.isEmpty()) {
                                    categories.removeAt(position)
                                    categories.add(
                                        0,
                                        activity!!.getString(R.string.select_category)
                                    )
                                    SharedPrefsUtils.saveCategories(
                                        activity!!,
                                        categories.toSet()
                                    )
                                    categories.removeAt(0)
                                    adapter.notifyDataSetChanged()
                                } else {
                                    //Notify the user if the category is not deleted
                                    Toast.makeText(
                                        activity!!,
                                        R.string.cat_delete_error,
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            })
                    }.setNegativeButton(R.string.no) { _, _ ->
                        //Do nothing, only dismiss the dialog
                    }.create().show()


                true
            }

            //Remove the fragment when the user clicks "clear" button in the toolbar
            root.toolbar.setNavigationOnClickListener {
                activity!!.supportFragmentManager
                    .popBackStackImmediate()
            }
        }

        //Return the root view
        return root
    }
}