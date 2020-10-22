package com.asif.expensemanager.db

import android.app.Application
import android.os.AsyncTask
import androidx.lifecycle.LiveData

class MonthRepository(application: Application) {
    private val monthDao: MonthDao = ExpensesDB.getInstance(application).monthDao()

    fun getAllMonths(): LiveData<List<Month>> = monthDao.getAll()

    fun getMonthsById(id: Int): LiveData<List<Month>> = monthDao.getById(id)

    fun insertMonth(month: Month) {
        InsertMonthAsyncTask(monthDao).execute(month)
    }

    fun updateMonth(month: Month) {
        UpdateMonthAsyncTask(monthDao).execute(month)
    }

    fun deleteMonth(month: Month) {
        DeleteMonthAsyncTask(monthDao).execute(month)
    }

    companion object {
        internal class InsertMonthAsyncTask(private val dao: MonthDao) :
            AsyncTask<Month, Unit, Unit>() {
            override fun doInBackground(vararg params: Month?) {
                for (param in params) {
                    if (param != null) {
                        dao.insert(param)
                    }
                }
            }

        }

        internal class UpdateMonthAsyncTask(private val dao: MonthDao) :
            AsyncTask<Month, Unit, Unit>() {
            override fun doInBackground(vararg params: Month?) {
                for (param in params) {
                    if (param != null) {
                        dao.update(param)
                    }
                }
            }

        }

        internal class DeleteMonthAsyncTask(private val dao: MonthDao) :
            AsyncTask<Month, Unit, Unit>() {
            override fun doInBackground(vararg params: Month?) {
                for (param in params) {
                    if (param != null) {
                        dao.delete(param)
                    }
                }
            }

        }
    }

}