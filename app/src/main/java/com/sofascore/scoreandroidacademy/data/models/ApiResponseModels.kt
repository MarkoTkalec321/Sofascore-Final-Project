package com.sofascore.scoreandroidacademy.data.models

import java.io.Serializable

data class Sport(
    val id: Int,
    val name: String,
    val slug: String
): Serializable

data class MatchResponse(
    val id: Int,
    val slug: String,
    val tournament: Tournament,
    val homeTeam: Team,
    val awayTeam: Team,
    val status: String,
    val startDate: String,
    val homeScore: Score,
    val awayScore: Score,
    val winnerCode: String,
    val round: Int
): Serializable


data class Tournament(
    val id: Int,
    val name: String,
    val slug: String,
    val sport: Sport,
    val country: Country
): Serializable


data class Country(
    val id: Int,
    val name: String
): Serializable

data class Team(
    val id: Int,
    val name: String,
    val country: Country
): Serializable

data class Score(
    val total: Int,
    val period2: Int? = null
): Serializable

