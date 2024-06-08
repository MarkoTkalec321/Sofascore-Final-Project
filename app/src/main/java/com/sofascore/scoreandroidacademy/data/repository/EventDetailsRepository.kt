package com.sofascore.scoreandroidacademy.data.repository

import android.app.Application
import android.util.Log
import com.sofascore.scoreandroidacademy.data.models.CountryResponse
import com.sofascore.scoreandroidacademy.data.models.EventDetailsResponse
import com.sofascore.scoreandroidacademy.data.models.PlayerResponse
import com.sofascore.scoreandroidacademy.data.remote.Network
import com.sofascore.scoreandroidacademy.data.remote.Result
import com.sofascore.scoreandroidacademy.util.EventDetailsViewItem
import com.sofascore.scoreandroidacademy.util.safeResponse
import java.util.TreeMap

class EventDetailsRepository(application: Application) {
    private val api = Network.getInstance()

    suspend fun getIncidentsByEventId(eventId: Int) : ArrayList<EventDetailsViewItem> {
        val result = safeResponse { api.getIncidentsByEventId(eventId) }

        return when (result) {
            is Result.Success -> {
                val incidents = result.data.map { incident ->
                    EventDetailsResponse(
                        player = incident.player?.let {
                            PlayerResponse(
                                id = it.id,
                                name = incident.player.name,
                                slug = incident.player.slug,
                                country = CountryResponse(
                                    id = incident.player.country.id,
                                    name = incident.player.country.name
                                ),
                                position = incident.player.position
                            )
                        },
                        teamSide = incident.teamSide,
                        color = incident.color,
                        id = incident.id,
                        time = incident.time,
                        type = incident.type,
                        scoringTeam = incident.scoringTeam,
                        homeScore = incident.homeScore,
                        awayScore = incident.awayScore,
                        goalType = incident.goalType,
                        text = incident.text
                    )
                }

                val statusIncidentsMap = TreeMap<String, MutableList<EventDetailsResponse>>()
                val statusIncidents = ArrayList<EventDetailsViewItem>()

                incidents.forEach { incident ->
                   incident.text?.let { key ->
                       statusIncidentsMap.getOrPut(key) { mutableListOf() }
                   }
                }

                val allKeys = statusIncidentsMap.keys.reversed()
                val keyIterator = allKeys.iterator()
                var currentKey: String? = if (keyIterator.hasNext()) keyIterator.next() else null

                Log.d("allKeys", allKeys.toString())
                incidents.forEach { incident ->
                    if (incident.text == null) {
                        currentKey?.let { key ->
                            //Log.d("currentKey", key)

                            statusIncidentsMap.getOrPut(key) { mutableListOf() }.add(incident)
                        }
                    } else {
                        currentKey = if (keyIterator.hasNext()) keyIterator.next() else null
                    }
                }

                statusIncidentsMap.values.forEach { incidentList ->
                    incidentList.sortByDescending { it.time }
                }

                statusIncidentsMap.forEach{ (key, value) ->
                    statusIncidents.add(EventDetailsViewItem.EventStatusData(key))
                    value.forEach{ incident ->
                        //Log.d("INCIDENTS123", "getIncidentsByEventId: ${incident}")
                        statusIncidents.add(EventDetailsViewItem.EventIncidentsData(incident))
                    }
                }

                //Log.d("statusIncidentsMap", "getIncidentsByEventId: ${statusIncidentsMap}")

                val logString = StringBuilder()
                statusIncidentsMap.forEach { (key, value) ->
                    logString.append("Key: $key\n")
                    value.forEach { incident ->
                        logString.append("  - ${incident}\n") // Adjust formatting as needed
                    }
                }
                Log.d("statusIncidentsMap", "getIncidentsByEventId:\n$logString")

                //Log.d("INCIDENTS", "getIncidentsByEventId: ${statusIncidents}")
                statusIncidents
            }

            is Result.Error -> throw Exception("Error processing incidents: ${result.error.message}")
        }
    }
}