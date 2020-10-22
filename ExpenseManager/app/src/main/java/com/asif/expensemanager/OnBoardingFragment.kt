package com.asif.expensemanager

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_on_boarding.view.*

class OnBoardingFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_on_boarding, container, false)

        root.btnContinue.setOnClickListener {
            val name = root.inputName.editText?.text.toString().trim()
            if(name.isEmpty()){
                root.inputName.error = resources.getString(R.string.name_error)
            }else{
                root.inputName.error = null
                val budget = root.inputBudget.editText?.text.toString().toFloatOrNull()
                val income = root.inputIncome.editText?.text.toString().toFloatOrNull()
                if(budget != null
                    && income != null
                    && budget > 0
                    && income > 0){
                    Utils.saveBudget(context!!, budget)
                    Utils.saveIncome(context!!, income)
                }
                Utils.saveUserName(context!!, name)

                //TODO: Remove intent and add required functionality
                if(context is MainActivity) {
                    startActivity(Intent(context, MainActivity::class.java))
                }
            }
        }

        return root
    }
}