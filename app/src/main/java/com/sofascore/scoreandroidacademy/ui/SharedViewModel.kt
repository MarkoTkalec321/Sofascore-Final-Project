package com.sofascore.scoreandroidacademy.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.viewModelScope
import com.sofascore.scoreandroidacademy.data.local.SofascoreDatabase
import com.sofascore.scoreandroidacademy.data.local.dao.MatchDao
import com.sofascore.scoreandroidacademy.data.local.entity.MatchEntity
import com.sofascore.scoreandroidacademy.data.local.entity.SportEntity
import com.sofascore.scoreandroidacademy.data.models.SportResponse
import com.sofascore.scoreandroidacademy.data.repository.Resource
import com.sofascore.scoreandroidacademy.data.remote.Result
import com.sofascore.scoreandroidacademy.data.repository.MatchRepository
import com.sofascore.scoreandroidacademy.data.repository.SportInfoRepository
import com.sofascore.scoreandroidacademy.util.Event
import com.sofascore.scoreandroidacademy.util.getDatesRange
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import com.sofascore.scoreandroidacademy.data.remote.Result as MyResult
import kotlinx.coroutines.launch
import java.util.Calendar

class SharedViewModel(application: Application) : AndroidViewModel(application) {

    private val _sportsList = MutableLiveData<Result<List<SportEntity>>>()
    val sportsList: LiveData<Result<List<SportEntity>>> = _sportsList


    private val _datesList = MutableLiveData<List<Calendar>>()
    val datesList: LiveData<List<Calendar>> get() = _datesList

    private val _selectedSportDate = MutableLiveData<Pair<String, String>>()
    val selectedSportDate: LiveData<Pair<String, String>> = _selectedSportDate

    /*private val _selectedSportDate = MutableLiveData<Event<Pair<String, String>>>()
    val selectedSportDate: LiveData<Event<Pair<String, String>>> = _selectedSportDate*/

    private val sportInfoRepository = SportInfoRepository(application)

    init {
        fetchSports()
        fetchDates()
    }
    //treba mi za delete svega ako mi app inspection ne radi
    /*private val matchDao = SofascoreDatabase.getInstance(application).matchDao()
    private val sportDao = SofascoreDatabase.getInstance(application).sportDao()
    private val teamDao = SofascoreDatabase.getInstance(application).teamDao()
    init {
        viewModelScope.launch {
            Log.d("Nuked","nuked")
            sportDao.deleteAllFromSportsTable()
            teamDao.deleteAllFromTeamsTable()
            matchDao.deleteAllFromMatchesTable()
        }
    }*/

    /*fun updateSportAndDate(sport: String, date: String) {
        val currentPair = _selectedSportDate.value?.peekContent()
        if (currentPair == null || currentPair.first != sport || currentPair.second != date) {
            _selectedSportDate.value = Event(Pair(sport, date))
            Log.d("SharedViewModel", "Updating sport and date: $sport, $date")
        } else {
            Log.d("SharedViewModel", "No update needed for sport and date.")
        }
    }*/

    fun updateSportAndDate(sport: String, date: String) {
        val current = _selectedSportDate.value
        if (current == null || current.first != sport || current.second != date) {
            Log.d("SharedViewModel", "Updating sport and date: $sport, $date")
            _selectedSportDate.value = Pair(sport, date)
        } else {
            Log.d("SharedViewModel", "No update needed for sport and date.")
        }
    }

    private fun fetchSports() {
        viewModelScope.launch {
            sportInfoRepository.getSports().collect {resource ->

                when (resource) {

                    is Resource.Success -> {
                        val sportsList = resource.data
                        _sportsList.value = Result.Success(sportsList)
                    }
                    is Resource.Failure -> {
                        Log.d("Sports", "failure ${resource.error.message}")
                        _sportsList.value = Result.Error(Throwable(resource.error.message))
                    }
                    is Resource.Loading -> {
                        Log.d("Loading", "Loading...")
                    }
                }
            }
        }
    }

    private fun fetchDates() {
        _datesList.value = getDatesRange()
    }


}
