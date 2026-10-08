package com.cibertec.elbuensabor

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var etNombre: EditText
    private lateinit var etCorreo: EditText
    private lateinit var etTelefono: EditText
    private lateinit var etDireccion: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnRegistrarse: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        etNombre       = findViewById(R.id.etNombre)
        etCorreo       = findViewById(R.id.etCorreo)
        etTelefono     = findViewById(R.id.etTelefono)
        etDireccion    = findViewById(R.id.etDireccion)
        etPassword     = findViewById(R.id.etPasswordRegister)
        btnRegistrarse = findViewById(R.id.btnRegistrarse)

        findViewById<TextView>(R.id.btnVolverRegister).setOnClickListener {
            finish()
        }

        btnRegistrarse.setOnClickListener {
            val nombre    = etNombre.text.toString().trim()
            val correo    = etCorreo.text.toString().trim()
            val telefono  = etTelefono.text.toString().trim()
            val direccion = etDireccion.text.toString().trim()
            val password  = etPassword.text.toString().trim()

            if (nombre.isEmpty() || correo.isEmpty() || telefono.isEmpty() ||
                direccion.isEmpty() || password.isEmpty()) {
                Toast.makeText(this,
                    "Por favor completa todos los campos",
                    Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            btnRegistrarse.isEnabled = false
            lifecycleScope.launch {
                val error = UsuarioManager.registrar(
                    nombre, correo, telefono, direccion, password
                )
                btnRegistrarse.isEnabled = true

                if (error == null) {
                    Toast.makeText(this@RegisterActivity,
                        "Cuenta creada con éxito",
                        Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@RegisterActivity,
                        error, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}