package com.cibertec.elbuensabor

import com.cibertec.elbuensabor.data.ProductoLocal

data class CarritoItem(
    val plato: ProductoLocal,
    var cantidad: Int = 1
)