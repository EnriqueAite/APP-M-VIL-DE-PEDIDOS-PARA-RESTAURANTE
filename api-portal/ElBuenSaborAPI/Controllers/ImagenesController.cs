using Microsoft.AspNetCore.Mvc;

namespace ElBuenSaborAPI.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class ImagenesController : ControllerBase
    {
        private readonly IWebHostEnvironment _env;

        public ImagenesController(IWebHostEnvironment env)
        {
            _env = env;
        }

        // POST: api/imagenes/subir
        [HttpPost("subir")]
        public async Task<IActionResult> Subir(IFormFile archivo)
        {
            if (archivo == null || archivo.Length == 0)
                return BadRequest(new { mensaje = "No se envió ningún archivo" });

            var extensionesPermitidas = new[] { ".jpg", ".jpeg", ".png", ".webp" };
            var extension = Path.GetExtension(archivo.FileName).ToLower();

            if (!extensionesPermitidas.Contains(extension))
                return BadRequest(new { mensaje = "Formato de imagen no permitido" });

            // Nombre único para evitar que se sobreescriban
            var nombreArchivo = $"{Guid.NewGuid()}{extension}";
            var rutaCarpeta = Path.Combine(_env.WebRootPath, "imagenes");

            if (!Directory.Exists(rutaCarpeta))
                Directory.CreateDirectory(rutaCarpeta);

            var rutaCompleta = Path.Combine(rutaCarpeta, nombreArchivo);

            using (var stream = new FileStream(rutaCompleta, FileMode.Create))
            {
                await archivo.CopyToAsync(stream);
            }

            // Construir la URL pública
            var urlBase = $"{Request.Scheme}://{Request.Host}";
            var urlImagen = $"/imagenes/{nombreArchivo}";

            return Ok(new { url = urlImagen });
        }
    }
}