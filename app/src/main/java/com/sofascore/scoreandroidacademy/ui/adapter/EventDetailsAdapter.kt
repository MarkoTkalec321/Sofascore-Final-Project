package com.sofascore.scoreandroidacademy.ui.adapter

import android.content.Context
import android.graphics.drawable.LayerDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.sofascore.scoreandroidacademy.R
import com.sofascore.scoreandroidacademy.data.models.EventDetailsResponse
import com.sofascore.scoreandroidacademy.databinding.LayoutEventDetailsMatchStatusBinding
import com.sofascore.scoreandroidacademy.databinding.LayoutFootballAwayIncidentsBinding
import com.sofascore.scoreandroidacademy.databinding.LayoutFootballHomeIncidentsBinding

import com.sofascore.scoreandroidacademy.util.EventDetailsViewItem
import com.sofascore.scoreandroidacademy.util.IncidentBinding
import com.sofascore.scoreandroidacademy.util.ViewType
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class EventDetailsAdapter(
    private val context: Context,
    private val sportName: String,
    private val startDate: String? = null,
    private val status: String
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val items: MutableList<EventDetailsViewItem> = mutableListOf()

    inner class EventStatusViewHolder(private val binding: LayoutEventDetailsMatchStatusBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(eventStatus: String){
            if(status == "inprogress") {
                binding.eventStatus.setTextColor(ContextCompat.getColor(context, R.color.specific_live))
            } else {
                binding.eventStatus.setTextColor(ContextCompat.getColor(context, R.color.on_surface_on_surface_lv_2))
            }

            binding.eventStatus.text = eventStatus
        }
    }
    inner class EventIncidentViewHolder(private val binding: LayoutFootballHomeIncidentsBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(incident: EventDetailsResponse){
            bindIncident(binding, incident, context)
        }
    }

    inner class EventIncidentAwayViewHolder(private val binding: LayoutFootballAwayIncidentsBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(incident: EventDetailsResponse){
            bindIncident(binding, incident, context)
        }
    }

    private fun bindIncident(binding: ViewBinding, incident: EventDetailsResponse, context: Context) {

        binding.apply {

            val minute = root.findViewById<TextView>(R.id.minute)
            val minuteBasketball = root.findViewById<TextView>(R.id.minute_basketball)
            val playerName = root.findViewById<TextView>(R.id.player_name)
            val constraintLayoutScore = root.findViewById<ConstraintLayout>(R.id.constraint_layout_score)
            val homeScore = root.findViewById<TextView>(R.id.home_score)
            val awayScore = root.findViewById<TextView>(R.id.away_score)
            val logoType = root.findViewById<ImageView>(R.id.logo_type)

            if(sportName == "Football"){
                minute.text = String.format("${incident.time}'")
            } else if (sportName == "Basketball"){
                minuteBasketball.text = String.format("${incident.time}")
                minute.visibility = View.GONE
                playerName.visibility = View.GONE
            } else if(sportName == "American Football"){
                minuteBasketball.visibility = View.GONE

                if(startDate != null) {
                    val formatter = DateTimeFormatter.ofPattern("HH:mm")

                    val timeString = startDate
                        .substringAfter('T')
                        .substringBefore('+')
                        .substringBeforeLast(':')

                    val startTime = LocalTime.parse(timeString, formatter)
                    val updatedTime = startTime.plusMinutes(incident.time.toLong())

                    val formattedUpdatedTime = updatedTime.format(formatter)
                    minute.text = formattedUpdatedTime
                }
            }

            playerName.text = incident.player?.name
            if (incident.homeScore == null || incident.awayScore == null) {
                constraintLayoutScore.visibility = View.GONE
            } else {
                homeScore.text = incident.homeScore.toString()
                awayScore.text = incident.awayScore.toString()
            }

            when (incident.type) {
                "goal" -> {
                    when (incident.goalType) {
                        "regular" -> logoType.setImageResource(R.drawable.ic_football_regular_goal)
                        "penalty" -> logoType.setImageResource(R.drawable.ic_football_penalty_score)
                        "penaltymissed" -> logoType.setImageResource(R.drawable.ic_football_penalty_missed)
                        "owngoal" -> logoType.setImageResource(R.drawable.ic_football_autogoal)

                        "onepoint" -> logoType.setImageResource(R.drawable.ic_basketball_1)
                        "twopoint" -> logoType.setImageResource(R.drawable.ic_basketball_2)
                        "threepoint" -> logoType.setImageResource(R.drawable.ic_basketball_3)

                        "touchdown" -> logoType.setImageResource(R.drawable.ic_amf_touchdown)
                        "extrapoint" -> logoType.setImageResource(R.drawable.ic_amf_extra_point)
                        "fieldgoal" -> logoType.setImageResource(R.drawable.ic_amf_field_goal)
                        "safety" -> logoType.setImageResource(R.drawable.ic_amf_rogue)

                    }
                }
                "card" -> {
                    when (incident.color) {
                        "yellow" -> logoType.setImageResource(R.drawable.ic_football_card_yellow)
                        "red" -> logoType.setImageResource(R.drawable.ic_football_card_red)
                        "yellowred" -> {
                            val yellowCardDrawable = ContextCompat.getDrawable(
                                context,
                                R.drawable.ic_football_card_yellow
                            )
                            val redCardDrawable = ContextCompat.getDrawable(
                                context,
                                R.drawable.ic_football_card_red
                            )

                            val layers = arrayOf(yellowCardDrawable, redCardDrawable)
                            val layerDrawable = LayerDrawable(layers)

                            layerDrawable.setLayerInset(1, 12, 8, 0, 0)

                            logoType.setImageDrawable(layerDrawable)
                        }
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            ViewType.LayoutOne -> EventStatusViewHolder(LayoutEventDetailsMatchStatusBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            ViewType.LayoutTwo -> EventIncidentViewHolder(LayoutFootballHomeIncidentsBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            ViewType.LayoutThree -> EventIncidentAwayViewHolder(LayoutFootballAwayIncidentsBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is EventDetailsViewItem.EventStatusData -> ViewType.LayoutOne
            is EventDetailsViewItem.EventIncidentsData -> {
                val incident = (items[position] as EventDetailsViewItem.EventIncidentsData).eventIncidents
                if (incident.type == "goal") {
                    when (incident.scoringTeam) {
                        "home" -> ViewType.LayoutTwo
                        "away" -> ViewType.LayoutThree
                        else -> { throw IllegalArgumentException("Invalid scoring team type") }
                    }
                } else {
                    when (incident.teamSide) {
                        "home" -> ViewType.LayoutTwo
                        "away" -> ViewType.LayoutThree
                        else -> { throw IllegalArgumentException("Invalid team side type") }
                    }
                }
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is EventDetailsViewItem.EventStatusData -> (holder as EventStatusViewHolder).bind(item.eventStatus)
            is EventDetailsViewItem.EventIncidentsData -> {
                if (holder is EventIncidentViewHolder) {
                    holder.bind(item.eventIncidents)
                } else if (holder is EventIncidentAwayViewHolder) {
                    holder.bind(item.eventIncidents)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }


    fun updateItems(newItems: MutableList<EventDetailsViewItem>) {
        val difCallback = EventDetailsDiffCallback(items, newItems)
        val diffResult = DiffUtil.calculateDiff(difCallback)
        items.clear()
        items.addAll(newItems)
        diffResult.dispatchUpdatesTo(this)
    }

    class EventDetailsDiffCallback(
        private val oldList: List<EventDetailsViewItem>,
        private val newList: List<EventDetailsViewItem>
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