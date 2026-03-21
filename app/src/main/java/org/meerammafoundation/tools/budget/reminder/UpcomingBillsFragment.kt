package org.meerammafoundation.tools.budget.reminder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.meerammafoundation.tools.R

class UpcomingBillsFragment : Fragment() {

    private lateinit var viewModel: BillReminderViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: BillReminderAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.reminder_fragment_bill_reminder, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewBills)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = BillReminderAdapter(
            emptyList(),
            { bill -> showMarkPaidDialog(bill) },
            { bill -> showEditBillDialog(bill) },
            { bill -> showDeleteBillDialog(bill) }
        )
        recyclerView.adapter = adapter

        viewModel = ViewModelProvider(requireActivity())[BillReminderViewModel::class.java]

        viewModel.billsWithStatus.observe(viewLifecycleOwner) { bills ->
            val unpaidBills = bills.filter { it.bill.isPaid == false }
            adapter.updateData(unpaidBills)
        }

        return view
    }

    private fun showMarkPaidDialog(bill: BillReminder) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Mark as Paid")
            .setMessage("Mark '${bill.name}' as paid?")
            .setPositiveButton("Yes") { _, _ ->
                viewModel.markAsPaid(bill.id)
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun showEditBillDialog(bill: BillReminder) {
        Toast.makeText(requireContext(), "Edit coming soon", Toast.LENGTH_SHORT).show()
    }

    private fun showDeleteBillDialog(bill: BillReminder) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Delete Bill")
            .setMessage("Are you sure you want to delete '${bill.name}'?")
            .setPositiveButton("Delete") { _, _ ->
                viewModel.deleteBill(bill)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}