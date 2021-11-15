package ws;

import java.net.URL;
import javax.xml.namespace.QName;
import ws.ServicioWeb;
import ws.ServicioWebService;
import java.util.ArrayList;
import java.util.List;

public class Cliente {
    public static void main(String[] args) throws Exception {
        ServicioWebService s = new ServicioWebService(new URL("http://localhost:8080/ServicioWeb?wsdl"),
                new QName("http://ws/", "ServicioWebService"));
        ServicioWeb obj = s.getServicioWebPort();
        System.out.println(obj.suma(100, 200));
        System.out.println(obj.mayusculas("hola"));
        List<Integer> a = new ArrayList<Integer>();
        a.add(1);
        a.add(2);
        a.add(3);
        List<Integer> b = new ArrayList<Integer>();
        b.add(4);
        b.add(5);
        b.add(6);
        List<Integer> c = obj.suma2(a, b);
        for (int i = 0; i < c.size(); i++)
            System.out.println(c.get(i));
    }
}