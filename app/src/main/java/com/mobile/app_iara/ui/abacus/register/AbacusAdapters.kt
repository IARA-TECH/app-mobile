package com.mobile.app_iara.ui.abacus.register

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mobile.app_iara.databinding.ItemColumnBinding
import com.mobile.app_iara.databinding.ItemLineBinding
import com.mobile.app_iara.databinding.ItemSubColumnBinding

class LinesAdapter(
    private val lines: MutableList<String>,
    private val onDelete: (Int) -> Unit
) : RecyclerView.Adapter<LinesAdapter.LineViewHolder>() {

    inner class LineViewHolder(val binding: ItemLineBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LineViewHolder {
        val binding = ItemLineBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LineViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LineViewHolder, position: Int) {
        val line = lines[position]
        holder.binding.tvLineName.text = line
        holder.binding.btnDeleteLine.setOnClickListener {
            onDelete(holder.adapterPosition)
        }
    }

    override fun getItemCount() = lines.size
}


class ColumnsAdapter(
    private val columns: MutableList<AbacusColumn>,
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
        holder.binding.tvSubColumnCount.text = "${column.subColumns.size} sub-colunas"
        holder.binding.btnDeleteColumn.setOnClickListener {
            onDelete(holder.adapterPosition)
        }
    }

    override fun getItemCount() = columns.size
}


class SubColumnsAdapter(
    private val subColumns: MutableList<SubColumn>,
    private val onDelete: (Int) -> Unit
) : RecyclerView.Adapter<SubColumnsAdapter.SubColumnViewHolder>() {

    inner class SubColumnViewHolder(val binding: ItemSubColumnBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubColumnViewHolder {
        val binding = ItemSubColumnBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SubColumnViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SubColumnViewHolder, position: Int) {
        val subColumn = subColumns[position]
        holder.binding.tvSubColumnInfo.text = "Cor: ${subColumn.color}, Valor: ${subColumn.value}"
        holder.binding.btnDeleteSubColumn.setOnClickListener {
            onDelete(holder.adapterPosition)
        }
    }

    override fun getItemCount() = subColumns.size
}