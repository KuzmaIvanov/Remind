package com.example.remind.model

data class CategoryItem(
    val id: Int,
    val name: String,
    val description: String,
    val listCalendarItem: MutableList<CalendarItem>
)
