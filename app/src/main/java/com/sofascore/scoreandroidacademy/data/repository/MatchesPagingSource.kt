package com.sofascore.scoreandroidacademy.data.repository

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.sofascore.scoreandroidacademy.util.RoundMatchesViewItem

const val ITEMS_PER_PAGE = 12
class MatchesPagingSource(
    private val tournamentId: Int,
    private val repository: MatchRepository
) : PagingSource<Int, RoundMatchesViewItem>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, RoundMatchesViewItem> {
        val currentPage = params.key ?: 0
        Log.d("currentPage", currentPage.toString())

        return repository.getMatchesByTournamentIdAndSpan(tournamentId, currentPage)
    }

    override fun getRefreshKey(state: PagingState<Int, RoundMatchesViewItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}