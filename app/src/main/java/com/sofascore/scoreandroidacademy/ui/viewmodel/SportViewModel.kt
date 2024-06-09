package com.sofascore.scoreandroidacademy.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sofascore.scoreandroidacademy.data.local.entity.MatchEntity
import com.sofascore.scoreandroidacademy.data.remote.Result
import com.sofascore.scoreandroidacademy.data.repository.MatchRepository
import com.sofascore.scoreandroidacademy.data.repository.Resource
import com.sofascore.scoreandroidacademy.util.Event
import kotlinx.coroutines.launch

class SportViewModel(application: Application) : AndroidViewModel(application) {

    private val _matchList = MutableLiveData<Event<Result<List<MatchEntity>>>>()
    val matchList: LiveData<Event<Result<List<MatchEntity>>>> = _matchList

    private val matchRepository  = MatchRepository(application)

    fun fetchMatchResponses(sportName: String, date: String) {
        viewModelScope.launch {
            matchRepository.getMatchesByDate(sportName, date)
                //.distinctUntilChanged()
                .collect { resource ->
                    when (resource) {
                        is Resource.Success -> {

                            val matchList123 = resource.data
                            //if (_matchList.value?.peekContent() != Result.Success(matchList123)) {
                                _matchList.value = Event(Result.Success(matchList123))
                                Log.d("SportViewModel", "${matchList.value.toString()}")
                            //}
                        }
                        is Resource.Failure -> {
                            _matchList.value = Event(Result.Error(Throwable(resource.error.message)))
                            Log.d("Matches", "failure ${resource.error.message}")
                        }
                        is Resource.Loading -> {
                            Log.d("Loading", "Loading matches...")
                        }
                    }
                }
        }
    }
}