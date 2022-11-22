package com.example.remind

import android.app.Application
import com.example.remind.model.CalendarItemService

class App: Application() {
    val chipGroupCalendarItemsService = CalendarItemService()
}