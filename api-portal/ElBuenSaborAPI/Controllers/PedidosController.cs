using ElBuenSaborAPI.Data;
using ElBuenSaborAPI.Models;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace ElBuenSaborAPI.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class PedidosController : ControllerBase
    {
        private readonly ElBuenSaborContext _context;

        public PedidosController(ElBuenSaborContext context)
        {
            _context = context;
        }

        // GET: api/pedidos
        [HttpGet]
        public async Task<ActionResult<IEnumerable<Pedido>>> GetPedidos()
        {
            return await _context.Pedidos
                .Include(p => p.Usuario)
                .Include(p => p.Detalles)
                    .ThenInclude(d => d.Producto)
                .OrderByDescending(p => p.FechaPedido)
                .ToListAsync();
        }

        // GET: api/pedidos/usuario/1
        [HttpGet("usuario/{idUsuario}")]
        public async Task<ActionResult<IEnumerable<Pedido>>> GetPedidosUsuario(int idUsuario)
        {
            return await _context.Pedidos
                .Include(p => p.Detalles)
                    .ThenInclude(d => d.Producto)
                .Where(p => p.IdUsuario == idUsuario)
                .OrderByDescending(p => p.FechaPedido)
                .ToListAsync();
        }

        // GET: api/pedidos/1
        [HttpGet("{id}")]
        public async Task<ActionResult<Pedido>> GetPedido(int id)
        {
            var pedido = await _context.Pedidos
                .Include(p => p.Usuario)
                .Include(p => p.Detalles)
                    .ThenInclude(d => d.Producto)
                .FirstOrDefaultAsync(p => p.IdPedido == id);

            if (pedido == null) return NotFound();
            return pedido;
        }

        // POST: api/pedidos
        [HttpPost]
        public async Task<ActionResult<Pedido>> PostPedido(CrearPedidoDto request)
        {
            int totalPedidos = await _context.Pedidos.CountAsync();
            var codigo = "Pedido #" + (totalPedidos + 1).ToString().PadLeft(4, '0');

            var pedido = new Pedido
            {
                IdUsuario = request.IdUsuario,
                Codigo = codigo,
                Estado = "Pendiente",
                Total = request.Total,
                DireccionEntrega = request.DireccionEntrega,
                FechaPedido = DateTime.Now
            };

            _context.Pedidos.Add(pedido);
            await _context.SaveChangesAsync(); // Guarda primero para obtener el IdPedido

            foreach (var d in request.Detalles)
            {
                _context.DetallesPedido.Add(new DetallePedido
                {
                    IdPedido = pedido.IdPedido,
                    IdProducto = d.IdProducto,
                    Cantidad = d.Cantidad,
                    PrecioUnitario = d.PrecioUnitario
                });
            }

            await _context.SaveChangesAsync();

            var pedidoCompleto = await _context.Pedidos
                .Include(p => p.Usuario)
                .Include(p => p.Detalles)
                    .ThenInclude(d => d.Producto)
                .FirstOrDefaultAsync(p => p.IdPedido == pedido.IdPedido);

            return CreatedAtAction(nameof(GetPedido),
                new { id = pedido.IdPedido }, pedidoCompleto);
        }

        // PATCH: api/pedidos/1/estado
        [HttpPatch("{id}/estado")]
        public async Task<IActionResult> CambiarEstado(int id,
            [FromBody] CambiarEstadoRequest request)
        {
            var pedido = await _context.Pedidos.FindAsync(id);
            if (pedido == null) return NotFound();

            var estadosValidos = new[]
                { "Pendiente", "Preparando", "Enviado", "Entregado", "Cancelado" };
            if (!estadosValidos.Contains(request.Estado))
                return BadRequest(new { mensaje = "Estado no válido" });

            pedido.Estado = request.Estado;
            await _context.SaveChangesAsync();
            return NoContent();
        }

        // PATCH: api/pedidos/1/cancelar
        [HttpPatch("{id}/cancelar")]
        public async Task<IActionResult> CancelarPedido(int id)
        {
            var pedido = await _context.Pedidos.FindAsync(id);
            if (pedido == null) return NotFound();

            if (pedido.Estado == "Entregado")
                return BadRequest(new
                {
                    mensaje =
                    "No se puede cancelar un pedido ya entregado"
                });

            pedido.Estado = "Cancelado";
            await _context.SaveChangesAsync();
            return NoContent();
        }

        // DELETE: api/pedidos/1
        [HttpDelete("{id}")]
        public async Task<IActionResult> DeletePedido(int id)
        {
            var pedido = await _context.Pedidos.FindAsync(id);
            if (pedido == null) return NotFound();
            _context.Pedidos.Remove(pedido);
            await _context.SaveChangesAsync();
            return NoContent();
        }
    }

    public class CambiarEstadoRequest
    {
        public string Estado { get; set; } = string.Empty;
    }
}