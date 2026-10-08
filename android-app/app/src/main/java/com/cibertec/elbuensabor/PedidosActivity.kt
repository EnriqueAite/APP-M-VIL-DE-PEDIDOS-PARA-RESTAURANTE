package com.cibertec.elbuensabor

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.cibertec.elbuensabor.data.PedidoDto
import kotlinx.coroutines.launch

class PedidosActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pedidos)

        findViewById<TextView>(R.id.btnVolverPedidos).setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
        }

        findViewById<LinearLayout>(R.id.navMenuPedidos).setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
        }

        findViewById<LinearLayout>(R.id.navCarritoPedidos).setOnClickListener {
            val intent = Intent(this, CarritoActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
        }

        cargarPedidos()
    }

    @Suppress("DEPRECATION")
    override fun onBackPressed() {
        val intent = Intent(this, MenuActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        cargarPedidos()
    }

    private fun cargarPedidos() {
        lifecycleScope.launch {
            PedidoManager.sincronizarPedidosUsuario()
            renderPedidos()
        }
    }

    private fun renderPedidos() {
        val activos    = PedidoManager.obtenerActivos()
        val entregados = PedidoManager.obtenerEntregados()

        val tvSinPedidos     = findViewById<TextView>(R.id.tvSinPedidos)
        val layoutActivos    = findViewById<LinearLayout>(R.id.layoutActivos)
        val layoutEntregados = findViewById<LinearLayout>(R.id.layoutEntregados)
        val contenedorActivos    = findViewById<LinearLayout>(R.id.contenedorActivos)
        val contenedorEntregados = findViewById<LinearLayout>(R.id.contenedorEntregados)

        contenedorActivos.removeAllViews()
        contenedorEntregados.removeAllViews()

        if (activos.isEmpty() && entregados.isEmpty()) {
            tvSinPedidos.visibility = View.VISIBLE
            layoutActivos.visibility = View.GONE
            layoutEntregados.visibility = View.GONE
            return
        }

        tvSinPedidos.visibility = View.GONE

        if (activos.isNotEmpty()) {
            layoutActivos.visibility = View.VISIBLE
            activos.forEach { pedido ->
                contenedorActivos.addView(crearVistaPedido(pedido))
            }
        } else {
            layoutActivos.visibility = View.GONE
        }

        if (entregados.isNotEmpty()) {
            layoutEntregados.visibility = View.VISIBLE
            entregados.forEach { pedido ->
                contenedorEntregados.addView(crearVistaPedido(pedido))
            }
        } else {
            layoutEntregados.visibility = View.GONE
        }
    }

    private fun crearVistaPedido(pedido: PedidoDto): View {
        val view = LayoutInflater.from(this).inflate(R.layout.item_pedido, null, false)

        val ivIcono       = view.findViewById<ImageView>(R.id.ivEstadoIcono)
        val tvCodigo      = view.findViewById<TextView>(R.id.tvCodigoPedido)
        val tvBadge       = view.findViewById<TextView>(R.id.tvEstadoBadge)
        val tvResumen     = view.findViewById<TextView>(R.id.tvResumenProductos)
        val tvTotalCorto  = view.findViewById<TextView>(R.id.tvTotalCorto)
        val btnExpandir   = view.findViewById<TextView>(R.id.btnExpandir)
        val layoutExpandido = view.findViewById<LinearLayout>(R.id.layoutExpandido)
        val tvMensaje     = view.findViewById<TextView>(R.id.tvMensajeEstado)
        val tvDireccion   = view.findViewById<TextView>(R.id.tvDireccionPedido)
        val layoutProductos = view.findViewById<LinearLayout>(R.id.layoutProductosPedido)

        val dotPendiente  = view.findViewById<View>(R.id.dotPendiente)
        val dotPreparando = view.findViewById<View>(R.id.dotPreparando)
        val dotEnviado    = view.findViewById<View>(R.id.dotEnviado)
        val dotEntregado  = view.findViewById<View>(R.id.dotEntregado)

        tvCodigo.text = pedido.codigo
        tvTotalCorto.text = "Total: S/ %.2f".format(pedido.total)

        val detalles = pedido.detalles ?: emptyList()

        tvResumen.text = detalles.joinToString(" • ") {
            "${it.cantidad}x ${it.producto?.nombre ?: "Producto"}"
        }

        when (pedido.estado) {
            "Pendiente" -> {
                ivIcono.setImageResource(R.drawable.ic_estado_pendiente)
                tvBadge.text = "Pendiente"
                tvBadge.setBackgroundResource(R.drawable.bg_badge_pendiente)
                tvMensaje.text = "Tu pedido ha sido recibido y estamos esperando confirmación."
            }
            "Preparando" -> {
                ivIcono.setImageResource(R.drawable.ic_estado_preparando)
                tvBadge.text = "Preparando"
                tvBadge.setBackgroundResource(R.drawable.bg_badge_preparando)
                tvMensaje.text = "Tu pedido está siendo preparado en cocina."
            }
            "Enviado" -> {
                ivIcono.setImageResource(R.drawable.ic_estado_enviado)
                tvBadge.text = "Enviado"
                tvBadge.setBackgroundResource(R.drawable.bg_badge_enviado)
                tvMensaje.text = "Tu pedido va en camino."
            }
            "Entregado" -> {
                ivIcono.setImageResource(R.drawable.ic_estado_entregado)
                tvBadge.text = "Entregado"
                tvBadge.setBackgroundResource(R.drawable.bg_badge_entregado)
                tvMensaje.text = "Tu pedido fue entregado. ¡Buen provecho!"
            }
            "Cancelado" -> {
                ivIcono.setImageResource(R.drawable.ic_estado_pendiente)
                tvBadge.text = "Cancelado"
                tvBadge.setBackgroundResource(R.drawable.bg_badge_cancelado)
                tvMensaje.text = "Este pedido fue cancelado. Por favor, intenta pedir de nuevo."
            }
        }

        val pasos = listOf("Pendiente", "Preparando", "Enviado", "Entregado")
        val pasoActual = pasos.indexOf(pedido.estado) // será -1 si está Cancelado, lo manejamos abajo

        val dots = listOf(dotPendiente, dotPreparando, dotEnviado, dotEntregado)
        dots.forEachIndexed { index, dot ->
            if (pedido.estado == "Cancelado") {
                dot.setBackgroundResource(R.drawable.bg_circle_gris)
            } else if (index <= pasoActual) {
                dot.setBackgroundResource(R.drawable.bg_circle_brown)
            } else {
                dot.setBackgroundResource(R.drawable.bg_circle_gris)
            }
        }

        tvDireccion.text = "📍 ${pedido.direccionEntrega}"

        layoutProductos.removeAllViews()
        detalles.forEach { d ->
            val prodView = LayoutInflater.from(this)
                .inflate(R.layout.item_producto_pedido, layoutProductos, false)
            prodView.findViewById<TextView>(R.id.tvCantidadProd).text = "${d.cantidad}"
            prodView.findViewById<TextView>(R.id.tvNombreProd).text =
                d.producto?.nombre ?: "Producto"
            prodView.findViewById<TextView>(R.id.tvPrecioProd).text =
                "S/ %.2f".format(d.precioUnitario * d.cantidad)
            layoutProductos.addView(prodView)
        }

        var expandido = false
        val toggle = {
            expandido = !expandido
            layoutExpandido.visibility = if (expandido) View.VISIBLE else View.GONE
            btnExpandir.text = if (expandido) "▲" else "▼"
        }
        btnExpandir.setOnClickListener { toggle() }
        view.findViewById<View>(R.id.tvCodigoPedido).setOnClickListener { toggle() }

        return view
    }
}