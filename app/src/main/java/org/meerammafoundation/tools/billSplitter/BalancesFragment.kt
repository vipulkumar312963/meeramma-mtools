package org.meerammafoundation.tools.billSplitter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.meerammafoundation.tools.R

class BalancesFragment : Fragment() {

    private lateinit var viewModel: BillSplitterViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: BalanceAdapter
    private var groupId: Long = 0
    private val memberNames = mutableMapOf<Long, String>()

    companion object {
        fun newInstance(groupId: Long): BalancesFragment {
            val fragment = BalancesFragment()
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
        val view = inflater.inflate(R.layout.fragment_balances, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewBalances)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Load member names
        viewModel.getMembers(groupId).observe(viewLifecycleOwner) { members ->
            members.forEach { memberNames[it.id] = it.name }
        }

        viewModel.getBalances(groupId).observe(viewLifecycleOwner) { balances ->
            adapter = BalanceAdapter(balances) { memberId -> memberNames[memberId] ?: "Unknown" }
            recyclerView.adapter = adapter
        }

        return view
    }
}