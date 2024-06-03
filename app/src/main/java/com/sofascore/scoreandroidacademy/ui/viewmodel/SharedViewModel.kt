package com.sofascore.scoreandroidacademy.ui.viewmodel

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
import com.sofascore.scoreandroidacademy.util.getCurrentDate
import com.sofascore.scoreandroidacademy.util.getDatesRange
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import com.sofascore.scoreandroidacademy.data.remote.Result as MyResult
import kotlinx.coroutines.launch
import java.util.Calendar

class SharedViewModel(application: Application) : AndroidViewModel(application) {

    private val _selectedSport = MutableLiveData<Event<Result<String>>>()
    val selectedSport: LiveData<Event<Result<String>>> = _selectedSport

    private val _selectedDate = MutableLiveData<Event<Result<String>>>()
    val selectedDate: LiveData<Event<Result<String>>> get() = _selectedDate



    private val _sportsList = MutableLiveData<Result<List<SportEntity>>>()
    val sportsList: LiveData<Result<List<SportEntity>>> = _sportsList

    private val _datesList = MutableLiveData<List<Calendar>>()
    val datesList: LiveData<List<Calendar>> get() = _datesList

    /*private val _selectedSportDate = MutableLiveData<Pair<String, String>>()
    val selectedSportDate: LiveData<Pair<String, String>> = _selectedSportDate*/

    private val _selectedSportDate = MutableLiveData<Event<Pair<String, String>>>()
    val selectedSportDate: LiveData<Event<Pair<String, String>>> = _selectedSportDate

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


    private val _temporarySelectedSport = MutableLiveData<Event<Result<String>>>()
    val temporarySelectedSport: LiveData<Event<Result<String>>> = _temporarySelectedSport
    // private var temporarySelectedSport: String? = null

    /*fun updateSport(sport: String) {
        if (datesList.value == null) {  // Check if dates are already loaded

            updateSportAndDate(sport, getCurrentDate())
        } else {
            _temporarySelectedSport.value = Event(Result.Success(sport))  // Store sport temporarily
        }
    }

    fun updateDate(date: String) {
        _temporarySelectedSport.let { sport ->
            val sport123 = Event(Result.Success(sport))
            Log.d("isus", "$sport")
            updateSportAndDate(sport123.toString(), date)
            //_temporarySelectedSport.value = null   // Clear after updating
        }
    }*/
    /*fun updateSport(sport: String) {
        val currentSport = _selectedSport.value?.peekContent().toString()
        if (currentSport != sport) {
            _selectedSport.value = Event(Result.Success(sport))
            Log.d("SharedViewModel", "Updating sport: ${sport}")
        } else {
            Log.d("SharedViewModel", "No update needed for sport.")
        }
    }

    fun updateDate(date: String) {
        val currentDate = _selectedDate.value?.peekContent().toString()
        if (currentDate != date) {
            _selectedDate.value = Event(Result.Success(date))
            Log.d("SharedViewModel", "Updating date: ${date}")
        } else {
            Log.d("SharedViewModel", "No update needed for date.")
        }
    }*/

    fun updateSportAndDate(sport: String, date: String) {
        val currentPair = _selectedSportDate.value?.peekContent()
        if (currentPair == null || currentPair.first != sport || currentPair.second != date) {
            _selectedSportDate.value = Event(Pair(sport, date))
            Log.d("SharedViewModel", "Updating sport and date: $sport, $date")
        } else {
            Log.d("SharedViewModel", "No update needed for sport and date.")
        }
    }

    private fun fetchSports() {
        viewModelScope.launch {
            sportInfoRepository.getSports().collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        _sportsList.value = Result.Success(resource.data)
                    }
                    is Resource.Failure -> {
                        _sportsList.value = Result.Error(Exception(resource.error.message))
                    }
                    is Resource.Loading -> {
                    }
                }
            }
        }
    }


    private fun fetchDates() {
        _datesList.value = getDatesRange()
    }


}
