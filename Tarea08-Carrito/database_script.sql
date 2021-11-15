grant all privileges on *.* to 'hugo'@'localhost';
grant all privileges on 'carrito_compra' to 'hugo'@'localhost';

create database carrito_compra;

use carrito_compra;

create table articulos(
    id_articulo integer auto_increment primary key,
    nombre_articulo varchar(200) not null,
    desc_articulo varchar(256) not null,
    precio decimal(8,2) not null,
    stock integer not null
);

create table foto_articulos(
    id_foto integer auto_increment primary key,
    foto longblob not null,
    id_articulo integer not null
);

create table carritos(
    id_carrito integer auto_increment primary key,
    cantidad integer not null,
    id_articulo integer not null
);

alter table foto_articulos add foreign key(id_articulo) references articulos(id_articulo);
alter table carritos add foreign key(id_articulo) references articulos(id_articulo);
