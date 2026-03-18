package org.meerammafoundation.tools.billSplitter

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface BillDao {
    @Insert
    suspend fun insertBill(bill: Bill): Long

    @Update
    suspend fun updateBill(bill: Bill)

    @Delete
    suspend fun deleteBill(bill: Bill)

    @Query("SELECT * FROM bills WHERE groupId = :groupId ORDER BY createdAt DESC")
    fun getBillsByGroup(groupId: Long): Flow<List<Bill>>

    @Transaction
    @Query("SELECT * FROM bills WHERE id = :billId")
    fun getBillWithShares(billId: Long): Flow<BillWithShares>
}

data class BillWithShares(
    @Embedded val bill: Bill,
    @Relation(
        parentColumn = "id",
        entityColumn = "billId"
    )
    val shares: List<BillShare>
)