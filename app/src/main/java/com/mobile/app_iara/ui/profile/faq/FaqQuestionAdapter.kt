package com.mobile.app_iara.ui.profile.faq

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mobile.app_iara.R

class FaqQuestionAdapter(private val duvidas: List<FaqQuestion>) :
    RecyclerView.Adapter<FaqQuestionAdapter.DuvidaViewHolder>(){

    class DuvidaViewHolder(duvidaView: View) : RecyclerView.ViewHolder(duvidaView) {
        val titulo: TextView = duvidaView.findViewById(R.id.tituloDuvida)
        val descricao: TextView =  duvidaView.findViewById(R.id.descricaoDuvida)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DuvidaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_questions, parent, false)
        return DuvidaViewHolder(view)
    }

    override fun onBindViewHolder(holder: DuvidaViewHolder, position: Int) {
        val duvida = duvidas[position]
        holder.titulo.text = duvida.titulo
        holder.descricao.text = duvida.descricao
    }

    override fun getItemCount() = duvidas.size

}