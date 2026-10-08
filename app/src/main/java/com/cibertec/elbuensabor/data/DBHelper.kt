package com.cibertec.elbuensabor.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context) :
    SQLiteOpenHelper(context, "elbuensabor.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE productos (
                id_producto INTEGER PRIMARY KEY,
                id_categoria INTEGER,
                nombre TEXT,
                descripcion TEXT,
                precio REAL,
                imagen_url TEXT,
                categoria_nombre TEXT
            )
        """)

        db.execSQL("""
            CREATE TABLE usuario_sesion (
                id_usuario INTEGER PRIMARY KEY,
                nombre TEXT,
                correo TEXT,
                telefono TEXT,
                direccion TEXT,
                password TEXT
            )
        """)

        db.execSQL("""
            CREATE TABLE carrito (
                id_item INTEGER PRIMARY KEY AUTOINCREMENT,
                id_producto INTEGER,
                nombre TEXT,
                precio REAL,
                imagen_url TEXT,
                cantidad INTEGER
            )
        """)

        db.execSQL("""
            CREATE TABLE pedidos_local (
                id_pedido_local INTEGER PRIMARY KEY AUTOINCREMENT,
                id_pedido_servidor INTEGER,
                codigo TEXT,
                estado TEXT,
                total REAL,
                direccion TEXT,
                fecha TEXT,
                sincronizado INTEGER DEFAULT 0
            )
        """)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS productos")
        db.execSQL("DROP TABLE IF EXISTS usuario_sesion")
        db.execSQL("DROP TABLE IF EXISTS carrito")
        db.execSQL("DROP TABLE IF EXISTS pedidos_local")
        onCreate(db)
    }

    // ---------- PRODUCTOS ----------

    fun guardarProductos(productos: List<ProductoDto>) {
        val db = writableDatabase
        db.execSQL("DELETE FROM productos")
        for (p in productos) {
            val values = ContentValues().apply {
                put("id_producto", p.idProducto)
                put("id_categoria", p.idCategoria)
                put("nombre", p.nombre)
                put("descripcion", p.descripcion)
                put("precio", p.precio)
                put("imagen_url", p.imagenUrl)
                put("categoria_nombre", p.categoria?.nombre ?: "")
            }
            db.insert("productos", null, values)
        }
    }

    fun obtenerProductosLocal(): List<ProductoLocal> {
        val lista = mutableListOf<ProductoLocal>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM productos", null)
        while (cursor.moveToNext()) {
            lista.add(
                ProductoLocal(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow("id_producto")),
                    idCategoria = cursor.getInt(cursor.getColumnIndexOrThrow("id_categoria")),
                    nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                    descripcion = cursor.getString(cursor.getColumnIndexOrThrow("descripcion")) ?: "",
                    precio = cursor.getDouble(cursor.getColumnIndexOrThrow("precio")),
                    imagenUrl = cursor.getString(cursor.getColumnIndexOrThrow("imagen_url")),
                    categoriaNombre = cursor.getString(cursor.getColumnIndexOrThrow("categoria_nombre")) ?: ""
                )
            )
        }
        cursor.close()
        return lista
    }

    // ---------- SESIÓN DE USUARIO ----------

    fun guardarSesionUsuario(usuario: UsuarioDto) {
        val db = writableDatabase
        db.execSQL("DELETE FROM usuario_sesion")
        val values = ContentValues().apply {
            put("id_usuario", usuario.idUsuario)
            put("nombre", usuario.nombre)
            put("correo", usuario.correo)
            put("telefono", usuario.telefono)
            put("direccion", usuario.direccion)
            put("password", usuario.password)
        }
        db.insert("usuario_sesion", null, values)
    }

    fun obtenerSesionUsuario(): UsuarioLocal? {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM usuario_sesion LIMIT 1", null)
        var usuario: UsuarioLocal? = null
        if (cursor.moveToFirst()) {
            usuario = UsuarioLocal(
                id = cursor.getInt(cursor.getColumnIndexOrThrow("id_usuario")),
                nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                correo = cursor.getString(cursor.getColumnIndexOrThrow("correo")),
                telefono = cursor.getString(cursor.getColumnIndexOrThrow("telefono")) ?: "",
                direccion = cursor.getString(cursor.getColumnIndexOrThrow("direccion")) ?: "",
                password = cursor.getString(cursor.getColumnIndexOrThrow("password"))
            )
        }
        cursor.close()
        return usuario
    }

    fun cerrarSesionUsuario() {
        val db = writableDatabase
        db.execSQL("DELETE FROM usuario_sesion")
    }

    // ---------- CARRITO ----------

    fun agregarAlCarrito(producto: ProductoLocal) {
        val db = writableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM carrito WHERE id_producto = ?",
            arrayOf(producto.id.toString())
        )
        if (cursor.moveToFirst()) {
            val cantidadActual = cursor.getInt(cursor.getColumnIndexOrThrow("cantidad"))
            db.execSQL(
                "UPDATE carrito SET cantidad = ? WHERE id_producto = ?",
                arrayOf(cantidadActual + 1, producto.id)
            )
        } else {
            val values = ContentValues().apply {
                put("id_producto", producto.id)
                put("nombre", producto.nombre)
                put("precio", producto.precio)
                put("imagen_url", producto.imagenUrl)
                put("cantidad", 1)
            }
            db.insert("carrito", null, values)
        }
        cursor.close()
    }

    fun obtenerCarrito(): List<CarritoItemLocal> {
        val lista = mutableListOf<CarritoItemLocal>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM carrito", null)
        while (cursor.moveToNext()) {
            lista.add(
                CarritoItemLocal(
                    idItem = cursor.getInt(cursor.getColumnIndexOrThrow("id_item")),
                    idProducto = cursor.getInt(cursor.getColumnIndexOrThrow("id_producto")),
                    nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                    precio = cursor.getDouble(cursor.getColumnIndexOrThrow("precio")),
                    imagenUrl = cursor.getString(cursor.getColumnIndexOrThrow("imagen_url")),
                    cantidad = cursor.getInt(cursor.getColumnIndexOrThrow("cantidad"))
                )
            )
        }
        cursor.close()
        return lista
    }

    fun actualizarCantidadCarrito(idItem: Int, nuevaCantidad: Int) {
        val db = writableDatabase
        db.execSQL(
            "UPDATE carrito SET cantidad = ? WHERE id_item = ?",
            arrayOf(nuevaCantidad, idItem)
        )
    }

    fun eliminarDelCarrito(idItem: Int) {
        val db = writableDatabase
        db.execSQL("DELETE FROM carrito WHERE id_item = ?", arrayOf(idItem))
    }

    fun vaciarCarrito() {
        val db = writableDatabase
        db.execSQL("DELETE FROM carrito")
    }

    // ---------- PEDIDOS LOCALES (respaldo antes de sincronizar) ----------

    fun guardarPedidoLocal(codigo: String, total: Double, direccion: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("codigo", codigo)
            put("estado", "Pendiente")
            put("total", total)
            put("direccion", direccion)
            put("fecha", System.currentTimeMillis().toString())
            put("sincronizado", 0)
        }
        return db.insert("pedidos_local", null, values)
    }

    fun marcarPedidoSincronizado(idPedidoLocal: Long, idServidor: Int) {
        val db = writableDatabase
        db.execSQL(
            "UPDATE pedidos_local SET sincronizado = 1, id_pedido_servidor = ? WHERE id_pedido_local = ?",
            arrayOf(idServidor, idPedidoLocal)
        )
    }
}

// ---------- Clases de datos locales ----------

data class ProductoLocal(
    val id: Int,
    val idCategoria: Int,
    val nombre: String,
    val descripcion: String,
    val precio: Double,
    val imagenUrl: String?,
    val categoriaNombre: String
)

data class UsuarioLocal(
    val id: Int,
    val nombre: String,
    val correo: String,
    val telefono: String,
    val direccion: String,
    val password: String
)

data class CarritoItemLocal(
    val idItem: Int,
    val idProducto: Int,
    val nombre: String,
    val precio: Double,
    val imagenUrl: String?,
    var cantidad: Int
)