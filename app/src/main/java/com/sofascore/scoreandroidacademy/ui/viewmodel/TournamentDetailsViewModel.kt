package com.sofascore.scoreandroidacademy.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sofascore.scoreandroidacademy.data.local.entity.TournamentEntity

class TournamentDetailsViewModel: ViewModel() {

    private val _currentTournament = MutableLiveData<TournamentEntity>()
    val currentTournament: LiveData<TournamentEntity> get() = _currentTournament

    fun setTournament(currentTournament: TournamentEntity) {
        _currentTournament.value = currentTournament
    }


}