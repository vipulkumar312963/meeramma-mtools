package org.meerammafoundation.tools.budget.reminder

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class BillReminderPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> UpcomingBillsFragment()
            1 -> PaidBillsFragment()
            else -> throw IllegalStateException("Invalid position")
        }
    }
}