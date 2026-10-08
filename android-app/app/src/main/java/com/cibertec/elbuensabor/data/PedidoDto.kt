package com.cibertec.elbuensabor.data

data class DetallePedidoDto(
    val idDetalle: Int,
    val idPedido: Int,
    val idProducto: Int,
    val cantidad: Int,
    val precioUnitario: Double,
    val producto: ProductoDto?
)

data class PedidoDto(
    val idPedido: Int,
    val idUsuario: Int,
    val codigo: String,
    val estado: String,
    val total: Double,
    val direccionEntrega: String,
    val fechaPedido: String,
    val usuario: UsuarioDto?,
    val detalles: List<DetallePedidoDto>?
)

data class CrearPedidoRequest(
    val idUsuario: Int,
    val total: Double,
    val direccionEntrega: String,
    val detalles: List<CrearDetalleRequest>
)

data class CrearDetalleRequest(
    val idProducto: Int,
    val cantidad: Int,
    val precioUnitario: Double
)

data class CambiarEstadoRequest(
    val estado: String
)