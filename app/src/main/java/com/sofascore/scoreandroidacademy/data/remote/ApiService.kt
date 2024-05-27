package com.sofascore.scoreandroidacademy.data.remote

import com.sofascore.scoreandroidacademy.data.models.MatchResponse
import com.sofascore.scoreandroidacademy.data.models.SportResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {

    @GET("/sports/")
    suspend fun getAllSports() : List<SportResponse>

    @GET("/sport/{slug}/events/{date}")
    suspend fun getMatchesByDate(
        @Path("slug") slug: String,
        @Path("date") date: String
    ): List<MatchResponse>


}