package com.mobile.app_iara.ui.abacus.register

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mobile.app_iara.data.model.request.ColumnCreateRequest
import com.mobile.app_iara.databinding.ItemColumnBinding

class ColumnsAdapter(
    private val columns: MutableList<ColumnCreateRequest>,
    private val onDelete: (Int) -> Unit
) : RecyclerView.Adapter<ColumnsAdapter.ColumnViewHolder>() {

    inner class ColumnViewHolder(val binding: ItemColumnBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColumnViewHolder {
        val binding = ItemColumnBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ColumnViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ColumnViewHolder, position: Int) {
        val column = columns[position]
        holder.binding.tvColumnName.text = column.name
        holder.binding.tvSubColumnCount.text = "Cor: ${column.color}, Valor: ${column.value}"
        holder.binding.btnDeleteColumn.setOnClickListener {
            onDelete(holder.adapterPosition)
        }
    }
    override fun getItemCount() = columns.size
}