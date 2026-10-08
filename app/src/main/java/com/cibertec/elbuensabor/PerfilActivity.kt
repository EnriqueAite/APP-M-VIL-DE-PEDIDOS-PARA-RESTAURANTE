package com.cibertec.elbuensabor

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class PerfilActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)

        findViewById<TextView>(R.id.btnVolverPerfil).setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
        }

        findViewById<LinearLayout>(R.id.navMenuPerfil).setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
        }

        findViewById<LinearLayout>(R.id.navCarritoPerfil).setOnClickListener {
            val intent = Intent(this, CarritoActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
        }

        findViewById<LinearLayout>(R.id.navPedidosPerfil).setOnClickListener {
            val intent = Intent(this, PedidosActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
        }

        findViewById<TextView>(R.id.btnCerrarSesionPerfil).setOnClickListener {
            UsuarioManager.cerrarSesion()
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        findViewById<TextView>(R.id.btnActualizarDatos).setOnClickListener {
            mostrarDialogoActualizar()
        }

        cargarDatos()
    }

    @Suppress("DEPRECATION")
    override fun onBackPressed() {
        val intent = Intent(this, MenuActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        cargarDatos()
    }

    private fun cargarDatos() {
        findViewById<TextView>(R.id.tvNombrePerfil).text = UsuarioManager.nombre
        findViewById<TextView>(R.id.tvCorreoPerfil).text = UsuarioManager.correo
        findViewById<TextView>(R.id.tvTelefonoPerfil).text = UsuarioManager.telefono
        findViewById<TextView>(R.id.tvDireccionPerfil).text = UsuarioManager.direccion
        findViewById<TextView>(R.id.tvPasswordPerfil).text =
            "•".repeat(UsuarioManager.password.length)
        findViewById<TextView>(R.id.tvMiembroPerfil).text = "Miembro activo"
    }

    private fun mostrarDialogoActualizar() {
        val layout = layoutInflater.inflate(R.layout.dialog_actualizar_perfil, null)
        val etNombre    = layout.findViewById<EditText>(R.id.etDialogNombrePerfil)
        val etCorreo    = layout.findViewById<EditText>(R.id.etDialogCorreoPerfil)
        val etTelefono  = layout.findViewById<EditText>(R.id.etDialogTelefonoPerfil)
        val etDireccion = layout.findViewById<EditText>(R.id.etDialogDireccionPerfil)
        val etPassword  = layout.findViewById<EditText>(R.id.etDialogPasswordPerfil)

        etNombre.setText(UsuarioManager.nombre)
        etCorreo.setText(UsuarioManager.correo)
        etTelefono.setText(UsuarioManager.telefono)
        etDireccion.setText(UsuarioManager.direccion)
        etPassword.setText(UsuarioManager.password)

        AlertDialog.Builder(this)
            .setTitle("Actualizar datos")
            .setView(layout)
            .setPositiveButton("Guardar") { _, _ ->
                val nombre    = etNombre.text.toString().trim()
                val correo    = etCorreo.text.toString().trim()
                val telefono  = etTelefono.text.toString().trim()
                val direccion = etDireccion.text.toString().trim()
                val password  = etPassword.text.toString().trim()

                if (nombre.isEmpty() || correo.isEmpty() || password.isEmpty()) {
                    Toast.makeText(this, "Completa los campos obligatorios",
                        Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                lifecycleScope.launch {
                    val exito = UsuarioManager.actualizarPerfil(
                        nombre, correo, telefono, direccion, password
                    )
                    if (exito) {
                        cargarDatos()
                        Toast.makeText(this@PerfilActivity,
                            "Datos actualizados", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@PerfilActivity,
                            "No se pudo actualizar", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}