package com.sofascore.scoreandroidacademy.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.paging.LOG_TAG
import androidx.recyclerview.widget.LinearLayoutManager
import com.sofascore.scoreandroidacademy.databinding.FragmentLayoutStandingsListBinding
import com.sofascore.scoreandroidacademy.ui.adapter.StandingsAdapter
import com.sofascore.scoreandroidacademy.ui.viewmodel.StandingsViewModel
import com.sofascore.scoreandroidacademy.ui.viewmodel.TournamentDetailsViewModel

class StandingsFragment: Fragment() {

    private var _binding: FragmentLayoutStandingsListBinding? = null
    private val binding get() = _binding!!

    private val tournamentDetailsViewModel by activityViewModels<TournamentDetailsViewModel>()
    private val standingsViewModel by viewModels<StandingsViewModel>()


    companion object {
        fun newInstance(): StandingsFragment {
            return StandingsFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLayoutStandingsListBinding.inflate(inflater, container, false)

        tournamentDetailsViewModel.currentTournament.observe(viewLifecycleOwner) { tournament ->
            standingsViewModel.getStandingTeams(tournament.id)
        }

        val adapter = StandingsAdapter()

        binding.recyclerViewStandings.adapter = adapter

        binding.recyclerViewStandings.layoutManager = LinearLayoutManager(context)
        standingsViewModel.teamStandingsData.observe(viewLifecycleOwner) { standings ->
            adapter.updateData(standings.sortedStandingsRows)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigateUp()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, onBackPressedCallback)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}