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
            binding.tvTitle.text = item.title
            binding.tvSize.text = if (item.fileSize.isNotEmpty()) item.fileSize else "未知大小"
            binding.tvSource.text = item.sourceName
            
            val infoList = mutableListOf<String>()
            if (item.uploadDate.isNotEmpty()) infoList.add(item.uploadDate)
            if (item.seeders > 0) infoList.add("种子: ${item.seeders}")
            if (item.leechers > 0) infoList.add("下载: ${item.leechers}")
            binding.tvInfo.text = infoList.joinToString(" | ")
            
            val isFav = favoriteStatus[item.magnetLink] ?: false
            binding.btnFavorite.setIconResource(
                if (isFav) R.drawable.ic_favorite_filled else R.drawable.ic_favorite_outline
            )
            
            binding.btnCopy.setOnClickListener { onCopyClick(item) }
            binding.btnFavorite.setOnClickListener { onFavoriteClick(item) }
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
