package com.example.accelerometer
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AcceleratorDao {
    @Insert
    fun insert(accelerator: Accelerator)
    @Query("SELECT * FROM accelerator_table")
    fun getAllAccelerators(): Flow<List<Accelerator>>

}