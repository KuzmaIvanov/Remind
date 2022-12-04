package com.example.remind.model

typealias CalendarItemsListener = (items: MutableList<CalendarItem>) -> Unit

class CalendarItemService {

    private var items = mutableListOf<CalendarItem>()

    private val listeners = mutableSetOf<CalendarItemsListener>()

    fun getItems(): MutableList<CalendarItem> {
        return items
    }

    fun clearItems() {
        items.clear()
    }

    fun count(): Int = items.size

    fun deleteItem(item: CalendarItem) {
        val indexToDelete = items.indexOfFirst { it.id == item.id }
        if(indexToDelete != -1) {
            items = ArrayList(items)
            items.removeAt(indexToDelete)
            notifyChanges()
        }
    }

    fun addItem(item: CalendarItem) {
        items = ArrayList(items)
        items.add(item)
        notifyChanges()
    }

    fun changeItem(calendarItem: CalendarItem, choice: String) {
        val indexToChange = items.indexOfFirst { it.id == calendarItem.id }
        if(indexToChange != -1) {
            items = ArrayList(items)
            when(choice) {
                "every day" -> {
                    items[indexToChange].isEveryDay = true
                    items[indexToChange].isEveryWeek = false
                    items[indexToChange].isOnceOnAParticularDay = false
                }
                "every week" -> {
                    items[indexToChange].isEveryDay = false
                    items[indexToChange].isEveryWeek = true
                    items[indexToChange].isOnceOnAParticularDay = false
                }
                "once on a particular day" -> {
                    items[indexToChange].isEveryDay = false
                    items[indexToChange].isEveryWeek = false
                    items[indexToChange].isOnceOnAParticularDay = true
                }
            }
        }
    }

    fun addListener(listener: CalendarItemsListener) {
        listeners.add(listener)
        listener.invoke(items)
    }

    fun removeListener(listener: CalendarItemsListener) {
        listeners.remove(listener)
    }

    private fun notifyChanges() {
        listeners.forEach { it.invoke(items) }
    }
}