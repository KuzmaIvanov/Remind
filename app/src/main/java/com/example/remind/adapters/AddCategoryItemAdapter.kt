package com.example.remind.adapters

import android.content.Context
import android.icu.util.Calendar
import android.text.format.DateFormat.is24HourFormat
import android.view.*
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.example.remind.databinding.ItemAddCategoryRecyclerViewBinding

interface AddCategoryItemListener {
    fun onCategoryItemDelete(calendar: Calendar)
}

class AddCategoryItemAdapter(
    private val fullDateList: List<Calendar>,
    private val addCategoryItemListener: AddCategoryItemListener
) : RecyclerView.Adapter<AddCategoryItemAdapter.AddCategoryItemViewHolder>(), View.OnLongClickListener {

    private lateinit var ourContext: Context

    class AddCategoryItemViewHolder(
        val binding: ItemAddCategoryRecyclerViewBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddCategoryItemViewHolder {
        ourContext = parent.context
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemAddCategoryRecyclerViewBinding.inflate(inflater, parent, false)
        binding.root.setOnLongClickListener(this)
        return AddCategoryItemAdapter.AddCategoryItemViewHolder(binding)
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

    override fun onBindViewHolder(holder: AddCategoryItemViewHolder, position: Int) {
        val fullDate = fullDateList[position]
        with(holder.binding) {
            holder.itemView.tag = fullDate
            addCategoryItemRecyclerViewTime.text = getReadyFullTime(
                if (is24HourFormat(ourContext)) fullDate.get(Calendar.HOUR_OF_DAY) else fullDate.get(Calendar.HOUR),
                fullDate.get(Calendar.MINUTE)
            )
        }
    }

    override fun getItemCount(): Int {
        return fullDateList.size
    }

    override fun onLongClick(p0: View): Boolean {
        showPopupMenu(p0)
        return true
    }

    private fun showPopupMenu(view: View) {
        val calendar = view.tag as Calendar
        val popupMenu = PopupMenu(view.context, view)
        popupMenu.menu.add(0, ID_REMOVE, Menu.NONE, "remove")
        popupMenu.gravity = Gravity.END
        popupMenu.setOnMenuItemClickListener {
            when(it.itemId) {
                ID_REMOVE -> {
                    addCategoryItemListener.onCategoryItemDelete(calendar)
                }
            }
            return@setOnMenuItemClickListener true
        }
        popupMenu.show()
    }

    companion object {
        private const val ID_REMOVE = 1
    }
}