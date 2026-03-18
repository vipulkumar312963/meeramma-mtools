package org.meerammafoundation.tools.billSplitter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.meerammafoundation.tools.R
import org.meerammafoundation.tools.budget.GroupDetailActivity

class MembersFragment : Fragment() {

    private lateinit var viewModel: BillSplitterViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MemberAdapter
    private var groupId: Long = 0

    companion object {
        fun newInstance(groupId: Long): MembersFragment {
            val fragment = MembersFragment()
            val args = Bundle()
            args.putLong("groupId", groupId)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            groupId = it.getLong("groupId")
        }
        viewModel = ViewModelProvider(requireActivity())[BillSplitterViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_members, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewMembers)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        viewModel.getMembers(groupId).observe(viewLifecycleOwner) { members ->
            adapter = MemberAdapter(members) { member ->
                showDeleteMemberDialog(member)
            }
            recyclerView.adapter = adapter
        }

        // Setup FAB in activity
        (activity as? GroupDetailActivity)?.setFabClickListener {
            showAddMemberDialog()
        }

        return view
    }

    private fun showAddMemberDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_member, null)
        val etName = dialogView.findViewById<EditText>(R.id.etMemberName)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Add Member")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val name = etName.text.toString().trim()
                if (name.isNotEmpty()) {
                    viewModel.addMember(groupId, name)
                } else {
                    etName.error = "Name cannot be empty"
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showDeleteMemberDialog(member: Member) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Remove Member")
            .setMessage("Are you sure you want to remove ${member.name}?")
            .setPositiveButton("Remove") { _, _ ->
                viewModel.removeMember(member)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}