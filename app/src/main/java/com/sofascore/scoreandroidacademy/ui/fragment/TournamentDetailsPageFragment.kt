package com.sofascore.scoreandroidacademy.ui.fragment

import android.graphics.Outline
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.sofascore.scoreandroidacademy.R
import com.sofascore.scoreandroidacademy.data.local.entity.TournamentEntity
import com.sofascore.scoreandroidacademy.data.repository.MatchRepository
import com.sofascore.scoreandroidacademy.databinding.FragmentTournamentDetailsPageBinding
import com.sofascore.scoreandroidacademy.ui.adapter.FragmentAdapter
import com.sofascore.scoreandroidacademy.ui.viewmodel.TournamentDetailsViewModel
import com.sofascore.scoreandroidacademy.util.IconConverter.Companion.loadImageFromByteArray
import kotlin.math.abs

class TournamentDetailsPageFragment: Fragment() {

    private var _binding: FragmentTournamentDetailsPageBinding? = null
    private val binding get() = _binding!!

    private val tournamentDetailsViewModel by activityViewModels<TournamentDetailsViewModel>()
    //private val tournamentDetailsViewModel by viewModels<TournamentDetailsViewModel>()

    private lateinit var tournamentTabLayout: TabLayout
    private lateinit var tournamentViewPager: ViewPager2

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTournamentDetailsPageBinding.inflate(inflater, container, false)

        tournamentTabLayout = binding.tournamentDetailsTab
        tournamentViewPager = binding.viewPager

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

        binding.frameLayoutArrowBack.setOnClickListener {

            findNavController().navigateUp()

        }

        val tabs = listOf("Matches", "Standings")

        val adapter = FragmentAdapter(childFragmentManager, lifecycle)
        tournamentViewPager.adapter = adapter

        adapter.addFragment(MatchesFragment.newInstance(), "Matches")
        adapter.addFragment(StandingsFragment.newInstance(), "Standings")


        TabLayoutMediator(tournamentTabLayout, tournamentViewPager) { tab, position ->
            context?.let {
                tab.text = tabs[position]
            }
        }.attach()

        val tournament = arguments?.getSerializable("tournament") as TournamentEntity
        tournament.let {
            tournamentDetailsViewModel.setTournament(it)
        }

        val countryLogo = binding.tournamentDetailsHeader.countryLogo
        countryLogo.post {
            val outlineProvider = object : ViewOutlineProvider() {
                override fun getOutline(view: View, outline: Outline) {
                    val width = view.width
                    val height = view.height
                    val smallerDimension = if (width < height) width else height
                    val padding = (smallerDimension * 0.1).toInt()

                    outline.setOval(padding, padding, width - padding, height - padding)
                }
            }
            countryLogo.outlineProvider = outlineProvider
            countryLogo.clipToOutline = true
        }

        binding.appBarLayout.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            if (abs(verticalOffset) >= appBarLayout.totalScrollRange - binding.headerTournamentName.height) {
                binding.headerTournamentName.visibility = View.VISIBLE
            } else {
                binding.headerTournamentName.visibility = View.GONE
            }
        }

        tournamentDetailsViewModel.currentTournament.observe(viewLifecycleOwner) { tournament ->
            binding.headerTournamentName.text = tournament.name
            binding.tournamentDetailsHeader.apply {
                tournamentName.text = tournament.name
                countryName.text = tournament.country.name
                tournamentLogo.loadImageFromByteArray(tournament.tournamentLogo)

                countryLogo.setImageResource(
                    when (tournament.country.name) {
                        "Spain" -> R.drawable.es
                        "England" -> R.drawable.gb
                        "Croatia" -> R.drawable.hr
                        "USA" -> R.drawable.us
                        else -> 0
                    }
                )
            }
        }

    }
    override fun onDestroyView() {
        super.onDestroyView()
        tournamentViewPager.adapter = null
        _binding = null
    }
}