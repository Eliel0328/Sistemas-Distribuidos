import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

class Prueba {

    public static void main(String[] args) throws Exception {
        URL url = new URL("http://sisdis.sytes.net:8080/Servicio/rest/ws/prueba");

        HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
        conexion.setDoOutput(true);
        conexion.setRequestMethod("POST");
        conexion.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        String parametros = "a=" + URLEncoder.encode("60", "UTF-8");

        // String parametros = "a=" + URLEncoder.encode("24", "UTF-8");
        // parametros += "&b=" + URLEncoder.encode("23", "UTF-8");
        // parametros += "&c=" + URLEncoder.encode("33", "UTF-8");
        // parametros += "&d=" + URLEncoder.encode("27", "UTF-8");

        // String parametros = "p1=" + URLEncoder.encode("62.5663", "UTF-8");
        // parametros += "&p2=" + URLEncoder.encode("92.699", "UTF-8");
        // parametros += "&p3=" + URLEncoder.encode("53.1124", "UTF-8");
        // parametros += "&p4=" + URLEncoder.encode("95.4542", "UTF-8");

        // String parametros = "a=" + URLEncoder.encode("b+(c&d)*(b=d)%c", "UTF-8");
        // parametros += "&b=" + URLEncoder.encode("79", "UTF-8");
        // parametros += "&c=" + URLEncoder.encode("98", "UTF-8");
        // parametros += "&d=" + URLEncoder.encode("84", "UTF-8");

        OutputStream os = conexion.getOutputStream();

        os.write(parametros.getBytes());
        os.flush();

        if (conexion.getResponseCode() == 200) {

            BufferedReader br = new BufferedReader(new InputStreamReader((conexion.getInputStream())));

            String respuesta;

            while ((respuesta = br.readLine()) != null)
                System.out.println(respuesta);

        } else {
            BufferedReader br = new BufferedReader(new InputStreamReader((conexion.getErrorStream())));
            String respuesta;
            while ((respuesta = br.readLine()) != null)
                System.out.println(respuesta);
        }
        conexion.disconnect();
    }
}