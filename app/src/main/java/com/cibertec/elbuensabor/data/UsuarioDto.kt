package com.cibertec.elbuensabor.data

data class UsuarioDto(
    val idUsuario: Int,
    val nombre: String,
    val correo: String,
    val telefono: String?,
    val direccion: String?,
    val password: String,
    val fechaRegistro: String?
)

data class LoginRequest(
    val correo: String,
    val password: String
)