package com.example.remind.screens

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.text.format.DateFormat.is24HourFormat
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.content.getSystemService
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.remind.AlarmReceiver
import com.example.remind.App
import com.example.remind.R
import com.example.remind.adapters.AddCategoryItemAdapter
import com.example.remind.adapters.AddCategoryItemListener
import com.example.remind.database.MyDbHelper
import com.example.remind.databinding.FragmentAddCategoryItemBinding
import com.example.remind.model.CalendarItem
import com.example.remind.model.CalendarItemService
import com.example.remind.model.CalendarItemsListener
import com.example.remind.model.CategoryItem
import com.example.remind.utils.CalendarUtils
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.util.*

class AddCategoryItemFragment : Fragment() {

    private var _binding: FragmentAddCategoryItemBinding? = null
    private val binding get() = _binding!!
    private lateinit var addCategoryItemRecView: RecyclerView
    private lateinit var adapter: AddCategoryItemAdapter
    private lateinit var dbTableName: String

    private val calendarItemService: CalendarItemService
        get() = (activity?.applicationContext as App).chipGroupCalendarItemsService

    private var increment = 1
        get() = field++

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.save_delete_category_item_menu, menu)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddCategoryItemBinding.inflate(inflater, container, false)
        dbTableName = requireArguments().getString("db_table_name")!!

        val addTimeButton: ExtendedFloatingActionButton = binding.flActBtnToAddTime

        addCategoryItemRecView = binding.addCategoryItemRecyclerView
        adapter = AddCategoryItemAdapter(object : AddCategoryItemListener {
            override fun onCategoryItemDelete(calendarItem: CalendarItem) {
                calendarItemService.deleteItem(calendarItem)
            }

            override fun onCategoryItemChange(calendarItem: CalendarItem) {
                openMaterialAlertDialog(calendarItem)
            }
        })
        addCategoryItemRecView.layoutManager = LinearLayoutManager(requireContext())
        addCategoryItemRecView.adapter = adapter

        val divider = MaterialDividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL)
        addCategoryItemRecView.addItemDecoration(divider)

        calendarItemService.addListener(calendarItemsListener)

        addTimeButton.setOnClickListener {
            openTimePicker()
        }

        return binding.root
    }

    private fun openMaterialAlertDialog(calendarItem: CalendarItem) {
        val everyDayString = resources.getString(R.string.radio_every_day)
        val everyWeekString = resources.getString(R.string.radio_every_week)
        val onceParticularDayString = resources.getString(R.string.radio_particular_day)
        val arrayOfOptions = arrayOf(
            everyDayString,
            everyWeekString,
            onceParticularDayString
        )
        var choice: String = arrayOfOptions[0]
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.material_alert_dialog_title)
            .setSingleChoiceItems(arrayOfOptions, 0) { _, i ->
                choice = arrayOfOptions[i]
            }
            .setPositiveButton(resources.getString(R.string.accept)) { dialog, _ ->
                val hour = calendarItem.calendar.get(Calendar.HOUR_OF_DAY)
                val minute = calendarItem.calendar.get(Calendar.MINUTE)
                when(choice) {
                    everyDayString -> {
                        calendarItem.calendar.time = Calendar.getInstance().time
                        calendarItem.calendar.set(Calendar.HOUR_OF_DAY, hour)
                        calendarItem.calendar.set(Calendar.MINUTE, minute)
                        calendarItem.calendar.set(Calendar.SECOND, 0)
                        calendarItem.calendar.set(Calendar.MILLISECOND, 0)
                        calendarItemService.changeItem(calendarItem, choice)
                        adapter.notifyDataSetChanged()
                    }
                    everyWeekString -> {
                        calendarItem.calendar.time = Calendar.getInstance().time
                        calendarItem.calendar.set(Calendar.HOUR_OF_DAY, hour)
                        calendarItem.calendar.set(Calendar.MINUTE, minute)
                        calendarItem.calendar.set(Calendar.SECOND, 0)
                        calendarItem.calendar.set(Calendar.MILLISECOND, 0)
                        openDayPicker(calendarItem, choice)
                    }
                    onceParticularDayString -> {
                        openDatePicker(calendarItem, onceParticularDayString)
                    }
                }
                dialog.dismiss()
            }
            .setNegativeButton(resources.getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun openDayPicker(calendarItem: CalendarItem, ch: String) {
        val arrayOfDays = arrayOf(
            "monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday"
        )
        var choice: String = arrayOfDays[0]
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Choose your day of week:")
            .setSingleChoiceItems(arrayOfDays, 0) { _, i ->
                choice = arrayOfDays[i]
            }
            .setPositiveButton(resources.getString(R.string.accept)) { dialog, _ ->
                calendarItem.calendar.set(Calendar.DAY_OF_WEEK, CalendarUtils.convertDayStringToInt(choice))
                calendarItemService.changeItem(calendarItem, ch)
                adapter.notifyDataSetChanged()
                dialog.dismiss()
            }
            .setNegativeButton(resources.getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()

    }

    private fun openDatePicker(calendarItem: CalendarItem, choice: String) {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select your date:")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()
        datePicker.show(childFragmentManager, "date_picker")
        datePicker.addOnPositiveButtonClickListener {
            val calendar = Calendar.getInstance()
            calendar.time = Date(it)
            calendarItem.calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR))
            calendarItem.calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH))
            calendarItem.calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH))
            calendarItemService.changeItem(calendarItem, choice)
            adapter.notifyDataSetChanged()
        }
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
            calendar.set(Calendar.MILLISECOND, 0)
            calendarItemService.addItem(
                CalendarItem(
                increment,
                calendar,
                isEveryDay = true,
                isEveryWeek = false,
                isOnceOnAParticularDay = false)
            )
        }
    }

    private val calendarItemsListener: CalendarItemsListener = {
        adapter.listCalendarItem = it
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_save -> {
                if(calendarItemService.count() != 0 && binding.inputNameTextInputEditText.text.toString() != "") {
                    val name: String = binding.inputNameTextInputEditText.text.toString()
                    val description: String = binding.inputDescriptionTextInputEditText.text.toString()
                    val categoryItem = CategoryItem(0, name, description, calendarItemService.getItems())
                    val dbHelper = MyDbHelper(requireContext())
                    val idCategoryItemToAddAlarm: Long = dbHelper.addCategoryItem(categoryItem, dbTableName)
                    backToCategoriesFragment()
                    setAlarm(idCategoryItemToAddAlarm, name)
                    Toast.makeText(requireContext(), "Added!", Toast.LENGTH_SHORT).show()
                    calendarItemService.clearItems()
                } else {
                    Toast.makeText(requireContext(), "Please enter name and add time if necessary", Toast.LENGTH_SHORT).show()
                }
                true
            }
            R.id.action_clear -> {
                calendarItemService.clearItems()
                adapter.notifyDataSetChanged()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setAlarm(idCategoryItemToAddAlarm: Long, nameOfNotification: String) {
        val context = requireContext()
        val alarmManager: AlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        var actionIncrement = 1;
        calendarItemService.getItems().forEach {
            val intent = Intent(context, AlarmReceiver::class.java)
            intent.putExtra("title", nameOfNotification)
            intent.putExtra("description", resources.getString(R.string.notification_content_text))
            intent.putExtra("notificationID", idCategoryItemToAddAlarm.toInt())
            intent.action = "action$dbTableName$actionIncrement"
            val pendingIntent = PendingIntent.getBroadcast(context, idCategoryItemToAddAlarm.toInt(), intent, PendingIntent.FLAG_UPDATE_CURRENT)
            if(it.isEveryDay) {
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, it.calendar.timeInMillis, AlarmManager.INTERVAL_DAY, pendingIntent)
            } else if (it.isEveryWeek) {
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, it.calendar.timeInMillis, AlarmManager.INTERVAL_DAY*7, pendingIntent)
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, it.calendar.timeInMillis, pendingIntent)
            }
            actionIncrement++
        }
    }

    private fun backToCategoriesFragment() {
        val fragment = CategoriesFragment()
        val bundle = Bundle()
        bundle.putString("db_table_name", dbTableName)
        fragment.arguments = bundle
        requireActivity().supportFragmentManager
            .beginTransaction()
            .replace(R.id.rootForFragment, fragment)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        calendarItemService.removeListener(calendarItemsListener)
        _binding = null
    }

}