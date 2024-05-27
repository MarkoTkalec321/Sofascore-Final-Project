package com.sofascore.scoreandroidacademy.data.repository

import android.app.Application
import android.util.Log
import com.sofascore.scoreandroidacademy.data.local.SofascoreDatabase
import com.sofascore.scoreandroidacademy.data.local.entity.MatchEntity
import com.sofascore.scoreandroidacademy.data.local.entity.TeamEntity
import com.sofascore.scoreandroidacademy.data.models.CountryResponse
import com.sofascore.scoreandroidacademy.data.models.ScoreResponse
import com.sofascore.scoreandroidacademy.data.models.SportResponse
import com.sofascore.scoreandroidacademy.data.models.TeamResponse
import com.sofascore.scoreandroidacademy.data.models.TournamentResponse
import com.sofascore.scoreandroidacademy.data.remote.Network
import com.sofascore.scoreandroidacademy.data.remote.Result
import com.sofascore.scoreandroidacademy.util.safeResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MatchRepository(application: Application) {
    private val api = Network.getInstance()
    private val matchDao = SofascoreDatabase.getInstance(application).matchDao()
    private val teamDao = SofascoreDatabase.getInstance(application).teamDao()

    suspend fun getMatchesByDate(sportName: String, date: String) =
        repoResource(
            shouldFetch = { matches ->
                val shouldFetchData = matches.isNullOrEmpty() || matches.any { it.status == "inprogress" }
                shouldFetchData
            },
            fetch = {
                safeResponse { safeResponse { api.getMatchesByDate(sportName.lowercase(), date) } }
            },
            process = { data ->
                withContext(Dispatchers.IO) {
                    when (data) {
                        is Result.Success -> {
                            val teams = data.data.flatMap { match ->
                                listOf(
                                    TeamEntity(
                                        id = match.homeTeam.id,
                                        name = match.homeTeam.name,
                                        country = match.homeTeam.country
                                    ),
                                    TeamEntity(
                                        id = match.awayTeam.id,
                                        name = match.awayTeam.name,
                                        country = match.awayTeam.country
                                    )
                                )
                            }.distinctBy { it.id }

                            launch { teamDao.insertTeam(teams) }

                            val matchList = data.data.map { match ->
                                MatchEntity(
                                    id = match.id,
                                    slug = match.slug,
                                    homeTeam = TeamResponse(
                                        id = match.homeTeam.id,
                                        name = match.homeTeam.name,
                                        country = CountryResponse(
                                            id = match.homeTeam.country.id,
                                            name = match.homeTeam.country.name
                                        )
                                    ),
                                    awayTeam = TeamResponse(
                                        id = match.awayTeam.id,
                                        name = match.awayTeam.name,
                                        country = CountryResponse(
                                            id = match.awayTeam.country.id,
                                            name = match.awayTeam.country.name
                                        )
                                    ),
                                    /*homeTeamId = match.homeTeam.id,
                                    awayTeamId = match.awayTeam.id,*/
                                    tournament = TournamentResponse(
                                        id = match.tournament.id,
                                        name = match.tournament.name,
                                        slug = match.tournament.slug,
                                        sport = SportResponse(
                                            id = match.tournament.sport.id,
                                            name = match.tournament.sport.name,
                                            slug = match.tournament.sport.slug
                                        ),
                                        country = CountryResponse(
                                            id = match.tournament.country.id,
                                            name = match.tournament.country.name
                                        )
                                    ),
                                    status = match.status,
                                    startDate = match.startDate,
                                    homeScore = ScoreResponse(
                                        total = match.homeScore.total,
                                        period2 = match.homeScore.period2
                                    ),
                                    awayScore = ScoreResponse(
                                        total = match.awayScore.total,
                                        period2 = match.awayScore.period2
                                    ),
                                    winnerCode = match.winnerCode,
                                    round = match.round,
                                    date = date,
                                    sportName = sportName
                                )
                            }
                            Log.d("matchList", matchList.toString())
                            matchList
                        }

                        is Result.Error -> {
                            Log.e("MatchError", "Error processing matches: ${data.error.message}")
                            listOf()
                        }
                    }
                }
            },
            save = {
                matchDao.insertMatchList(it)
            },
            load = {
                matchDao.getMatchesByDateAndSportName(date, sportName)
            }
        )


}