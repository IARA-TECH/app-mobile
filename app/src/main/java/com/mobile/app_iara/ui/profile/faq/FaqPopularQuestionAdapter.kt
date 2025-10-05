package com.mobile.app_iara.ui.profile.faq

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mobile.app_iara.R

class FaqPopularQuestionAdapter(private val populares: List<FaqPopularQuestion>) :
    RecyclerView.Adapter<FaqPopularQuestionAdapter.PopularViewHolder>(){

    class PopularViewHolder(popularView: View) : RecyclerView.ViewHolder(popularView) {
        val titulo: TextView = popularView.findViewById(R.id.tituloPopular)
        val descricao: TextView =  popularView.findViewById(R.id.descricaoPopular)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PopularViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_popular, parent, false)
        return PopularViewHolder(view)
    }

    override fun onBindViewHolder(holder: PopularViewHolder, position: Int) {
        val popular = populares[position]
        holder.titulo.text = popular.titulo
        holder.descricao.text = popular.descricao
    }

    override fun getItemCount() = populares.size

}