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
    val round: Int
): Serializable


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
    val total: Int? = null,
    val period2: Int? = null
): Serializable

