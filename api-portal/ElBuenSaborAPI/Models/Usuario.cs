namespace ElBuenSaborAPI.Models
{
    public class Usuario
    {
        public int IdUsuario { get; set; }
        public string Nombre { get; set; } = string.Empty;
        public string Correo { get; set; } = string.Empty;
        public string? Telefono { get; set; }
        public string? Direccion { get; set; }
        public string Password { get; set; } = string.Empty;
        public DateOnly FechaRegistro { get; set; } = DateOnly.FromDateTime(DateTime.Now);

        public ICollection<Pedido> Pedidos { get; set; } = new List<Pedido>();
    }
}