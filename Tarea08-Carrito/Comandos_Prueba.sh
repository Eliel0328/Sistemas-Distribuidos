mv Servicio.java Servicio/negocio/
cd Servicio/

javac -cp $CATALINA_HOME/lib/javax.ws.rs-api-2.0.1.jar:$CATALINA_HOME/lib/gson-2.3.1.jar:. negocio/Servicio.java
rm WEB-INF/classes/negocio/*
cp negocio/*.class WEB-INF/classes/negocio/.
jar cvf Servicio.war WEB-INF META-INF
rm ../apache-tomcat-8.5.72/webapps/Servicio.war 
rm -r ../apache-tomcat-8.5.72/webapps/Servicio
cp Servicio.war ../apache-tomcat-8.5.72/webapps/
cd ../apache-tomcat-8.5.72/webapps/
ls

export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64
export CATALINA_HOME=/home/ubuntu/apache-tomcat-8.5.72

sh $CATALINA_HOME/bin/catalina.sh start
sh $CATALINA_HOME/bin/catalina.sh stop

mv carrito_Prueba.html apache-tomcat-8.5.72/webapps/ROOT/



select a.id_articulo, a.nombre_articulo, a.desc_articulo, a.precio,a.stock, b.id_articulo from articulos a left outer join foto_articulos b on a.id_articulo = b.id_articulo;
select a.id_articulo, a.nombre_articulo, a.desc_articulo, a.precio, a.stock, b.id_articulo from articulos a LEFT outer join foto_articulos b on a.id_articulo=b.id_articulo where a.nombre_articulo like '%asd%';
select stock from articulos where id_articulo=5;
Select cantidad from carritos where id_articulo=5;

select c.id_carrito, c.cantidad, a.id_articulo, a.nombre_articulo, a.desc_articulo, a.precio, a.stock
from articulos a inner join carritos c 
on c.id_articulo=a.id_articulo; 

--------------------------
select a.id_articulo, a.nombre_articulo, a.desc_articulo, a.precio, a.stock, b.id_articulo, c.id_carrito, c.cantidad
from articulos a, foto_articulos b, carritos c
where a.id_articulo=b.id_articulo
and a.id_articulo=c.id_articulo;


select c.id_carrito, c.cantidad, a.id_articulo, a.nombre_articulo, a.desc_articulo, a.precio, a.stock, b.id_articulo
from articulos a inner join carritos c 
on c.id_articulo=a.id_articulo and on inner join foto_articulos b on a.id_articulo=b.id_articulo;

select c.id_carrito, c.cantidad, a.id_articulo, a.nombre_articulo, a.desc_articulo, a.precio, a.stock, b.id_articulo 
from articulos a
inner join carritos c on c.id_articulo = a.id_articulo
left outer join foto_articulos b on a.id_articulo = b.id_articulo;
