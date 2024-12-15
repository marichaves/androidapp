package com.example.contato.myapp2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var btnVerContatos: Button
    private lateinit var btnNovoContato: Button
    private lateinit var btnAlterarContato: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializar os botões
        btnVerContatos = findViewById(R.id.btnMeusContatos)
        btnNovoContato = findViewById(R.id.btnNovoContato)
        btnAlterarContato = findViewById(R.id.btnAlterarContato)

        // Configurar o clique para ir para a tela de Alterar Contato
        btnAlterarContato.setOnClickListener {
            val intent = Intent(this, MeusContatosActivity::class.java)
            intent.putExtra("modo", "alterar")
            startActivity(intent)
        }

        // Configurar os outros botões similarmente
        btnVerContatos.setOnClickListener {
            val intent = Intent(this, MeusContatosActivity::class.java)
            startActivity(intent)
        }

        btnNovoContato.setOnClickListener {
            val intent = Intent(this, NovoContatoActivity::class.java)
            startActivity(intent)
        }

    }
}
