package org.meerammafoundation.tools.billSplitter

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.meerammafoundation.tools.R
import org.meerammafoundation.tools.budget.GroupDetailActivity
import java.text.NumberFormat
import java.util.Locale

class BillsFragment : Fragment() {

    private lateinit var viewModel: BillSplitterViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: BillAdapter
    private var groupId: Long = 0
    private val memberNames = mutableMapOf<Long, String>()
    private val memberList = mutableListOf<Member>()
    private val TAG = "BillsFragment"

    companion object {
        fun newInstance(groupId: Long): BillsFragment {
            val fragment = BillsFragment()
            val args = Bundle()
            args.putLong("groupId", groupId)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            groupId = it.getLong("groupId", -1)
            Log.d(TAG, "onCreate - Group ID: $groupId")
        }
        viewModel = ViewModelProvider(requireActivity())[BillSplitterViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "onCreateView - Creating view for groupId: $groupId")

        val view = inflater.inflate(R.layout.fragment_bills, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewBills)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Load members for spinner and names
        viewModel.getMembers(groupId).observe(viewLifecycleOwner) { members ->
            Log.d(TAG, "Received ${members.size} members")
            memberList.clear()
            memberList.addAll(members)
            memberNames.clear()
            members.forEach { memberNames[it.id] = it.name }
        }

        viewModel.getBills(groupId).observe(viewLifecycleOwner) { bills ->
            Log.d(TAG, "Received ${bills.size} bills")
            adapter = BillAdapter(
                bills,
                { memberId -> memberNames[memberId] ?: "Unknown" },
                { memberList.size }
            )
            recyclerView.adapter = adapter
        }

        // Setup FAB click listener
        try {
            val activity = requireActivity() as? GroupDetailActivity
            activity?.setFabClickListener {
                Log.d(TAG, "FAB clicked in Bills tab")
                showAddBillDialog()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error setting FAB listener", e)
        }

        return view
    }

    private fun showAddBillDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_bill, null)
        val etDescription = dialogView.findViewById<EditText>(R.id.etBillDescription)
        val etAmount = dialogView.findViewById<EditText>(R.id.etBillAmount)
        val spinnerPaidBy = dialogView.findViewById<Spinner>(R.id.spinnerPaidBy)
        val radioGroup = dialogView.findViewById<RadioGroup>(R.id.radioGroupSplit)
        val radioEqual = dialogView.findViewById<RadioButton>(R.id.radioEqual)
        val radioCustom = dialogView.findViewById<RadioButton>(R.id.radioCustom)
        val layoutCustomSplit = dialogView.findViewById<LinearLayout>(R.id.layoutCustomSplit)
        val containerCustomSplit = dialogView.findViewById<LinearLayout>(R.id.containerCustomSplit)

        if (memberList.isEmpty()) {
            Toast.makeText(requireContext(), "Add members first", Toast.LENGTH_SHORT).show()
            return
        }

        // Create member names list for spinner
        val memberNamesList = memberList.map { it.name }

        // Custom spinner adapter with proper colors
        val spinnerAdapter = object : ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_spinner_item,
            memberNamesList
        ) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent) as TextView
                view.setTextColor(resources.getColor(R.color.text_primary, null))
                view.textSize = 16f
                return view
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getDropDownView(position, convertView, parent) as TextView
                view.setTextColor(resources.getColor(R.color.text_primary, null))
                view.setBackgroundColor(resources.getColor(R.color.card_background, null))
                view.textSize = 16f
                view.setPadding(32, 16, 32, 16)
                return view
            }
        }
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerPaidBy.adapter = spinnerAdapter

        // Handle split type toggle
        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            layoutCustomSplit.visibility = if (checkedId == R.id.radioCustom) View.VISIBLE else View.GONE
            if (checkedId == R.id.radioCustom) {
                setupCustomSplitFields(containerCustomSplit)
            }
        }

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("Add Bill")
            .setView(dialogView)
            .setPositiveButton("Add", null)
            .setNegativeButton("Cancel", null)
            .create()

        dialog.setOnShowListener {
            val positiveButton = dialog.getButton(Dialog.BUTTON_POSITIVE)
            positiveButton.setOnClickListener {
                val description = etDescription.text.toString().trim()
                val amountStr = etAmount.text.toString().trim()
                val selectedMemberIndex = spinnerPaidBy.selectedItemPosition

                if (description.isEmpty()) {
                    etDescription.error = "Description required"
                    return@setOnClickListener
                }
                if (amountStr.isEmpty()) {
                    etAmount.error = "Amount required"
                    return@setOnClickListener
                }
                val amount = amountStr.toDoubleOrNull()
                if (amount == null || amount <= 0) {
                    etAmount.error = "Invalid amount"
                    return@setOnClickListener
                }
                if (selectedMemberIndex < 0 || selectedMemberIndex >= memberList.size) {
                    Toast.makeText(requireContext(), "Select a payer", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                val paidById = memberList[selectedMemberIndex].id

                val splitType = if (radioEqual.isChecked) SplitType.EQUAL else SplitType.CUSTOM

                if (splitType == SplitType.CUSTOM) {
                    // Collect custom shares
                    val shares = mutableListOf<Pair<Long, Double>>()
                    var totalShare = 0.0
                    for (i in 0 until containerCustomSplit.childCount) {
                        val row = containerCustomSplit.getChildAt(i)
                        val etShare = row.findViewById<EditText>(R.id.etShareAmount)
                        val shareStr = etShare.text.toString().trim()
                        if (shareStr.isEmpty()) {
                            etShare.error = "Enter share"
                            return@setOnClickListener
                        }
                        val share = shareStr.toDoubleOrNull()
                        if (share == null || share <= 0) {
                            etShare.error = "Invalid amount"
                            return@setOnClickListener
                        }
                        shares.add(Pair(memberList[i].id, share))
                        totalShare += share
                    }
                    if (Math.abs(totalShare - amount) > 0.01) {
                        Toast.makeText(requireContext(), "Shares must sum to ₹$amount", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }
                    viewModel.addBill(groupId, description, amount, paidById, splitType, shares)
                } else {
                    viewModel.addBill(groupId, description, amount, paidById, splitType)
                }

                dialog.dismiss()
                Toast.makeText(requireContext(), "Bill added", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }

    private fun setupCustomSplitFields(container: LinearLayout) {
        container.removeAllViews()
        val inflater = LayoutInflater.from(requireContext())

        memberList.forEach { member ->
            val row = inflater.inflate(R.layout.item_custom_split, container, false)
            val tvName = row.findViewById<TextView>(R.id.tvMemberName)
            val etShare = row.findViewById<EditText>(R.id.etShareAmount)

            tvName.text = member.name
            tvName.setTextColor(resources.getColor(R.color.text_primary, null))

            etShare.setTextColor(resources.getColor(R.color.text_primary, null))
            etShare.setHintTextColor(resources.getColor(R.color.text_secondary, null))

            container.addView(row)
        }
    }
}