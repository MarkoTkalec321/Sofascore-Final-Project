package com.sofascore.scoreandroidacademy.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/*class DateAdapter(private val dates: List<Calendar>, private val onDateSelected: (String) -> Unit) : RecyclerView.Adapter<DateAdapter.DateViewHolder>() {

    private var selectedPosition = RecyclerView.NO_POSITION

    class DateViewHolder(private val binding: ItemDateBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(date: Calendar, isSelected: Boolean, onDateSelected: (String) -> Unit) {
            binding.dateText.text = SimpleDateFormat("EEE, dd.MM.yyyy", Locale.getDefault()).format(date.time)
            binding.dateText.isSelected = isSelected

            itemView.setOnClickListener {
                onDateSelected(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date.time))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DateViewHolder {
        val binding = ItemDateBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DateViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DateViewHolder, position: Int) {
        val isSelected = position == selectedPosition
        holder.bind(dates[position], isSelected) { dateStr ->
            selectedPosition = position
            notifyDataSetChanged()
            onDateSelected(dateStr)
        }
    }

    override fun getItemCount() = dates.size

    fun selectDateByIndex(index: Int) {
        selectedPosition = index
        notifyDataSetChanged()
    }
}*/
