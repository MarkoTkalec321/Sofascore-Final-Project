package com.sofascore.scoreandroidacademy.util

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.sofascore.scoreandroidacademy.data.models.CountryResponse
import com.sofascore.scoreandroidacademy.data.models.ScoreResponse
import com.sofascore.scoreandroidacademy.data.models.SportResponse
import com.sofascore.scoreandroidacademy.data.models.TeamResponse
import com.sofascore.scoreandroidacademy.data.models.TournamentResponse

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun sportToJson(value: SportResponse?): String? = value?.let { gson.toJson(it) }

    @TypeConverter
    fun jsonToSport(value: String?): SportResponse? = value?.let { gson.fromJson(it, SportResponse::class.java) }

    @TypeConverter
    fun countryToJson(value: CountryResponse?): String? = value?.let { gson.toJson(it) }

    @TypeConverter
    fun jsonToCountry(value: String?): CountryResponse? = value?.let { gson.fromJson(it, CountryResponse::class.java) }

    @TypeConverter
    fun scoreToJson(value: ScoreResponse?): String? = value?.let { gson.toJson(it) }

    @TypeConverter
    fun jsonToScore(value: String?): ScoreResponse? = value?.let { gson.fromJson(it, ScoreResponse::class.java) }

    @TypeConverter
    fun tournamentToJson(value: TournamentResponse?): String? = value?.let { gson.toJson(it) }

    @TypeConverter
    fun jsonToTournament(value: String?): TournamentResponse? = value?.let { gson.fromJson(it, TournamentResponse::class.java) }

    @TypeConverter
    fun teamToJson(value: TeamResponse?): String? = value?.let { gson.toJson(it) }

    @TypeConverter
    fun jsonToTeam(value: String?): TeamResponse? = value?.let { gson.fromJson(it, TeamResponse::class.java) }

}