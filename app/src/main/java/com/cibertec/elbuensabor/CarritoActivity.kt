package com.cibertec.elbuensabor

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class CarritoActivity : AppCompatActivity() {

    private lateinit var rvCarrito: RecyclerView
    private lateinit var tvSubtotal: TextView
    private lateinit var tvTotal: TextView
    private lateinit var adapter: CarritoAdapter

    private val delivery = 8.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_carrito)

        rvCarrito  = findViewById(R.id.rvCarrito)
        tvSubtotal = findViewById(R.id.tvSubtotal)
        tvTotal    = findViewById(R.id.tvTotal)

        // READ — leer carrito desde SQLite
        val itemsActuales = CarritoManager.obtenerItems().toMutableList()

        adapter = CarritoAdapter(itemsActuales) {
            actualizarTotales()
        }
        rvCarrito.layoutManager = LinearLayoutManager(this)
        rvCarrito.adapter = adapter

        actualizarTotales()

        findViewById<TextView>(R.id.btnVolver).setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
        }

        findViewById<TextView>(R.id.btnConfirmar).setOnClickListener {
            if (CarritoManager.obtenerItems().isEmpty()) {
                Toast.makeText(this, "Tu carrito está vacío",
                    Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val btn = findViewById<TextView>(R.id.btnConfirmar)
            btn.isEnabled = false
            btn.text = "Procesando..."

            lifecycleScope.launch {
                val exito = PedidoManager.crearPedido(UsuarioManager.direccion)
                btn.isEnabled = true
                btn.text = "✅  Confirmar Pedido"

                if (exito) {
                    // DELETE ALL — vaciar carrito en SQLite
                    CarritoManager.vaciar()
                    adapter.notifyDataSetChanged()
                    actualizarTotales()
                    Toast.makeText(this@CarritoActivity,
                        "¡Pedido confirmado!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@CarritoActivity,
                        PedidosActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                            Intent.FLAG_ACTIVITY_SINGLE_TOP
                    startActivity(intent)
                } else {
                    Toast.makeText(this@CarritoActivity,
                        "No se pudo confirmar el pedido. Intenta de nuevo.",
                        Toast.LENGTH_SHORT).show()
                }
            }
        }

        findViewById<LinearLayout>(R.id.navMenu).setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
        }

        findViewById<LinearLayout>(R.id.navPedidosCarrito).setOnClickListener {
            val intent = Intent(this, PedidosActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
        }

        findViewById<LinearLayout>(R.id.navPerfilCarrito).setOnClickListener {
            val intent = Intent(this, PerfilActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
        }
    }

    @Suppress("DEPRECATION")
    override fun onBackPressed() {
        val intent = Intent(this, MenuActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        actualizarTotales()
    }

    private fun actualizarTotales() {
        val subtotal = CarritoManager.calcularSubtotal()
        val total = subtotal + delivery
        tvSubtotal.text = "S/ %.2f".format(subtotal)
        tvTotal.text    = "S/ %.2f".format(total)
    }
}