package com.cibertec.elbuensabor

import android.content.Context
import com.cibertec.elbuensabor.data.CarritoItemLocal
import com.cibertec.elbuensabor.data.DBHelper
import com.cibertec.elbuensabor.data.ProductoLocal

object CarritoManager {

    private lateinit var dbHelper: DBHelper

    fun init(context: Context) {
        dbHelper = DBHelper(context.applicationContext)
    }

    // CREATE — agrega producto al carrito (si ya existe, suma 1)
    fun agregar(plato: ProductoLocal) {
        dbHelper.agregarAlCarrito(plato)
    }

    // READ — obtiene todos los items del carrito desde SQLite
    fun obtenerItems(): List<CarritoItemLocal> {
        return dbHelper.obtenerCarrito()
    }

    // UPDATE — cambia la cantidad de un item
    fun actualizarCantidad(idItem: Int, nuevaCantidad: Int) {
        dbHelper.actualizarCantidadCarrito(idItem, nuevaCantidad)
    }

    // DELETE — elimina un item por su id
    fun eliminarItem(idItem: Int) {
        dbHelper.eliminarDelCarrito(idItem)
    }

    // DELETE ALL — vacía el carrito completo (al confirmar pedido)
    fun vaciar() {
        dbHelper.vaciarCarrito()
    }

    // Calcula el subtotal leyendo desde SQLite
    fun calcularSubtotal(): Double {
        return obtenerItems().sumOf { it.precio * it.cantidad }
    }
}