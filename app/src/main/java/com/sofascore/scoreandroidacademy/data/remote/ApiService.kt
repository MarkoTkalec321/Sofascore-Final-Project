package com.sofascore.scoreandroidacademy.data.remote

import com.sofascore.scoreandroidacademy.data.models.MatchResponse
import com.sofascore.scoreandroidacademy.data.models.SportResponse
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

    @GET("/tournament/{id}")
    suspend fun getTournamentsById(
        @Path("id") id: Int
    ): List<TournamentResponse>

    @GET("tournament/{id}/image")
    @Streaming
    suspend fun getTournamentLogo(
        @Path("id") teamId: Int)
    : ResponseBody

    @GET("team/{id}/image")
    @Streaming
    suspend fun getTeamLogo(
        @Path("id") teamId: Int)
    : ResponseBody
}
