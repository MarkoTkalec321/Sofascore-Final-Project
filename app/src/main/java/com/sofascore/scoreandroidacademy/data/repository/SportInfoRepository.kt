package com.sofascore.scoreandroidacademy.data.repository

import android.app.Application
import android.util.Log
import com.sofascore.scoreandroidacademy.data.local.SofascoreDatabase
import com.sofascore.scoreandroidacademy.data.local.dao.MatchDao
import com.sofascore.scoreandroidacademy.data.local.dao.SportDao
import com.sofascore.scoreandroidacademy.data.local.dao.TeamDao
import com.sofascore.scoreandroidacademy.data.local.entity.MatchEntity
import com.sofascore.scoreandroidacademy.data.local.entity.SportEntity
import com.sofascore.scoreandroidacademy.data.local.entity.TeamEntity
import com.sofascore.scoreandroidacademy.data.remote.Network
import com.sofascore.scoreandroidacademy.util.safeResponse
import com.sofascore.scoreandroidacademy.data.remote.Result

class SportInfoRepository(application: Application) {
    private val api = Network.getInstance()
    private val sportDao = SofascoreDatabase.getInstance(application).sportDao()

    suspend fun getSports() =
        repoResource(
            shouldFetch = { true },
            fetch = {
               safeResponse { safeResponse { api.getAllSports() } }
            },
            process = {data ->
                when (data){
                    is Result.Success -> {
                        data.data.map { SportEntity(id = it.id, name = it.name, slug = it.slug) }
                    }
                    is Result.Error -> {
                        listOf()
                    }
                }
            },
            save = { sportDao.insertSportList(it) },
            load = { sportDao.getAllSports() },
        )
}

//todo test
class MatchRepository(application: Application) {
    /*private val api = Network.getInstance()
    private val matchDao = SofascoreDatabase.getInstance(application).matchDao()

    suspend fun getMatches() =
        repoResource(
            shouldFetch = { true },
            fetch = {
                safeResponse { safeResponse { api.getAllSports() } }
            },
            process = {data ->
                when (data){
                    is Result.Success -> {
                        data.data.map { MatchEntity(id = it.id, slug = it.slug, ) }
                    }
                    is Result.Error -> {
                        listOf()
                    }
                }
            },
            save = { matchDao.insertMatch(it) },
            load = { matchDao.getAllMatches() },
        )*/

}

