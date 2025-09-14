package com.mobile.app_iara.ui.profile.termsandprivacy

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mobile.app_iara.R

class TermosAdapter(private val termos: List<Termo>) :
    RecyclerView.Adapter<TermosAdapter.TermoViewHolder>() {

    class TermoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titulo: TextView = itemView.findViewById(R.id.tituloTermo)
        val descricao: TextView = itemView.findViewById(R.id.textoTermo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TermoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_termo, parent, false)
        return TermoViewHolder(view)
    }

    override fun onBindViewHolder(holder: TermoViewHolder, position: Int) {
        val termo = termos[position]
        holder.titulo.text = termo.titulo
        holder.descricao.text = termo.descricao
    }

    override fun getItemCount() = termos.size
}
