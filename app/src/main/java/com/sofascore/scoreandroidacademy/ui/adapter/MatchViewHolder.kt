package com.sofascore.scoreandroidacademy.ui.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.sofascore.scoreandroidacademy.R
import com.sofascore.scoreandroidacademy.data.local.entity.MatchEntity
import com.sofascore.scoreandroidacademy.databinding.LayoutMatchBinding
import com.sofascore.scoreandroidacademy.util.IconConverter.Companion.loadImageFromByteArray
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@SuppressLint("SetTextI18n")
class MatchViewHolder(private val binding: LayoutMatchBinding,
                      private val onMatchClick: (MatchEntity) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    private val dateTimeFormat = SimpleDateFormat("dd.MM.yy", Locale.getDefault())

    init {
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")
        timeFormat.timeZone = TimeZone.getDefault()
    }

    fun bind(match: MatchEntity, insideMatchesAdapter: Boolean) {
        resetColors()
        itemView.setOnClickListener {
            onMatchClick(match)
        }

        binding.startTime.text = formatStartTime(match.startDate, insideMatchesAdapter)

        binding.homeTeamLogo.loadImageFromByteArray(match.homeTeam.teamLogo)
        binding.awayTeamLogo.loadImageFromByteArray(match.awayTeam.teamLogo)
        binding.homeTeamName.text = match.homeTeam.name
        binding.awayTeamName.text = match.awayTeam.name

        if (match.status != "notstarted") {
            binding.homeScore.visibility = View.VISIBLE
            binding.awayScore.visibility = View.VISIBLE
            binding.homeScore.text = match.homeScore.total.toString()
            binding.awayScore.text = match.awayScore.total.toString()
        } else {
            binding.homeScore.visibility = View.INVISIBLE
            binding.awayScore.visibility = View.INVISIBLE
        }

        Log.d("MatchDetails", "Home Score: ${match.homeScore}")
        Log.d("MatchDetails", "Away Score: ${match.awayScore}")
        when (match.status) {
            "finished" -> handleFinishedMatch(match)
            "notstarted" -> { binding.finishTime.text = "-" }
            "inprogress" -> handleInProgressMatch(match)
        }

    }

    private fun resetColors() {
        val defaultColor = ContextCompat.getColor(binding.root.context, R.color.on_surface_on_surface_lv_2)
        binding.homeTeamName.setTextColor(defaultColor)
        binding.homeScore.setTextColor(defaultColor)
        binding.awayTeamName.setTextColor(defaultColor)
        binding.awayScore.setTextColor(defaultColor)
        binding.finishTime.setTextColor(defaultColor)
    }

    private fun formatStartTime(startDate: String, insideMatchesAdapter: Boolean): String {
        val date = dateFormat.parse(startDate) ?: return "Time Error"
        return if (!insideMatchesAdapter) {
            timeFormat.format(date)
        } else {
            dateTimeFormat.format(date)
        }
    }

    private fun handleFinishedMatch(match: MatchEntity) {
        binding.finishTime.text = "FT"
        setTextColorForTeams(match.winnerCode)
    }

    private fun handleInProgressMatch(match: MatchEntity) {
        val startDate = dateFormat.parse(match.startDate) ?: return
        val currentDate = Date()
        val durationInMinutes = (currentDate.time - startDate.time) / 60000  // Milliseconds to minutes
        binding.finishTime.text = "${durationInMinutes}'"
        setLiveMatchColors()
    }


    private fun setColor(homeColor: Int, awayColor: Int) {
        binding.homeTeamName.setTextColor(ContextCompat.getColor(binding.root.context, homeColor))
        binding.homeScore.setTextColor(ContextCompat.getColor(binding.root.context, homeColor))
        binding.awayTeamName.setTextColor(ContextCompat.getColor(binding.root.context, awayColor))
        binding.awayScore.setTextColor(ContextCompat.getColor(binding.root.context, awayColor))
    }

    private fun setLiveMatchColors() {
        val liveColor = ContextCompat.getColor(binding.root.context, R.color.specific_live)
        binding.finishTime.setTextColor(liveColor)
        binding.homeScore.setTextColor(liveColor)
        binding.awayScore.setTextColor(liveColor)
    }

    private fun setTextColorForTeams(winnerCode: String?) {
        when (winnerCode) {
            "home" -> {
                setColor(R.color.on_surface_on_surface_lv_1, R.color.on_surface_on_surface_lv_2)
            }
            "away" -> {
                setColor(R.color.on_surface_on_surface_lv_2, R.color.on_surface_on_surface_lv_1)
            }
            "draw" -> {
                setColor(R.color.on_surface_on_surface_lv_2, R.color.on_surface_on_surface_lv_2)
            }
        }
    }

}