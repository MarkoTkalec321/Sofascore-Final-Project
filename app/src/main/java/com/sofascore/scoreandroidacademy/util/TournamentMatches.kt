package com.sofascore.scoreandroidacademy.util

import com.sofascore.scoreandroidacademy.data.local.entity.MatchEntity
import com.sofascore.scoreandroidacademy.data.models.TournamentResponse

class TournamentMatches {
    companion object {
        const val LayoutOne = 0
        const val LayoutTwo = 1
    }

    var viewType: Int = 0

    var tournament: TournamentResponse? = null

    constructor(viewType: Int, tournament: TournamentResponse) {
        this.viewType = viewType
        this.tournament = tournament
    }


    var match: MatchEntity? = null

    constructor(viewType: Int, match: MatchEntity) {
        this.viewType = viewType
        this.match = match

    }

    /*var startDate: String? = null
    var status: String? = null
    var homeScore: String? = null
    var awayScore: String? = null
    var winnerCode: String? = null
    var awayTeamName: String? = null
    var homeTeamName: String? = null

    constructor(viewType: Int, startDate: String, status: String, homeScore: String, awayScore: String,
                winnerCode: String, awayTeamName: String, homeTeamName: String) {
        this.viewType = viewType
        this.startDate = startDate
        this.status = status
        this.homeScore = homeScore
        this.awayScore = awayScore
        this.winnerCode = winnerCode
        this.awayTeamName = awayTeamName
        this.homeTeamName = homeTeamName

    }*/
}