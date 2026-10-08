package com.cibertec.elbuensabor.data

data class CategoriaDto(
    val idCategoria: Int,
    val nombre: String
)

data class ProductoDto(
    val idProducto: Int,
    val idCategoria: Int,
    val nombre: String,
    val descripcion: String?,
    val precio: Double,
    val imagenUrl: String?,
    val activo: Boolean,
    val categoria: CategoriaDto?
)