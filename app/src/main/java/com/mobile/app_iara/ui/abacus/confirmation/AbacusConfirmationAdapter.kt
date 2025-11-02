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
    // Recebe uma LISTA MUTÁVEL para que possamos alterá-la
    private val informacoes: List<Line>
) : RecyclerView.Adapter<AbacusConfirmationAdapter.ConfirmacaoViewHolder>() {

    class ConfirmacaoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titulo: TextView = itemView.findViewById(R.id.infoTitulo)
        val categoria: TextView = itemView.findViewById(R.id.infoCategoria)

        // Agora é um EditText
        val quantidade: EditText = itemView.findViewById(R.id.infoNumeroEdit)

        // Guardião para evitar loops infinitos de TextWatcher
        var textWatcher: TextWatcher? = null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConfirmacaoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_confirmation, parent, false)
        return ConfirmacaoViewHolder(view)
    }

    override fun onBindViewHolder(holder: ConfirmacaoViewHolder, position: Int) {
        // Usamos 'getOrNull' para segurança
        val informacao = informacoes.getOrNull(position) ?: return

        // Preenche os TextViews normais
        holder.titulo.text = informacao.title
        holder.categoria.text = informacao.category

        // --- LÓGICA DE EDIÇÃO ---

        // 1. Remove o listener antigo antes de setar o texto
        // Isso impede que o 'afterTextChanged' dispare só porque o item foi reciclado
        holder.quantidade.removeTextChangedListener(holder.textWatcher)

        // 2. Seta o texto atual
        holder.quantidade.setText(informacao.value.toString())

        // 3. Cria um novo listener
        holder.textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                // Pega a posição ATUAL do holder (pode ter mudado)
                val currentPosition = holder.adapterPosition
                if (currentPosition != RecyclerView.NO_POSITION) {
                    val newQuantity = s.toString().toIntOrNull() ?: 0

                    // ATUALIZA A LISTA!
                    // Esta é a "fonte da verdade"
                    informacoes[currentPosition].value = newQuantity
                }
            }
        }

        // 4. Adiciona o novo listener
        holder.quantidade.addTextChangedListener(holder.textWatcher)
    }

    override fun getItemCount() = informacoes.size

}