package com.mobile.app_iara.ui.abacus

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mobile.app_iara.databinding.ItemAbacusCardBinding


class AbacusAdapter(
    private var abacusList: List<Abacus>
) : RecyclerView.Adapter<AbacusAdapter.AbacusViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AbacusViewHolder {
        val binding = ItemAbacusCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AbacusViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AbacusViewHolder, position: Int) {
        holder.bind(abacusList[position])
    }

    override fun getItemCount(): Int = abacusList.size

    fun updateData(newList: List<Abacus>) {
        this.abacusList = newList
        notifyDataSetChanged()
    }

    inner class AbacusViewHolder(private val binding: ItemAbacusCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(abacus: Abacus) {
            binding.tvTitle.text = abacus.title
            binding.tvDescription.text = "Descrição: ${abacus.description}"
            binding.tvLines.text = "Linhas: ${abacus.lines}"
            binding.tvColumns.text = "Colunas: ${abacus.columns}"
        }
    }
}