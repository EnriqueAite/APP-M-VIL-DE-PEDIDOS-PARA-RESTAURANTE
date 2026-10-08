using ElBuenSaborAPI.Models;
using Microsoft.EntityFrameworkCore;

namespace ElBuenSaborAPI.Data
{
    public class ElBuenSaborContext : DbContext
    {
        public ElBuenSaborContext(DbContextOptions<ElBuenSaborContext> options)
            : base(options) { }

        public DbSet<Categoria> Categorias { get; set; }
        public DbSet<Producto> Productos { get; set; }
        public DbSet<Usuario> Usuarios { get; set; }
        public DbSet<Administrador> Administradores { get; set; }
        public DbSet<Pedido> Pedidos { get; set; }
        public DbSet<DetallePedido> DetallesPedido { get; set; }

        protected override void OnModelCreating(ModelBuilder modelBuilder)
        {
            // Tabla: categorias
            modelBuilder.Entity<Categoria>(e => {
                e.ToTable("categorias");
                e.HasKey(c => c.IdCategoria);
                e.Property(c => c.IdCategoria).HasColumnName("id_categoria");
                e.Property(c => c.Nombre).HasColumnName("nombre").HasMaxLength(50);
            });

            // Tabla: productos
            modelBuilder.Entity<Producto>(e => {
                e.ToTable("productos");
                e.HasKey(p => p.IdProducto);
                e.Property(p => p.IdProducto).HasColumnName("id_producto");
                e.Property(p => p.IdCategoria).HasColumnName("id_categoria");
                e.Property(p => p.Nombre).HasColumnName("nombre").HasMaxLength(100);
                e.Property(p => p.Descripcion).HasColumnName("descripcion").HasMaxLength(255);
                e.Property(p => p.Precio).HasColumnName("precio").HasColumnType("decimal(8,2)");
                e.Property(p => p.ImagenUrl).HasColumnName("imagen_url").HasMaxLength(255);  // <-- NUEVA LÍNEA
                e.Property(p => p.Activo).HasColumnName("activo");
                e.HasOne(p => p.Categoria)
                 .WithMany(c => c.Productos)
                 .HasForeignKey(p => p.IdCategoria);
            });

            // Tabla: usuarios
            modelBuilder.Entity<Usuario>(e => {
                e.ToTable("usuarios");
                e.HasKey(u => u.IdUsuario);
                e.Property(u => u.IdUsuario).HasColumnName("id_usuario");
                e.Property(u => u.Nombre).HasColumnName("nombre").HasMaxLength(100);
                e.Property(u => u.Correo).HasColumnName("correo").HasMaxLength(100);
                e.Property(u => u.Telefono).HasColumnName("telefono").HasMaxLength(20);
                e.Property(u => u.Direccion).HasColumnName("direccion").HasMaxLength(255);
                e.Property(u => u.Password).HasColumnName("password").HasMaxLength(255);
                e.Property(u => u.FechaRegistro).HasColumnName("fecha_registro");
            });

            // Tabla: administrador
            modelBuilder.Entity<Administrador>(e => {
                e.ToTable("administrador");
                e.HasKey(a => a.IdAdmin);
                e.Property(a => a.IdAdmin).HasColumnName("id_admin");
                e.Property(a => a.Nombre).HasColumnName("nombre").HasMaxLength(100);
                e.Property(a => a.Correo).HasColumnName("correo").HasMaxLength(100);
                e.Property(a => a.Password).HasColumnName("password").HasMaxLength(255);
                e.Property(a => a.Telefono).HasColumnName("telefono").HasMaxLength(20);
                e.Property(a => a.UltimoAcceso).HasColumnName("ultimo_acceso");
            });

            // Tabla: pedidos
            modelBuilder.Entity<Pedido>(e => {
                e.ToTable("pedidos");
                e.HasKey(p => p.IdPedido);
                e.Property(p => p.IdPedido).HasColumnName("id_pedido");
                e.Property(p => p.IdUsuario).HasColumnName("id_usuario");
                e.Property(p => p.Codigo).HasColumnName("codigo").HasMaxLength(20);
                e.Property(p => p.Estado).HasColumnName("estado").HasMaxLength(20);
                e.Property(p => p.Total).HasColumnName("total").HasColumnType("decimal(8,2)");
                e.Property(p => p.DireccionEntrega).HasColumnName("direccion_entrega").HasMaxLength(255);
                e.Property(p => p.FechaPedido).HasColumnName("fecha_pedido");
                e.HasOne(p => p.Usuario)
                 .WithMany(u => u.Pedidos)
                 .HasForeignKey(p => p.IdUsuario);
            });

            // Tabla: detalle_pedido
            modelBuilder.Entity<DetallePedido>(e => {
                e.ToTable("detalle_pedido");
                e.HasKey(d => d.IdDetalle);
                e.Property(d => d.IdDetalle).HasColumnName("id_detalle");
                e.Property(d => d.IdPedido).HasColumnName("id_pedido");
                e.Property(d => d.IdProducto).HasColumnName("id_producto");
                e.Property(d => d.Cantidad).HasColumnName("cantidad");
                e.Property(d => d.PrecioUnitario).HasColumnName("precio_unitario").HasColumnType("decimal(8,2)");
                e.HasOne(d => d.Pedido)
                 .WithMany(p => p.Detalles)
                 .HasForeignKey(d => d.IdPedido);
                e.HasOne(d => d.Producto)
                 .WithMany(p => p.Detalles)
                 .HasForeignKey(d => d.IdProducto);
            });
        }
    }
}