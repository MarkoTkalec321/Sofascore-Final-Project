package com.sofascore.scoreandroidacademy.ui.fragment

import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.sofascore.scoreandroidacademy.R
import com.sofascore.scoreandroidacademy.data.local.entity.SportEntity
import com.sofascore.scoreandroidacademy.ui.adapter.FragmentAdapter
import com.sofascore.scoreandroidacademy.databinding.FragmentMainListPageBinding
import com.sofascore.scoreandroidacademy.data.remote.Result
import com.sofascore.scoreandroidacademy.ui.viewmodel.MainListPageViewModel
import com.sofascore.scoreandroidacademy.util.DataStoreManager
import com.sofascore.scoreandroidacademy.util.IconConverter
import com.sofascore.scoreandroidacademy.util.getFormattedDate
import com.sofascore.scoreandroidacademy.util.isSameDay
import com.sofascore.scoreandroidacademy.util.getCurrentDate
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainListPageFragment : Fragment() {
    private var _binding: FragmentMainListPageBinding? = null
    private val binding get() = _binding!!

    private lateinit var sportViewPager: ViewPager2
    private lateinit var sportTabLayout: TabLayout
    private lateinit var dateTabLayout: TabLayout

    private val mainListPageViewModel by activityViewModels<MainListPageViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.fragmentToolbar.iconSettings.setOnClickListener {
            findNavController().navigate(R.id.action_MainListPageFragment_to_SettingsFragment)
        }

        val dataStoreManager = DataStoreManager(requireContext())
        lifecycleScope.launch {
            dataStoreManager.themePreference.collect { savedTheme ->
                when (savedTheme) {
                    "light" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    "dark" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                }
            }
        }
        lifecycleScope.launch {
            if (dataStoreManager.isFirstLaunch.first()) {
                dataStoreManager.setFirstLaunchDone()
                //code for the first launch
            }
        }
        setupObservers()

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainListPageBinding.inflate(inflater, container, false)
        sportViewPager = binding.viewPager
        sportTabLayout = binding.tabLayout
        dateTabLayout = binding.dateTabLayout

        return binding.root
    }

    private fun setupObservers() {

        mainListPageViewModel.sportsList.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Success -> {
                    setupSportViewPagerAndTabs(result.data)
                }
                is Result.Error -> Log.e("API Error", "Error fetching data: ${result.error.message}", result.error)
            }
        }
        mainListPageViewModel.datesList.observe(viewLifecycleOwner) { dates ->
            setupDateTabs(dates)
        }
    }

    private fun setupSportViewPagerAndTabs(sportsList: List<SportEntity>) {
        val sportNames = sportsList.map { it.name }

        val adapter = FragmentAdapter(childFragmentManager, lifecycle)
        sportViewPager.adapter = adapter

        sportNames.map {
            val fragment = SportFragment.newInstance()
            adapter.addFragment(fragment, it)
        }

        sportViewPager.post {
            TabLayoutMediator(sportTabLayout, sportViewPager) { tab, position ->
                context?.let { context ->
                    val iconSize = IconConverter.dpToPx(context, 32)
                    val icons = listOf(
                        R.drawable.icon,
                        R.drawable.icon_basketball,
                        R.drawable.icon_american_football
                    )
                    val icon = IconConverter.resizeIcon(context, icons[position], iconSize, iconSize)
                    tab.icon = icon

                    val sportName = if (sportNames[position] == "American Football") "Am. Football" else sportNames[position]
                    tab.text = sportName
                }
            }.attach()
        }

        sportTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {


                switchToDateTab(getCurrentDate())
                val test =
                    (sportViewPager.adapter as FragmentAdapter).getPageTitle(sportViewPager.currentItem)
                mainListPageViewModel.updateSportAndDate(test, getCurrentDate())

            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

    }

    private fun switchToDateTab(dateFromArg: String? = null) {
        val dates = mainListPageViewModel.datesList.value ?: return
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        val targetDate = when {
            dateFromArg != null && dateFromArg != "defaultDate" -> dateFormat.parse(dateFromArg).let {
                Calendar.getInstance().apply {
                    if (it != null) {
                        time = it
                    }
                }
            }
            else -> Calendar.getInstance()
        }

        dates.indexOfFirst { isSameDay(it, targetDate) }.takeIf { it != -1 }?.let {
            dateTabLayout.getTabAt(it)?.select()
        }
    }


    private fun setupDateTabs(dates: List<Calendar>) {
        //dateTabLayout.removeAllTabs()
        val todayIndex = dates.indexOfFirst { isSameDay(it, Calendar.getInstance()) }

        for (date in dates) {
            dateTabLayout.addTab(dateTabLayout.newTab().setText(getFormattedDate(date)))
        }

        if (todayIndex != -1) {
            dateTabLayout.getTabAt(todayIndex)?.select()
        }

        dateTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {

                val test = (sportViewPager.adapter as FragmentAdapter).getPageTitle(sportViewPager.currentItem)
                val test2 = DateFormat.format("yyyy-MM-dd", dates[tab.position]).toString()
                mainListPageViewModel.updateSportAndDate(test, test2)

            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        sportViewPager.adapter = null
        arguments?.clear()
        _binding = null
    }
}


