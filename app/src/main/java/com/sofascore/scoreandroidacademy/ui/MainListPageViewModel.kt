package com.sofascore.scoreandroidacademy.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sofascore.scoreandroidacademy.data.local.entity.SportEntity
import com.sofascore.scoreandroidacademy.data.models.Sport
import com.sofascore.scoreandroidacademy.data.repository.Resource
import com.sofascore.scoreandroidacademy.data.remote.Result
import com.sofascore.scoreandroidacademy.data.repository.SportInfoRepository
import com.sofascore.scoreandroidacademy.util.getDatesRange
import com.sofascore.scoreandroidacademy.data.remote.Result as MyResult
import kotlinx.coroutines.launch
import java.util.Calendar

class MainListPageViewModel(application: Application) : AndroidViewModel(application) {

    private val _sportsList = MutableLiveData<MyResult<List<Sport>>>()
    val sportsList: LiveData<MyResult<List<Sport>>> get() = _sportsList

    private val _datesList = MutableLiveData<List<Calendar>>()
    val datesList: LiveData<List<Calendar>> get() = _datesList

    private val sportInfoRepository = SportInfoRepository(application)

    init {
        fetchSports()
        fetchDates()
    }

    private fun fetchDates() {
        _datesList.value = getDatesRange()
    }

    private fun fetchSports() {
        viewModelScope.launch {
            sportInfoRepository.getSports().collect {resource ->
                when (resource) {

                    is Resource.Success -> {
                        val sportsList = convertSportEntitiesToSports(resource.data)
                        _sportsList.value = Result.Success(sportsList)
                    }
                    is Resource.Failure -> {
                        Log.d("Sports", "failure ${resource.error.message}")
                        _sportsList.value = Result.Error(Throwable(resource.error.message))
                    }
                    is Resource.Loading -> {
                       Log.d("Loading", "${resource.data.toString()}")
                    }
                }
            }
        }
    }

    private fun convertSportEntitiesToSports(entities: List<SportEntity>): List<Sport> {
        return entities.map {
            Sport(
                id = it.id,
                name = it.name,
                slug = it.slug
            )
        }
    }
}
