package negocio;

import com.google.gson.*;

public class Carrito {
    int id_carrito;
    int cantidad;
    int id_articulo;
    String nombre_articulo;
    String desc_articulo;
    float precio;
    int stock;
    byte[] foto;

    // @FormParam necesita un metodo que convierta una String al objeto de tipo
    // Carrito
    public static Carrito valueOf(String s) throws Exception {
        Gson j = new GsonBuilder().registerTypeAdapter(byte[].class, new AdaptadorGsonBase64()).create();
        return (Carrito) j.fromJson(s, Carrito.class);
    }
}
