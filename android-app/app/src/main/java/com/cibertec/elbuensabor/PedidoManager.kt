package com.cibertec.elbuensabor

import com.cibertec.elbuensabor.data.CrearDetalleRequest
import com.cibertec.elbuensabor.data.CrearPedidoRequest
import com.cibertec.elbuensabor.data.PedidoDto
import com.cibertec.elbuensabor.data.RetrofitClient

object PedidoManager {

    var pedidosUsuario: List<PedidoDto> = emptyList()
        private set

    // Crea el pedido en el servidor a partir del carrito actual
    suspend fun crearPedido(direccion: String): Boolean {
        val items = CarritoManager.obtenerItems()  // ahora lee de SQLite
        if (items.isEmpty()) return false

        val subtotal = CarritoManager.calcularSubtotal()
        val total = subtotal + 8.0

        val detalles = items.map {
            CrearDetalleRequest(
                idProducto     = it.idProducto,
                cantidad       = it.cantidad,
                precioUnitario = it.precio
            )
        }

        val request = CrearPedidoRequest(
            idUsuario        = UsuarioManager.idUsuario,
            total            = total,
            direccionEntrega = direccion,
            detalles         = detalles
        )

        return try {
            val respuesta = RetrofitClient.api.crearPedido(request)
            respuesta.isSuccessful
        } catch (e: Exception) {
            false
        }
    }

    suspend fun sincronizarPedidosUsuario(): Boolean {
        return try {
            val respuesta = RetrofitClient.api.obtenerPedidosUsuario(UsuarioManager.idUsuario)
            if (respuesta.isSuccessful) {
                pedidosUsuario = respuesta.body() ?: emptyList()
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    fun obtenerActivos(): List<PedidoDto> =
        pedidosUsuario.filter { it.estado != "Entregado" }

    fun obtenerEntregados(): List<PedidoDto> =
        pedidosUsuario.filter { it.estado == "Entregado" }
}