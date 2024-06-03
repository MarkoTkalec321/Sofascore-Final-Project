package com.sofascore.scoreandroidacademy.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sofascore.scoreandroidacademy.data.models.StandingsMatchResponse
import com.sofascore.scoreandroidacademy.data.repository.MatchRepository
import kotlinx.coroutines.launch

class StandingsViewModel(application: Application): AndroidViewModel(application) {

    private var _teamStandingsData = MutableLiveData<StandingsMatchResponse>()
    val teamStandingsData: LiveData<StandingsMatchResponse> get() = _teamStandingsData

    private val matchRepository  = MatchRepository(application)

    fun getStandingTeams(tournamentId: Int){
        viewModelScope.launch {
            val standings = matchRepository.getStandingTeamsById(tournamentId)
            _teamStandingsData.postValue(standings)
        }
    }

}