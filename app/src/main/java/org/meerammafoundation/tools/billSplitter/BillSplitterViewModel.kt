package org.meerammafoundation.tools.billSplitter

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class BillSplitterViewModel(application: Application) : AndroidViewModel(application) {
    private val database = BillSplitterDatabase.getDatabase(application)
    private val repository = BillSplitterRepository(database)

    private val _selectedGroup = MutableLiveData<Group?>()
    val selectedGroup: LiveData<Group?> = _selectedGroup

    // ✅ Flow → LiveData conversion
    val allGroups: LiveData<List<Group>> = repository.getAllGroups().asLiveData()

    fun selectGroup(group: Group) {
        _selectedGroup.value = group
    }

    fun clearSelectedGroup() {
        _selectedGroup.value = null
    }

    fun createGroup(name: String) = viewModelScope.launch {
        repository.createGroup(name)
    }

    fun deleteGroup(group: Group) = viewModelScope.launch {
        repository.deleteGroup(group)
    }

    fun addMember(groupId: Long, name: String) = viewModelScope.launch {
        repository.addMember(groupId, name)
    }

    fun removeMember(member: Member) = viewModelScope.launch {
        repository.removeMember(member)
    }

    // ✅ getMembers now returns LiveData
    fun getMembers(groupId: Long): LiveData<List<Member>> =
        repository.getMembersByGroup(groupId).asLiveData()

    // ✅ getBills now returns LiveData
    fun getBills(groupId: Long): LiveData<List<Bill>> =
        repository.getBillsByGroup(groupId).asLiveData()

    fun addBill(
        groupId: Long,
        description: String,
        amount: Double,
        paidById: Long,
        splitType: SplitType,
        shares: List<Pair<Long, Double>>? = null
    ) = viewModelScope.launch {
        repository.addBill(groupId, description, amount, paidById, splitType, shares)
    }

    fun deleteBill(bill: Bill) = viewModelScope.launch {
        repository.deleteBill(bill)
    }

    // ✅ getBalances using liveData builder
    fun getBalances(groupId: Long): LiveData<Map<Long, Double>> = liveData {
        emit(repository.getBalances(groupId))
    }
    // Add to BillSplitterViewModel.kt

    fun getGroupById(groupId: Long): LiveData<Group?> {
        return repository.getGroupById(groupId).asLiveData()
    }

    fun updateGroup(group: Group) = viewModelScope.launch {
        repository.updateGroup(group)
    }
}