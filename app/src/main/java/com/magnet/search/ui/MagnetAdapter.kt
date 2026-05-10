package com.magnet.search.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.magnet.search.R
import com.magnet.search.data.MagnetItem

class MagnetAdapter(
    private val onCopy: (MagnetItem) -> Unit
) : RecyclerView.Adapter<MagnetAdapter.ViewHolder>() {

    private val items = mutableListOf<MagnetItem>()

    fun setData(newItems: List<MagnetItem>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_result, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        private val tvInfo: TextView = view.findViewById(R.id.tvInfo)
        private val btnCopy: View = view.findViewById(R.id.btnCopy)

        fun bind(item: MagnetItem) {
            tvTitle.text = item.title
            val info = mutableListOf<String>()
            if (item.size.isNotEmpty()) info.add(item.size)
            if (item.date.isNotEmpty()) info.add(item.date)
            if (item.source.isNotEmpty()) info.add(item.source)
            tvInfo.text = info.joinToString(" | ")
            
            btnCopy.setOnClickListener { onCopy(item) }
            
            itemView.setOnLongClickListener {
                onCopy(item)
                true
            }
        }
    }
}
