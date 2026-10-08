package com.cibertec.elbuensabor.data

data class AdminDto(
    val idAdmin: Int,
    val nombre: String,
    val correo: String,
    val password: String,
    val telefono: String?,
    val ultimoAcceso: String?
)