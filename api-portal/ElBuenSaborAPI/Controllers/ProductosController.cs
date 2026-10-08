using ElBuenSaborAPI.Data;
using ElBuenSaborAPI.Models;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace ElBuenSaborAPI.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class ProductosController : ControllerBase
    {
        private readonly ElBuenSaborContext _context;

        public ProductosController(ElBuenSaborContext context)
        {
            _context = context;
        }

        // GET: api/productos
        [HttpGet]
        public async Task<ActionResult<IEnumerable<Producto>>> GetProductos()
        {
            return await _context.Productos
                .Include(p => p.Categoria)
                .Where(p => p.Activo)
                .ToListAsync();
        }

        // GET: api/productos/categoria/1
        [HttpGet("categoria/{idCategoria}")]
        public async Task<ActionResult<IEnumerable<Producto>>> GetPorCategoria(int idCategoria)
        {
            return await _context.Productos
                .Include(p => p.Categoria)
                .Where(p => p.IdCategoria == idCategoria && p.Activo)
                .ToListAsync();
        }

        // GET: api/productos/1
        [HttpGet("{id}")]
        public async Task<ActionResult<Producto>> GetProducto(int id)
        {
            var producto = await _context.Productos
                .Include(p => p.Categoria)
                .FirstOrDefaultAsync(p => p.IdProducto == id);
            if (producto == null) return NotFound();
            return producto;
        }

        // POST: api/productos
        [HttpPost]
        public async Task<ActionResult<Producto>> PostProducto(Producto producto)
        {
            _context.Productos.Add(producto);
            await _context.SaveChangesAsync();
            return CreatedAtAction(nameof(GetProducto),
                new { id = producto.IdProducto }, producto);
        }

        // PUT: api/productos/1
        [HttpPut("{id}")]
        public async Task<IActionResult> PutProducto(int id, Producto producto)
        {
            if (id != producto.IdProducto) return BadRequest();
            _context.Entry(producto).State = EntityState.Modified;
            await _context.SaveChangesAsync();
            return NoContent();
        }

        // DELETE: api/productos/1 (desactiva en vez de borrar)
        [HttpDelete("{id}")]
        public async Task<IActionResult> DeleteProducto(int id)
        {
            var producto = await _context.Productos.FindAsync(id);
            if (producto == null) return NotFound();
            producto.Activo = false;
            await _context.SaveChangesAsync();
            return NoContent();
        }
    }
}