package com.sofascore.scoreandroidacademy.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sofascore.scoreandroidacademy.data.local.entity.MatchEntity
import com.sofascore.scoreandroidacademy.data.models.EventDetailsResponse
import com.sofascore.scoreandroidacademy.data.models.MatchResponse
import com.sofascore.scoreandroidacademy.data.repository.EventDetailsRepository
import com.sofascore.scoreandroidacademy.data.repository.MatchRepository
import com.sofascore.scoreandroidacademy.util.EventDetailsViewItem
import kotlinx.coroutines.launch

class EventDetailsViewModel(application: Application): AndroidViewModel(application) {

    private val _eventDetailsData = MutableLiveData<MatchEntity>()
    val eventDetailsData : LiveData<MatchEntity> get() = _eventDetailsData

    private val _incidentsData = MutableLiveData<ArrayList<EventDetailsViewItem>>()
    val incidentsData : LiveData<ArrayList<EventDetailsViewItem>> get() = _incidentsData

    private val _sportNameStartDateStatus = MutableLiveData<Triple<String, String?, String>>()
    val sportNameStartDateStatus: LiveData<Triple<String, String?, String>> get() = _sportNameStartDateStatus

    private val matchRepository = MatchRepository(application)
    private val eventDetailsRepository = EventDetailsRepository(application)

    fun getEventDetails(eventId: Int) {
        viewModelScope.launch {
            val eventDetails = matchRepository.getEventDetailsByEventId(eventId)

            if (eventDetails.tournament.sport.name == "American Football") {
                setSportNameStartDateStatus(eventDetails.tournament.sport.name, eventDetails.startDate, eventDetails.status)
            } else {
                setSportNameStartDateStatus(eventDetails.tournament.sport.name, null, eventDetails.status)
            }
            _eventDetailsData.postValue(eventDetails)
        }
    }

    fun getIncidents(eventId: Int) {
        viewModelScope.launch {
            val incidents = eventDetailsRepository.getIncidentsByEventId(eventId)
            _incidentsData.postValue(incidents)
        }
    }

    private fun setSportNameStartDateStatus(sportName: String, startDate: String? = null, status: String) {
        _sportNameStartDateStatus.postValue(Triple(sportName, startDate, status))
    }

}