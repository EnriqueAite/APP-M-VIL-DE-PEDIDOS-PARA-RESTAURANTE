CREATE DATABASE IF NOT EXISTS `elbuensabor`
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE `elbuensabor`;

-- Tabla: administrador
CREATE TABLE `administrador` (
  `id_admin`      INT          NOT NULL AUTO_INCREMENT,
  `nombre`        VARCHAR(100) NOT NULL,
  `correo`        VARCHAR(100) NOT NULL,
  `password`      VARCHAR(255) NOT NULL,
  `telefono`      VARCHAR(20)  DEFAULT NULL,
  `ultimo_acceso` DATETIME     DEFAULT NULL,
  PRIMARY KEY (`id_admin`),
  UNIQUE KEY `uq_admin_correo` (`correo`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- Tabla: categorias
CREATE TABLE `categorias` (
  `id_categoria` INT         NOT NULL AUTO_INCREMENT,
  `nombre`       VARCHAR(50) NOT NULL,
  PRIMARY KEY (`id_categoria`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- Tabla: usuarios
CREATE TABLE `usuarios` (
  `id_usuario`      INT          NOT NULL AUTO_INCREMENT,
  `nombre`          VARCHAR(100) NOT NULL,
  `correo`          VARCHAR(100) NOT NULL,
  `telefono`        VARCHAR(20)  DEFAULT NULL,
  `direccion`       VARCHAR(255) DEFAULT NULL,
  `password`        VARCHAR(255) NOT NULL,
  `fecha_registro`  DATE         NOT NULL DEFAULT (CURDATE()),
  PRIMARY KEY (`id_usuario`),
  UNIQUE KEY `uq_usuario_correo` (`correo`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- Tabla: productos (depende de categorias)
CREATE TABLE `productos` (
  `id_producto`  INT          NOT NULL AUTO_INCREMENT,
  `id_categoria` INT          NOT NULL,
  `nombre`       VARCHAR(100) NOT NULL,
  `descripcion`  VARCHAR(255) DEFAULT NULL,
  `precio`       DECIMAL(8,2) NOT NULL,
  `imagen_url`   VARCHAR(255) DEFAULT NULL,
  `activo`       TINYINT(1)   NOT NULL DEFAULT 1,
  PRIMARY KEY (`id_producto`),
  CONSTRAINT `fk_producto_categoria`
    FOREIGN KEY (`id_categoria`)
    REFERENCES `categorias` (`id_categoria`)
    ON DELETE RESTRICT
    ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- Tabla: pedidos (depende de usuarios)
CREATE TABLE `pedidos` (
  `id_pedido`        INT          NOT NULL AUTO_INCREMENT,
  `id_usuario`       INT          NOT NULL,
  `codigo`           VARCHAR(20)  NOT NULL,
  `estado`           ENUM('Pendiente','Preparando','Enviado','Entregado','Cancelado')
                                  NOT NULL DEFAULT 'Pendiente',
  `total`            DECIMAL(8,2) NOT NULL,
  `direccion_entrega` VARCHAR(255) NOT NULL,
  `fecha_pedido`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_pedido`),
  UNIQUE KEY `uq_pedido_codigo` (`codigo`),
  CONSTRAINT `fk_pedido_usuario`
    FOREIGN KEY (`id_usuario`)
    REFERENCES `usuarios` (`id_usuario`)
    ON DELETE RESTRICT
    ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- Tabla: detalle_pedido (depende de pedidos y productos)
CREATE TABLE `detalle_pedido` (
  `id_detalle`     INT          NOT NULL AUTO_INCREMENT,
  `id_pedido`      INT          NOT NULL,
  `id_producto`    INT          NOT NULL,
  `cantidad`       INT          NOT NULL DEFAULT 1,
  `precio_unitario` DECIMAL(8,2) NOT NULL,
  PRIMARY KEY (`id_detalle`),
  CONSTRAINT `fk_detalle_pedido`
    FOREIGN KEY (`id_pedido`)
    REFERENCES `pedidos` (`id_pedido`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_detalle_producto`
    FOREIGN KEY (`id_producto`)
    REFERENCES `productos` (`id_producto`)
    ON DELETE RESTRICT
    ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- Registros: administrador
INSERT INTO `administrador` (`id_admin`, `nombre`, `correo`, `password`, `telefono`, `ultimo_acceso`) VALUES
(1, 'Alexander Velasquez', 'admin@elbuensabor.com', 'admin123', '987738810', '2026-06-20 22:45:38');


-- Registros: categorias
INSERT INTO `categorias` (`id_categoria`, `nombre`) VALUES
(1, 'carnes'),
(2, 'pescados'),
(3, 'ensaladas'),
(4, 'bebidas');


-- Registros: usuarios
INSERT INTO `usuarios` (`id_usuario`, `nombre`, `correo`, `telefono`, `direccion`, `password`, `fecha_registro`) VALUES
(1, 'Emilia Huanachin', 'cliente@elbuensabor.com', '987 654 321', 'Av. Siempre Viva 123', '123456', '2026-06-20');


-- Registros: productos
INSERT INTO `productos` (`id_producto`, `id_categoria`, `nombre`, `descripcion`, `precio`, `imagen_url`, `activo`) VALUES
( 1, 1, 'Lomo Saltado',          'Con papas fritas y arroz',          28.00, '/imagenes/096424cd-3e0e-400c-8221-339a7b11a1c3.png', 1),
( 2, 1, 'Pollo a la Brasa',      '1/4 pollo + papas + ensalada',      22.00, '/imagenes/11feab32-5fb5-4749-b94b-99748b65da97.jpg', 1),
( 3, 2, 'Ceviche Mixto',         'Pescado, mariscos, cancha',         35.00, '/imagenes/6079ed7b-1903-4cd8-93dc-2b0c02fd6b1b.jpg', 1),
( 4, 3, 'Ensalada Mediterránea', 'Lechuga, tomate, aceitunas',        15.00, '/imagenes/2a3d6d63-7d64-44e0-bb67-7c02da23e40c.jpg', 1),
( 5, 4, 'Inca Kola',             'Bebida gaseosa 500ml',               6.00, '/imagenes/bb7448b4-83d8-4d42-b620-c519a47c3ed9.jpg', 1),
( 8, 1, 'Hamburguesa',           'Pan con carne, queso y lechuga',    15.00, '/imagenes/11d4b3c7-fa47-4c2a-8ef4-997c32652788.jpg', 1),
( 9, 1, 'Churrasco',             'Churrasco + papas, huevo y tomates',25.00, '/imagenes/2ed7803a-d21b-4241-b926-f3f5e1769cb0.jpg', 1),
(10, 2, 'Arroz con mariscos',    'Arroz, pulpo y verduras',           30.00, '/imagenes/51a0d0e2-c4b0-48e1-a6d9-cb7d6433d841.jpg', 1),
(11, 2, 'Pescado Frito',         'Pescado, ensalada y chifles',       36.00, '/imagenes/eed55c4c-ae7c-4ae8-b653-ba1c1be891e3.jpg', 1),
(12, 3, 'Ensalada César',        'Pescado, lechuga y salsa',          40.00, '/imagenes/4c121a66-5dd0-43ce-a9c0-257865ffbc4c.jpg', 1),
(13, 3, 'Ensalada de Frutas',    'Plátano, fresas, manzana, etc.',    15.00, '/imagenes/855b0cc6-42c4-433a-a1f5-86a316bedfea.jpg', 1),
(14, 4, 'Coca Cola',             'Bebida gaseosa 500ml',               6.00, '/imagenes/1d24d43f-6954-4b4d-a93a-df0cc93a1601.jpg', 1),
(15, 4, 'Chicha morada',         'Maíz Morado + Piña',                5.00, '/imagenes/1fcc41d8-9516-4765-9dd9-0442c0b243ef.jpg', 1),
(16, 4, 'Jugo surtido',          'Mango + Manzana + Fresas',          5.00, '/imagenes/c544bb00-d353-44fa-bf23-6f6be7040969.jpg', 1);