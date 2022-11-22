package com.example.remind.model

import android.icu.util.Calendar

data class CalendarItem(
    val id: Int,
    var calendar: Calendar,
    var isEveryDay: Boolean,
    var isEveryWeek: Boolean,
    var isOnceOnAParticularDay: Boolean
)
