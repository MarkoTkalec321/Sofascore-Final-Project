package com.sofascore.scoreandroidacademy.data.local.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.sofascore.scoreandroidacademy.data.models.CountryResponse
import com.sofascore.scoreandroidacademy.data.models.ScoreResponse
import com.sofascore.scoreandroidacademy.data.models.TeamResponse
import com.sofascore.scoreandroidacademy.data.models.TournamentResponse
import java.io.Serializable

@Entity(tableName = "matches",

)
data class MatchEntity(
    @PrimaryKey
    val id: Int,
    val slug: String,

    @Embedded(prefix = "homeTeam_")
    val homeTeam: TeamResponse,

    @Embedded(prefix = "awayTeam_")
    val awayTeam: TeamResponse,

    @Embedded(prefix = "tournament_")
    val tournament: TournamentResponse,

    val status: String,
    val startDate: String,

    @Embedded(prefix = "home_")
    val homeScore: ScoreResponse,

    @Embedded(prefix = "away_")
    val awayScore: ScoreResponse,

    val winnerCode: String?,
    val round: Int,
    val date: String,
    val sportName: String
) : Serializable

@Entity(tableName = "teams")
data class TeamEntity(
    @PrimaryKey
    val id: Int,
    val name: String,
    @Embedded(prefix = "country_")
    val country: CountryResponse
)

@Entity(tableName = "sports")
data class SportEntity(
    @PrimaryKey
    val id: Int,
    val name: String,
    val slug: String
)

