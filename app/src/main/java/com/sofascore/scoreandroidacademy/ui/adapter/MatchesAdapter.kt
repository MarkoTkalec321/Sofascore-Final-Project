package com.sofascore.scoreandroidacademy.ui.adapter
import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.paging.PagingData
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.sofascore.scoreandroidacademy.R
import com.sofascore.scoreandroidacademy.data.local.entity.MatchEntity
import com.sofascore.scoreandroidacademy.databinding.LayoutTextViewLinearLayoutBinding
import com.sofascore.scoreandroidacademy.util.RoundMatchesViewItem
import com.sofascore.scoreandroidacademy.util.ViewType
import com.sofascore.scoreandroidacademy.util.ViewHolderFactory

class MatchesAdapter(
    private val onMatchClick: (MatchEntity) -> Unit,
    private val insideMatchesAdapter: Boolean = true
) : PagingDataAdapter<RoundMatchesViewItem, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ViewType.LayoutOne -> RoundViewHolder(LayoutTextViewLinearLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            ViewType.LayoutTwo -> ViewHolderFactory.createMatchViewHolder(parent, onMatchClick)
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder.itemView.visibility = View.VISIBLE
        val item = getItem(position) ?: return
        when (item) {
            is RoundMatchesViewItem.RoundData -> (holder as? RoundViewHolder)?.bind(item.round)
            is RoundMatchesViewItem.MatchData -> {
                (holder as MatchViewHolder).bind(item.match, insideMatchesAdapter)
            }
        }
    }

    inner class RoundViewHolder(private val binding: LayoutTextViewLinearLayoutBinding): RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(match: Int) {
            binding.dateTextView.text = "Round $match"
            binding.dateTextView.setTextColor(ContextCompat.getColor(binding.root.context, R.color.on_surface_on_surface_lv_1))
        }
    }

    override fun getItemViewType(position: Int): Int {
        getItem(position)?.let {
            return when (it) {
                is RoundMatchesViewItem.RoundData -> ViewType.LayoutOne
                is RoundMatchesViewItem.MatchData -> ViewType.LayoutTwo
            }
        }
        return super.getItemViewType(position)
    }
    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<RoundMatchesViewItem>() {
            override fun areItemsTheSame(oldItem: RoundMatchesViewItem, newItem: RoundMatchesViewItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: RoundMatchesViewItem, newItem: RoundMatchesViewItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}
