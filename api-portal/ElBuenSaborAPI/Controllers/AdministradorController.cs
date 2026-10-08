using ElBuenSaborAPI.Data;
using ElBuenSaborAPI.Models;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace ElBuenSaborAPI.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class AdministradorController : ControllerBase
    {
        private readonly ElBuenSaborContext _context;

        public AdministradorController(ElBuenSaborContext context)
        {
            _context = context;
        }

        // POST: api/administrador/login
        [HttpPost("login")]
        public async Task<ActionResult<Administrador>> Login([FromBody] LoginAdminRequest request)
        {
            var admin = await _context.Administradores
                .FirstOrDefaultAsync(a =>
                    a.Correo == request.Correo &&
                    a.Password == request.Password);

            if (admin == null)
                return Unauthorized(new { mensaje = "Credenciales incorrectas" });

            admin.UltimoAcceso = DateTime.Now;
            await _context.SaveChangesAsync();

            return admin;
        }

        // GET: api/administrador/1
        [HttpGet("{id}")]
        public async Task<ActionResult<Administrador>> GetAdmin(int id)
        {
            var admin = await _context.Administradores.FindAsync(id);
            if (admin == null) return NotFound();
            return admin;
        }

        // PUT: api/administrador/1
        [HttpPut("{id}")]
        public async Task<IActionResult> PutAdmin(int id, Administrador admin)
        {
            if (id != admin.IdAdmin) return BadRequest();
            _context.Entry(admin).State = EntityState.Modified;
            await _context.SaveChangesAsync();
            return NoContent();
        }
    }

    public class LoginAdminRequest
    {
        public string Correo { get; set; } = string.Empty;
        public string Password { get; set; } = string.Empty;
    }
}