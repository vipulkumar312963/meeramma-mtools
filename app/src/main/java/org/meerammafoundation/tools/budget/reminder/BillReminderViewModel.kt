package org.meerammafoundation.tools.budget.reminder

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class BillReminderViewModel(application: Application) : AndroidViewModel(application) {

    private val database = BillReminderDatabase.getDatabase(application)
    private val repository = BillReminderRepository(database)

    val allBills: LiveData<List<BillReminder>> = repository.getAllBills().asLiveData()
    val unpaidBills: LiveData<List<BillReminder>> = repository.getUnpaidBills().asLiveData()
    val paidBills: LiveData<List<BillReminder>> = repository.getPaidBills().asLiveData()
    val billsWithStatus: LiveData<List<BillReminderWithStatus>> = repository.getBillsWithStatus().asLiveData()

    private val _selectedBill = MutableLiveData<BillReminder?>()
    val selectedBill: LiveData<BillReminder?> = _selectedBill

    fun selectBill(bill: BillReminder) {
        _selectedBill.value = bill
    }

    fun createBill(
        name: String,
        amount: Double,
        dueDate: Long,
        category: BillCategory,
        recurrence: RecurrenceType,
        notes: String = ""
    ) = viewModelScope.launch {
        repository.createBill(name, amount, dueDate, category, recurrence, notes)
    }

    fun updateBill(bill: BillReminder) = viewModelScope.launch {
        repository.updateBill(bill)
    }

    fun deleteBill(bill: BillReminder) = viewModelScope.launch {
        repository.deleteBill(bill)
    }

    fun markAsPaid(billId: Long) = viewModelScope.launch {
        repository.markAsPaid(billId)
    }

    fun markAsUnpaid(billId: Long) = viewModelScope.launch {
        repository.markAsUnpaid(billId)
    }
}