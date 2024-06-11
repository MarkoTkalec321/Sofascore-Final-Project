package com.sofascore.scoreandroidacademy.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.sofascore.scoreandroidacademy.R
import com.sofascore.scoreandroidacademy.databinding.FragmentSettingsBinding
import com.sofascore.scoreandroidacademy.util.DataStoreManager
import kotlinx.coroutines.launch

class SettingsFragment: Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
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


        val dataStoreManager = DataStoreManager(requireContext())

        lifecycleScope.launch {
            dataStoreManager.themePreference.collect { savedTheme ->
                with(binding) {
                    when (savedTheme) {
                        "light" -> {
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                            lightImageView.isSelected = true
                            darkImageView.isSelected = false
                            lightImageView.setImageResource(R.drawable.ic_radio_1)
                            darkImageView.setImageResource(R.drawable.ic_radio_0)

                        }
                        "dark" -> {
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                            lightImageView.isSelected = false
                            darkImageView.isSelected = true
                            lightImageView.setImageResource(R.drawable.ic_radio_0)
                            darkImageView.setImageResource(R.drawable.ic_radio_1)
                        }
                    }
                }
            }
        }

        binding.linearLayoutLight.setOnClickListener {
            lifecycleScope.launch { dataStoreManager.saveThemePreference("light") }
        }

        binding.linearLayoutDark.setOnClickListener {
            lifecycleScope.launch { dataStoreManager.saveThemePreference("dark") }
        }
    }


}