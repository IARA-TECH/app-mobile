package com.mobile.app_iara.ui.abacus

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mobile.app_iara.data.model.AbacusData
import com.mobile.app_iara.databinding.ItemAbacusCardBinding

class SelectAbacusAdapter(
    private var abacusDataList: List<AbacusData>,
    private val onAbacusSelected: (abacusData: AbacusData) -> Unit
) : RecyclerView.Adapter<SelectAbacusAdapter.SelectAbacusViewHolder>() {

    inner class SelectAbacusViewHolder(val binding: ItemAbacusCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(abacusData: AbacusData) {
            binding.tvTitle.text = abacusData.name
            binding.tvDescription.text = "Descrição: ${abacusData.description}"
            binding.tvLines.text = "Linhas: ${abacusData.lines.size}"
            binding.tvColumns.text = "Colunas: ${abacusData.columns.size}"
            binding.imageButtonDelete.visibility = View.GONE
            binding.imageButtonEdit.visibility = View.GONE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectAbacusViewHolder {
        val binding = ItemAbacusCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SelectAbacusViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SelectAbacusViewHolder, position: Int) {
        val abacusData = abacusDataList[position]
        holder.bind(abacusData)

        holder.itemView.setOnClickListener {
            onAbacusSelected(abacusData)
        }
    }

    override fun getItemCount(): Int = abacusDataList.size

    fun updateData(newList: List<AbacusData>) {
        this.abacusDataList = newList
        notifyDataSetChanged()
    }
}