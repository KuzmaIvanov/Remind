package com.example.remind.screens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.remind.R
import com.example.remind.adapters.CategoriesAdapter
import com.example.remind.database.MyDbHelper
import com.example.remind.databinding.FragmentCategoriesBinding
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.google.android.material.floatingactionbutton.FloatingActionButton

class CategoriesFragment : Fragment() {

    private var _binding: FragmentCategoriesBinding? = null
    private val binding get() = _binding!!
    private lateinit var categoriesRecyclerView: RecyclerView
    private lateinit var adapter: CategoriesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCategoriesBinding.inflate(inflater, container, false)
        val dbTableName = requireArguments().getString("db_table_name")!!

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
        adapter = CategoriesAdapter()
        categoriesRecyclerView.adapter = adapter
        categoriesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        val divider = MaterialDividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL)
        categoriesRecyclerView.addItemDecoration(divider)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}