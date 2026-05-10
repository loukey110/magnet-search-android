package com.magnet.search.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.magnet.search.R
import com.magnet.search.data.model.MagnetItem
import com.magnet.search.databinding.ItemMagnetBinding

class MagnetAdapter(
    private val onCopyClick: (MagnetItem) -> Unit,
    private val onFavoriteClick: (MagnetItem) -> Unit
) : ListAdapter<MagnetItem, MagnetAdapter.ViewHolder>(DiffCallback) {

    private val favoriteStatus = mutableMapOf<String, Boolean>()

    fun updateFavoriteStatus(magnetLink: String, isFavorite: Boolean) {
        favoriteStatus[magnetLink] = isFavorite
        currentList.indexOfFirst { it.magnetLink == magnetLink }.let { position ->
            if (position >= 0) notifyItemChanged(position)
        }
    }

    fun setFavoriteStatusMap(map: Map<String, Boolean>) {
        favoriteStatus.clear()
        favoriteStatus.putAll(map)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMagnetBinding.inflate(
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
        private val binding: ItemMagnetBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: MagnetItem) {
            binding.apply {
                tvTitle.text = item.title
                tvSize.text = item.fileSize.ifEmpty { "未知大小" }
                tvSource.text = item.sourceName
                
                val info = mutableListOf<String>()
                if (item.uploadDate.isNotEmpty()) info.add(item.uploadDate)
                if (item.seeders > 0) info.add("种子: ${item.seeders}")
                if (item.leechers > 0) info.add("下载: ${item.leechers}")
                tvInfo.text = info.joinToString(" | ")
                
                val isFav = favoriteStatus[item.magnetLink] ?: false
                btnFavorite.setIconResource(
                    if (isFav) R.drawable.ic_favorite_filled else R.drawable.ic_favorite_outline
                )
                
                btnCopy.setOnClickListener { onCopyClick(item) }
                btnFavorite.setOnClickListener { onFavoriteClick(item) }
                
                root.setOnLongClickListener {
                    onCopyClick(item)
                    true
                }
            }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<MagnetItem>() {
        override fun areItemsTheSame(oldItem: MagnetItem, newItem: MagnetItem): Boolean {
            return oldItem.magnetLink == newItem.magnetLink
        }

        override fun areContentsTheSame(oldItem: MagnetItem, newItem: MagnetItem): Boolean {
            return oldItem == newItem
        }
    }
}
