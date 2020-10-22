package com.asif.expensemanager.db

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData

class MonthViewModel(application: Application): AndroidViewModel(application){
    private val repo = MonthRepository(application)

    fun getAllMonths(): LiveData<List<Month>> = repo.getAllMonths()

    fun getMonthsById(id: Int): LiveData<List<Month>> = repo.getMonthsById(id)

    fun insertMonth(month: Month) = repo.insertMonth(month)

    fun updateMonth(month: Month) = repo.updateMonth(month)

    fun deleteMonth(month: Month) = repo.deleteMonth(month)
}