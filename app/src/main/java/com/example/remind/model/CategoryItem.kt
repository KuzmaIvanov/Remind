package com.example.remind.model

data class CategoryItem(
    val name: String,
    val description: String,
    val listCalendarItem: MutableList<CalendarItem>
)
