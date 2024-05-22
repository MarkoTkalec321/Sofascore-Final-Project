package com.sofascore.scoreandroidacademy.data.local.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.sofascore.scoreandroidacademy.data.models.Country
import com.sofascore.scoreandroidacademy.data.models.Score
import com.sofascore.scoreandroidacademy.data.models.Tournament

@Entity(tableName = "matches",
    foreignKeys = [
        ForeignKey(
            entity = TeamEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("homeTeamId"),
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = TeamEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("awayTeamId"),
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class MatchEntity(
    @PrimaryKey
    val id: Int,
    val slug: String,

    val homeTeamId: Int,
    val awayTeamId: Int,

    @Embedded(prefix = "tournament_")
    val tournament: Tournament,

    val status: String,
    val startDate: String,

    @Embedded(prefix = "home_")
    val homeScore: Score,

    @Embedded(prefix = "away_")
    val awayScore: Score,

    val winnerCode: String,
    val round: Int
)

@Entity(tableName = "teams")
data class TeamEntity(
    @PrimaryKey
    val id: Int,
    val name: String,
    @Embedded(prefix = "country_")
    val country: Country
)

@Entity(tableName = "sports")
data class SportEntity(
    @PrimaryKey
    val id: Int,
    val name: String,
    val slug: String
)

