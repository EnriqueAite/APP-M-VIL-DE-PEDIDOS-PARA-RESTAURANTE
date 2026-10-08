package com.cibertec.elbuensabor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cibertec.elbuensabor.data.CarritoItemLocal
import com.cibertec.elbuensabor.data.RetrofitClient

class CarritoAdapter(
    private var lista: MutableList<CarritoItemLocal>,
    private val onCambio: () -> Unit
) : RecyclerView.Adapter<CarritoAdapter.CarritoViewHolder>() {

    inner class CarritoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imagen: ImageView     = itemView.findViewById(R.id.ivPlatoCarrito)
        val nombre: TextView      = itemView.findViewById(R.id.tvNombreCarrito)
        val precio: TextView      = itemView.findViewById(R.id.tvPrecioCarrito)
        val cantidad: TextView    = itemView.findViewById(R.id.tvCantidad)
        val btnMenos: TextView    = itemView.findViewById(R.id.btnMenos)
        val btnMas: TextView      = itemView.findViewById(R.id.btnMas)
        val btnEliminar: TextView = itemView.findViewById(R.id.btnEliminar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarritoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_carrito, parent, false)
        return CarritoViewHolder(view)
    }

    override fun onBindViewHolder(holder: CarritoViewHolder, position: Int) {
        val item = lista[position]

        if (!item.imagenUrl.isNullOrEmpty()) {
            Glide.with(holder.itemView.context)
                .load(RetrofitClient.IMAGE_BASE_URL + item.imagenUrl)
                .placeholder(R.drawable.ic_ensalada)
                .error(R.drawable.ic_ensalada)
                .centerCrop()
                .into(holder.imagen)
        } else {
            holder.imagen.setImageResource(R.drawable.ic_ensalada)
        }

        holder.nombre.text   = item.nombre
        holder.precio.text   = "S/ %.2f".format(item.precio)
        holder.cantidad.text = item.cantidad.toString()

        // UPDATE — aumentar cantidad
        holder.btnMas.setOnClickListener {
            val pos = holder.bindingAdapterPosition
            if (pos >= 0 && pos < lista.size) {
                val nuevaCantidad = lista[pos].cantidad + 1
                CarritoManager.actualizarCantidad(lista[pos].idItem, nuevaCantidad)
                lista[pos] = lista[pos].copy(cantidad = nuevaCantidad)
                notifyItemChanged(pos)
                onCambio()
            }
        }

        // UPDATE — reducir cantidad (mínimo 1)
        holder.btnMenos.setOnClickListener {
            val pos = holder.bindingAdapterPosition
            if (pos >= 0 && pos < lista.size && lista[pos].cantidad > 1) {
                val nuevaCantidad = lista[pos].cantidad - 1
                CarritoManager.actualizarCantidad(lista[pos].idItem, nuevaCantidad)
                lista[pos] = lista[pos].copy(cantidad = nuevaCantidad)
                notifyItemChanged(pos)
                onCambio()
            }
        }

        // DELETE — eliminar item del carrito
        holder.btnEliminar.setOnClickListener {
            val pos = holder.bindingAdapterPosition
            if (pos >= 0 && pos < lista.size) {
                CarritoManager.eliminarItem(lista[pos].idItem)
                lista.removeAt(pos)
                notifyItemRemoved(pos)
                notifyItemRangeChanged(pos, lista.size)
                onCambio()
            }
        }
    }

    override fun getItemCount(): Int = lista.size
}