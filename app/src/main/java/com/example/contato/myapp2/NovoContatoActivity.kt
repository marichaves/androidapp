package com.example.contato.myapp2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class NovoContatoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_novo_contato)

        val dbHelper = DatabaseHelper(this)

        val etNome: EditText = findViewById(R.id.etNome)
        val etTelefone: EditText = findViewById(R.id.etTelefone)
        val etEmail: EditText = findViewById(R.id.etEmail)
        val btnSalvar: Button = findViewById(R.id.btnSalvar)
        val btnVoltar: Button = findViewById(R.id.btnVoltar)

        btnSalvar.setOnClickListener {
            val nome = etNome.text.toString()
            val telefone = etTelefone.text.toString()
            val email = etEmail.text.toString()

            if (nome.isNotEmpty() && email.isNotEmpty()) {
                val id = dbHelper.addContato(nome, telefone, email)
                if (id > 0) {
                    Toast.makeText(this, "Contato salvo com sucesso!", Toast.LENGTH_SHORT).show()
                    voltarParaTelaInicial()
                } else {
                    Toast.makeText(this, "Erro ao salvar contato!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Preencha os campos obrigatórios!", Toast.LENGTH_SHORT).show()
            }
        }

        btnVoltar.setOnClickListener {
            voltarParaTelaInicial()
        }
    }

    private fun voltarParaTelaInicial() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }
}
