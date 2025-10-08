package com.mobile.app_iara.ui.abacus.confirmation

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mobile.app_iara.R
import java.util.Locale

class AbacusConfirmationAdapter(private val informacoes: List<Line>):
    RecyclerView.Adapter<AbacusConfirmationAdapter.ConfirmacaoViewHolder>() {

    class ConfirmacaoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titulo: TextView = itemView.findViewById(R.id.infoTitulo)
        val bolinha: View = itemView.findViewById(R.id.circulo)
        val quantidade: TextView = itemView.findViewById(R.id.infoNumero)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConfirmacaoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_confirmation, parent, false)
        return ConfirmacaoViewHolder(view)
    }

    override fun onBindViewHolder(holder: ConfirmacaoViewHolder, position: Int) {
        val informacao = informacoes[position]
        holder.titulo.text = informacao.titulo
        holder.quantidade.text = String.format(Locale.getDefault(), "%d", informacao.quantidade)

        val background = holder.bolinha.background as GradientDrawable
        background.setColor(Color.parseColor(informacao.cor))
    }

    override fun getItemCount() = informacoes.size
}