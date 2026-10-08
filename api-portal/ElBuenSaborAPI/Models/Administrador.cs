namespace ElBuenSaborAPI.Models
{
    public class Administrador
    {
        public int IdAdmin { get; set; }
        public string Nombre { get; set; } = string.Empty;
        public string Correo { get; set; } = string.Empty;
        public string Password { get; set; } = string.Empty;
        public string? Telefono { get; set; }
        public DateTime? UltimoAcceso { get; set; }
    }
}