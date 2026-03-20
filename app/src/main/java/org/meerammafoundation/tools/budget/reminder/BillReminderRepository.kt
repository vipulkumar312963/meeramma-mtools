package org.meerammafoundation.tools.budget.reminder

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Calendar

class BillReminderRepository(private val db: BillReminderDatabase) {

    private val dao = db.billReminderDao()

    fun getAllBills(): Flow<List<BillReminder>> = dao.getAllBills()

    fun getUnpaidBills(): Flow<List<BillReminder>> = dao.getUnpaidBills()

    fun getPaidBills(): Flow<List<BillReminder>> = dao.getPaidBills()

    fun getBillById(billId: Long): Flow<BillReminder?> = dao.getBillById(billId)

    suspend fun createBill(
        name: String,
        amount: Double,
        dueDate: Long,
        category: BillCategory,
        recurrence: RecurrenceType,
        notes: String = ""
    ): Long {
        val bill = BillReminder(
            name = name,
            amount = amount,
            dueDate = dueDate,
            category = category,
            recurrence = recurrence,
            notes = notes
        )
        return dao.insertBill(bill)
    }

    suspend fun updateBill(bill: BillReminder) {
        val updatedBill = bill.copy(updatedAt = System.currentTimeMillis())
        dao.updateBill(updatedBill)
    }

    suspend fun deleteBill(bill: BillReminder) {
        dao.deleteBill(bill)
    }

    suspend fun markAsPaid(billId: Long) {
        val now = System.currentTimeMillis()
        dao.markAsPaid(billId, now, now)
    }

    suspend fun markAsUnpaid(billId: Long) {
        dao.markAsUnpaid(billId, System.currentTimeMillis())
    }

    fun getBillsWithStatus(): Flow<List<BillReminderWithStatus>> {
        return dao.getUnpaidBills().map { bills ->
            bills.map { bill ->
                val daysUntilDue = ((bill.dueDate - System.currentTimeMillis()) / (24 * 60 * 60 * 1000)).toInt()
                val status = when {
                    daysUntilDue < 0 -> BillStatus.OVERDUE
                    daysUntilDue == 0 -> BillStatus.DUE_TODAY
                    daysUntilDue <= 7 -> BillStatus.UPCOMING
                    else -> BillStatus.UPCOMING
                }
                BillReminderWithStatus(
                    bill = bill,
                    daysUntilDue = daysUntilDue,
                    status = status
                )
            }.sortedBy { it.daysUntilDue }
        }
    }
}