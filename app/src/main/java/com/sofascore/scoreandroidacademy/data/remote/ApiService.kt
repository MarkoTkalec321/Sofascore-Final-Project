package com.sofascore.scoreandroidacademy.data.remote

import com.sofascore.scoreandroidacademy.data.local.entity.MatchEntity
import com.sofascore.scoreandroidacademy.data.local.entity.TournamentEntity
import com.sofascore.scoreandroidacademy.data.models.EventDetailsResponse
import com.sofascore.scoreandroidacademy.data.models.MatchResponse
import com.sofascore.scoreandroidacademy.data.models.SportResponse
import com.sofascore.scoreandroidacademy.data.models.StandingsMatchResponse
import com.sofascore.scoreandroidacademy.data.models.TournamentResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Streaming

interface ApiService {

    @GET("/sports/")
    suspend fun getAllSports() : List<SportResponse>

    @GET("/sport/{slug}/events/{date}")
    suspend fun getMatchesByDate(
        @Path("slug") slug: String,
        @Path("date") date: String
    ): List<MatchResponse>

    @GET("/tournament/{id}/image")
    @Streaming
    suspend fun getTournamentLogo(
        @Path("id") teamId: Int)
    : ResponseBody

    @GET("/team/{id}/image")
    @Streaming
    suspend fun getTeamLogo(
        @Path("id") teamId: Int)
    : ResponseBody

    @GET("/tournament/{id}/events/{span}/{page}")
    suspend fun getMatchesByIdAndSpan(
        @Path("id") tournamentId: Int,
        @Path("span") span: String,
        @Path("page") page: Int
    ): List<MatchResponse>

    @GET("/tournament/{id}/standings")
    suspend fun getMatchesByIdAndSpan(
        @Path("id") tournamentId: Int
    ): List<StandingsMatchResponse>

    @GET("/event/{id}")
    suspend fun getEventDetailsByEventId(
        @Path("id") eventId: Int
    ): MatchEntity

    @GET("/event/{id}/incidents")
    suspend fun getIncidentsByEventId(
        @Path("id") eventId: Int
    ): List<EventDetailsResponse>

}
