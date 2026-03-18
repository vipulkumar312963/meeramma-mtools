package org.meerammafoundation.tools.budget

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import org.meerammafoundation.tools.R
import org.meerammafoundation.tools.billSplitter.BillSplitterViewModel
import org.meerammafoundation.tools.billSplitter.GroupPagerAdapter

class GroupDetailActivity : AppCompatActivity() {

    private lateinit var viewModel: BillSplitterViewModel
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2
    private lateinit var tvGroupName: TextView
    private lateinit var fabAdd: FloatingActionButton
    private lateinit var ivSettings: ImageView
    private lateinit var backButton: ImageView

    companion object {
        const val EXTRA_GROUP_ID = "extra_group_id"
        const val EXTRA_GROUP_NAME = "extra_group_name"
        private const val TAG = "GroupDetailActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate started")

        setContentView(R.layout.activity_group_detail)
        Log.d(TAG, "ContentView set")

        // Get group data from intent
        val groupId = intent.getLongExtra(EXTRA_GROUP_ID, -1)
        val groupName = intent.getStringExtra(EXTRA_GROUP_NAME) ?: "Group"

        Log.d(TAG, "Opening group detail - ID: $groupId, Name: $groupName")

        if (groupId == -1L) {
            Log.e(TAG, "Invalid group ID received")
            Toast.makeText(this, "Error: Invalid group", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        try {
            // Initialize ViewModel
            Log.d(TAG, "Initializing ViewModel")
            viewModel = ViewModelProvider(this)[BillSplitterViewModel::class.java]

            // Initialize views
            Log.d(TAG, "Initializing views")
            backButton = findViewById(R.id.backButton)
            tvGroupName = findViewById(R.id.tvGroupName)
            ivSettings = findViewById(R.id.ivSettings)
            tabLayout = findViewById(R.id.tabLayout)
            viewPager = findViewById(R.id.viewPager)
            fabAdd = findViewById(R.id.fabAdd)

            Log.d(TAG, "All views found successfully")

            // Set group name
            tvGroupName.text = groupName
            Log.d(TAG, "Group name set: $groupName")

            // Back button click listener
            backButton.setOnClickListener {
                Log.d(TAG, "Back button clicked")
                finish()
            }

            // Settings icon click listener
            ivSettings.setOnClickListener {
                Log.d(TAG, "Settings icon clicked")
                val intent = Intent(this, GroupSettingsActivity::class.java).apply {
                    putExtra(GroupSettingsActivity.EXTRA_GROUP_ID, groupId)
                    putExtra(GroupSettingsActivity.EXTRA_GROUP_NAME, groupName)
                }
                startActivity(intent)
            }

            // Create adapter with fragments
            Log.d(TAG, "Creating GroupPagerAdapter with groupId: $groupId")
            val adapter = GroupPagerAdapter(this, groupId)
            viewPager.adapter = adapter
            Log.d(TAG, "ViewPager adapter set successfully")

            // Connect TabLayout with ViewPager
            Log.d(TAG, "Attaching TabLayoutMediator")
            TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                tab.text = when (position) {
                    0 -> "Members"
                    1 -> "Bills"
                    2 -> "Balances"
                    else -> ""
                }
            }.attach()
            Log.d(TAG, "TabLayoutMediator attached successfully")

            // Handle FAB visibility based on current tab
            viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    fabAdd.visibility = if (position <= 1) View.VISIBLE else View.GONE
                    Log.d(TAG, "Tab changed to position: $position, FAB visibility: ${fabAdd.visibility}")
                }
            })

            Log.d(TAG, "onCreate completed successfully")

        } catch (e: Exception) {
            Log.e(TAG, "Error in onCreate: ${e.message}", e)
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    fun setFabClickListener(listener: View.OnClickListener) {
        fabAdd.setOnClickListener(listener)
    }

    fun showFab() {
        fabAdd.visibility = View.VISIBLE
    }

    fun hideFab() {
        fabAdd.visibility = View.GONE
    }
}