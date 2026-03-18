package org.meerammafoundation.tools.billSplitter

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import org.meerammafoundation.tools.billSplitter.BillShare

@Dao
interface BillShareDao {
    @Insert
    suspend fun insertBillShare(billShare: BillShare)

    @Insert
    suspend fun insertAllBillShares(billShares: List<BillShare>)

    @Query("SELECT * FROM bill_shares WHERE billId = :billId")
    fun getSharesByBill(billId: Long): Flow<List<BillShare>>

    @Query("DELETE FROM bill_shares WHERE billId = :billId")
    suspend fun deleteSharesByBill(billId: Long)
}