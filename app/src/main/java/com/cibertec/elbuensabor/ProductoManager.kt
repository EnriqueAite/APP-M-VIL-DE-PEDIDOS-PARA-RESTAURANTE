package com.cibertec.elbuensabor

import android.content.Context
import com.cibertec.elbuensabor.data.DBHelper
import com.cibertec.elbuensabor.data.ProductoLocal
import com.cibertec.elbuensabor.data.RetrofitClient

object ProductoManager {

    private lateinit var dbHelper: DBHelper
    var productos: MutableList<ProductoLocal> = mutableListOf()
        private set

    fun init(context: Context) {
        dbHelper = DBHelper(context.applicationContext)
        productos = dbHelper.obtenerProductosLocal().toMutableList()
    }

    suspend fun sincronizarDesdeServidor(): Boolean {
        return try {
            val respuesta = RetrofitClient.api.obtenerProductos()
            if (respuesta.isSuccessful) {
                val lista = respuesta.body() ?: emptyList()
                dbHelper.guardarProductos(lista)
                productos = dbHelper.obtenerProductosLocal().toMutableList()
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    fun obtenerLocal(): List<ProductoLocal> {
        productos = dbHelper.obtenerProductosLocal().toMutableList()
        return productos
    }

    fun siguienteId(): Int = (productos.maxOfOrNull { it.id } ?: 0) + 1

    // Estas funciones por ahora solo afectan la lista en memoria.
    // Más adelante (paso de Admin) las conectaremos para que llamen
    // a la API real (POST/PUT/DELETE) en vez de solo memoria.
    fun agregar(producto: ProductoLocal) {
        productos.add(producto)
    }

    fun eliminar(id: Int) {
        productos.removeAll { it.id == id }
    }

    fun editar(productoEditado: ProductoLocal) {
        val index = productos.indexOfFirst { it.id == productoEditado.id }
        if (index != -1) productos[index] = productoEditado
    }
}