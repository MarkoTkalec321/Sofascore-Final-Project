package com.sofascore.scoreandroidacademy.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sofascore.scoreandroidacademy.data.models.MatchResponse
import com.sofascore.scoreandroidacademy.data.models.Sport
import com.sofascore.scoreandroidacademy.data.remote.Result
import com.sofascore.scoreandroidacademy.data.repository.MatchRepository
import kotlinx.coroutines.launch

class SportViewModel(application: Application) : AndroidViewModel(application) {

    private val _matchList = MutableLiveData<Result<List<MatchResponse>>>()
    val matchList: LiveData<Result<List<MatchResponse>>> get() = _matchList

    private val matchRepository  = MatchRepository(application)

    init {
        fetchMatchResponses()
    }

    private fun fetchMatchResponses() {
        viewModelScope.launch {

        }
    }
}