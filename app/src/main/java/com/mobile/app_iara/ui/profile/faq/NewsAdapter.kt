package com.mobile.app_iara.ui.profile.faq

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mobile.app_iara.R
import com.mobile.app_iara.data.model.NewsData

class NewsAdapter(
    private var noticias: List<NewsData>
) : RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titulo: TextView = itemView.findViewById(R.id.tituloPopular)
        val descricao: TextView = itemView.findViewById(R.id.descricaoPopular)
        val imagem: ImageView = itemView.findViewById(R.id.imageView15)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_popular, parent, false)
        return NewsViewHolder(view)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val noticia = noticias[position]

        holder.titulo.text = noticia.titulo
        holder.descricao.text = noticia.fonte

        Glide.with(holder.itemView.context)
            .load(noticia.imagem)
            .placeholder(R.drawable.il_without_image_popular)
            .error(R.drawable.il_without_image_popular)
            .centerCrop()
            .into(holder.imagem)
    }

    override fun getItemCount() = noticias.size

    fun updateData(newNoticias: List<NewsData>) {
        this.noticias = newNoticias
        notifyDataSetChanged()
    }
}