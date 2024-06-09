package com.sofascore.scoreandroidacademy.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.sofascore.scoreandroidacademy.R
import com.sofascore.scoreandroidacademy.data.local.entity.MatchEntity
import com.sofascore.scoreandroidacademy.databinding.FragmentEventDetailsPageBinding
import com.sofascore.scoreandroidacademy.ui.adapter.EventDetailsAdapter
import com.sofascore.scoreandroidacademy.ui.viewmodel.EventDetailsViewModel
import com.sofascore.scoreandroidacademy.util.IconConverter.Companion.loadImageFromByteArray
import java.time.Duration
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class EventDetailsPageFragment: Fragment() {
    private var _binding: FragmentEventDetailsPageBinding? = null
    private val binding get() = _binding!!

    private val eventDetailsViewModel by viewModels<EventDetailsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEventDetailsPageBinding.inflate(inflater, container, false)


        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigateUp()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, onBackPressedCallback)

        binding.frameLayoutArrowBack.setOnClickListener {
            findNavController().navigateUp()
        }

        val eventId = arguments?.getSerializable("eventId") as? Int
        Log.d("eventId", eventId.toString())

        if (eventId != null) {
            eventDetailsViewModel.getEventDetails(eventId)

            binding.constraintLayoutResults.visibility = View.GONE
            binding.constraintLayoutNoResults.visibility = View.GONE

            eventDetailsViewModel.eventDetailsData.observe(viewLifecycleOwner) { event ->

                binding.buttonViewTournamentDetails.setOnClickListener{
                    findNavController().navigate(R.id.action_EventDetailsPageFragment_to_TournamentDetailsPageFragment,
                        Bundle().apply {putSerializable("tournament", event.tournament)})
                }

                val notStarted = setupToolbarAndHeader(event)

                if (notStarted) {
                    binding.constraintLayoutResults.visibility = View.GONE
                    binding.constraintLayoutNoResults.visibility = View.VISIBLE
                } else {
                    binding.constraintLayoutResults.visibility = View.VISIBLE
                    binding.constraintLayoutNoResults.visibility = View.GONE


                    eventDetailsViewModel.getIncidents(eventId)

                    val layoutManager = LinearLayoutManager(context)
                    binding.eventDetailsRecyclerView.layoutManager = layoutManager

                    eventDetailsViewModel.sportNameStartDateStatus.observe(viewLifecycleOwner) { (sportName, startDate, status) ->

                        val adapter = EventDetailsAdapter(requireContext(), sportName, startDate, status)
                        binding.eventDetailsRecyclerView.adapter = adapter

                        eventDetailsViewModel.incidentsData.observe(viewLifecycleOwner) { incidents ->
                            adapter.updateItems(incidents)
                        }
                    }
                }
            }

        } else {
            Log.e("EventDetailsPageFragment", "No eventId found in arguments")
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setupToolbarAndHeader(event: MatchEntity): Boolean{
        var result = false
        with(binding) {
            tournamentLogo.loadImageFromByteArray(event.tournament.tournamentLogo)
            eventDetails.text = String.format("${event.tournament.sport.name}, ${event.tournament.country.name}, ${event.tournament.name}, Round ${event.round}")
            homeLogo.loadImageFromByteArray(event.homeTeam.teamLogo)
            homeTeamName.text = event.homeTeam.name
            awayTeamLogo.loadImageFromByteArray(event.awayTeam.teamLogo)
            awayTeamName.text = event.awayTeam.name

            result = when (event.status) {
                "notstarted" -> {
                    startDateOrResult.visibility = View.VISIBLE
                    time.visibility = View.VISIBLE
                    linearLayoutScoreTotal.visibility = View.GONE
                    ftOrLive.visibility = View.GONE

                    val dateTime = OffsetDateTime.parse(event.startDate)

                    val formattedDate = dateTime.format(DateTimeFormatter.ofPattern("dd.MM.yyy"))
                    startDateOrResult.text = formattedDate

                    val formattedHoursMinutes = dateTime.format(DateTimeFormatter.ofPattern("HH:mm"))
                    time.text = formattedHoursMinutes

                   true
                }
                "finished", "inprogress" -> {
                    startDateOrResult.visibility = View.GONE
                    time.visibility = View.GONE
                    linearLayoutScoreTotal.visibility = View.VISIBLE
                    ftOrLive.visibility = View.VISIBLE
                    between.text = "-"

                    homeScoreTotal.text = event.homeScore.total.toString()
                    awayScoreTotal.text = event.awayScore.total.toString()

                    if (event.status == "finished") {
                        ftOrLive.text = "Full Time"
                        homeScoreTotal.setTextColor(ContextCompat.getColor(requireContext(), if (event.winnerCode == "home") R.color.on_surface_on_surface_lv_1 else R.color.on_surface_on_surface_lv_2))
                        awayScoreTotal.setTextColor(ContextCompat.getColor(requireContext(), if (event.winnerCode == "away") R.color.on_surface_on_surface_lv_1 else R.color.on_surface_on_surface_lv_2))

                        if (event.winnerCode == "draw") {
                            homeScoreTotal.setTextColor(ContextCompat.getColor(requireContext(), R.color.on_surface_on_surface_lv_2))
                            awayScoreTotal.setTextColor(ContextCompat.getColor(requireContext(), R.color.on_surface_on_surface_lv_2))
                        }
                    } else {  // inprogress
                        homeScoreTotal.setTextColor(ContextCompat.getColor(requireContext(), R.color.specific_live))
                        awayScoreTotal.setTextColor(ContextCompat.getColor(requireContext(), R.color.specific_live))
                        val durationInMinutes = Duration.between(OffsetDateTime.parse(event.startDate), Instant.now().atOffset(ZoneOffset.UTC)).toMinutes()
                        ftOrLive.text = "${durationInMinutes}'"
                    }
                   false
                }
                else -> false
            }
        }
        return result
    }

}