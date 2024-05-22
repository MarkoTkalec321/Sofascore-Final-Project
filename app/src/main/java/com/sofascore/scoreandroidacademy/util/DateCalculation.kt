package com.sofascore.scoreandroidacademy.util

import java.text.SimpleDateFormat
import java.util.*

fun getFormattedDate(calendar: Calendar): String {
    val today = Calendar.getInstance()

    return when {
        isSameDay(calendar, today) -> "TODAY\n${SimpleDateFormat("dd.MM.", Locale.getDefault()).format(calendar.time)}"
        else -> "${SimpleDateFormat("EEE", Locale.getDefault()).format(calendar.time).uppercase(Locale.getDefault())}\n${SimpleDateFormat("dd.MM.", Locale.getDefault()).format(calendar.time)}"
    }
}

fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
            cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
}

fun getDatesRange(): List<Calendar> {
    val dates = mutableListOf<Calendar>()
    val calendar = Calendar.getInstance()

    dates.add(calendar.clone() as Calendar)

    repeat(3) {
        calendar.add(Calendar.DAY_OF_YEAR, -1)
        dates.add(0, calendar.clone() as Calendar)
    }

    calendar.add(Calendar.DAY_OF_YEAR, 3)

    repeat(3) {
        calendar.add(Calendar.DAY_OF_YEAR, 1)
        dates.add(calendar.clone() as Calendar)
    }

    return dates
}