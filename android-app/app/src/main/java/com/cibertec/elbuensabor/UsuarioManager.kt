package com.cibertec.elbuensabor

import android.content.Context
import com.cibertec.elbuensabor.data.DBHelper
import com.cibertec.elbuensabor.data.LoginRequest
import com.cibertec.elbuensabor.data.RetrofitClient
import com.cibertec.elbuensabor.data.UsuarioDto
import com.cibertec.elbuensabor.data.UsuarioLocal

object UsuarioManager {

    private lateinit var dbHelper: DBHelper

    var idUsuario: Int = -1
        private set
    var nombre: String = ""
        private set
    var correo: String = ""
        private set
    var telefono: String = ""
        private set
    var direccion: String = ""
        private set
    var password: String = ""
        private set

    fun init(context: Context) {
        dbHelper = DBHelper(context.applicationContext)
        cargarSesionLocal()
    }

    private fun cargarSesionLocal() {
        val sesion = dbHelper.obtenerSesionUsuario()
        if (sesion != null) {
            idUsuario = sesion.id
            nombre    = sesion.nombre
            correo    = sesion.correo
            telefono  = sesion.telefono
            direccion = sesion.direccion
            password  = sesion.password
        }
    }

    fun haySesionActiva(): Boolean = idUsuario != -1

    // Devuelve true si el login fue exitoso
    suspend fun login(correoIngresado: String, passwordIngresado: String): Boolean {
        return try {
            val respuesta = RetrofitClient.api.loginUsuario(
                LoginRequest(correoIngresado, passwordIngresado)
            )
            if (respuesta.isSuccessful) {
                val usuario = respuesta.body() ?: return false
                guardarSesion(usuario)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    // Devuelve un mensaje de error, o null si el registro fue exitoso
    suspend fun registrar(
        nombreNuevo: String,
        correoNuevo: String,
        telefonoNuevo: String,
        direccionNueva: String,
        passwordNuevo: String
    ): String? {
        return try {
            val usuarioDto = UsuarioDto(
                idUsuario = 0,
                nombre = nombreNuevo,
                correo = correoNuevo,
                telefono = telefonoNuevo,
                direccion = direccionNueva,
                password = passwordNuevo,
                fechaRegistro = null
            )
            val respuesta = RetrofitClient.api.registrarUsuario(usuarioDto)
            if (respuesta.isSuccessful) {
                null // sin error
            } else if (respuesta.code() == 409) {
                "El correo ya está registrado"
            } else {
                "No se pudo crear la cuenta"
            }
        } catch (e: Exception) {
            "Error de conexión: ${e.message}"
        }
    }

    suspend fun actualizarPerfil(
        nombreNuevo: String,
        correoNuevo: String,
        telefonoNuevo: String,
        direccionNueva: String,
        passwordNuevo: String
    ): Boolean {
        return try {
            val usuarioDto = UsuarioDto(
                idUsuario = idUsuario,
                nombre = nombreNuevo,
                correo = correoNuevo,
                telefono = telefonoNuevo,
                direccion = direccionNueva,
                password = passwordNuevo,
                fechaRegistro = null
            )
            val respuesta = RetrofitClient.api.actualizarUsuario(idUsuario, usuarioDto)
            if (respuesta.isSuccessful) {
                guardarSesion(usuarioDto.copy(idUsuario = idUsuario))
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    private fun guardarSesion(usuario: UsuarioDto) {
        idUsuario = usuario.idUsuario
        nombre    = usuario.nombre
        correo    = usuario.correo
        telefono  = usuario.telefono ?: ""
        direccion = usuario.direccion ?: ""
        password  = usuario.password

        dbHelper.guardarSesionUsuario(usuario)
    }

    fun cerrarSesion() {
        idUsuario = -1
        nombre = ""
        correo = ""
        telefono = ""
        direccion = ""
        password = ""
        dbHelper.cerrarSesionUsuario()
        CarritoManager.vaciar()
    }
}