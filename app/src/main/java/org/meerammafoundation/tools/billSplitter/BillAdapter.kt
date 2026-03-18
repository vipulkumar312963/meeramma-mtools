package org.meerammafoundation.tools.billSplitter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.meerammafoundation.tools.R
import java.text.NumberFormat
import java.util.Locale

class BillAdapter(
    private val bills: List<Bill>,
    private val getMemberName: (Long) -> String,
    private val getMemberCount: () -> Int
) : RecyclerView.Adapter<BillAdapter.BillViewHolder>() {

    private val format = NumberFormat.getCurrencyInstance(Locale("en", "IN"))

    class BillViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvDescription: TextView = itemView.findViewById(R.id.tvBillDescription)
        private val tvAmount: TextView = itemView.findViewById(R.id.tvBillAmount)
        private val tvPaidBy: TextView = itemView.findViewById(R.id.tvPaidBy)
        private val tvSplitDetails: TextView = itemView.findViewById(R.id.tvSplitDetails)
        private val tvBillIcon: TextView = itemView.findViewById(R.id.tvBillIcon)

        fun bind(bill: Bill, getMemberName: (Long) -> String, memberCount: Int, format: NumberFormat) {
            tvDescription.text = bill.description
            tvAmount.text = format.format(bill.amount)
            tvPaidBy.text = "Paid by: ${getMemberName(bill.paidById)}"

            tvSplitDetails.text = when (bill.splitType) {
                SplitType.EQUAL -> "Split equally between $memberCount people"
                SplitType.CUSTOM -> "Split custom amounts"
            }

            tvBillIcon.text = when (bill.splitType) {
                SplitType.EQUAL -> "🤝"
                SplitType.CUSTOM -> "✂️"
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BillViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_bill, parent, false)
        return BillViewHolder(view)
    }

    override fun onBindViewHolder(holder: BillViewHolder, position: Int) {
        holder.bind(bills[position], getMemberName, getMemberCount(), format)
    }

    override fun getItemCount() = bills.size
}