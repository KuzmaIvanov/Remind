package com.example.remind.screens

import android.icu.util.Calendar
import android.os.Bundle
import android.text.format.DateFormat.is24HourFormat
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.remind.adapters.AddCategoryItemAdapter
import com.example.remind.adapters.AddCategoryItemListener
import com.example.remind.databinding.FragmentAddCategoryItemBinding
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat

class AddCategoryItemFragment : Fragment() {

    private var _binding: FragmentAddCategoryItemBinding? = null
    private val binding get() = _binding!!
    private val listOfFullDates: MutableList<Calendar> = ArrayList()
    private lateinit var addCategoryItemRecView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddCategoryItemBinding.inflate(inflater, container, false)
        val addTimeButton: ExtendedFloatingActionButton = binding.flActBtnToAddTime
        addCategoryItemRecView = binding.addCategoryItemRecyclerView
        addCategoryItemRecView.layoutManager = LinearLayoutManager(requireContext())
        addCategoryItemRecView.adapter = AddCategoryItemAdapter(listOfFullDates, object: AddCategoryItemListener {
            override fun onCategoryItemDelete(calendar: Calendar) {
                val calendarForRemoveIndex:Int = listOfFullDates.indexOfFirst {
                    it == calendar
                }
                listOfFullDates.removeAt(calendarForRemoveIndex)
                addCategoryItemRecView.adapter?.notifyItemRemoved(calendarForRemoveIndex)
            }

        })

        addTimeButton.setOnClickListener {
            openTimePicker()
        }

        return binding.root
    }

    private fun openTimePicker() {
        val isSystem24hour = is24HourFormat(requireContext())
        val clockFormat = if(isSystem24hour) TimeFormat.CLOCK_24H else TimeFormat.CLOCK_12H
        val picker: MaterialTimePicker = MaterialTimePicker.Builder()
            .setTimeFormat(clockFormat)
            .setHour(0)
            .setMinute(0)
            .setTitleText("Select time")
            .build()
        picker.show(childFragmentManager, "time_picker")
        picker.addOnPositiveButtonClickListener {
            val calendar = Calendar.getInstance()
            if(isSystem24hour) {
                calendar.set(Calendar.HOUR_OF_DAY, picker.hour)
            } else {
                calendar.set(Calendar.HOUR, picker.hour)
            }
            calendar.set(Calendar.MINUTE, picker.minute)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0);
            listOfFullDates.add(calendar)
            addCategoryItemRecView.adapter?.notifyItemInserted(listOfFullDates.size - 1)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}