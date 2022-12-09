package com.example.remind.adapters

import android.view.*
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.example.remind.databinding.ItemCategoriesBinding
import com.example.remind.model.CategoryItem

interface CategoriesActionListener {
    fun onCategoryItemDetails(categoryItem: CategoryItem)
    fun onCategoryItemDelete(categoryItem: CategoryItem)
}

class CategoriesAdapter(
    private val actionListener: CategoriesActionListener
): RecyclerView.Adapter<CategoriesAdapter.CategoriesViewHolder>(), View.OnClickListener, View.OnLongClickListener {

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
        binding.root.setOnLongClickListener(this)
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

    override fun onLongClick(p0: View): Boolean {
        showPopupMenu(p0)
        return true
    }

    private fun showPopupMenu(view: View) {
        val categoryItem = view.tag as CategoryItem
        val popupMenu = PopupMenu(view.context, view)
        popupMenu.menu.add(0, ID_REMOVE, Menu.NONE, "remove")
        popupMenu.gravity = Gravity.END
        popupMenu.setOnMenuItemClickListener {
            when(it.itemId) {
                ID_REMOVE -> {
                    actionListener.onCategoryItemDelete(categoryItem)
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