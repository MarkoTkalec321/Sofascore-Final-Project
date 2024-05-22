package com.sofascore.scoreandroidacademy.data.remote

import com.sofascore.scoreandroidacademy.data.models.Sport
import retrofit2.http.GET

interface ApiService {

    @GET("/sports/")
    suspend fun getAllSports() : List<Sport>


}