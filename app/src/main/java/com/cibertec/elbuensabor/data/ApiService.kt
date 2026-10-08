package com.cibertec.elbuensabor.data

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {

    // ---------- Productos ----------
    @GET("api/productos")
    suspend fun obtenerProductos(): Response<List<ProductoDto>>

    @GET("api/categorias")
    suspend fun obtenerCategorias(): Response<List<CategoriaDto>>

    // ---------- Usuarios ----------
    @POST("api/usuarios/login")
    suspend fun loginUsuario(@Body request: LoginRequest): Response<UsuarioDto>

    @POST("api/usuarios")
    suspend fun registrarUsuario(@Body usuario: UsuarioDto): Response<UsuarioDto>

    @PUT("api/usuarios/{id}")
    suspend fun actualizarUsuario(
        @Path("id") id: Int,
        @Body usuario: UsuarioDto
    ): Response<Unit>

    // ---------- Administrador ----------
    @POST("api/administrador/login")
    suspend fun loginAdmin(@Body request: LoginRequest): Response<AdminDto>

    @PUT("api/administrador/{id}")
    suspend fun actualizarAdmin(
        @Path("id") id: Int,
        @Body admin: AdminDto
    ): Response<Unit>

    // ---------- Pedidos ----------
    @GET("api/pedidos/usuario/{idUsuario}")
    suspend fun obtenerPedidosUsuario(
        @Path("idUsuario") idUsuario: Int
    ): Response<List<PedidoDto>>

    @POST("api/pedidos")
    suspend fun crearPedido(@Body pedido: CrearPedidoRequest): Response<PedidoDto>

    @PATCH("api/pedidos/{id}/estado")
    suspend fun cambiarEstadoPedido(
        @Path("id") id: Int,
        @Body request: CambiarEstadoRequest
    ): Response<Unit>

    @GET("api/pedidos")
    suspend fun obtenerTodosPedidos(): Response<List<PedidoDto>>
}