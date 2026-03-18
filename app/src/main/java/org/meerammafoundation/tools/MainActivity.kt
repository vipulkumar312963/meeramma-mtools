package org.meerammafoundation.tools

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import org.meerammafoundation.tools.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Find all cards by their IDs
        val cardFinancial = findViewById<CardView>(R.id.cardFinancial)
        val cardBudget = findViewById<CardView>(R.id.cardBudget)
        val cardHealth = findViewById<CardView>(R.id.cardHealth)
        val cardFamily = findViewById<CardView>(R.id.cardFamily)
        val cardDocuments = findViewById<CardView>(R.id.cardDocuments)
        val cardCulture = findViewById<CardView>(R.id.cardCulture)
        val cardFavorites = findViewById<CardView>(R.id.cardFavorites)
        val cardContact = findViewById<CardView>(R.id.cardContact)
        val cardDonate = findViewById<CardView>(R.id.cardDonate)

        // Find menu and icons
        val hamburgerMenu = findViewById<TextView>(R.id.hamburgerMenu)
        val notificationIcon = findViewById<TextView>(R.id.notificationIcon)
        val profileIcon = findViewById<TextView>(R.id.profileIcon)
        val bottomAbout = findViewById<TextView>(R.id.bottomAbout)

        // Set click listeners for cards
        cardFinancial.setOnClickListener {
            val intent = Intent(this, FinancialToolsActivity::class.java)
            startActivity(intent)
        }

        cardBudget.setOnClickListener {
            val intent = Intent(this, BudgetToolsActivity::class.java)
            startActivity(intent)
        }

        cardHealth.setOnClickListener {
            val intent = Intent(this, HealthToolsActivity::class.java)
            startActivity(intent)
        }

        cardFamily.setOnClickListener {
            val intent = Intent(this, FamilyToolsActivity::class.java)
            startActivity(intent)
        }

        cardDocuments.setOnClickListener {
            val intent = Intent(this, DocumentBuilderActivity::class.java)
            startActivity(intent)
        }

        cardCulture.setOnClickListener {
            val intent = Intent(this, CultureToolsActivity::class.java)
            startActivity(intent)
        }

        cardFavorites.setOnClickListener {
            val intent = Intent(this, FavoritesActivity::class.java)
            startActivity(intent)
        }

        cardContact.setOnClickListener {
            val contactUrl = "https://docs.google.com/forms/d/e/1FAIpQLSdnfUxwmlUB2SrGE8g0VtV6mASdW1ZAERJRECjvLw0uXm2mEg/viewform"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(contactUrl))
            startActivity(intent)
        }

        cardDonate.setOnClickListener {
            val donateUrl = "https://docs.google.com/forms/d/e/1FAIpQLSfazrD_iQVz1uftCxrDfxCaySaUEPTyl_uRe1WXYQVO3yWdOg/viewform"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(donateUrl))
            startActivity(intent)
        }

        // Set click listeners for top bar icons
        hamburgerMenu.setOnClickListener {
            Toast.makeText(this, "☰ Menu - Coming Soon!", Toast.LENGTH_SHORT).show()
            // Future: Open navigation drawer with Login, Settings, etc.
        }

        notificationIcon.setOnClickListener {
            Toast.makeText(this, "🔔 Notifications - Coming Soon!", Toast.LENGTH_SHORT).show()
            // Future: Show notifications
        }

        profileIcon.setOnClickListener {
            Toast.makeText(this, "👤 Profile/Login - Coming Soon!", Toast.LENGTH_SHORT).show()
            // Future: Open login screen or profile
        }

        bottomAbout.setOnClickListener {
            val intent = Intent(this, AboutActivity::class.java)
            startActivity(intent)
        }
    }
}