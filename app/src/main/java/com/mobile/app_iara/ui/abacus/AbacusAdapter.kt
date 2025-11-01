package com.mobile.app_iara.ui.abacus

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mobile.app_iara.databinding.ItemAbacusCardBinding


class AbacusAdapter(
    private var abacusList: List<Abacus>,
    private val onDeleteClick: (abacus: Abacus) -> Unit,
    private val onEditClick: (abacus: Abacus) -> Unit
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
        val abacus = abacusList[position]
        holder.bind(abacus)

        holder.binding.imageButtonDelete.setOnClickListener {
            onDeleteClick(abacus)
        }

        holder.binding.imageButtonEdit.setOnClickListener {
            onEditClick(abacus)
        }
    }

    override fun getItemCount(): Int = abacusList.size

    fun updateData(newList: List<Abacus>) {
        this.abacusList = newList
        notifyDataSetChanged()
    }

    inner class AbacusViewHolder(val binding: ItemAbacusCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(abacus: Abacus) {
            binding.tvTitle.text = abacus.title
            binding.tvDescription.text = "Descrição: ${abacus.description}"
            binding.tvLines.text = "Linhas: ${abacus.lines}"
            binding.tvColumns.text = "Colunas: ${abacus.columns}"
        }
    }
}