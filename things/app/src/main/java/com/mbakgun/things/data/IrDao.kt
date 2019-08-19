package com.mbakgun.things.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

/**
 * Created by burakakgun on 8.06.2019.
 */

@Dao
interface IrDao {

    @Insert
    fun insert(irData: IrData)

    @Delete
    fun delete(irData: IrData)

    @Update
    fun update(irData: IrData)

    @Query("SELECT * FROM IrRecords where name like :speech LIMIT 1")
    fun getByName(speech: String): IrData?

    @get:Query("SELECT * FROM IrRecords")
    val getAll: List<IrData>
}
