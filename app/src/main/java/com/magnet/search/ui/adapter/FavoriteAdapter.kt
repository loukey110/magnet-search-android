package com.magnet.search.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.magnet.search.databinding.ItemFavoriteBinding
import com.magnet.search.data.model.Favorite

class FavoriteAdapter(
    private val onCopyClick: (Favorite) -> Unit,
    private val onDeleteClick: (Favorite) -> Unit
) : ListAdapter<Favorite, FavoriteAdapter.ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemFavoriteBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(
        private val binding: ItemFavoriteBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Favorite) {
            binding.apply {
                tvTitle.text = item.title
                tvSize.text = item.fileSize.ifEmpty { "" }
                tvSource.text = item.sourceName
                
                btnCopy.setOnClickListener { onCopyClick(item) }
                btnDelete.setOnClickListener { onDeleteClick(item) }
                
                root.setOnLongClickListener {
                    onCopyClick(item)
                    true
                }
            }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Favorite>() {
        override fun areItemsTheSame(oldItem: Favorite, newItem: Favorite): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Favorite, newItem: Favorite): Boolean {
            return oldItem == newItem
        }
    }
}
