package com.sofascore.scoreandroidacademy.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sofascore.scoreandroidacademy.data.local.entity.SportEntity
import com.sofascore.scoreandroidacademy.data.repository.Resource
import com.sofascore.scoreandroidacademy.data.remote.Result
import com.sofascore.scoreandroidacademy.data.repository.SportInfoRepository
import com.sofascore.scoreandroidacademy.util.Event
import com.sofascore.scoreandroidacademy.util.getDatesRange
import kotlinx.coroutines.launch
import java.util.Calendar

class MainListPageViewModel(application: Application) : AndroidViewModel(application) {

    private val _selectedSport = MutableLiveData<Event<Result<String>>>()
    val selectedSport: LiveData<Event<Result<String>>> = _selectedSport

    private val _selectedDate = MutableLiveData<Event<Result<String>>>()
    val selectedDate: LiveData<Event<Result<String>>> get() = _selectedDate



    private val _sportsList = MutableLiveData<Result<List<SportEntity>>>()
    val sportsList: LiveData<Result<List<SportEntity>>> = _sportsList

    private val _datesList = MutableLiveData<List<Calendar>>()
    val datesList: LiveData<List<Calendar>> get() = _datesList

    private val _selectedSportDate = MutableLiveData<Event<Pair<String, String>>>()
    val selectedSportDate: LiveData<Event<Pair<String, String>>> = _selectedSportDate

    private val sportInfoRepository = SportInfoRepository(application)

    init {
        fetchSports()
        fetchDates()
    }

    private val _temporarySelectedSport = MutableLiveData<Event<Result<String>>>()
    val temporarySelectedSport: LiveData<Event<Result<String>>> = _temporarySelectedSport


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
