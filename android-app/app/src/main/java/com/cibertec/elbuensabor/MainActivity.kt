package com.cibertec.elbuensabor

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnRegister: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ProductoManager.init(this)
        UsuarioManager.init(this)
        CarritoManager.init(this)

        etEmail     = findViewById(R.id.etEmail)
        etPassword  = findViewById(R.id.etPassword)
        btnLogin    = findViewById(R.id.btnLogin)
        btnRegister = findViewById(R.id.btnRegister)

        btnLogin.setOnClickListener {
            val email    = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this,
                    "Por favor completa todos los campos",
                    Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            btnLogin.isEnabled = false
            lifecycleScope.launch {
                val exito = UsuarioManager.login(email, password)
                btnLogin.isEnabled = true

                if (exito) {
                    startActivity(Intent(this@MainActivity, MenuActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this@MainActivity,
                        "Credenciales incorrectas",
                        Toast.LENGTH_SHORT).show()
                }
            }
        }

        btnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}