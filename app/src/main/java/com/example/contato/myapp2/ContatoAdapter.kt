package com.example.contato.myapp2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ContatoAdapter(
    private val contatos: List<Contato>,
    private val onEditarClick: (Contato) -> Unit,
    private val onDeletarClick: (Contato) -> Unit
) : RecyclerView.Adapter<ContatoAdapter.ContatoViewHolder>() {

    inner class ContatoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nomeTextView: TextView = itemView.findViewById(R.id.nomeTextView)
        val telefoneTextView: TextView = itemView.findViewById(R.id.telefoneTextView)
        val emailTextView: TextView = itemView.findViewById(R.id.emailTextView)
        val btnEditar: ImageButton = itemView.findViewById(R.id.btnEditar)
        val btnDeletar: ImageButton = itemView.findViewById(R.id.btnDeletar)

        fun bind(contato: Contato) {
            // Configura os dados do contato
            nomeTextView.text = contato.nome
            telefoneTextView.text = contato.telefone
            emailTextView.text = contato.email

            // Configura os cliques dos botões
            btnEditar.setOnClickListener {
                onEditarClick(contato)
            }

            btnDeletar.setOnClickListener {
                onDeletarClick(contato)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContatoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_contato, parent, false)
        return ContatoViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContatoViewHolder, position: Int) {
        val contato = contatos[position]
        holder.bind(contato)
    }

    override fun getItemCount(): Int = contatos.size
}
