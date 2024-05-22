package com.sofascore.scoreandroidacademy.ui

import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.sofascore.scoreandroidacademy.R
import com.sofascore.scoreandroidacademy.ui.adapter.MainListViewPagerAdapter
import com.sofascore.scoreandroidacademy.databinding.FragmentMainListPageBinding
import com.sofascore.scoreandroidacademy.data.remote.Result
import com.sofascore.scoreandroidacademy.data.models.Sport
import com.sofascore.scoreandroidacademy.util.getFormattedDate
import com.sofascore.scoreandroidacademy.util.isSameDay
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainListPageFragment : Fragment() {
    private var _binding: FragmentMainListPageBinding? = null
    private val binding get() = _binding!!
    private lateinit var sportViewPager: ViewPager2
    private lateinit var sportTabLayout: TabLayout
    private lateinit var dateTabLayout: TabLayout
    private val mainListPageViewModel: MainListPageViewModel by viewModels()

    private lateinit var sports: List<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainListPageBinding.inflate(inflater, container, false)
        sportViewPager = binding.viewPager
        sportTabLayout = binding.tabLayout
        dateTabLayout = binding.dateTabLayout

        setupObservers()

        return binding.root
    }

    private fun setupObservers() {

        mainListPageViewModel.sportsList.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Success -> {
                    sports = result.data.map { it.name }
                    setupViewPagerAndTabs(result.data)}
                is Result.Error -> Log.e("API Error", "Error fetching data: ${result.error.message}", result.error)

            }
        }
        mainListPageViewModel.datesList.observe(viewLifecycleOwner) { dates ->
            setupDateTabs(dates)
        }
    }

    private val fragmentReferences = mutableMapOf<String, SportFragment>()

    private fun setupViewPagerAndTabs(sportsList: List<Sport>) {
        val sportNames = sportsList.map { it.name }
        val date = getCurrentDate()

        val adapter = MainListViewPagerAdapter(childFragmentManager, lifecycle)
        sportViewPager.adapter = adapter

        sportNames.forEach { sportName ->
            val fragment = SportFragment.newInstance(sportName, date)
            adapter.addFragment(fragment, sportName)
            fragmentReferences[sportName] = fragment
        }

        TabLayoutMediator(sportTabLayout, sportViewPager) { tab, position ->
            tab.setIcon(
                when (sportNames[position]) {
                    "Football" -> R.drawable.icon
                    "Basketball" -> R.drawable.icon_basketball
                    "American Football" -> R.drawable.icon_american_football
                    else -> 0
                }
            )
            tab.text = if (sportNames[position] == "American Football") "Am. Football" else sportNames[position]
        }.attach()

        sportViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                switchToDateToday()

                val sportName = sportNames[position]
                val currentDate = getCurrentDate()
                updateFragmentsForNewSportAndDate(sportName, currentDate)
            }
        })
    }

    private fun updateFragmentsForNewSportAndDate(sportName: String, newDate: String) {
        val currentFragment = fragmentReferences[sportName]
        currentFragment?.updateSportAndDate(sportName, newDate)
    }

    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Calendar.getInstance().time)
    }


    private fun switchToDateToday() {
        val dates = mainListPageViewModel.datesList.value ?: return
        val todayIndex = dates.indexOfFirst { isSameDay(it, Calendar.getInstance()) }
        if (todayIndex != -1) {
            dateTabLayout.selectTab(dateTabLayout.getTabAt(todayIndex))
        }
    }

    private fun setupDateTabs(dates: List<Calendar>) {
        dateTabLayout.removeAllTabs()
        val todayIndex = dates.indexOfFirst { isSameDay(it, Calendar.getInstance()) }

        for (date in dates) {
            dateTabLayout.addTab(dateTabLayout.newTab().setText(getFormattedDate(date)))
        }

        if (todayIndex != -1) {
            dateTabLayout.getTabAt(todayIndex)?.select()
        }

        dateTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                val selectedDate = DateFormat.format("yyyy-MM-dd", dates[tab.position]).toString()
                val currentSport = sports[sportViewPager.currentItem]
                updateFragmentsForNewSportAndDate(currentSport, selectedDate)
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


