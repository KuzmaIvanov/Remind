package com.example.remind.adapters

import android.content.Context
import android.icu.util.Calendar
import android.text.format.DateFormat.is24HourFormat
import android.view.*
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.remind.databinding.ItemAddCategoryRecyclerViewBinding
import com.example.remind.model.CalendarItem
import com.example.remind.utils.CalendarUtils

interface AddCategoryItemListener {
    fun onCategoryItemDelete(calendarItem: CalendarItem)
    fun onCategoryItemChange(calendarItem: CalendarItem)
}

class AddCategoryItemDiffCallBack(
    private val oldList: List<CalendarItem>,
    private val newList: List<CalendarItem>
): DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldChipGroupCalendar = oldList[oldItemPosition]
        val newChipGroupCalendar = newList[newItemPosition]
        return oldChipGroupCalendar.id == newChipGroupCalendar.id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldChipGroupCalendar = oldList[oldItemPosition]
        val newChipGroupCalendar = newList[newItemPosition]
        return oldChipGroupCalendar == newChipGroupCalendar
    }
}

class AddCategoryItemAdapter(
    private val addCategoryItemListener: AddCategoryItemListener
) : RecyclerView.Adapter<AddCategoryItemAdapter.AddCategoryItemViewHolder>(), View.OnLongClickListener {

    private lateinit var ourContext: Context

    var listCalendarItem: List<CalendarItem> = emptyList()
        set(newValue) {
            val diffCallback = AddCategoryItemDiffCallBack(field, newValue)
            val diffResult = DiffUtil.calculateDiff(diffCallback)
            field = newValue
            diffResult.dispatchUpdatesTo(this)
        }

    class AddCategoryItemViewHolder(
        val binding: ItemAddCategoryRecyclerViewBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddCategoryItemViewHolder {
        ourContext = parent.context
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemAddCategoryRecyclerViewBinding.inflate(inflater, parent, false)
        binding.root.setOnLongClickListener(this)
        return AddCategoryItemViewHolder(binding)
    }

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
                    "${calendarItem.calendar.get(Calendar.MONTH) + 1}-${calendarItem.calendar.get(Calendar.YEAR)}"
        }
    }

    override fun onBindViewHolder(holder: AddCategoryItemViewHolder, position: Int) {
        val chipGroupCalendarItem = listCalendarItem[position]
        with(holder.binding) {
            holder.itemView.tag = chipGroupCalendarItem
            addCategoryItemRecyclerViewTime.text = getReadyFullTime(
                if (is24HourFormat(ourContext)) chipGroupCalendarItem.calendar.get(Calendar.HOUR_OF_DAY) else chipGroupCalendarItem.calendar.get(Calendar.HOUR),
                chipGroupCalendarItem.calendar.get(Calendar.MINUTE)
            )
            addCategoryItemRecyclerViewDate.text = showDate(chipGroupCalendarItem)
        }
    }

    override fun getItemCount(): Int {
        return listCalendarItem.size
    }

    override fun onLongClick(p0: View): Boolean {
        showPopupMenu(p0)
        return true
    }

    private fun showPopupMenu(view: View) {
        val calendarItem = view.tag as CalendarItem
        val popupMenu = PopupMenu(view.context, view)
        popupMenu.menu.add(0, ID_REMOVE, Menu.NONE, "remove")
        popupMenu.menu.add(1, ID_CHANGE, Menu.NONE, "change")
        popupMenu.gravity = Gravity.END
        popupMenu.setOnMenuItemClickListener {
            when(it.itemId) {
                ID_REMOVE -> {
                    addCategoryItemListener.onCategoryItemDelete(calendarItem)
                }
                ID_CHANGE -> {
                    addCategoryItemListener.onCategoryItemChange(calendarItem)
                }
            }
            return@setOnMenuItemClickListener true
        }
        popupMenu.show()
    }

    companion object {
        private const val ID_REMOVE = 1
        private const val ID_CHANGE = 2
    }

}