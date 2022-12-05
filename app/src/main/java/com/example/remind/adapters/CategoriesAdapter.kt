package com.example.remind.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.remind.databinding.ItemCategoriesBinding
import com.example.remind.model.CategoryItem

interface CategoriesActionListener {
    fun onCategoryItemDetails(categoryItem: CategoryItem)
}

class CategoriesAdapter(
    private val actionListener: CategoriesActionListener
): RecyclerView.Adapter<CategoriesAdapter.CategoriesViewHolder>(), View.OnClickListener {

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
        binding.root.setOnClickListener(this)
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

    override fun onClick(p0: View) {
        val categoryItem = p0.tag as CategoryItem
        actionListener.onCategoryItemDetails(categoryItem)
    }

}