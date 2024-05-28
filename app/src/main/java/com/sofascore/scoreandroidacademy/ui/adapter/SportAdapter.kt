package com.sofascore.scoreandroidacademy.ui.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.sofascore.scoreandroidacademy.R
import com.sofascore.scoreandroidacademy.data.local.entity.MatchEntity
import com.sofascore.scoreandroidacademy.data.local.entity.TournamentEntity
import com.sofascore.scoreandroidacademy.data.models.MatchResponse
import com.sofascore.scoreandroidacademy.data.models.TournamentResponse
import com.sofascore.scoreandroidacademy.databinding.LayoutMatchBinding
import com.sofascore.scoreandroidacademy.databinding.LayoutTournamentBinding
import com.sofascore.scoreandroidacademy.util.IconConverter.Companion.loadImageFromByteArray
import com.sofascore.scoreandroidacademy.util.TournamentViewItem
import com.sofascore.scoreandroidacademy.util.ViewType

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class SportAdapter(
    private val onTournamentClick: (TournamentEntity) -> Unit,
    private val onMatchClick: (MatchEntity) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        private val items: MutableList<TournamentViewItem> = mutableListOf()

    inner class TournamentViewHolder(private val binding: LayoutTournamentBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(tournament: TournamentEntity) {
            itemView.setOnClickListener {
                onTournamentClick(tournament)
            }

            binding.tournamentLogo.loadImageFromByteArray(tournament.tournamentLogo)
            binding.countryName.text = tournament.country.name
            binding.countryName.setTextColor(ContextCompat.getColor(binding.root.context, R.color.on_surface_on_surface_lv_1))
            binding.tournamentName.text = tournament.name
            binding.tournamentName.setTextColor(ContextCompat.getColor(binding.root.context, R.color.on_surface_on_surface_lv_2))
        }
    }

    @SuppressLint("SetTextI18n")
    inner class MatchViewHolder(private val binding: LayoutMatchBinding) : RecyclerView.ViewHolder(binding.root) {
        private val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.getDefault())
        private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        init {
            dateFormat.timeZone = TimeZone.getTimeZone("UTC")
            timeFormat.timeZone = TimeZone.getDefault()
        }

        fun bind(match: MatchEntity) {
            resetColors()
            itemView.setOnClickListener {
                onMatchClick(match)
            }

            binding.startTime.text = formatStartTime(match.startDate)

            when (match.status) {
                "finished" -> handleFinishedMatch(match)
                "notstarted" -> { binding.finishTime.text = "-"}
                "inprogress" -> handleInProgressMatch(match)
            }

            binding.homeTeamLogo.loadImageFromByteArray(match.homeTeam.teamLogo)
            binding.awayTeamLogo.loadImageFromByteArray(match.awayTeam.teamLogo)
            binding.homeTeamName.text = match.homeTeam.name
            binding.homeScore.text = match.homeScore.total?.toString() ?: ""
            binding.awayTeamName.text = match.awayTeam.name
            binding.awayScore.text = match.awayScore.total?.toString() ?: ""
        }

        private fun resetColors() {
            val defaultColor = ContextCompat.getColor(binding.root.context, R.color.on_surface_on_surface_lv_2)
            binding.homeTeamName.setTextColor(defaultColor)
            binding.homeScore.setTextColor(defaultColor)
            binding.awayTeamName.setTextColor(defaultColor)
            binding.awayScore.setTextColor(defaultColor)
            binding.finishTime.setTextColor(defaultColor)
        }

        private fun formatStartTime(startDate: String): String {
            val date = dateFormat.parse(startDate) ?: return "Time Error"
            return timeFormat.format(date)
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ViewType.LayoutOne -> TournamentViewHolder(LayoutTournamentBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            ViewType.LayoutTwo -> MatchViewHolder(LayoutMatchBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is TournamentViewItem.TournamentData -> ViewType.LayoutOne
            is TournamentViewItem.MatchData -> ViewType.LayoutTwo
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is TournamentViewItem.TournamentData -> (holder as TournamentViewHolder).bind(item.tournament)
            is TournamentViewItem.MatchData -> (holder as MatchViewHolder).bind(item.match)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun updateItems(newItems: MutableList<TournamentViewItem>) {
        val diffCallback = TournamentMatchesDiffCallback(items, newItems)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        items.clear()
        items.addAll(newItems)
        diffResult.dispatchUpdatesTo(this)
    }

    class TournamentMatchesDiffCallback(
        private val oldList: List<TournamentViewItem>,
        private val newList: List<TournamentViewItem>
    ) : DiffUtil.Callback() {

        override fun getOldListSize() = oldList.size

        override fun getNewListSize() = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }

}