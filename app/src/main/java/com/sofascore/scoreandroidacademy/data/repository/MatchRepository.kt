package com.sofascore.scoreandroidacademy.data.repository

import android.app.Application
import android.util.Log
import androidx.paging.PagingSource
import com.sofascore.scoreandroidacademy.data.local.SofascoreDatabase
import com.sofascore.scoreandroidacademy.data.local.entity.MatchEntity
import com.sofascore.scoreandroidacademy.data.local.entity.TeamEntity
import com.sofascore.scoreandroidacademy.data.local.entity.TournamentEntity
import com.sofascore.scoreandroidacademy.data.models.CountryResponse
import com.sofascore.scoreandroidacademy.data.models.MatchResponse
import com.sofascore.scoreandroidacademy.data.models.ScoreResponse
import com.sofascore.scoreandroidacademy.data.models.SortedStandingsRowResponse
import com.sofascore.scoreandroidacademy.data.models.SportResponse
import com.sofascore.scoreandroidacademy.data.models.StandingsMatchResponse
import com.sofascore.scoreandroidacademy.data.models.TeamResponse
import com.sofascore.scoreandroidacademy.data.models.TournamentResponse
import com.sofascore.scoreandroidacademy.data.remote.Network
import com.sofascore.scoreandroidacademy.data.remote.Result
import com.sofascore.scoreandroidacademy.util.RoundMatchesViewItem
import com.sofascore.scoreandroidacademy.util.safeResponse
import com.sofascore.scoreandroidacademy.util.toByteArray
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okio.ByteString.Companion.toByteString
import java.util.TreeMap

class MatchRepository(application: Application) {
    private val api = Network.getInstance()
    private val matchDao = SofascoreDatabase.getInstance(application).matchDao()
    private val teamDao = SofascoreDatabase.getInstance(application).teamDao()
    private val tournamentDao = SofascoreDatabase.getInstance(application).tournamentDao()

    suspend fun getMatchesByDate(sportName: String, date: String) =
        repoResource(
            shouldFetch = { matches ->
                matches.isNullOrEmpty() || matches.any { it.status == "inprogress" }
            },
            fetch = {
                safeResponse { safeResponse { api.getMatchesByDate(sportName.lowercase(), date) } }
            },
            process = { data ->
                withContext(Dispatchers.IO) {
                    when (data) {
                        is Result.Success -> {

                            val matchList = data.data.map { match ->

                                val deferredTeams = mutableListOf<Deferred<TeamEntity>>()
                                val deferredTournaments = mutableListOf<Deferred<TournamentEntity>>()

                                val existingHomeTeam = teamDao.getTeamById(match.homeTeam.id)
                                val existingAwayTeam = teamDao.getTeamById(match.awayTeam.id)
                                val existingTournament = tournamentDao.getTournamentById(match.tournament.id)

                                val homeTeamLogoDeferred = async {
                                    existingHomeTeam?.teamLogo ?: run {
                                        val response = safeResponse { api.getTeamLogo(match.homeTeam.id) }
                                        when (response) {
                                            is Result.Success -> response.data.toByteArray()
                                            is Result.Error -> {
                                                Log.e("API Error", "Failed to fetch home team logo: ${response.error.message}")
                                                null
                                            }
                                        }
                                    }
                                }
                                val awayTeamLogoDeferred = async {
                                    existingAwayTeam?.teamLogo ?: run {
                                        val response = safeResponse { api.getTeamLogo(match.awayTeam.id) }
                                        when (response) {
                                            is Result.Success -> response.data.toByteArray()
                                            is Result.Error -> {
                                                Log.e("API Error", "Failed to fetch away team logo: ${response.error.message}")
                                                null
                                            }
                                        }
                                    }
                                }
                                val tournamentLogoDeferred = async {
                                    existingTournament?.tournamentLogo ?: run {
                                        val response = safeResponse { api.getTournamentLogo(match.tournament.id) }
                                        when (response) {
                                            is Result.Success -> response.data.toByteArray()
                                            is Result.Error -> {
                                                Log.e("API Error", "Failed to fetch tournament logo: ${response.error.message}")
                                                null
                                            }
                                        }
                                    }
                                }

                                deferredTeams.add(async {
                                    TeamEntity(
                                        id = match.homeTeam.id,
                                        name = match.homeTeam.name,
                                        country = match.homeTeam.country,
                                        teamLogo = homeTeamLogoDeferred.await()
                                    )
                                })
                                deferredTeams.add(async {
                                    TeamEntity(
                                        id = match.awayTeam.id,
                                        name = match.awayTeam.name,
                                        country = match.awayTeam.country,
                                        teamLogo = awayTeamLogoDeferred.await()
                                    )
                                })
                                deferredTournaments.add(async {
                                    TournamentEntity(
                                        id = match.tournament.id,
                                        name = match.tournament.name,
                                        slug = match.tournament.slug,
                                        sport = match.tournament.sport,
                                        country = match.tournament.country,
                                        tournamentLogo = tournamentLogoDeferred.await(),
                                        date = date
                                    )
                                })


                                val teams = deferredTeams.awaitAll().distinctBy { it.id }
                                val tournaments = deferredTournaments.awaitAll().distinctBy { it.id }

                                launch { teamDao.insertTeam(teams) }
                                launch { tournamentDao.insertTournament(tournaments) }

                                MatchEntity(
                                    id = match.id,
                                    slug = match.slug,
                                    homeTeam = TeamEntity(
                                        id = match.homeTeam.id,
                                        name = match.homeTeam.name,
                                        country = CountryResponse(
                                            id = match.homeTeam.country.id,
                                            name = match.homeTeam.country.name
                                        ),
                                        teamLogo = homeTeamLogoDeferred.await()
                                    ),
                                    awayTeam = TeamEntity(
                                        id = match.awayTeam.id,
                                        name = match.awayTeam.name,
                                        country = CountryResponse(
                                            id = match.awayTeam.country.id,
                                            name = match.awayTeam.country.name
                                        ),
                                        teamLogo = awayTeamLogoDeferred.await()
                                    ),
                                    tournament = TournamentEntity(
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
                                        ),
                                        tournamentLogo = tournamentLogoDeferred.await(),
                                        date = date
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
                            //Log.d("matchList", matchList.toString())
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

    suspend fun getMatchesByTournamentIdAndSpan(tournamentId: Int, currentPage: Int): PagingSource.LoadResult<Int, RoundMatchesViewItem> = withContext(Dispatchers.IO) {
        try {
            val data = safeResponse { api.getMatchesByIdAndSpan(tournamentId, "next", currentPage) }
            when (data) {
                is Result.Success -> {
                    val matchResponses = data.data.map { match ->

                        val homeLogo = getTeamLogoSafe(match.homeTeam.id)
                        val awayLogo = getTeamLogoSafe(match.awayTeam.id)

                        MatchEntity(
                            id = match.id,
                            slug = match.slug,
                            homeTeam = TeamEntity(
                                id = match.homeTeam.id,
                                name = match.homeTeam.name,
                                country = CountryResponse(
                                    id = match.homeTeam.country.id,
                                    name = match.homeTeam.country.name
                                ),
                                teamLogo = homeLogo
                            ),
                            awayTeam = TeamEntity(
                                id = match.awayTeam.id,
                                name = match.awayTeam.name,
                                country = CountryResponse(
                                    id = match.awayTeam.country.id,
                                    name = match.awayTeam.country.name
                                ),
                                teamLogo = awayLogo
                            ),
                            tournament = TournamentEntity(
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
                                ),
                                tournamentLogo = null,
                                date = null
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
                            date = null,
                            sportName = null
                        )
                    }

                    val roundMatchesMap = TreeMap<Int, MutableList<MatchEntity>>()
                    val roundMatches = ArrayList<RoundMatchesViewItem>()

                    //Log.d("matchrepo", matchResponses.toString())
                    matchResponses.map { matchEntity ->
                        roundMatchesMap.getOrPut(matchEntity.round) { mutableListOf() }.add(matchEntity)
                    }

                    //Log.d("matchrepo", roundMatchesMap.toString())
                    roundMatchesMap.forEach { (key, value) ->
                        roundMatches.add(RoundMatchesViewItem.RoundData(key))
                        value.forEach { matchEntity ->
                            roundMatches.add(RoundMatchesViewItem.MatchData(matchEntity))
                        }
                    }

                    //Log.d("matchrepo", roundMatches.toString())

                    PagingSource.LoadResult.Page(
                        data = roundMatches,
                        prevKey = if (currentPage == 0) null else currentPage - 1,
                        nextKey = if (data.data.isEmpty()) null else currentPage + 1
                    )
                }
                is Result.Error -> PagingSource.LoadResult.Error(data.error)
            }
        } catch (e: Exception) {
            PagingSource.LoadResult.Error(e)
        }
    }

    private suspend fun getTeamLogoSafe(teamId: Int): ByteArray? {
        val response = safeResponse { api.getTeamLogo(teamId) }
        when(response) {
            is Result.Success -> return response.data.toByteArray()
            is Result.Error -> {
                Log.e("API Error", "Failed to fetch home team logo: ${response.error.message}")
                return null
            }
        }
    }

    suspend fun getStandingTeamsById(tournamentId: Int): StandingsMatchResponse {
        val result = safeResponse { api.getMatchesByIdAndSpan(tournamentId) }
        return when (result) {
            is Result.Success -> {
                val match = result.data.firstOrNull { it.type == "total" }
                if (match != null) {
                    StandingsMatchResponse(
                        id = match.id,
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
                        type = match.type,
                        sortedStandingsRows = match.sortedStandingsRows.map { row ->
                            SortedStandingsRowResponse(
                                id = row.id,
                                team = TeamResponse(
                                    id = row.team.id,
                                    name = row.team.name,
                                    country = CountryResponse(
                                        id = row.team.country.id,
                                        name = row.team.country.name
                                    )
                                ),
                                points = row.points,
                                scoresFor = row.scoresFor,
                                scoresAgainst = row.scoresAgainst,
                                played = row.played,
                                wins = row.wins,
                                draws = row.draws,
                                losses = row.losses
                            )
                        }
                    )
                } else {
                    throw IllegalStateException("No match of type 'total' found for tournament ID $tournamentId")
                }
            }
            is Result.Error -> throw Exception("Error processing matches: ${result.error.message}")
        }
    }



}