/*
  Servicio.java
  Servicio web tipo REST
  Carlos Pineda Guerrero, Octubre 2021
*/

package negocio;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.QueryParam;
import javax.ws.rs.FormParam;
import javax.ws.rs.core.Response;

import java.sql.*;
import javax.sql.DataSource;
import javax.naming.Context;
import javax.naming.InitialContext;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.*;

// la URL del servicio web es http://localhost:8080/Servicio/rest/ws
// donde:
//	"Servicio" es el dominio del servicio web (es decir, el nombre de archivo Servicio.war)
//	"rest" se define en la etiqueta <url-pattern> de <servlet-mapping> en el archivo WEB-INF\web.xml
//	"ws" se define en la siguiente anotacin @Path de la clase Servicio

@Path("ws")
public class Servicio {
    static DataSource pool = null;
    static {
        try {
            Context ctx = new InitialContext();
            pool = (DataSource) ctx.lookup("java:comp/env/jdbc/datasource_Servicio");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static Gson j = new GsonBuilder().registerTypeAdapter(byte[].class, new AdaptadorGsonBase64())
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").create();

    @POST
    @Path("captura_articulo")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response alta(@FormParam("articulo") Articulo articulo) throws Exception {
        Connection conexion = pool.getConnection();

        if (articulo.nombre_articulo == null || articulo.nombre_articulo.equals(""))
            return Response.status(400).entity(j.toJson(new Error("Se debe ingresar un nombre de articulo"))).build();

        if (articulo.desc_articulo == null || articulo.desc_articulo.equals(""))
            return Response.status(400).entity(j.toJson(new Error("Se debe ingresar la descripcion del articulo")))
                    .build();

        if (articulo.precio <= 0.0f)
            return Response.status(400).entity(j.toJson(new Error("Ingresar un precio real"))).build();

        if (articulo.cantidad <= 0)
            return Response.status(400).entity(j.toJson(new Error("Ingresar una cantidad valida"))).build();

        if (articulo.foto == null)
            return Response.status(400).entity(j.toJson(new Error("Se debe ingresar una fotografia del articulo")))
                    .build();

        try {
            PreparedStatement stmt_1 = conexion
                    .prepareStatement("Select id_articulo from articulos where desc_articulo=?");
            try {
                stmt_1.setString(1, articulo.desc_articulo);
                ResultSet rs = stmt_1.executeQuery();

                try {
                    if (rs.next())
                        return Response.status(400).entity(j.toJson(new Error("El articulo ya existe"))).build();
                } finally {
                    rs.close();
                }
            } finally {
                stmt_1.close();
            }

            PreparedStatement stmt_2 = conexion.prepareStatement("Insert into articulos values(0, ?, ?, ?, ?)");
            try {
                stmt_2.setString(1, articulo.nombre_articulo);
                stmt_2.setString(2, articulo.desc_articulo);
                stmt_2.setFloat(3, articulo.precio);
                stmt_2.setInt(4, articulo.cantidad);
                stmt_2.executeUpdate();
            } finally {
                stmt_2.close();
            }

            PreparedStatement stmt_4 = conexion
                    .prepareStatement("select * from articulos order by id_articulo desc limit 1");
            try {
                ResultSet rs = stmt_4.executeQuery();
                try {
                    if (rs.next()) {
                        Articulo a = new Articulo();
                        a.id_articulo = rs.getInt(1);

                        if (articulo.foto != null) {
                            PreparedStatement stmt_3 = conexion.prepareStatement(
                                    "INSERT INTO foto_articulos VALUES (0,?,(SELECT id_articulo FROM articulos WHERE id_articulo=?))");
                            try {
                                stmt_3.setBytes(1, articulo.foto);
                                stmt_3.setInt(2, a.id_articulo);
                                stmt_3.executeUpdate();
                            } finally {
                                stmt_3.close();
                            }
                        }

                        return Response.ok().entity(j.toJson(a.id_articulo)).build();
                    }
                    return Response.status(400).entity(j.toJson(new Error("El ID del articulo no existe"))).build();
                } finally {
                    rs.close();
                }
            } finally {
                stmt_4.close();
            }

        } catch (Exception e) {
            return Response.status(400).entity(j.toJson(new Error(e.getMessage()))).build();
        } finally {
            conexion.close();
        }
    }

    @POST
    @Path("articulos")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response consultaArticulos(@FormParam("articulo") Articulo articulo) throws Exception {
        Connection conexion = pool.getConnection();
        boolean dataAvailable = false;
        ArrayList<Articulo> articulos = new ArrayList<Articulo>();

        try {
            PreparedStatement stmt_1 = conexion.prepareStatement(
                    "select a.id_articulo, a.nombre_articulo, a.desc_articulo, a.precio, a.stock, b.foto from articulos a left outer join foto_articulos b on a.id_articulo = b.id_articulo");

            try {
                ResultSet rs = stmt_1.executeQuery();
                try {
                    while (rs.next()) {
                        dataAvailable = true;
                        Articulo a = new Articulo();
                        a.id_articulo = rs.getInt(1);
                        a.nombre_articulo = rs.getString(2);
                        a.desc_articulo = rs.getString(3);
                        a.precio = rs.getFloat(4);
                        a.cantidad = rs.getInt(5);
                        a.foto = rs.getBytes(6);
                        articulos.add(a);
                    }
                    if (dataAvailable) {
                        return Response.ok().entity(j.toJson(articulos)).build();
                    } else {
                        return Response.status(400).entity(j.toJson(new Error("No articulos disponibles"))).build();
                    }

                } finally {
                    rs.close();
                }
            } finally {
                stmt_1.close();
            }
        } catch (Exception e) {
            return Response.status(400).entity(j.toJson(new Error(e.getMessage()))).build();
        } finally {
            conexion.close();
        }
    }

    @POST
    @Path("buscar_articulo")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response compra_articulos(@FormParam("nombre_articulo") String nombre_articulo) throws Exception {
        Connection conexion = pool.getConnection();
        boolean dataAvailable = false;
        ArrayList<Articulo> articulos = new ArrayList<>();

        try {
            PreparedStatement stmt_1 = conexion.prepareStatement(
                    "select a.id_articulo, a.nombre_articulo, a.desc_articulo, a.precio, a.stock, b.foto from articulos a LEFT outer join foto_articulos b on a.id_articulo=b.id_articulo where a.nombre_articulo like ?");

            try {
                stmt_1.setString(1, "%" + nombre_articulo + "%");

                ResultSet rs = stmt_1.executeQuery();
                try {
                    while (rs.next()) {
                        dataAvailable = true;
                        Articulo a = new Articulo();
                        a.id_articulo = rs.getInt(1);
                        a.nombre_articulo = rs.getString(2);
                        a.desc_articulo = rs.getString(3);
                        a.precio = rs.getFloat(4);
                        a.cantidad = rs.getInt(5);
                        a.foto = rs.getBytes(6);
                        articulos.add(a);
                    }

                    if (dataAvailable) {
                        return Response.ok().entity(j.toJson(articulos)).build();
                    } else {
                        return Response.status(400).entity(j.toJson(new Error("El Articulo no existe"))).build();
                    }

                } finally {
                    rs.close();
                }
            } finally {
                stmt_1.close();
            }
        } catch (Exception e) {
            return Response.status(400).entity(j.toJson(new Error(e.getMessage()))).build();
        } finally {
            conexion.close();
        }
    }

    @POST
    @Path("compra")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response compra(@FormParam("carrito") Carrito carrito) throws Exception {
        Connection conexion = pool.getConnection();

        if (carrito.cantidad <= 0)
            return Response.status(400).entity(j.toJson(new Error("Ingresar una cantidad valida"))).build();

        try {
            PreparedStatement stmt_1 = conexion.prepareStatement("select stock from articulos where id_articulo=?");

            try {
                stmt_1.setInt(1, carrito.id_articulo);

                ResultSet rs = stmt_1.executeQuery();
                try {
                    if (rs.next()) {
                        Articulo a = new Articulo();
                        a.cantidad = rs.getInt(1);

                        if (a.cantidad >= carrito.cantidad) {
                            PreparedStatement stmt_2 = conexion
                                    .prepareStatement("Update articulos set stock=? where id_articulo=?");
                            try {
                                stmt_2.setInt(1, a.cantidad - carrito.cantidad);
                                stmt_2.setInt(2, carrito.id_articulo);
                                stmt_2.executeUpdate();

                                PreparedStatement stmt_3 = conexion
                                        .prepareStatement("Select cantidad from carritos where id_articulo=?");

                                        
                                stmt_3.setInt(1, carrito.id_articulo);
                                ResultSet rsb = stmt_3.executeQuery();
                                try {
                                    if (rsb.next()) {
                                        Articulo b = new Articulo();
                                        b.cantidad = rsb.getInt(1);

                                        PreparedStatement stmt_4 = conexion
                                                .prepareStatement("Update carritos set cantidad=? where id_articulo=?");
                                        try {
                                            stmt_4.setInt(1, b.cantidad + carrito.cantidad);
                                            stmt_4.setInt(2, carrito.id_articulo);
                                            stmt_4.executeUpdate();
                                        } finally {
                                            stmt_4.close();
                                        }
                                    } else {
                                        PreparedStatement stmt_4 = conexion
                                                .prepareStatement("Insert into carritos values (0, ?, ?)");
                                        try {
                                            stmt_4.setInt(1, carrito.cantidad);
                                            stmt_4.setInt(2, carrito.id_articulo);
                                            stmt_4.executeUpdate();
                                        } finally {
                                            stmt_4.close();
                                        }
                                    }
                                } finally {
                                    stmt_3.close();
                                }
                            } finally {
                                stmt_2.close();
                            }
                        } else {
                            return Response.status(400).entity(j.toJson(new Error("El Articulo insuficiente"))).build();
                        }
                    } else {
                        return Response.status(400).entity(j.toJson(new Error("El Articulo no existe"))).build();
                    }
                } finally {
                    rs.close();
                }
            } finally {
                stmt_1.close();
            }
        } catch (Exception e) {
            return Response.status(400).entity(j.toJson(new Error(e.getMessage()))).build();
        } finally {
            conexion.close();
        }
        return Response.ok().build();
    }

    @POST
    @Path("carrito")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response carrito(@FormParam("articulo") Articulo articulo) throws Exception {
        Connection conexion = pool.getConnection();
        boolean dataAvailable = false;
        ArrayList<Carrito> carritos = new ArrayList<>();

        try {
            PreparedStatement stmt_1 = conexion.prepareStatement(
                    "select c.id_carrito, c.cantidad, a.id_articulo, a.nombre_articulo, a.desc_articulo, a.precio, a.stock, b.foto from articulos a inner join carritos c on c.id_articulo = a.id_articulo left outer join foto_articulos b on a.id_articulo = b.id_articulo");
            try {
                ResultSet rs = stmt_1.executeQuery();
                try {
                    while (rs.next()) {
                        dataAvailable = true;
                        Carrito a = new Carrito();
                        a.id_carrito = rs.getInt(1);
                        a.cantidad = rs.getInt(2);
                        a.id_articulo = rs.getInt(3);
                        a.nombre_articulo = rs.getString(4);
                        a.desc_articulo = rs.getString(5);
                        a.precio = rs.getFloat(6);
                        a.stock = rs.getInt(7);
                        a.foto = rs.getBytes(8);
                        carritos.add(a);
                    }
                    if (dataAvailable) {
                        return Response.ok().entity(j.toJson(carritos)).build();
                    } else {
                        return Response.status(400).entity(j.toJson(new Error("El Articulo no existe"))).build();
                    }

                } finally {
                    rs.close();
                }
            } finally {
                stmt_1.close();
            }
        } catch (Exception e) {
            return Response.status(400).entity(j.toJson(new Error(e.getMessage()))).build();
        } finally {
            conexion.close();
        }
    }
}
