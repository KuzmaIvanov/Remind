package com.example.remind.screens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.remind.adapters.CalendarItemJsonAdapter
import com.example.remind.adapters.DetailsAdapter
import com.example.remind.databinding.FragmentCategoryItemDetailsBinding
import com.example.remind.model.CalendarItem
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type


class CategoryItemDetailsFragment : Fragment() {

    private var _binding: FragmentCategoryItemDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: DetailsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCategoryItemDetailsBinding.inflate(inflater, container, false)
        val nameCategoryItem = requireArguments().getString("name")
        val descriptionCategoryItem = requireArguments().getString("description")
        val listCalendarItemAsString = requireArguments().getString("calendarItem")
        val gsonBuilder = GsonBuilder()
        gsonBuilder.registerTypeAdapter(CalendarItem::class.java, CalendarItemJsonAdapter())
        val gson = gsonBuilder.create()
        val listType: Type = object : TypeToken<MutableList<CalendarItem>>() {}.type
        val listCalendarItem: MutableList<CalendarItem> = gson.fromJson(listCalendarItemAsString, listType)
        adapter = DetailsAdapter()
        val detailsRecyclerView: RecyclerView = binding.detailsRecyclerView
        detailsRecyclerView.adapter = adapter
        detailsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        val divider = MaterialDividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL)
        detailsRecyclerView.addItemDecoration(divider)
        adapter.addItems(listCalendarItem)
        binding.nameItemTextView.text = nameCategoryItem
        binding.descriptionItemTextView.text = descriptionCategoryItem
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}