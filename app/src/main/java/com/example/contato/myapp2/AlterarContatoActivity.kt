package com.example.contato.myapp2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AlterarContatoActivity : AppCompatActivity() {

    private lateinit var editTextNome: EditText
    private lateinit var editTextTelefone: EditText
    private lateinit var editTextEmail: EditText
    private lateinit var btnSalvar: Button
    private lateinit var btnCancelar: Button
    private lateinit var dbHelper: DatabaseHelper
    private var contatoId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alterar_meu_contato)

        // Inicializar views
        editTextNome = findViewById(R.id.edtNome)
        editTextTelefone = findViewById(R.id.edtTelefone)
        editTextEmail = findViewById(R.id.edtEmail)
        btnSalvar = findViewById(R.id.btnSalvar)
        btnCancelar = findViewById(R.id.btnCancelar)

        dbHelper = DatabaseHelper(this)

        // Receber dados do contato selecionado
        contatoId = intent.getIntExtra("contato_id", -1)

        // Verificar se o ID é válido
        if (contatoId == -1) {
            Toast.makeText(this, "Erro: ID do contato inválido", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        // Carregar dados do contato do banco de dados
        carregarDadosContato()

        // Configurar botão Salvar
        btnSalvar.setOnClickListener {
            salvarAlteracoes()
        }

        // Configurar botão Cancelar
        btnCancelar.setOnClickListener {
            finish()
        }
    }

    private fun carregarDadosContato() {
        try {
            val contato = dbHelper.getContato(contatoId)
            if (contato != null) {
                editTextNome.setText(contato[DatabaseHelper.COLUMN_NOME])
                editTextTelefone.setText(contato[DatabaseHelper.COLUMN_TELEFONE])
                editTextEmail.setText(contato[DatabaseHelper.COLUMN_EMAIL])
            } else {
                Toast.makeText(this, "Contato não encontrado", Toast.LENGTH_LONG).show()
                finish()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Erro ao carregar dados: ${e.message}", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun salvarAlteracoes() {
        val novoNome = editTextNome.text.toString().trim()
        val novoTelefone = editTextTelefone.text.toString().trim()
        val novoEmail = editTextEmail.text.toString().trim()

        // Validação dos campos
        if (novoNome.isEmpty() || novoTelefone.isEmpty()) {
            Toast.makeText(this, "Nome e telefone são obrigatórios", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            // Atualizar contato no banco de dados
            val success = dbHelper.updateContato(
                contatoId,
                novoNome,
                novoTelefone,
                novoEmail
            )

            if (success) {
                // Define o resultado como OK e passa os dados atualizados de volta
                setResult(RESULT_OK, Intent().apply {
                    putExtra("contato_id", contatoId)
                    putExtra("contato_nome", novoNome)
                    putExtra("contato_telefone", novoTelefone)
                    putExtra("contato_email", novoEmail)
                })
                Toast.makeText(this, "Contato atualizado com sucesso!", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Erro ao atualizar contato", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Erro ao atualizar: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroy() {
        dbHelper.close()
        super.onDestroy()
    }
}
