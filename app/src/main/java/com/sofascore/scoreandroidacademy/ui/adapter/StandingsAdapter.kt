package com.sofascore.scoreandroidacademy.ui.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.sofascore.scoreandroidacademy.data.models.SortedStandingsRowResponse
import com.sofascore.scoreandroidacademy.data.models.StandingsMatchResponse
import com.sofascore.scoreandroidacademy.databinding.LayoutStandingsBinding

class StandingsAdapter : RecyclerView.Adapter<StandingsAdapter.ViewHolder>() {
    private var rows: List<SortedStandingsRowResponse> = emptyList()
    private var firstRow: SortedStandingsRowResponse? = null

    class ViewHolder(private val binding: LayoutStandingsBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n", "DefaultLocale")
        fun bind(row: SortedStandingsRowResponse, position: Int, firstRow: SortedStandingsRowResponse?) {
            with(binding) {
                number.text = (position + 1).toString()
                team.text = row.team.name
                played.text = row.played.toString()
                wins.text = row.wins.toString()
                losses.text = row.losses.toString()

                firstRow?.let {
                    difference.text = (it.scoresFor - row.scoresAgainst).toString()
                    val gamesBehind = (it.wins - row.wins + row.losses - it.losses) / 2.0
                    binding.gamesBehind.text = String.format("%.1f", gamesBehind)
                }

                val pct = (row.wins + 0.5 * row.draws) / row.played
                percentage.text = String.format("%.3f", pct)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = LayoutStandingsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(rows[position], position, firstRow)
    }

    override fun getItemCount(): Int = rows.size

    fun updateData(newRows: List<SortedStandingsRowResponse>) {
        val diffCallback = ItemClassDiffCallback(this.rows, newRows)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.rows = newRows
        firstRow = rows.firstOrNull()
        diffResult.dispatchUpdatesTo(this)
    }
    class ItemClassDiffCallback(
        private val oldList: List<SortedStandingsRowResponse>,
        private val newList: List<SortedStandingsRowResponse>
    ) : DiffUtil.Callback() {

        override fun getOldListSize() = oldList.size

        override fun getNewListSize() = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].id == newList[newItemPosition].id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}

