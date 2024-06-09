package com.sofascore.scoreandroidacademy.data.local.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.sofascore.scoreandroidacademy.data.models.CountryResponse
import com.sofascore.scoreandroidacademy.data.models.ScoreResponse
import com.sofascore.scoreandroidacademy.data.models.SportResponse
import com.sofascore.scoreandroidacademy.data.models.TeamResponse
import com.sofascore.scoreandroidacademy.data.models.TournamentResponse
import java.io.Serializable

@Entity(tableName = "sports")
data class SportEntity(
    @PrimaryKey
    val id: Int,
    val name: String,
    val slug: String
) : Serializable


@Entity(tableName = "matches")
data class MatchEntity(
    @PrimaryKey
    val id: Int,
    val slug: String,

    @Embedded(prefix = "homeTeam_")
    val homeTeam: TeamEntity,

    @Embedded(prefix = "awayTeam_")
    val awayTeam: TeamEntity,

    @Embedded(prefix = "tournament_")
    val tournament: TournamentEntity,

    val status: String,
    val startDate: String,

    @Embedded(prefix = "home_")
    val homeScore: ScoreResponse,

    @Embedded(prefix = "away_")
    val awayScore: ScoreResponse,

    val winnerCode: String?,
    val round: Int,
    val date: String?,
    val sportName: String?
) : Serializable

@Entity(tableName = "teams")
data class TeamEntity(
    @PrimaryKey
    val id: Int,
    val name: String,
    @Embedded(prefix = "country_")
    val country: CountryResponse,
    val teamLogo: ByteArray?
) : Serializable
{
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TeamEntity

        if (id != other.id) return false
        if (name != other.name) return false
        if (country != other.country) return false
        if (!teamLogo.contentEquals(other.teamLogo)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + name.hashCode()
        result = 31 * result + country.hashCode()
        result = 31 * result + (teamLogo?.contentHashCode() ?: 0)
        return result
    }
}

@Entity(tableName = "tournaments")
data class TournamentEntity(
    @PrimaryKey
    val id: Int,
    val name: String,
    val slug: String,
    val sport: SportResponse,
    val country: CountryResponse,
    val tournamentLogo: ByteArray?,
    val date: String?
) : Serializable
{
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TournamentEntity

        if (id != other.id) return false
        if (name != other.name) return false
        if (slug != other.slug) return false
        if (!tournamentLogo.contentEquals(other.tournamentLogo)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + name.hashCode()
        result = 31 * result + slug.hashCode()
        result = 31 * result + (tournamentLogo?.contentHashCode() ?: 0)
        return result
    }
}

