package org.meerammafoundation.tools.budget

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.meerammafoundation.tools.R
import org.meerammafoundation.tools.billSplitter.BillSplitterViewModel
import org.meerammafoundation.tools.billSplitter.Group
import org.meerammafoundation.tools.billSplitter.GroupAdapter

class BillSplitterMainActivity : AppCompatActivity() {

    private lateinit var viewModel: BillSplitterViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var fabAddGroup: FloatingActionButton
    private lateinit var adapter: GroupAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bill_splitter_main)

        // Initialize ViewModel
        viewModel = ViewModelProvider(this)[BillSplitterViewModel::class.java]

        // Back button
        val backButton = findViewById<TextView>(R.id.backButton)
        backButton.setOnClickListener { finish() }

        // Setup RecyclerView
        recyclerView = findViewById(R.id.recyclerViewGroups)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Observe groups (LiveData)
        viewModel.allGroups.observe(this) { groups ->
            adapter = GroupAdapter(groups) { group ->
                // Navigate to group detail
                val intent = Intent(this, GroupDetailActivity::class.java).apply {
                    putExtra(GroupDetailActivity.EXTRA_GROUP_ID, group.id)
                    putExtra(GroupDetailActivity.EXTRA_GROUP_NAME, group.name)
                }
                startActivity(intent)
            }
            recyclerView.adapter = adapter
        }

        // Setup FAB
        fabAddGroup = findViewById(R.id.fabAddGroup)
        fabAddGroup.setOnClickListener {
            showAddGroupDialog()
        }
    }

    private fun showAddGroupDialog() {
        val dialog = MaterialAlertDialogBuilder(this)
            .setTitle("Create New Group")
            .setView(R.layout.dialog_add_group)
            .setPositiveButton("Create", null)
            .setNegativeButton("Cancel", null)
            .create()

        dialog.setOnShowListener {
            val input = dialog.findViewById<EditText>(R.id.etGroupName) ?: return@setOnShowListener
            val positiveButton = dialog.getButton(Dialog.BUTTON_POSITIVE)

            positiveButton.setOnClickListener {
                val groupName = input.text.toString().trim()
                if (groupName.isNotEmpty()) {
                    viewModel.createGroup(groupName)
                    dialog.dismiss()
                } else {
                    input.error = "Group name cannot be empty"
                }
            }
        }

        dialog.show()
    }
}