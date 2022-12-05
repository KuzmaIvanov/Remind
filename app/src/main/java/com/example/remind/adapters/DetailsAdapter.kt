package com.example.remind.adapters

import android.content.Context
import android.icu.util.Calendar
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.remind.databinding.ItemAddCategoryRecyclerViewBinding
import com.example.remind.model.CalendarItem
import com.example.remind.model.CategoryItem
import com.example.remind.utils.CalendarUtils

class DetailsAdapter : RecyclerView.Adapter<DetailsAdapter.DetailsViewHolder>() {

    private var items: MutableList<CalendarItem> = mutableListOf()
    private lateinit var ourContext: Context

    fun addItems(items: MutableList<CalendarItem>) {
        this.items = items
        notifyDataSetChanged()
    }

    class DetailsViewHolder(
        val binding: ItemAddCategoryRecyclerViewBinding
    ): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemAddCategoryRecyclerViewBinding.inflate(inflater, parent, false)
        ourContext = parent.context
        return DetailsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DetailsViewHolder, position: Int) {
       val calendarItem = items[position]
        with(holder.binding) {
            addCategoryItemRecyclerViewTime.text = getReadyFullTime(
                if (DateFormat.is24HourFormat(ourContext)) calendarItem.calendar.get(Calendar.HOUR_OF_DAY) else calendarItem.calendar.get(Calendar.HOUR),
                calendarItem.calendar.get(Calendar.MINUTE)
            )
            addCategoryItemRecyclerViewDate.text = showDate(calendarItem)
        }
    }

    override fun getItemCount(): Int = items.size

    private fun getReadyFullTime(hour: Int, minutes: Int): String {
        val readyFullTime = StringBuilder()
        readyFullTime.append(hour)
        readyFullTime.append(":")
        if(minutes<10) {
            readyFullTime.append(0)
            readyFullTime.append(minutes)
        } else {
            readyFullTime.append(minutes)
        }
        return readyFullTime.toString()
    }

    private fun showDate(calendarItem: CalendarItem): String {
        return if(calendarItem.isEveryDay) {
            "Every day"
        } else if (calendarItem.isEveryWeek) {
            "Every ${CalendarUtils.convertDayIntToString(calendarItem.calendar.get(Calendar.DAY_OF_WEEK))}"
        } else {
            "${calendarItem.calendar.get(Calendar.DAY_OF_MONTH)}-" +
                    "${calendarItem.calendar.get(Calendar.MONTH) + 1}-${calendarItem.calendar.get(
                        Calendar.YEAR)}"
        }
    }
}