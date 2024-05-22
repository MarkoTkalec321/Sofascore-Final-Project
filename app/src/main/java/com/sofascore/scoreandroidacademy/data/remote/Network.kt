package com.sofascore.scoreandroidacademy.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Network {
    private const val BASE_URL = "https://academy-backend.sofascore.dev"
    private var INSTANCE: ApiService? = null

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    //test
    fun getInstance(): ApiService {
        synchronized(this) {
            if (INSTANCE == null) {
                INSTANCE = retrofit.create(ApiService::class.java)
            }
            return INSTANCE!!
        }
    }

    /*fun getInstance(): ApiService {
        if(INSTANCE == null) {
            INSTANCE = retrofit.create(ApiService::class.java)
        }
        return INSTANCE!!
    }*/
}
