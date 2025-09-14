package com.mobile.app_iara.ui.profile.termsandprivacy

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mobile.app_iara.R

class PrivacidadeAdapter(private val privacidades: List<Termo>) :
    RecyclerView.Adapter<PrivacidadeAdapter.PrivacidadeViewHolder>() {

    class PrivacidadeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titulo: TextView = itemView.findViewById(R.id.tituloPrivacidade)
        val descricao: TextView = itemView.findViewById(R.id.textoPrivacidade)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PrivacidadeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_privacidade, parent, false)
        return PrivacidadeViewHolder(view)
    }

    override fun onBindViewHolder(holder: PrivacidadeViewHolder, position: Int) {
        val privacidade = privacidades[position]
        holder.titulo.text = privacidade.titulo
        holder.descricao.text = privacidade.descricao
    }

    override fun getItemCount() = privacidades.size
}
