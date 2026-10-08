namespace ElBuenSaborAPI.Models
{
    public class Producto
    {
        public int IdProducto { get; set; }
        public int IdCategoria { get; set; }
        public string Nombre { get; set; } = string.Empty;
        public string? Descripcion { get; set; }
        public decimal Precio { get; set; }
        public string? ImagenUrl { get; set; }
        public bool Activo { get; set; } = true;

        public Categoria? Categoria { get; set; }
        public ICollection<DetallePedido> Detalles { get; set; } = new List<DetallePedido>();
    }
}