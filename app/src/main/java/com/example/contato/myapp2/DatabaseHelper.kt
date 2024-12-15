package com.example.contato.myapp2

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "contatos.db"
        const val DATABASE_VERSION = 1

        // Nome da tabela e colunas
        const val TABLE_NAME = "contatos"
        const val COLUMN_ID = "id"
        const val COLUMN_NOME = "nome"
        const val COLUMN_TELEFONE = "telefone"
        const val COLUMN_EMAIL = "email"

        // Mensagens de erro e sucesso
        const val ERROR_UPDATE = "Erro ao atualizar contato"
        const val ERROR_NOT_FOUND = "Contato não encontrado"
        const val SUCCESS_UPDATE = "Contato atualizado com sucesso"

        // SQL para criar a tabela
        private const val CREATE_TABLE = """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NOME TEXT NOT NULL,
                $COLUMN_TELEFONE TEXT,
                $COLUMN_EMAIL TEXT
            )
        """
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    // Verificar integridade do banco de dados
    fun verificarBancoDeDados(): Boolean {
        val db = readableDatabase
        return try {
            val cursor = db.query(
                TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
            )
            cursor.use {
                true // Se chegou até aqui, a tabela existe e está acessível
            }
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Erro ao verificar banco de dados: ${e.message}")
            false
        } finally {
            db.close()
        }
    }

    // Create - Adicionar um contato
    fun addContato(nome: String, telefone: String, email: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NOME, nome)
            put(COLUMN_TELEFONE, telefone)
            put(COLUMN_EMAIL, email)
        }

        return try {
            val id = db.insert(TABLE_NAME, null, values)
            Log.d("DatabaseHelper", "Contato adicionado com ID: $id")
            id
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Erro ao adicionar contato: ${e.message}")
            -1
        } finally {
            db.close()
        }
    }

    // Read - Ler todos os contatos
    fun getAllContatos(): List<Map<String, String>> {
        val contatos = mutableListOf<Map<String, String>>()
        val db = readableDatabase

        try {
            val cursor = db.query(
                TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                "$COLUMN_NOME ASC"
            )

            cursor.use {
                while (it.moveToNext()) {
                    val contato = mapOf(
                        COLUMN_ID to it.getInt(it.getColumnIndexOrThrow(COLUMN_ID)).toString(),
                        COLUMN_NOME to it.getString(it.getColumnIndexOrThrow(COLUMN_NOME)),
                        COLUMN_TELEFONE to it.getString(it.getColumnIndexOrThrow(COLUMN_TELEFONE)),
                        COLUMN_EMAIL to it.getString(it.getColumnIndexOrThrow(COLUMN_EMAIL))
                    )
                    contatos.add(contato)
                }
            }
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Erro ao buscar contatos: ${e.message}")
        } finally {
            db.close()
        }
        return contatos
    }

    // Read - Buscar um contato específico por ID
    fun getContato(id: Int): Map<String, String>? {
        val db = readableDatabase

        return try {
            val cursor = db.query(
                TABLE_NAME,
                null,
                "$COLUMN_ID = ?",
                arrayOf(id.toString()),
                null,
                null,
                null
            )

            cursor.use {
                if (it.moveToFirst()) {
                    mapOf(
                        COLUMN_ID to it.getInt(it.getColumnIndexOrThrow(COLUMN_ID)).toString(),
                        COLUMN_NOME to it.getString(it.getColumnIndexOrThrow(COLUMN_NOME)),
                        COLUMN_TELEFONE to it.getString(it.getColumnIndexOrThrow(COLUMN_TELEFONE)),
                        COLUMN_EMAIL to it.getString(it.getColumnIndexOrThrow(COLUMN_EMAIL))
                    )
                } else null
            }
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Erro ao buscar contato: ${e.message}")
            null
        } finally {
            db.close()
        }
    }

    // Update - Atualizar um contato
    fun updateContato(id: Int, nome: String, telefone: String, email: String): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NOME, nome)
            put(COLUMN_TELEFONE, telefone)
            put(COLUMN_EMAIL, email)
        }

        return try {
            // Verificar se o registro existe
            val exists = db.query(
                TABLE_NAME,
                arrayOf(COLUMN_ID),
                "$COLUMN_ID = ?",
                arrayOf(id.toString()),
                null,
                null,
                null
            ).use { it.count > 0 }

            if (!exists) {
                Log.e("DatabaseHelper", "Registro com ID $id não encontrado")
                return false
            }

            val rowsAffected = db.update(
                TABLE_NAME,
                values,
                "$COLUMN_ID = ?",
                arrayOf(id.toString())
            )

            Log.d("DatabaseHelper", "Linhas afetadas: $rowsAffected")
            rowsAffected > 0
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Erro ao atualizar contato: ${e.message}")
            e.printStackTrace()
            false
        } finally {
            db.close()
        }
    }

    // Delete - Deletar um contato
    fun deleteContato(id: Int): Boolean {
        val db = writableDatabase
        return try {
            val success = db.delete(
                TABLE_NAME,
                "$COLUMN_ID = ?",
                arrayOf(id.toString())
            )
            Log.d("DatabaseHelper", "Contato deletado: $success linhas afetadas")
            success > 0
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Erro ao deletar contato: ${e.message}")
            false
        } finally {
            db.close()
        }
    }

    // Buscar contatos por nome
    fun buscarContatosPorNome(nome: String): List<Map<String, String>> {
        val contatos = mutableListOf<Map<String, String>>()
        val db = readableDatabase

        try {
            val cursor = db.query(
                TABLE_NAME,
                null,
                "$COLUMN_NOME LIKE ?",
                arrayOf("%$nome%"),
                null,
                null,
                "$COLUMN_NOME ASC"
            )

            cursor.use {
                while (it.moveToNext()) {
                    val contato = mapOf(
                        COLUMN_ID to it.getInt(it.getColumnIndexOrThrow(COLUMN_ID)).toString(),
                        COLUMN_NOME to it.getString(it.getColumnIndexOrThrow(COLUMN_NOME)),
                        COLUMN_TELEFONE to it.getString(it.getColumnIndexOrThrow(COLUMN_TELEFONE)),
                        COLUMN_EMAIL to it.getString(it.getColumnIndexOrThrow(COLUMN_EMAIL))
                    )
                    contatos.add(contato)
                }
            }
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Erro ao buscar contatos por nome: ${e.message}")
        } finally {
            db.close()
        }
        return contatos
    }
}
