package com.sofascore.scoreandroidacademy.util

import android.view.LayoutInflater
import android.view.ViewGroup
import com.sofascore.scoreandroidacademy.data.local.entity.MatchEntity
import com.sofascore.scoreandroidacademy.databinding.LayoutMatchBinding
import com.sofascore.scoreandroidacademy.ui.adapter.MatchViewHolder

object ViewHolderFactory {
    fun createMatchViewHolder(parent: ViewGroup, onMatchClick: (MatchEntity) -> Unit): MatchViewHolder {
        val binding = LayoutMatchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MatchViewHolder(binding, onMatchClick)
    }
}
