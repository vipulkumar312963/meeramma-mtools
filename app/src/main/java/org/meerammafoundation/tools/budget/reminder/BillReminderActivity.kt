package org.meerammafoundation.tools.budget.reminder

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.textfield.TextInputEditText
import org.meerammafoundation.tools.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class BillReminderActivity : AppCompatActivity() {

    private lateinit var viewModel: BillReminderViewModel
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2
    private lateinit var fabAdd: FloatingActionButton
    private lateinit var backButton: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.reminder_activity_bill_reminder)

        viewModel = ViewModelProvider(this)[BillReminderViewModel::class.java]

        backButton = findViewById(R.id.backButton)
        backButton.setOnClickListener { finish() }

        tabLayout = findViewById(R.id.tabLayout)
        viewPager = findViewById(R.id.viewPager)
        fabAdd = findViewById(R.id.fabAddBill)

        val adapter = BillReminderPagerAdapter(this)
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Upcoming"
                1 -> "Paid"
                else -> ""
            }
        }.attach()

        fabAdd.setOnClickListener {
            showAddBillDialog()
        }
    }

    private fun showAddBillDialog() {
        val dialogView = layoutInflater.inflate(R.layout.reminder_dialog_add_bill_reminder, null)
        val etName = dialogView.findViewById<TextInputEditText>(R.id.etBillName)
        val etAmount = dialogView.findViewById<TextInputEditText>(R.id.etBillAmount)
        val spinnerCategory = dialogView.findViewById<Spinner>(R.id.spinnerCategory)
        val spinnerRecurrence = dialogView.findViewById<Spinner>(R.id.spinnerRecurrence)
        val tvDueDate = dialogView.findViewById<TextView>(R.id.tvDueDate)
        val etNotes = dialogView.findViewById<TextInputEditText>(R.id.etNotes)

        var selectedDate: Long = System.currentTimeMillis()

        // Setup category spinner
        val categories = BillCategory.values().map { it.name.replace("_", " ").capitalize() }
        val categoryAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = categoryAdapter

        // Setup recurrence spinner
        val recurrences = RecurrenceType.values().map { it.name.replace("_", " ").capitalize() }
        val recurrenceAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, recurrences)
        recurrenceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerRecurrence.adapter = recurrenceAdapter

        // Date picker
        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        tvDueDate.text = dateFormat.format(Date(selectedDate))

        tvDueDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    val date = Calendar.getInstance()
                    date.set(year, month, dayOfMonth)
                    selectedDate = date.timeInMillis
                    tvDueDate.text = dateFormat.format(date.time)
                    tvDueDate.setTextColor(resources.getColor(R.color.text_primary, null))
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        val dialog = MaterialAlertDialogBuilder(this)
            .setTitle("Add Bill Reminder")
            .setView(dialogView)
            .setPositiveButton("Add", null)
            .setNegativeButton("Cancel", null)
            .create()

        dialog.setOnShowListener {
            val positiveButton = dialog.getButton(android.app.Dialog.BUTTON_POSITIVE)
            positiveButton.setOnClickListener {
                val name = etName.text.toString().trim()
                val amountStr = etAmount.text.toString().trim()
                val categoryIndex = spinnerCategory.selectedItemPosition
                val recurrenceIndex = spinnerRecurrence.selectedItemPosition

                if (name.isEmpty()) {
                    etName.error = "Enter bill name"
                    return@setOnClickListener
                }
                if (amountStr.isEmpty()) {
                    etAmount.error = "Enter amount"
                    return@setOnClickListener
                }
                val amount = amountStr.toDoubleOrNull()
                if (amount == null || amount <= 0) {
                    etAmount.error = "Enter valid amount"
                    return@setOnClickListener
                }

                val category = BillCategory.values()[categoryIndex]
                val recurrence = RecurrenceType.values()[recurrenceIndex]
                val notes = etNotes.text.toString().trim()

                viewModel.createBill(name, amount, selectedDate, category, recurrence, notes)
                Toast.makeText(this, "Bill reminder added", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
        }

        dialog.show()
    }
}