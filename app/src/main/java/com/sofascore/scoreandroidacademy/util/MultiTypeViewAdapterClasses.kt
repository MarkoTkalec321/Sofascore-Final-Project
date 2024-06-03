package com.sofascore.scoreandroidacademy.util

import com.sofascore.scoreandroidacademy.data.local.entity.MatchEntity
import com.sofascore.scoreandroidacademy.data.local.entity.TournamentEntity

sealed class TournamentViewItem {
    abstract val viewType: Int

    data class TournamentData(
        val tournament: TournamentEntity
    ) : TournamentViewItem() {
        override val viewType = ViewType.LayoutOne
    }

    data class MatchData(
        val match: MatchEntity
    ) : TournamentViewItem() {
        override val viewType = ViewType.LayoutTwo
    }
}

sealed class RoundMatchesViewItem {
    abstract val viewType: Int

    data class RoundData(
        val round: Int
    ) : RoundMatchesViewItem() {
        override val viewType = ViewType.LayoutOne
    }

    data class MatchData(
        val match: MatchEntity
    ) : RoundMatchesViewItem() {
        override val viewType = ViewType.LayoutTwo
    }
}


object ViewType {
    const val LayoutOne = 0
    const val LayoutTwo = 1
}
