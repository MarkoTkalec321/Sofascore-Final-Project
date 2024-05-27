package com.sofascore.scoreandroidacademy.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sofascore.scoreandroidacademy.databinding.FragmentTournamentDetailsPageBinding

class TournamentDetailsPageFragment: Fragment() {

    private var _binding: FragmentTournamentDetailsPageBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTournamentDetailsPageBinding.inflate(inflater, container, false)

        return binding.root
    }
}