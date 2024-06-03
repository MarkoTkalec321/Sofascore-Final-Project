package com.sofascore.scoreandroidacademy.ui.fragment

import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.sofascore.scoreandroidacademy.R
import com.sofascore.scoreandroidacademy.data.local.entity.SportEntity
import com.sofascore.scoreandroidacademy.ui.adapter.FragmentAdapter
import com.sofascore.scoreandroidacademy.databinding.FragmentMainListPageBinding
import com.sofascore.scoreandroidacademy.data.remote.Result
import com.sofascore.scoreandroidacademy.ui.viewmodel.SharedViewModel
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

    private val sharedViewModel by activityViewModels<SharedViewModel>()


    /*override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("selectedDateTabIndex", dateTabLayout.selectedTabPosition)

        val selectedDate = dateTabLayout.getTabAt(dateTabLayout.selectedTabPosition)?.tag as String
        outState.putString("selectedDate", selectedDate)

        Log.d("SaveState", "Saving state: Index=${dateTabLayout.selectedTabPosition}, Date=$selectedDate")
    }*/

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        /*Log.d("FragmentLifecycle", "onViewCreated is called")

        super.onViewCreated(view, savedInstanceState)

        val restoredDateTabIndex = savedInstanceState?.getInt("selectedDateTabIndex", 0) ?: 0
        val restoredDate = savedInstanceState?.getString("selectedDate") ?: "test"

        Log.d("FragmentState", "Restored Index: $restoredDateTabIndex, Restored Date: $restoredDate")*/

        /*savedInstanceState?.getInt("selectedDateTabIndex")?.let { index ->
            if (index != -1 && index < dateTabLayout.tabCount) {
                dateTabLayout.getTabAt(index)?.select()
            }
        }*/
        val args = try {
            MainListPageFragmentArgs.fromBundle(requireArguments())
        } catch (e: IllegalArgumentException) {
            Log.e("MainListPageFragment", "Date argument is missing: ${e.message}")
            MainListPageFragmentArgs("defaultDate")
        }

        val dateFromArg = args.date
        val sportFromArg = args.sport


        setupObservers(sportFromArg, dateFromArg)
        with(arguments) {
            this?.remove("date")
            this?.remove("sport")
        }

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

    private fun setupObservers(sportFromArg: String, dateFromArg: String) {

        sharedViewModel.sportsList.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Success -> {
                    setupSportViewPagerAndTabs(result.data, sportFromArg, dateFromArg)
                }
                is Result.Error -> Log.e("API Error", "Error fetching data: ${result.error.message}", result.error)
            }
        }
        sharedViewModel.datesList.observe(viewLifecycleOwner) { dates ->
            setupDateTabs(dates, sportFromArg, dateFromArg)
        }
    }

    private fun setupSportViewPagerAndTabs(sportsList: List<SportEntity>, sportFromArg: String, dateFromArg: String) {
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

                    // Check if the sport name is "American Football" and abbreviate it if so
                    val sportName = if (sportNames[position] == "American Football") "Am. Football" else sportNames[position]
                    tab.text = sportName
                }
            }.attach()

            /*sportViewPager.post {
                if (sportTabLayout.selectedTabPosition == -1 && sportTabLayout.tabCount > 0) {
                    sportTabLayout.getTabAt(0)?.select()
                }
            }*/
        }

        sportTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                //sportViewPager.post {
                    val sportSelected = if (sportFromArg == "defaultSport") {
                        //(sportViewPager.adapter as FragmentAdapter).getPageTitle(tab.position)
                        (sportViewPager.adapter as FragmentAdapter).getPageTitle(sportViewPager.currentItem)
                    } else {
                        sportFromArg
                    }
                    //Log.d("sportTabLayout", "Sport selected: $currentSport")

                    /*val dateSelected = if(dateFromArg == "defaultDate") {
                    getCurrentDate()
                } else { }*/

                    val dateSelected = if (dateFromArg == "defaultDate") {
                        getCurrentDate()
                    } else {
                        dateFromArg
                    }


                    Log.d("dateFromArg", dateFromArg.toString())
                    Log.d("sportFromArg", sportFromArg.toString())
                dateTabLayout.post {
                    switchToDateTab(getCurrentDate())


                    Log.d("sportViewPager", "sportViewPager CLICKED????!?!?!?!?")

                    //"2024-05-26"
                    //"Basketball"
                    //sharedViewModel.updateSportAndDate(currentSport, currentDate)
                    Log.d("sportViewPager", "Updating sport and date after page selected")

                    /*val date =
                    (dateTabLayout.getTabAt()adapter as FragmentAdapter).getPageTitle(
                        .currentItem
                    )*/

                    /*val selectedDate =
                    DateFormat.format("yyyy-MM-dd", dates[tab.position]).toString()*/

                    //sharedViewModel.updateSport(sportSelected)
                    val test =
                        (sportViewPager.adapter as FragmentAdapter).getPageTitle(sportViewPager.currentItem)
                    sharedViewModel.updateSportAndDate(test, getCurrentDate())
                }
                //}
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

    }

    private fun switchToDateTab(dateFromArg: String? = null) {
        val dates = sharedViewModel.datesList.value ?: return
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

        // Find the index of the target date and select the corresponding tab
        dates.indexOfFirst { isSameDay(it, targetDate) }.takeIf { it != -1 }?.let {
            dateTabLayout.getTabAt(it)?.select()
        }
    }
    /*private fun switchToDateTab(dateFromArg: String? = null) {
        val dates = sharedViewModel.datesList.value ?: return

        val targetDate = when {
            dateFromArg != null && dateFromArg != "defaultDate" -> SimpleDateFormat("yyyy-MM-dd").parse(dateFromArg)?.let {
                Calendar.getInstance().apply { time = it }
            }
            else -> Calendar.getInstance()
        }
        val targetDateIndex = dates.indexOfFirst { date -> targetDate.isSameDay(date, it) ?: false }
        if (targetDate,Index != -1) {
            dateTabLayout.getTabAt(targetDate,Index)?.select()
        }
        //val todayIndex = dates.indexOfFirst { isSameDay(it, Calendar.getInstance()) }
        *//*if (todayIndex != -1) {
            dateTabLayout.getTabAt(todayIndex)?.select()
        }*//*
    }*/


    private fun setupDateTabs(dates: List<Calendar>, sportFromArg: String, dateFromArg: String) {
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
                //sportViewPager.post {

                val sportSelected = if (sportFromArg == "defaultSport") {
                    (sportViewPager.adapter as FragmentAdapter).getPageTitle(sportViewPager.currentItem)
                } else {
                    sportFromArg
                }

                val newDateSelected = DateFormat.format("yyyy-MM-dd", dates[tab.position]).toString()
                if (newDateSelected != dateFromArg) {
                    Log.d("TabSelection", "Date has changed from $dateFromArg to $newDateSelected")

                    val test = (sportViewPager.adapter as FragmentAdapter).getPageTitle(sportViewPager.currentItem)
                    val test2 = DateFormat.format("yyyy-MM-dd", dates[tab.position]).toString()
                   // sharedViewModel.updateSportAndDate(test, test2)
                   // sharedViewModel.updateDate(newDateSelected)
                } else {
                    Log.d("TabSelection", "Date selected is the same as previous: $dateFromArg")
                }

                    /*val sportSelected = if(sportFromArg == "defaultSport") {
                        (sportViewPager.adapter as FragmentAdapter).getPageTitle(sportViewPager.currentItem)
                    } else {
                        sportFromArg
                    }

                    *//*val dateSelected = if(dateFromArg == "defaultDate"){
                        DateFormat.format("yyyy-MM-dd", dates[tab.position]).toString()
                    } else {
                        dateFromArg
                    }*//*

                    val newDateSelected = DateFormat.format("yyyy-MM-dd", dates[tab.position]).toString()

                    if (newDateSelected != dateFromArg) {
                        Log.d("TabSelection", "Date has changed from $dateFromArg to $newDateSelected")
                        dateFromArg = newDateSelected  // Update the current date
                        sharedViewModel.updateSportAndDate(sportSelected, newDateSelected)
                    } else {
                        Log.d("TabSelection", "Date selected is the same as previous: $dateFromArg")
                    }*/



                    /*val selectedDate =
                        DateFormat.format("yyyy-MM-dd", dates[tab.position]).toString()
                    val currentSport =
                        (sportViewPager.adapter as FragmentAdapter).getPageTitle(
                            sportViewPager.currentItem
                        )*/

                    //Log.d("dateTabLayout", "dateTabLayout CLICKED????!?!?!?!?")

                    val test = (sportViewPager.adapter as FragmentAdapter).getPageTitle(sportViewPager.currentItem)
                    val test2 = DateFormat.format("yyyy-MM-dd", dates[tab.position]).toString()
                    sharedViewModel.updateSportAndDate(test, test2)
                    //sharedViewModel.updateSportAndDate(sportSelected, dateSelected)
                    /*arguments?.putString("defaultDate", dateSelected)
                    arguments?.putString("defaultSport", sportSelected)*/
                //}
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


