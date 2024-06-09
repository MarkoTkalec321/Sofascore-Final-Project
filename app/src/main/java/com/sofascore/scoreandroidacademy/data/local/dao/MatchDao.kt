package com.sofascore.scoreandroidacademy.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.sofascore.scoreandroidacademy.data.local.entity.MatchEntity
import com.sofascore.scoreandroidacademy.data.local.entity.SportEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

@Dao
interface MatchDao {

    @Query("SELECT * FROM matches")
    fun getAllMatches(): Flow<List<MatchEntity>>

    @Query("SELECT * FROM matches WHERE date = :date AND sportName = :sportName")
    fun getMatchesByDateAndSportName(date: String, sportName: String): Flow<List<MatchEntity>>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMatchList(match: List<MatchEntity>)

    @Query("DELETE FROM matches")
    suspend fun deleteAllFromMatchesTable()
}
