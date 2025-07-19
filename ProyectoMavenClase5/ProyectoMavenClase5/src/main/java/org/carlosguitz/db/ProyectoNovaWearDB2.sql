-- drop database if exists ProyectoNovaWearDB2;
create database ProyectoNovaWearDB2;
use ProyectoNovaWearDB2;

create table Productos(
	idProducto int auto_increment,
    nombreProducto varchar(50),
    descripcionProducto varchar(100),
    stock int,
    precioProducto decimal(10,2),
    constraint pk_productos primary key (idProducto)
);

CREATE TABLE Usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre_completo VARCHAR(100), -- Nuevo campo
    correo_electronico VARCHAR(100) UNIQUE, -- Nuevo campo, idealmente UNIQUE
    nombre_usuario VARCHAR(50) NOT NULL UNIQUE,
    contrasena VARCHAR(255) NOT NULL,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);







---------------------- C R U D ----------------------------
--------------- PROCESOS ALMACENADOS DE PRODUCTOS -------------------------
-- Listar Productos
delimiter $$
	create procedure sp_ListarProductos()
		begin
			select
				P.idProducto as ID,
				P.nombreProducto as NOMBRE,
				P.descripcionProducto as DESCRIPCION,
				P.stock as STOCK,
				P.precioProducto as PRECIO
			from Productos P;
		end$$
delimiter ;

-- Agregar Producto
delimiter $$
	create procedure sp_AgregarProducto(
			in _nombreProducto varchar(50),
			in _descripcionProducto varchar(100),
			in _stock int,
			in _precioProducto decimal(10, 2)
		)
		begin
			insert into Productos (nombreProducto, descripcionProducto, stock, precioProducto)
			values (_nombreProducto, _descripcionProducto, _stock, _precioProducto);
	end$$
delimiter ;

-- Ejemplos de uso para Agregar Producto
call sp_AgregarProducto('Sudadero', 'Sudadero negro calido', 150, 175.99);
call sp_AgregarProducto('Gorra', 'Gorra color azul comoda', 200, 135.50);
call sp_AgregarProducto('Tennis"', 'Tennis comodos para correr', 80, 499.00);

select * from productos;
-- Buscar Producto por ID
delimiter $$
	create procedure sp_BuscarProducto(
		in _idProducto int)
		begin
			select * from Productos
			where idProducto = _idProducto;
		end$$
delimiter ;

-- Actualizar Producto
delimiter $$
	create procedure sp_ActualizarProducto(
		in _idProducto int,
		in _nombreProducto varchar(50),
		in _descripcionProducto varchar(100),
		in _stock int,
		in _precioProducto decimal(10, 2)
		)
		begin
			update Productos
			set nombreProducto = _nombreProducto,
				descripcionProducto = _descripcionProducto,
				stock = _stock,
				precioProducto = _precioProducto
			where idProducto = _idProducto;
		end$$
delimiter ;

-- Eliminar Producto
delimiter $$
	create procedure sp_EliminarProducto(
		in _idProducto int)
		begin
			delete from Productos
			where idProducto = _idProducto;
		end$$
delimiter ;

-- ---- FUNCION PARA USUARIOS -- - ------
select * from Usuarios;