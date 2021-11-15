package ws;

import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import java.util.ArrayList;
import java.util.List;

@WebService
public class ServicioWeb {
    @WebMethod
    public double suma(@WebParam(name = "a") double a, @WebParam(name = "b") double b) {
        return a + b;
    }

    @WebMethod
    public String mayusculas(@WebParam(name = "s") String s) {
        return s.toUpperCase();
    }

    @WebMethod
    public List<Integer> suma2(@WebParam(name = "a") List<Integer> a, @WebParam(name = "b") List<Integer> b) {
        List<Integer> c = new ArrayList<Integer>();
        for (int i = 0; i < a.size(); i++)
            c.add(a.get(i) + b.get(i));
        return c;
    }
}