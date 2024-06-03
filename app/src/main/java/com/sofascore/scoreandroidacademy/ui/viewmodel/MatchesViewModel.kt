package com.sofascore.scoreandroidacademy.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.sofascore.scoreandroidacademy.data.local.entity.MatchEntity
import com.sofascore.scoreandroidacademy.data.models.MatchResponse
import com.sofascore.scoreandroidacademy.data.models.TournamentResponse
import com.sofascore.scoreandroidacademy.data.repository.ITEMS_PER_PAGE
import com.sofascore.scoreandroidacademy.data.repository.MatchRepository
import com.sofascore.scoreandroidacademy.data.repository.MatchesPagingSource
import com.sofascore.scoreandroidacademy.util.RoundMatchesViewItem

class MatchesViewModel(application: Application,
                       private val repository: MatchRepository
) : AndroidViewModel(application) {

    private var _matchesLiveData = MutableLiveData<PagingData<RoundMatchesViewItem>>()
    val matchesLiveData: LiveData<PagingData<RoundMatchesViewItem>> get() = _matchesLiveData

    fun loadMatchesForTournament(tournamentId: Int) {
        Pager(
            config = PagingConfig(pageSize = ITEMS_PER_PAGE, enablePlaceholders = false),
            pagingSourceFactory = { MatchesPagingSource(tournamentId, repository) }
        )
        .flow
        .cachedIn(viewModelScope)
        .asLiveData().observeForever {
            _matchesLiveData.value = it
        }
    }
}