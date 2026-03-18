package org.meerammafoundation.tools.billSplitter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class GroupPagerAdapter(
    fragmentActivity: FragmentActivity,
    private val groupId: Long
) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> MembersFragment.newInstance(groupId)
            1 -> BillsFragment.newInstance(groupId)
            2 -> BalancesFragment.newInstance(groupId)
            else -> throw IllegalStateException("Invalid position $position")
        }
    }
}