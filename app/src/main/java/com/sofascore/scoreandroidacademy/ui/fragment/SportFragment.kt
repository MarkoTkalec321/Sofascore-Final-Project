package com.sofascore.scoreandroidacademy.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.sofascore.scoreandroidacademy.R
import com.sofascore.scoreandroidacademy.data.local.entity.MatchEntity
import com.sofascore.scoreandroidacademy.data.local.entity.TournamentEntity
import com.sofascore.scoreandroidacademy.databinding.FragmentSportBinding
import com.sofascore.scoreandroidacademy.data.remote.Result
import com.sofascore.scoreandroidacademy.ui.adapter.SportAdapter
import com.sofascore.scoreandroidacademy.ui.viewmodel.SharedViewModel
import com.sofascore.scoreandroidacademy.ui.viewmodel.SportViewModel
import com.sofascore.scoreandroidacademy.util.TournamentViewItem
import com.sofascore.scoreandroidacademy.util.ViewType
import com.sofascore.scoreandroidacademy.util.getCurrentDate
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.TreeMap

class SportFragment: Fragment() {
    private var _binding: FragmentSportBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel by activityViewModels<SharedViewModel>()
    private val sportViewModel by viewModels<SportViewModel>()

    private var lastFetchedSport: String? = null
    private var lastFetchedDate: String? = null

    companion object {
        fun newInstance(): SportFragment {
            return SportFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSportBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedViewModel.selectedSportDate.observe(viewLifecycleOwner) { event ->
            event.peekContent().let { (originalSport, date) ->

                var sport = originalSport
                if (sport == "American Football") {
                    sport = "american-football"
                }

                if (sport != lastFetchedSport || date != lastFetchedDate) {
                    Log.d("SportFragment", "Observer triggered: Sport: $sport, Date: $date")
                    sportViewModel.fetchMatchResponses(sport, date)
                    updateDateDisplay(date)
                }
            }
        }

        val adapter =
            SportAdapter(
                onTournamentClick = {
                    findNavController().navigate(R.id.action_MainListPageFragment_to_TournamentDetailsPageFragment, Bundle().apply {putSerializable("tournament", it)})
                },
                onMatchClick = {
                    findNavController().navigate(R.id.action_MainListPageFragment_to_EventDetailsPageFragment, Bundle().apply { putSerializable("match", it) })
                })

        val layoutManager = LinearLayoutManager(context)
        binding.tournamentMatchesRecyclerView.layoutManager = layoutManager
        binding.tournamentMatchesRecyclerView.setAdapter(adapter)

        sportViewModel.matchList.observe(viewLifecycleOwner) { event ->

            event.getContentIfNotHandled()?.let { result ->
                when (result) {
                    is Result.Success -> {

                        val tournamentMatchesMap = TreeMap<TournamentEntity, MutableList<MatchEntity>>(compareBy { it.id })
                        val tournamentMatches = ArrayList<TournamentViewItem>()
                        //Log.d("SportFragment", "Match List: ${result.data}")
                        val numberOfMatches = result.data.size
                        updateMatchCountDisplay(numberOfMatches)


                        result.data.forEach {

                            tournamentMatchesMap.getOrPut(it.tournament) { mutableListOf() }.add(it)
                        }

                        tournamentMatchesMap.forEach { (tournament, matches) ->
                            matches.sortBy { match ->
                                LocalDateTime.parse(match.startDate, DateTimeFormatter.ISO_DATE_TIME)
                            }
                        }

                        tournamentMatchesMap.forEach { (key, value) ->
                            if(value.isNotEmpty()) {
                                //Log.d("key123", key.toString())
                                tournamentMatches.add(TournamentViewItem.TournamentData(key))
                                value.forEach { matchEntity ->
                                    //Log.d("matchEntity", matchEntity.toString())
                                    tournamentMatches.add(TournamentViewItem.MatchData(matchEntity))
                                }
                            }
                        }

                        adapter.updateItems(tournamentMatches)
                    }
                    is Result.Error -> {
                        Log.d("SportFragment", "Error fetching matches: ${result.error.message}")
                    }
                }
            }
        }

    }

    private fun updateDateDisplay(date: String) {
        val currentDate = getCurrentDate()
        binding.includedTextViewHeader.dateTextView.text = if (date == currentDate) {
            "Today"
        } else {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val outputFormat = SimpleDateFormat("EEE, dd.MM.yyyy", Locale.getDefault())
            val dateParsed = inputFormat.parse(date)
            outputFormat.format(dateParsed!!)
        }
    }

    private fun updateMatchCountDisplay(count: Int) {
        val countText = if (count == 1) "$count Event" else "$count Events"
        with(binding) {
            includedTextViewHeader.eventCountTextView.text = countText
            includedTextViewHeader.eventCountTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.on_surface_on_surface_lv_1))
            includedTextViewHeader.dateTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.on_surface_on_surface_lv_1))
            if (count == 0) {
                noMatchesTextView.visibility = View.VISIBLE
                includedTextViewHeader.eventCountTextView.visibility = View.GONE
            } else {
               noMatchesTextView.visibility = View.GONE
                includedTextViewHeader.eventCountTextView.visibility = View.VISIBLE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        sharedViewModel.selectedSportDate.removeObservers(viewLifecycleOwner)
        sportViewModel.matchList.removeObservers(viewLifecycleOwner)
        _binding = null
    }
}
