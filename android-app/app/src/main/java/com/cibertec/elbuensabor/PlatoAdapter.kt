package com.cibertec.elbuensabor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cibertec.elbuensabor.data.ProductoLocal
import com.cibertec.elbuensabor.data.RetrofitClient

class PlatoAdapter(private var lista: MutableList<ProductoLocal>) :
    RecyclerView.Adapter<PlatoAdapter.PlatoViewHolder>() {

    inner class PlatoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imagen: ImageView     = itemView.findViewById(R.id.ivPlato)
        val nombre: TextView      = itemView.findViewById(R.id.tvNombre)
        val descripcion: TextView = itemView.findViewById(R.id.tvDescripcion)
        val precio: TextView      = itemView.findViewById(R.id.tvPrecio)
        val btnAgregar: TextView  = itemView.findViewById(R.id.btnAgregar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlatoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_plato, parent, false)
        return PlatoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlatoViewHolder, position: Int) {
        val plato = lista[position]
        holder.nombre.text      = plato.nombre
        holder.descripcion.text = plato.descripcion
        holder.precio.text      = "S/ %.2f".format(plato.precio)

        if (!plato.imagenUrl.isNullOrEmpty()) {
            Glide.with(holder.itemView.context)
                .load(RetrofitClient.IMAGE_BASE_URL + plato.imagenUrl)
                .placeholder(R.drawable.ic_ensalada)
                .error(R.drawable.ic_ensalada)
                .centerCrop()
                .into(holder.imagen)
        } else {
            holder.imagen.setImageResource(R.drawable.ic_ensalada)
        }

        holder.btnAgregar.setOnClickListener {
            CarritoManager.agregar(plato)
            Toast.makeText(
                holder.itemView.context,
                "${plato.nombre} agregado al carrito",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun getItemCount(): Int = lista.size

    fun actualizar(nuevaLista: List<ProductoLocal>) {
        lista = nuevaLista.toMutableList()
        notifyDataSetChanged()
    }
}