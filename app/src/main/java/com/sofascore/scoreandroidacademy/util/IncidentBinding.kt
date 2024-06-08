package com.sofascore.scoreandroidacademy.util

import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.Bindable
import androidx.databinding.Observable

abstract class IncidentBinding : Observable{
    @get:Bindable
    abstract val minute: TextView
    @get:Bindable
    abstract var playerName: String
    @get:Bindable
    abstract val homeScore: TextView
    @get:Bindable
    abstract val awayScore: TextView
    @get:Bindable
    abstract val logoType: ImageView
    @get:Bindable
    abstract val constraintLayoutScore: ConstraintLayout
}