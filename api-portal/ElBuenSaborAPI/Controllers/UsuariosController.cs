using ElBuenSaborAPI.Data;
using ElBuenSaborAPI.Models;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace ElBuenSaborAPI.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class UsuariosController : ControllerBase
    {
        private readonly ElBuenSaborContext _context;

        public UsuariosController(ElBuenSaborContext context)
        {
            _context = context;
        }

        // GET: api/usuarios
        [HttpGet]
        public async Task<ActionResult<IEnumerable<Usuario>>> GetUsuarios()
        {
            return await _context.Usuarios.ToListAsync();
        }

        // GET: api/usuarios/1
        [HttpGet("{id}")]
        public async Task<ActionResult<Usuario>> GetUsuario(int id)
        {
            var usuario = await _context.Usuarios.FindAsync(id);
            if (usuario == null) return NotFound();
            return usuario;
        }

        // POST: api/usuarios/login
        [HttpPost("login")]
        public async Task<ActionResult<Usuario>> Login([FromBody] LoginRequest request)
        {
            var usuario = await _context.Usuarios
                .FirstOrDefaultAsync(u =>
                    u.Correo == request.Correo &&
                    u.Password == request.Password);

            if (usuario == null)
                return Unauthorized(new { mensaje = "Credenciales incorrectas" });

            return usuario;
        }

        // POST: api/usuarios
        [HttpPost]
        public async Task<ActionResult<Usuario>> PostUsuario(Usuario usuario)
        {
            var existe = await _context.Usuarios
                .AnyAsync(u => u.Correo == usuario.Correo);
            if (existe)
                return Conflict(new { mensaje = "El correo ya está registrado" });

            _context.Usuarios.Add(usuario);
            await _context.SaveChangesAsync();
            return CreatedAtAction(nameof(GetUsuario),
                new { id = usuario.IdUsuario }, usuario);
        }

        // PUT: api/usuarios/1
        [HttpPut("{id}")]
        public async Task<IActionResult> PutUsuario(int id, Usuario usuario)
        {
            if (id != usuario.IdUsuario) return BadRequest();
            _context.Entry(usuario).State = EntityState.Modified;
            await _context.SaveChangesAsync();
            return NoContent();
        }

        // DELETE: api/usuarios/1
        [HttpDelete("{id}")]
        public async Task<IActionResult> DeleteUsuario(int id)
        {
            var usuario = await _context.Usuarios.FindAsync(id);
            if (usuario == null) return NotFound();

            var tienePedidos = await _context.Pedidos.AnyAsync(p => p.IdUsuario == id);
            if (tienePedidos)
                return BadRequest(new
                {
                    mensaje =
                    "No se puede eliminar: el cliente tiene pedidos registrados"
                });

            _context.Usuarios.Remove(usuario);
            await _context.SaveChangesAsync();
            return NoContent();
        }
    }

    // Clase auxiliar para el login
    public class LoginRequest
    {
        public string Correo { get; set; } = string.Empty;
        public string Password { get; set; } = string.Empty;
    }
}