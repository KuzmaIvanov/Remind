package com.example.remind.screens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.remind.R
import com.example.remind.databinding.FragmentCategoriesBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton

class CategoriesFragment : Fragment() {

    private var _binding: FragmentCategoriesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCategoriesBinding.inflate(inflater, container, false)
        val dbTableName = requireArguments().getString("db_table_name")

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}