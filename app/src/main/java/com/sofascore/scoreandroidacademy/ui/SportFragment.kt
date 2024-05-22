package com.sofascore.scoreandroidacademy.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sofascore.scoreandroidacademy.R
import com.sofascore.scoreandroidacademy.databinding.FragmentSportBinding

class SportFragment: Fragment() {
    private var _binding: FragmentSportBinding? = null
    private val binding get() = _binding!!

    companion object {
        const val ARG_SPORT_NAME = "sport_name"
        const val ARG_DATE = "date"

        fun newInstance(sportName: String, date: String): SportFragment {
            val fragment = SportFragment()
            val args = Bundle().apply {
                putString(ARG_SPORT_NAME, sportName)
                putString(ARG_DATE, date)
            }

            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_sport, container, false)
    }

    fun updateSportAndDate(newSport: String, newDate: String) {
        arguments?.apply {
            putString(ARG_SPORT_NAME, newSport)
            putString(ARG_DATE, newDate)
        }
        fetchData()
    }


    private fun fetchData() {
        val sportName = arguments?.getString(ARG_SPORT_NAME) ?: return
        val date = arguments?.getString(ARG_DATE) ?: return
        Log.d("SportFragment", "Sport: $sportName, Date: $date")


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}