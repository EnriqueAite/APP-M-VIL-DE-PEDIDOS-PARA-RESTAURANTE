namespace ElBuenSaborAPI.Models
{
    public class CrearPedidoDto
    {
        public int IdUsuario { get; set; }
        public decimal Total { get; set; }
        public string DireccionEntrega { get; set; } = string.Empty;
        public List<CrearDetalleDto> Detalles { get; set; } = new();
    }

    public class CrearDetalleDto
    {
        public int IdProducto { get; set; }
        public int Cantidad { get; set; }
        public decimal PrecioUnitario { get; set; }
    }
}