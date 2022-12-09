package com.example.remind.screens

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.remind.AlarmReceiver
import com.example.remind.R
import com.example.remind.adapters.CalendarItemJsonAdapter
import com.example.remind.adapters.CategoriesActionListener
import com.example.remind.adapters.CategoriesAdapter
import com.example.remind.database.MyDbHelper
import com.example.remind.databinding.FragmentCategoriesBinding
import com.example.remind.model.CalendarItem
import com.example.remind.model.CategoryItem
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.GsonBuilder

class CategoriesFragment : Fragment() {

    private var _binding: FragmentCategoriesBinding? = null
    private val binding get() = _binding!!
    private lateinit var categoriesRecyclerView: RecyclerView
    private lateinit var adapter: CategoriesAdapter
    private lateinit var dbTableName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.more_item_menu,menu)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCategoriesBinding.inflate(inflater, container, false)
        dbTableName = requireArguments().getString("db_table_name")!!
        initRecyclerView()

        val dbHelper = MyDbHelper(requireContext())
        adapter.addItems(dbHelper.getAllCategoryItem(dbTableName))

        val floatingActionButton: FloatingActionButton = binding.flActBtnToAddNotification
        floatingActionButton.setOnClickListener {
            val fragment = AddCategoryItemFragment()
            val bundle = Bundle()
            bundle.putString("db_table_name", dbTableName)
            fragment.arguments = bundle
            requireActivity().supportFragmentManager
                .beginTransaction()
                .replace(R.id.rootForFragment, fragment)
                .commit()
        }

        return binding.root
    }

    private fun initRecyclerView() {
        categoriesRecyclerView = binding.categoriesRecyclerView
        adapter = CategoriesAdapter(object : CategoriesActionListener{
            override fun onCategoryItemDetails(categoryItem: CategoryItem) {
                val fragment = CategoryItemDetailsFragment()
                val bundle = Bundle()
                val gsonBuilder = GsonBuilder()
                gsonBuilder.registerTypeAdapter(CalendarItem::class.java, CalendarItemJsonAdapter())
                val gson = gsonBuilder.create()
                bundle.putString("name", categoryItem.name)
                bundle.putString("description", categoryItem.description)
                bundle.putString("calendarItem", gson.toJson(categoryItem.listCalendarItem))
                fragment.arguments = bundle
                requireActivity().supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.rootForFragment, fragment)
                    .commit()
            }

            override fun onCategoryItemDelete(categoryItem: CategoryItem) {
                val dbHelper = MyDbHelper(requireContext())
                dbHelper.deleteCategoryItem(categoryItem, dbTableName)
                cancelAlarm(categoryItem)
                adapter.addItems(dbHelper.getAllCategoryItem(dbTableName))
                Toast.makeText(requireContext(), "Deleted!", Toast.LENGTH_SHORT).show()
            }
        })
        categoriesRecyclerView.adapter = adapter
        categoriesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        val divider = MaterialDividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL)
        categoriesRecyclerView.addItemDecoration(divider)
    }

    private fun cancelAlarm(categoryItem: CategoryItem) {
        val context = requireContext()
        var actionIncrement = 1
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        while (actionIncrement<=categoryItem.listCalendarItem.size) {
            val intent = Intent(context, AlarmReceiver::class.java)
            intent.action = "action$dbTableName$actionIncrement"
            val pendingIntent = PendingIntent.getBroadcast(context, categoryItem.id, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            if(pendingIntent!=null) {
                alarmManager.cancel(pendingIntent)
            }
            actionIncrement++
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_clear_all -> {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Delete all notifications")
                    .setMessage("Are you sure you want to delete all notifications?")
                    .setNeutralButton("Cancel") { dialog, which ->
                        dialog.dismiss()
                    }
                    .setPositiveButton("Yes") { dialog, which ->
                        val dbHelper = MyDbHelper(requireContext())
                        dbHelper.getAllCategoryItem(dbTableName).forEach {
                            cancelAlarm(it)
                        }
                        dbHelper.deleteAllCategoryItems(dbTableName)
                        adapter.addItems(dbHelper.getAllCategoryItem(dbTableName))
                        dialog.dismiss()
                        Toast.makeText(requireContext(), "Deleted successfully!", Toast.LENGTH_SHORT).show()
                    }
                    .show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}