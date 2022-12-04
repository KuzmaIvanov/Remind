package com.example.remind.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.remind.databinding.ItemCategoriesBinding
import com.example.remind.model.CategoryItem

class CategoriesAdapter: RecyclerView.Adapter<CategoriesAdapter.CategoriesViewHolder>() {

    private var items: ArrayList<CategoryItem> = ArrayList()

    fun addItems(items: ArrayList<CategoryItem>) {
        this.items = items
        notifyDataSetChanged()
    }

    class CategoriesViewHolder(
        val binding: ItemCategoriesBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriesViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemCategoriesBinding.inflate(inflater, parent, false)
        return CategoriesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoriesViewHolder, position: Int) {
        val categoryItem = items[position]
        with(holder.binding) {
            holder.itemView.tag = categoryItem
            nameItemTextView.text = categoryItem.name
            descriptionItemTextView.text = categoryItem.description
        }
    }

    override fun getItemCount(): Int = items.size

}