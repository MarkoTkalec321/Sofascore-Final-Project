package com.sofascore.scoreandroidacademy.data.models

import androidx.room.Embedded
import java.io.Serializable

data class SportResponse(
    val id: Int,
    val name: String,
    val slug: String
): Serializable

data class MatchResponse(
    val id: Int,
    val slug: String,
    val tournament: TournamentResponse,
    val homeTeam: TeamResponse,
    val awayTeam: TeamResponse,
    val status: String,
    val startDate: String,
    val homeScore: ScoreResponse,
    val awayScore: ScoreResponse,
    val winnerCode: String,
    val round: Int,
    val homeLogo: ByteArray?,
    val awayLogo: ByteArray?
): Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MatchResponse

        if (id != other.id) return false
        if (slug != other.slug) return false
        if (tournament != other.tournament) return false
        if (homeTeam != other.homeTeam) return false
        if (awayTeam != other.awayTeam) return false
        if (status != other.status) return false
        if (startDate != other.startDate) return false
        if (homeScore != other.homeScore) return false
        if (awayScore != other.awayScore) return false
        if (winnerCode != other.winnerCode) return false
        if (round != other.round) return false
        if (homeLogo != null) {
            if (other.homeLogo == null) return false
            if (!homeLogo.contentEquals(other.homeLogo)) return false
        } else if (other.homeLogo != null) return false
        if (awayLogo != null) {
            if (other.awayLogo == null) return false
            if (!awayLogo.contentEquals(other.awayLogo)) return false
        } else if (other.awayLogo != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + slug.hashCode()
        result = 31 * result + tournament.hashCode()
        result = 31 * result + homeTeam.hashCode()
        result = 31 * result + awayTeam.hashCode()
        result = 31 * result + status.hashCode()
        result = 31 * result + startDate.hashCode()
        result = 31 * result + homeScore.hashCode()
        result = 31 * result + awayScore.hashCode()
        result = 31 * result + winnerCode.hashCode()
        result = 31 * result + round
        result = 31 * result + (homeLogo?.contentHashCode() ?: 0)
        result = 31 * result + (awayLogo?.contentHashCode() ?: 0)
        return result
    }

}

data class TournamentResponse(
    val id: Int,
    val name: String,
    val slug: String,
    val sport: SportResponse,
    val country: CountryResponse
): Serializable


data class CountryResponse(
    val id: Int,
    val name: String
): Serializable

data class TeamResponse(
    val id: Int,
    val name: String,
    @Embedded(prefix = "country_")
    val country: CountryResponse
): Serializable

data class ScoreResponse(
    val total: Int,
    val period1: Int? = null,
    val period2: Int? = null,
    val period3: Int? = null,
    val period4: Int? = null,
    val overtime: Int? = null
) : Serializable

data class SortedStandingsRowResponse(
    val id: Int,
    val team: TeamResponse,
    val points: Int,
    val scoresFor: Int,
    val scoresAgainst: Int,
    val played: Int,
    val wins: Int,
    val draws: Int,
    val losses: Int
): Serializable

data class StandingsMatchResponse(
    val id: Int,
    val tournament: TournamentResponse,
    val type: String,
    val sortedStandingsRows: List<SortedStandingsRowResponse>
): Serializable

data class EventDetailsResponse(
    val player: PlayerResponse?,
    val teamSide: String? = null,
    val color: String? = null,
    val id: Int,
    val time: Int,
    val type: String,
    val scoringTeam: String? = null,
    val homeScore: Int? = null,
    val awayScore: Int? = null,
    val goalType: String? = null,
    val text: String? = null
) : Serializable

data class PlayerResponse(
    val id: Int,
    val name: String,
    val slug: String,
    val country: CountryResponse,
    val position: String
) : Serializable


