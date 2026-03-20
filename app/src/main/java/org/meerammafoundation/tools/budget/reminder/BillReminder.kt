package org.meerammafoundation.tools.budget.reminder

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bill_reminders")
data class BillReminder(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val amount: Double,
    @ColumnInfo(name = "due_date")
    val dueDate: Long,  // Timestamp
    val category: BillCategory,
    val recurrence: RecurrenceType,
    @ColumnInfo(name = "is_paid")
    val isPaid: Boolean = false,
    @ColumnInfo(name = "paid_date")
    val paidDate: Long? = null,
    @ColumnInfo(name = "notes")
    val notes: String = "",
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis()
)

enum class BillCategory {
    RENT,
    ELECTRICITY,
    WATER,
    GAS,
    INTERNET,
    PHONE,
    INSURANCE,
    SUBSCRIPTION,
    LOAN,
    OTHER
}

enum class RecurrenceType {
    ONE_TIME,
    MONTHLY,
    QUARTERLY,
    YEARLY
}