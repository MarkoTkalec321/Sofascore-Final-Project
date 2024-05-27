package com.sofascore.scoreandroidacademy.util

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.sofascore.scoreandroidacademy.data.models.CountryResponse
import com.sofascore.scoreandroidacademy.data.models.SportResponse

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
}