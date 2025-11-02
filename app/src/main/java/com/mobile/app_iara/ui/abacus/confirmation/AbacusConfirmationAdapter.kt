package com.mobile.app_iara.ui.abacus.confirmation

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mobile.app_iara.R

class AbacusConfirmationAdapter(
    private val informacoes: List<Line>
) : RecyclerView.Adapter<AbacusConfirmationAdapter.ConfirmacaoViewHolder>() {

    class ConfirmacaoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titulo: TextView = itemView.findViewById(R.id.infoTitulo)
        val categoria: TextView = itemView.findViewById(R.id.infoCategoria)

        val quantidade: EditText = itemView.findViewById(R.id.infoNumeroEdit)

        var textWatcher: TextWatcher? = null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConfirmacaoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_confirmation, parent, false)
        return ConfirmacaoViewHolder(view)
    }

    override fun onBindViewHolder(holder: ConfirmacaoViewHolder, position: Int) {
        val informacao = informacoes.getOrNull(position) ?: return

        holder.titulo.text = informacao.title
        holder.categoria.text = informacao.category
        holder.quantidade.removeTextChangedListener(holder.textWatcher)

        holder.quantidade.setText(informacao.value.toString())

        holder.textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val currentPosition = holder.adapterPosition
                if (currentPosition != RecyclerView.NO_POSITION) {
                    val newQuantity = s.toString().toIntOrNull() ?: 0
                    informacoes[currentPosition].value = newQuantity
                }
            }
        }

        holder.quantidade.addTextChangedListener(holder.textWatcher)
    }

    override fun getItemCount() = informacoes.size

}