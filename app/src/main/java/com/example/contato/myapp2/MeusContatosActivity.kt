package com.example.contato.myapp2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MeusContatosActivity : AppCompatActivity() {
    private lateinit var adapter: ContatoAdapter
    private lateinit var dbHelper: DatabaseHelper
    private val ALTERAR_CONTATO_REQUEST = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meus_contatos)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        dbHelper = DatabaseHelper(this)
        carregarContatos()

        val btnVoltar: Button = findViewById(R.id.btnVoltar)
        btnVoltar.setOnClickListener {
            finish()
        }
    }

    private fun carregarContatos() {
        val contatosDb = dbHelper.getAllContatos()
        val contatos = contatosDb.map {
            Contato(
                id = it["id"]?.toInt() ?: 0,
                nome = it["nome"] ?: "",
                telefone = it["telefone"] ?: "",
                email = it["email"] ?: ""
            )
        }

        adapter = ContatoAdapter(
            contatos = contatos,
            onEditarClick = { contato ->
                val intent = Intent(this, AlterarContatoActivity::class.java)
                intent.putExtra("contato_id", contato.id)
                startActivityForResult(intent, ALTERAR_CONTATO_REQUEST)
            },
            onDeletarClick = { contato ->
                confirmarExclusao(contato)
            }
        )
        findViewById<RecyclerView>(R.id.recyclerView).adapter = adapter
    }

    private fun confirmarExclusao(contato: Contato) {
        AlertDialog.Builder(this)
            .setTitle("Confirmar exclusão")
            .setMessage("Deseja realmente excluir o contato ${contato.nome}?")
            .setPositiveButton("Sim") { _, _ ->
                deletarContato(contato)
            }
            .setNegativeButton("Não", null)
            .show()
    }

    private fun deletarContato(contato: Contato) {
        dbHelper.deleteContato(contato.id)
        carregarContatos() // Recarrega a lista após deletar
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ALTERAR_CONTATO_REQUEST && resultCode == RESULT_OK) {
            carregarContatos() // Recarrega a lista após alteração
        }
    }

    override fun onResume() {
        super.onResume()
        carregarContatos() // Recarrega a lista ao voltar para a activity
    }
}
