namespace ElBuenSaborAPI.Models
{
    public class Pedido
    {
        public int IdPedido { get; set; }
        public int IdUsuario { get; set; }
        public string Codigo { get; set; } = string.Empty;
        public string Estado { get; set; } = "Pendiente";
        public decimal Total { get; set; }
        public string DireccionEntrega { get; set; } = string.Empty;
        public DateTime FechaPedido { get; set; } = DateTime.Now;

        public Usuario? Usuario { get; set; }
        public ICollection<DetallePedido> Detalles { get; set; } = new List<DetallePedido>();
    }
}