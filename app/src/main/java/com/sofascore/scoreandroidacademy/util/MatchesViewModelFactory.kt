package com.sofascore.scoreandroidacademy.util

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sofascore.scoreandroidacademy.data.repository.MatchRepository
import com.sofascore.scoreandroidacademy.ui.viewmodel.MatchesViewModel

class MatchesViewModelFactory(
    private val application: Application,
    private val repository: MatchRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MatchesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MatchesViewModel(application, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}