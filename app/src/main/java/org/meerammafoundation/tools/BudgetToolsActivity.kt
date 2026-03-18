package org.meerammafoundation.tools

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import org.meerammafoundation.tools.budget.MonthlyBudgetActivity
import org.meerammafoundation.tools.budget.AnnualBudgetActivity
import org.meerammafoundation.tools.budget.ExpenseTrackerActivity
import org.meerammafoundation.tools.budget.IncomeVsExpensesActivity
import org.meerammafoundation.tools.budget.BillSplitterMainActivity
import org.meerammafoundation.tools.budget.DebtPayoffActivity

class BudgetToolsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_budget_tools)

        // Find back button
        val backButton = findViewById<TextView>(R.id.backButton)
        backButton.setOnClickListener {
            finish() // Go back to main screen
        }

        // Find all tool cards – names must match XML IDs exactly
        val cardMonthlyBudget = findViewById<CardView>(R.id.cardBudgetMonthly)
        val cardAnnualBudget = findViewById<CardView>(R.id.cardBudgetAnnual)
        val cardExpenseTracker = findViewById<CardView>(R.id.cardBudgetExpense)
        val cardIncomeExpense = findViewById<CardView>(R.id.cardBudgetIncomeExpense)
        val cardBillSplitter = findViewById<CardView>(R.id.cardBudgetBillSplitter)
        val cardDebtPayoff = findViewById<CardView>(R.id.cardBudgetDebtPayoff)
        val cardSavingsGoals = findViewById<CardView>(R.id.cardBudgetSavings)
        val cardBillReminder = findViewById<CardView>(R.id.cardBudgetBillReminder)


        // Set click listeners for tools
        cardMonthlyBudget.setOnClickListener {
            val intent = Intent(this, MonthlyBudgetActivity::class.java)
            startActivity(intent)
        }

        cardAnnualBudget.setOnClickListener {
            val intent = Intent(this, AnnualBudgetActivity::class.java)
            startActivity(intent)
        }

        cardExpenseTracker.setOnClickListener {
            val intent = Intent(this, ExpenseTrackerActivity::class.java)
            startActivity(intent)
        }

        cardIncomeExpense.setOnClickListener {
            val intent = Intent(this, IncomeVsExpensesActivity::class.java)
            startActivity(intent)
        }

        cardBillSplitter.setOnClickListener {
            val intent = Intent(this, BillSplitterMainActivity::class.java)
            startActivity(intent)
        }

        cardDebtPayoff.setOnClickListener {
            val intent = Intent(this, DebtPayoffActivity::class.java)
            startActivity(intent)
        }

        cardSavingsGoals.setOnClickListener {
            Toast.makeText(this, "🎯 Savings Goals - Coming Soon!", Toast.LENGTH_SHORT).show()
        }

        cardBillReminder.setOnClickListener {
            Toast.makeText(this, "⏰ Bill Reminder - Coming Soon!", Toast.LENGTH_SHORT).show()
        }

        // Bottom navigation
        val bottomHome = findViewById<TextView>(R.id.bottomHome)
        val bottomAbout = findViewById<TextView>(R.id.bottomAbout)
        val bottomUpdate = findViewById<TextView>(R.id.bottomUpdate)

        bottomHome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }

        bottomAbout.setOnClickListener {
            val intent = Intent(this, AboutActivity::class.java)
            startActivity(intent)
        }

        bottomUpdate.setOnClickListener {
            Toast.makeText(this, "Update screen - Coming Soon!", Toast.LENGTH_LONG).show()
        }
    }
}