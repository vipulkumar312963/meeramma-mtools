package org.meerammafoundation.tools.billSplitter

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import org.meerammafoundation.tools.billSplitter.Group

@Entity(
    tableName = "members",
    foreignKeys = [ForeignKey(
        entity = Group::class,
        parentColumns = ["id"],
        childColumns = ["groupId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class Member(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val groupId: Long,
    val name: String
)