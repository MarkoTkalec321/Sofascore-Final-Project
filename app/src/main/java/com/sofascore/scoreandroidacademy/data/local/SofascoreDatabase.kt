package com.sofascore.scoreandroidacademy.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.sofascore.scoreandroidacademy.data.local.dao.MatchDao
import com.sofascore.scoreandroidacademy.data.local.dao.SportDao
import com.sofascore.scoreandroidacademy.data.local.dao.TeamDao
import com.sofascore.scoreandroidacademy.data.local.dao.TournamentDao
import com.sofascore.scoreandroidacademy.data.local.entity.MatchEntity
import com.sofascore.scoreandroidacademy.data.local.entity.TeamEntity
import com.sofascore.scoreandroidacademy.data.local.entity.SportEntity
import com.sofascore.scoreandroidacademy.data.local.entity.TournamentEntity
import com.sofascore.scoreandroidacademy.util.Converters

@Database(entities = [MatchEntity::class, TeamEntity::class, SportEntity::class, TournamentEntity::class], version = 18)
@TypeConverters(Converters::class)
abstract class SofascoreDatabase : RoomDatabase() {
    abstract fun matchDao(): MatchDao
    abstract fun teamDao(): TeamDao
    abstract fun sportDao(): SportDao
    abstract fun tournamentDao(): TournamentDao

    companion object {
        private const val DATABASE_NAME = "sofascore_database.db"

        @Volatile
        private var INSTANCE: SofascoreDatabase? = null

        fun getInstance(context: Context): SofascoreDatabase {

            var instance = INSTANCE


            if(instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        SofascoreDatabase::class.java,
                        DATABASE_NAME)
                        .fallbackToDestructiveMigration()
                        .build()

                    INSTANCE = instance
            }
            return instance

        }
    }
}