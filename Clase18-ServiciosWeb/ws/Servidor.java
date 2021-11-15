package ws;

import javax.xml.ws.Endpoint;

public class Servidor {
    public static void main(String[] args) {
        Endpoint.publish("http://localhost:8080/ServicioWeb", new ServicioWeb());
    }
}
