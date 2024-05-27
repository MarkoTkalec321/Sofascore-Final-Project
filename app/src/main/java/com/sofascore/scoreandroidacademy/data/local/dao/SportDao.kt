package com.sofascore.scoreandroidacademy.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sofascore.scoreandroidacademy.data.local.entity.SportEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SportDao {

    @Query("SELECT * FROM sports")
    fun getAllSports(): Flow<List<SportEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSportList(sport: List<SportEntity>)

    @Query("DELETE FROM sports")
    suspend fun deleteAllFromSportsTable()

}
