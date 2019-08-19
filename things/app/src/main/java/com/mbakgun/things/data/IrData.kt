package com.mbakgun.things.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by burakakgun on 8.06.2019.
 */
@Entity(tableName = "IrRecords")
data class IrData(@PrimaryKey(autoGenerate = true) var id: Int = 0, var name: String, var hexCode: String)
