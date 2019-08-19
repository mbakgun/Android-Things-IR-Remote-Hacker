package com.mbakgun.things.data

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * Created by burakakgun on 8.06.2019.
 */
@Database(entities = [IrData::class], version = 1, exportSchema = false)
abstract class IrDatabase : RoomDatabase() {

    abstract fun getIrDao(): IrDao
}
