package com.sofascore.scoreandroidacademy.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sofascore.scoreandroidacademy.data.local.entity.TeamEntity
import com.sofascore.scoreandroidacademy.data.local.entity.TournamentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TournamentDao {

    @Query("SELECT * FROM tournaments WHERE id = :tournamentId")
    suspend fun getTournamentById(tournamentId: Int): TournamentEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTournament(tournament: List<TournamentEntity>)

}