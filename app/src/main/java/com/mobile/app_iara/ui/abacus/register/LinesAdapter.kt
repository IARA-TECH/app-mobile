package com.mobile.app_iara.ui.abacus.register

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mobile.app_iara.databinding.ItemLineBinding
import com.mobile.app_iara.data.model.request.LineCreateRequest

class LinesAdapter(
    private val lines: MutableList<LineCreateRequest>,
    private val onDelete: (Int) -> Unit
) : RecyclerView.Adapter<LinesAdapter.LineViewHolder>() {

    inner class LineViewHolder(val binding: ItemLineBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LineViewHolder {
        val binding = ItemLineBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LineViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LineViewHolder, position: Int) {
        val line = lines[position]
        holder.binding.tvLineName.text = line.name
        holder.binding.tvLineType.text = line.type.name

        holder.binding.btnDeleteLine.setOnClickListener {
            onDelete(holder.adapterPosition)
        }
    }

    override fun getItemCount() = lines.size
}