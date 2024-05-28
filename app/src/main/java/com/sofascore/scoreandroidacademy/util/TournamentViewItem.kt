package com.sofascore.scoreandroidacademy.util

import com.sofascore.scoreandroidacademy.data.local.entity.MatchEntity
import com.sofascore.scoreandroidacademy.data.local.entity.TournamentEntity

sealed class TournamentViewItem {
    abstract val viewType: Int

    data class TournamentData(
        override val viewType: Int,
        val tournament: TournamentEntity
    ) : TournamentViewItem()

    data class MatchData(
        override val viewType: Int,
        val match: MatchEntity
    ) : TournamentViewItem()
}

object ViewType {
    const val LayoutOne = 0
    const val LayoutTwo = 1
}
