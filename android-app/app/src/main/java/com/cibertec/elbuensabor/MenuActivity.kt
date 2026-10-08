package com.cibertec.elbuensabor

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class MenuActivity : AppCompatActivity() {

    private lateinit var etSearch: EditText
    private lateinit var rvPlatos: RecyclerView
    private lateinit var adapter: PlatoAdapter

    private var categoriaActual = "todos"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        etSearch = findViewById(R.id.etSearch)
        rvPlatos = findViewById(R.id.rvPlatos)

        adapter = PlatoAdapter(ProductoManager.obtenerLocal().toMutableList())
        rvPlatos.layoutManager = LinearLayoutManager(this)
        rvPlatos.adapter = adapter

        configurarFiltros()
        configurarBusqueda()
        sincronizarProductos()

        findViewById<LinearLayout>(R.id.navCarrito).setOnClickListener {
            val intent = Intent(this, CarritoActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
        }

        findViewById<LinearLayout>(R.id.navPedidos).setOnClickListener {
            val intent = Intent(this, PedidosActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
        }

        findViewById<LinearLayout>(R.id.navPerfil).setOnClickListener {
            val intent = Intent(this, PerfilActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
        }

        findViewById<ImageView>(R.id.ivLogout).setOnClickListener {
            UsuarioManager.cerrarSesion()
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        filtrar(etSearch.text.toString())
    }

    private fun sincronizarProductos() {
        lifecycleScope.launch {
            val exito = ProductoManager.sincronizarDesdeServidor()
            if (exito) {
                filtrar(etSearch.text.toString())
            }
        }
    }

    private fun configurarFiltros() {
        val btnTodos     = findViewById<TextView>(R.id.btnTodos)
        val btnCarnes    = findViewById<TextView>(R.id.btnCarnes)
        val btnPescados  = findViewById<TextView>(R.id.btnPescados)
        val btnEnsaladas = findViewById<TextView>(R.id.btnEnsaladas)
        val btnBebidas   = findViewById<TextView>(R.id.btnBebidas)

        val botones = listOf(btnTodos, btnCarnes, btnPescados, btnEnsaladas, btnBebidas)

        marcarBotonActivo(botones, btnTodos)

        btnTodos.setOnClickListener {
            categoriaActual = "todos"
            marcarBotonActivo(botones, btnTodos)
            filtrar(etSearch.text.toString())
        }
        btnCarnes.setOnClickListener {
            categoriaActual = "carnes"
            marcarBotonActivo(botones, btnCarnes)
            filtrar(etSearch.text.toString())
        }
        btnPescados.setOnClickListener {
            categoriaActual = "pescados"
            marcarBotonActivo(botones, btnPescados)
            filtrar(etSearch.text.toString())
        }
        btnEnsaladas.setOnClickListener {
            categoriaActual = "ensaladas"
            marcarBotonActivo(botones, btnEnsaladas)
            filtrar(etSearch.text.toString())
        }
        btnBebidas.setOnClickListener {
            categoriaActual = "bebidas"
            marcarBotonActivo(botones, btnBebidas)
            filtrar(etSearch.text.toString())
        }
    }

    private fun marcarBotonActivo(botones: List<TextView>, activo: TextView) {
        botones.forEach { btn ->
            if (btn == activo) {
                btn.setBackgroundResource(R.drawable.bg_button_primary)
                btn.setTextColor(getColor(R.color.white))
            } else {
                btn.setBackgroundResource(R.drawable.bg_filter_inactive)
                btn.setTextColor(getColor(R.color.brown_primary))
            }
        }
    }

    private fun configurarBusqueda() {
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) { filtrar(s.toString()) }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun filtrar(query: String) {
        val resultado = ProductoManager.obtenerLocal().filter { plato ->
            val coincideCategoria = categoriaActual == "todos" ||
                    plato.categoriaNombre == categoriaActual
            val coincideBusqueda = query.isEmpty() ||
                    plato.nombre.contains(query, ignoreCase = true)
            coincideCategoria && coincideBusqueda
        }
        adapter.actualizar(resultado)
    }
}