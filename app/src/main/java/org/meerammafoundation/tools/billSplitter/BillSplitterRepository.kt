package org.meerammafoundation.tools.billSplitter

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class BillSplitterRepository(private val db: BillSplitterDatabase) {

    // Groups
    suspend fun createGroup(name: String): Long {
        val group = Group(name = name)
        return db.groupDao().insertGroup(group)
    }

    suspend fun deleteGroup(group: Group) = db.groupDao().deleteGroup(group)

    fun getAllGroups(): Flow<List<Group>> = db.groupDao().getAllGroups()

    // Members
    suspend fun addMember(groupId: Long, name: String): Long {
        val member = Member(groupId = groupId, name = name)
        return db.memberDao().insertMember(member)
    }

    suspend fun removeMember(member: Member) = db.memberDao().deleteMember(member)

    fun getMembersByGroup(groupId: Long): Flow<List<Member>> = db.memberDao().getMembersByGroup(groupId)

    // Bills
    suspend fun addBill(
        groupId: Long,
        description: String,
        amount: Double,
        paidById: Long,
        splitType: SplitType,
        shares: List<Pair<Long, Double>>? = null
    ) {
        val bill = Bill(
            groupId = groupId,
            description = description,
            amount = amount,
            paidById = paidById,
            splitType = splitType
        )
        val billId = db.billDao().insertBill(bill)

        if (splitType == SplitType.EQUAL) {
            // Get all members and split equally
            val members = db.memberDao().getMembersByGroup(groupId).first()
            val shareAmount = amount / members.size
            val billShares = members.map { member ->
                BillShare(
                    billId = billId,
                    memberId = member.id,
                    shareAmount = shareAmount
                )
            }
            db.billShareDao().insertAllBillShares(billShares)
        } else if (splitType == SplitType.CUSTOM && shares != null) {
            val billShares = shares.map { (memberId, shareAmount) ->
                BillShare(
                    billId = billId,
                    memberId = memberId,
                    shareAmount = shareAmount
                )
            }
            db.billShareDao().insertAllBillShares(billShares)
        }
    }

    suspend fun deleteBill(bill: Bill) = db.billDao().deleteBill(bill)

    fun getBillsByGroup(groupId: Long): Flow<List<Bill>> = db.billDao().getBillsByGroup(groupId)

    // Balances calculation
    suspend fun getBalances(groupId: Long): Map<Long, Double> {
        val members = db.memberDao().getMembersByGroup(groupId).first()
        val balances = members.associate { it.id to 0.0 }.toMutableMap()

        val bills = db.billDao().getBillsByGroup(groupId).first()
        for (bill in bills) {
            // Person who paid gets positive
            balances[bill.paidById] = balances[bill.paidById]!! + bill.amount

            // Shares: each member owes
            val shares = db.billShareDao().getSharesByBill(bill.id).first()
            for (share in shares) {
                balances[share.memberId] = balances[share.memberId]!! - share.shareAmount
            }
        }
        return balances
    }
    // Add to BillSplitterRepository.kt

    suspend fun updateGroup(group: Group) = db.groupDao().updateGroup(group)

    fun getGroupById(groupId: Long): Flow<Group?> = db.groupDao().getGroupById(groupId)
}