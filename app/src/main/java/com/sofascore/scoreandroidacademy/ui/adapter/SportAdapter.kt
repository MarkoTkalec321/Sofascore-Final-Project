package com.sofascore.scoreandroidacademy.ui.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
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
import com.sofascore.scoreandroidacademy.util.ViewHolderFactory

import com.sofascore.scoreandroidacademy.util.ViewType

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class SportAdapter(
    private val onTournamentClick: (TournamentEntity) -> Unit,
    private val onMatchClick: (MatchEntity) -> Unit) :
    RecyclerView.Adapter<ViewHolder>() {
        private val items: MutableList<TournamentViewItem> = mutableListOf()

    inner class TournamentViewHolder(private val binding: LayoutTournamentBinding): ViewHolder(binding.root) {
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ViewType.LayoutOne -> TournamentViewHolder(LayoutTournamentBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            //ViewType.LayoutTwo -> MatchViewHolder(LayoutMatchBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            ViewType.LayoutTwo -> ViewHolderFactory.createMatchViewHolder(parent, onMatchClick)
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
            is TournamentViewItem.MatchData ->
                (holder as MatchViewHolder).bind(item.match, false)
                //(holder as MatchViewHolder).bind(item.match)
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