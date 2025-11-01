package com.mobile.app_iara.ui.history

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mobile.app_iara.R

class AbacusHistoryAdapter(
    private var historyList: List<AbacusHistory>,
    private val onItemClicked: (AbacusHistory) -> Unit
) : RecyclerView.Adapter<AbacusHistoryAdapter.HistoryViewHolder>() {


    inner class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val photo: ImageView = itemView.findViewById(R.id.imageView10)
        val title: TextView = itemView.findViewById(R.id.item_title)
        val takenBy: TextView = itemView.findViewById(R.id.taken_by)
        val approvedBy: TextView = itemView.findViewById(R.id.approved_by)
        val timestamp: TextView = itemView.findViewById(R.id.timestamp)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_abacus_history, parent, false)
        return HistoryViewHolder(view)
    }

    override fun getItemCount(): Int {
        return historyList.size
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val currentItem = historyList[position]
        val context = holder.itemView.context

        Glide.with(context)
            .load(currentItem.urlPhoto)
            .placeholder(R.drawable.il_without_image)
            .error(R.drawable.il_without_image)
            .into(holder.photo)

        holder.title.text = currentItem.titulo
        holder.timestamp.text = currentItem.timestamp
        holder.takenBy.text = context.getString(R.string.taken_by_format, currentItem.name)
        holder.approvedBy.text = context.getString(R.string.approved_by_format, currentItem.approve)

        holder.itemView.setOnClickListener {
            onItemClicked(currentItem)
        }
    }

    fun updateData(newList: List<AbacusHistory>) {
        this.historyList = newList
        notifyDataSetChanged()
    }
}