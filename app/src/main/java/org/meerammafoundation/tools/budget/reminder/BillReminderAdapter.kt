package org.meerammafoundation.tools.budget.reminder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import org.meerammafoundation.tools.R
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BillReminderAdapter(
    private var bills: List<BillReminderWithStatus>,
    private val onPaidClick: (BillReminder) -> Unit,
    private val onEditClick: (BillReminder) -> Unit,
    private val onDeleteClick: (BillReminder) -> Unit
) : RecyclerView.Adapter<BillReminderAdapter.BillViewHolder>() {

    private val format = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
    private val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    fun updateData(newBills: List<BillReminderWithStatus>) {
        bills = newBills
        notifyDataSetChanged()
    }

    class BillViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvName: TextView = itemView.findViewById(R.id.tvBillName)
        private val tvAmount: TextView = itemView.findViewById(R.id.tvBillAmount)
        private val tvDueDate: TextView = itemView.findViewById(R.id.tvDueDate)
        private val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)
        private val tvCategory: TextView = itemView.findViewById(R.id.tvCategory)
        private val btnMarkPaid: Button = itemView.findViewById(R.id.btnMarkPaid)
        private val ivEdit: ImageView = itemView.findViewById(R.id.ivEditBill)
        private val ivDelete: ImageView = itemView.findViewById(R.id.ivDeleteBill)

        fun bind(
            billWithStatus: BillReminderWithStatus,
            format: NumberFormat,
            dateFormat: SimpleDateFormat,
            onPaid: (BillReminder) -> Unit,
            onEdit: (BillReminder) -> Unit,
            onDelete: (BillReminder) -> Unit
        ) {
            val bill = billWithStatus.bill
            tvName.text = bill.name
            tvAmount.text = format.format(bill.amount)
            tvDueDate.text = "Due: ${dateFormat.format(Date(bill.dueDate))}"
            tvCategory.text = bill.category.name.replace("_", " ").capitalize()

            // Set status color
            val statusText = when (billWithStatus.status) {
                BillStatus.PAID -> "Paid ✓"
                BillStatus.DUE_TODAY -> "Due Today!"
                BillStatus.OVERDUE -> "Overdue!"
                BillStatus.UPCOMING -> "${billWithStatus.daysUntilDue} days left"
            }
            tvStatus.text = statusText

            val statusColor = when (billWithStatus.status) {
                BillStatus.PAID -> R.color.primary
                BillStatus.DUE_TODAY -> R.color.primary
                BillStatus.OVERDUE -> R.color.text_secondary
                BillStatus.UPCOMING -> R.color.text_secondary
            }
            tvStatus.setTextColor(ContextCompat.getColor(itemView.context, statusColor))

            btnMarkPaid.setOnClickListener { onPaid(bill) }
            ivEdit.setOnClickListener { onEdit(bill) }
            ivDelete.setOnClickListener { onDelete(bill) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BillViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_bill_reminder, parent, false)
        return BillViewHolder(view)
    }

    override fun onBindViewHolder(holder: BillViewHolder, position: Int) {
        holder.bind(bills[position], format, dateFormat, onPaidClick, onEditClick, onDeleteClick)
    }

    override fun getItemCount() = bills.size
}