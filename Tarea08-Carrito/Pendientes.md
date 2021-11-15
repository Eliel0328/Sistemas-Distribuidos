Requerimientos funcionales

1. El sistema desplegará inicialmente un menú donde se podrá seleccionar las siguientes opciones: Captura de artículo y Compra de artículos. 
## Listo

2. Al seleccionar la opción "Captura de artículo" el sistema desplegará una pantalla que permitirá capturar la descripción del artículo, el precio, la cantidad en almacén y la fotografía del artículo. Los datos de los artículos se guardarán en una tabla llamada "articulos". Cada artículo tendrá un ID auto-incremental.
## Listo


3. Al seleccionar la opción "Compra de artículos" el sistema desplegará una pantalla que permitirá al usuario buscar artículos ingresando una palabra la cual se buscará en el campo "descripcion" de la tabla "articulos". La búsqueda se realizará utilizando la cláusula LIKE de SQL.
## Listo


4. Los datos de los artículos (fotografía, descripción y precio) que resulten de una búsqueda se desplegarán en la pantalla "Compra de artículos".
## Listo


5. Para cada artículo resultado de la búsqueda, se desplegará un botón de "Compra" y un campo de "Cantidad" con un valor default igual a 1.
## Listo

6. Cuando el usuario presione el botón de "Compra", si la cantidad de artículos a comprar es menor o igual a la cantidad de artículos en la tabla "articulos", se escribirá en una tabla llamada "carrito_compra" el ID del artículo y la cantidad, así mismo se restará la cantidad solicitada de la cantidad en la tabla de "artículos", de otra manera se desplegará un mensaje indicando al usuario el número de artículos disponibles. La escritura a la tabla "carrito_compra" y la actualización de la tabla "artículos" se deberán realizar dentro de una transacción.
## Listo


7. En la pantalla de "Compra de artículos" se dispondrá de un botón "Carrito de compra" el cual desplegará una pantalla con la lista de artículos en la tabla "carrito_compra", incluyendo una pequeña foto del artículo, descripción del artículo, cantidad, precio y costo (cantidad x precio).
## Listo


8. La pantalla de carrito de compra tendrá un botón que permitirá regresar a la pantalla "Compra de artículos".
## Listo


Requerimientos no funcionales

1. El back-end consistirá en un servicio web sobre Tomcat.

2. El back-end ejecutará en una máquina virtual con Ubuntu en Azure.

3. El DBMS será MySQL el cual ejecutará en la misma máquina virtual donde ejecutará Tomcat.

4. El front-end podrá ser desarrollado en cualquier lenguaje que permita ejecutarlo en un dispositivo móvil. Se recomienda utilizar HTML-Javascript.

