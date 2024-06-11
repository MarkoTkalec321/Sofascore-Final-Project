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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LOG_TAG
import androidx.paging.map
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.sofascore.scoreandroidacademy.R
import com.sofascore.scoreandroidacademy.data.local.entity.MatchEntity
import com.sofascore.scoreandroidacademy.data.repository.MatchRepository
import com.sofascore.scoreandroidacademy.databinding.FragmentLayoutMatchesListBinding
import com.sofascore.scoreandroidacademy.ui.adapter.MatchesAdapter
import com.sofascore.scoreandroidacademy.ui.viewmodel.MatchesViewModel
import com.sofascore.scoreandroidacademy.ui.viewmodel.TournamentDetailsViewModel
import com.sofascore.scoreandroidacademy.util.MatchesViewModelFactory
import com.sofascore.scoreandroidacademy.util.RoundMatchesViewItem
import kotlinx.coroutines.launch
import java.util.TreeMap

class MatchesFragment: Fragment() {
    private var _binding: FragmentLayoutMatchesListBinding? = null
    private val binding get() = _binding!!

    private val tournamentDetailsViewModel by activityViewModels<TournamentDetailsViewModel>()

    private val matchesViewModel by viewModels<MatchesViewModel> {
        MatchesViewModelFactory(
            application = requireActivity().application,
            repository = MatchRepository(requireActivity().application),
        )
    }

    companion object {
        fun newInstance(): MatchesFragment {
            return MatchesFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLayoutMatchesListBinding.inflate(inflater, container, false)

        tournamentDetailsViewModel.currentTournament.observe(viewLifecycleOwner) { tournament ->
            matchesViewModel.loadMatchesForTournament(tournament.id)
        }

        val matchesAdapter =
            MatchesAdapter(onMatchClick = {
            findNavController().navigate(R.id.action_MatchesFragment_to_EventDetailsPageFragment, Bundle().apply { putSerializable("eventId", it.id) }) }
            )

        with(binding.matchesRecyclerView) {
            layoutManager =
                object : LinearLayoutManager(context, RecyclerView.VERTICAL, false) {}
            setHasFixedSize(true)
            descendantFocusability = ViewGroup.FOCUS_BEFORE_DESCENDANTS
            (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
            adapter = matchesAdapter
        }
        binding.matchesRecyclerView

        matchesViewModel.matchesLiveData.observe(viewLifecycleOwner) { pagingData ->
            matchesAdapter.submitData(lifecycle, pagingData)
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