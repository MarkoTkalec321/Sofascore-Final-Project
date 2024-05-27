package com.sofascore.scoreandroidacademy.ui

import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.sofascore.scoreandroidacademy.R
import com.sofascore.scoreandroidacademy.data.local.entity.SportEntity
import com.sofascore.scoreandroidacademy.ui.adapter.MainListViewPagerAdapter
import com.sofascore.scoreandroidacademy.databinding.FragmentMainListPageBinding
import com.sofascore.scoreandroidacademy.data.remote.Result
import com.sofascore.scoreandroidacademy.util.IconConverter
import com.sofascore.scoreandroidacademy.util.getFormattedDate
import com.sofascore.scoreandroidacademy.util.isSameDay
import com.sofascore.scoreandroidacademy.util.getCurrentDate
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainListPageFragment : Fragment() {
    private var _binding: FragmentMainListPageBinding? = null
    private val binding get() = _binding!!
    private lateinit var sportViewPager: ViewPager2
    private lateinit var sportTabLayout: TabLayout
    private lateinit var dateTabLayout: TabLayout

    private var isUserInteraction = true

    //private lateinit var dateAdapter: DateAdapter
    //private lateinit var recyclerView: RecyclerView
    private val sharedViewModel by activityViewModels<SharedViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
        sharedViewModel.sportsList.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Success -> {
                    setupViewPagerAndTabs(result.data)
                }
                is Result.Error -> Log.e("API Error", "Error fetching data: ${result.error.message}", result.error)
            }
        }
        sharedViewModel.datesList.observe(viewLifecycleOwner) { dates ->
            setupDateTabs(dates)
        }
    }

    private fun setupViewPagerAndTabs(sportsList: List<SportEntity>) {
        val sportNames = sportsList.map { it.name }

        val adapter = MainListViewPagerAdapter(childFragmentManager, lifecycle)
        sportViewPager.adapter = adapter

        sportNames.forEach { sportName ->
            val fragment = SportFragment.newInstance()
            adapter.addFragment(fragment, sportName)
        }

        TabLayoutMediator(sportTabLayout, sportViewPager) { tab, position ->
            context?.let {
                val iconSize = IconConverter.dpToPx(it, 32)
                val icon = when (sportNames[position]) {
                    "Football" -> IconConverter.resizeIcon(it, R.drawable.icon, iconSize, iconSize)
                    "Basketball" -> IconConverter.resizeIcon(it, R.drawable.icon_basketball, iconSize, iconSize)
                    "American Football" -> IconConverter.resizeIcon(it, R.drawable.icon_american_football, iconSize, iconSize)
                    else -> null
                }
                tab.icon = icon
                tab.text = when (sportNames[position]) {
                    "American Football" -> "Am. Football"
                    else -> sportNames[position]
                }
            }
        }.attach()

        sportViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                if (isUserInteraction) {
                    val currentSport = adapter.getPageTitle(position)
                    val currentDate = getCurrentDate()
                    switchToDateToday()

                    Log.d("sportViewPager", "sportViewPager CLICKED????!?!?!?!?")
                    //"2024-05-26"
                    sharedViewModel.updateSportAndDate(currentSport, currentDate)
                }
            }
        })
    }
    private fun switchToDateToday() {
        val dates = sharedViewModel.datesList.value ?: return
        val todayIndex = dates.indexOfFirst { isSameDay(it, Calendar.getInstance()) }
        if (todayIndex != -1) {
            isUserInteraction = false
            dateTabLayout.getTabAt(todayIndex)?.select()
            isUserInteraction = true
        }
    }


    /*private fun switchToDateToday() {
        val dates = sharedViewModel.datesList.value ?: return
        val todayIndex = dates.indexOfFirst { isSameDay(it, Calendar.getInstance()) }
        if (todayIndex != -1) {
            dateTabLayout.selectTab(dateTabLayout.getTabAt(todayIndex))
        }
    }*/

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
                if (isUserInteraction) {
                    val selectedDate = DateFormat.format("yyyy-MM-dd", dates[tab.position]).toString()
                    val currentSport = (sportViewPager.adapter as MainListViewPagerAdapter).getPageTitle(sportViewPager.currentItem)

                    Log.d("dateTabLayout", "dateTabLayout CLICKED????!?!?!?!?")
                    sharedViewModel.updateSportAndDate(currentSport, selectedDate)
                }
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


