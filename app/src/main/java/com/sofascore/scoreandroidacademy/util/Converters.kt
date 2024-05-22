package com.sofascore.scoreandroidacademy.util

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.sofascore.scoreandroidacademy.data.models.Country
import com.sofascore.scoreandroidacademy.data.models.Sport

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun sportToJson(value: Sport?): String? = value?.let { gson.toJson(it) }

    @TypeConverter
    fun jsonToSport(value: String?): Sport? = value?.let { gson.fromJson(it, Sport::class.java) }

    @TypeConverter
    fun countryToJson(value: Country?): String? = value?.let { gson.toJson(it) }

    @TypeConverter
    fun jsonToCountry(value: String?): Country? = value?.let { gson.fromJson(it, Country::class.java) }
}