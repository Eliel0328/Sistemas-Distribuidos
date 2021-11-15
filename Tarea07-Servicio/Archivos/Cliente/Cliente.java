package Cliente;

import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.*;

public class Cliente {
    static Scanner myObj = new Scanner(System.in, "IBM850");
    static String URL_BASE = "http://20.120.12.47:8080/Servicio/rest/ws";
    static Gson j = new GsonBuilder().registerTypeAdapter(byte[].class, new AdaptadorGsonBase64())
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").create();

    static String REGEX_CORREO = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
    static String REGEX_FECHA = "\\d{4}-\\d{2}-\\d{2}";

    static boolean validarCorreo(String correo) {
        Pattern p = Pattern.compile(REGEX_CORREO);
        Matcher m = p.matcher(correo);
        return m.find();
    }

    static boolean validarFecha(String fecha) {
        Pattern p = Pattern.compile(REGEX_FECHA);
        Matcher m = p.matcher(fecha);
        return m.find();
    }

    static void altaUsuario() throws Exception {
        try {
            System.out.print("\nEmail: ");
            String email = myObj.nextLine();
            if (!validarCorreo(email)) {
                throw new Exception("Email no valido");
            }

            System.out.print("Nombre: ");
            String nombre = myObj.nextLine();
            if (nombre.length() == 0) {
                throw new Exception("Nombre no valido");
            }

            System.out.print("Apellido paterno: ");
            String apellido_paterno = myObj.nextLine();
            if (apellido_paterno.length() == 0) {
                throw new Exception("Apellido paterno no valido");
            }

            System.out.print("Apellido materno: ");
            String apellido_materno = myObj.nextLine();

            System.out.print("Fecha de nacimiento (AAAA-MM-DD): ");
            String fecha_nacimiento = myObj.nextLine();

            if (!validarFecha(fecha_nacimiento)) {
                throw new Exception("Fecha de nacimiento no valida");
            }

            System.out.print("Telefono: ");
            String telefono = myObj.nextLine();

            System.out.print("Genero (M/F): ");
            String genero = myObj.nextLine();

            if (!genero.equals("M") && !genero.equals("F")) {
                throw new Exception("Genero no valida");
            }

            URL url = new URL(URL_BASE + "/alta_usuario");
            HttpURLConnection conexion = (HttpURLConnection) url.openConnection();

            conexion.setDoOutput(true);
            conexion.setRequestMethod("POST");
            conexion.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            Usuario r = new Usuario();
            r.email = email;
            r.nombre = nombre;
            r.apellido_paterno = apellido_paterno;
            r.apellido_materno = apellido_materno;
            r.fecha_nacimiento = fecha_nacimiento;
            r.telefono = telefono;
            r.genero = genero;
            r.foto = null;

            String parametros = j.toJson(r);
            parametros = "usuario=" + URLEncoder.encode(parametros, "UTF-8");

            OutputStream os = conexion.getOutputStream();
            os.write(parametros.getBytes());
            os.flush();

            if (conexion.getResponseCode() == 200) {
                BufferedReader br = new BufferedReader(new InputStreamReader(conexion.getInputStream()));
                System.out.println("\nID del usuario creado: " + br.readLine());
            } else {
                BufferedReader br = new BufferedReader(new InputStreamReader(conexion.getErrorStream()));
                String respuesta = null;
                while ((respuesta = br.readLine()) != null)
                    System.out.println(respuesta);
            }

            conexion.disconnect();
        } catch (Exception e) {
            System.out.println("Error-Alta usuario: " + e.getMessage());
        }
    }

    static void consultaUsuario() {
        try {
            String id_usuario = myObj.nextLine();

            URL url = new URL(URL_BASE + "/consulta_usuario");
            HttpURLConnection conexion = (HttpURLConnection) url.openConnection();

            conexion.setDoOutput(true);
            conexion.setRequestMethod("POST");
            conexion.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            String parametros = "id_usuario=" + URLEncoder.encode(id_usuario, "UTF-8");

            OutputStream os = conexion.getOutputStream();
            os.write(parametros.getBytes());
            os.flush();

            if (conexion.getResponseCode() == 200) {
                BufferedReader br = new BufferedReader(new InputStreamReader(conexion.getInputStream()));

                String respuesta = null;
                String informacion = "";
                while ((respuesta = br.readLine()) != null)
                    informacion += respuesta;

                Usuario r = Usuario.valueOf(informacion);
                System.out.println("\tUsuario: ");
                System.out.println("\tEmail:                " + r.email);
                System.out.println("\tNombre:               " + r.nombre);
                System.out.println("\tApellido paterno:     " + r.apellido_paterno);
                System.out.println("\tApellido materno:     " + r.apellido_materno);
                System.out.println("\tFecha de nacimiento:  " + r.fecha_nacimiento);
                System.out.println("\tTelefono:             " + r.telefono);
                System.out.println("\tGenero:               " + r.genero);

                System.out.print("¿Desea modificar los datos del usuario (s/n)? ");
                String op = myObj.nextLine();

                op = op.toUpperCase();
                if (op.equals("S") || op.equals("SI")) {
                    System.out.println("Dejar el campo vacio si se desea conservar el valor actual");
                    modificaUsuario(r);
                }

            } else {
                BufferedReader br = new BufferedReader(new InputStreamReader(conexion.getErrorStream()));
                String respuesta = null;
                while ((respuesta = br.readLine()) != null)
                    System.out.println(respuesta);
            }

            conexion.disconnect();
        } catch (Exception e) {
            System.out.println("Error-Consulta usuario: " + e.getMessage());
        }
    }

    static void modificaUsuario(Usuario actual) {
        try {
            Usuario aux = new Usuario();
            aux.id_usuario = actual.id_usuario;

            System.out.print("\nEmail: ");
            String email = myObj.nextLine();
            if (email.length() == 0) {
                aux.email = actual.email;
            } else if (!validarCorreo(email)) {
                throw new Exception("Email no valido");
            } else {
                aux.email = email;
            }

            System.out.print("Nombre: ");
            String nombre = myObj.nextLine();
            if (nombre.length() == 0) {
                aux.nombre = actual.nombre;
            } else {
                aux.nombre = nombre;
            }

            System.out.print("Apellido paterno: ");
            String apellido_paterno = myObj.nextLine();
            if (apellido_paterno.length() == 0) {
                aux.apellido_paterno = actual.apellido_paterno;
            } else {
                aux.apellido_paterno = apellido_paterno;
            }

            System.out.print("Apellido materno: ");
            String apellido_materno = myObj.nextLine();
            if (apellido_materno.length() == 0) {
                aux.apellido_materno = actual.apellido_materno;
            } else {
                aux.apellido_materno = apellido_materno;
            }

            System.out.print("Fecha de nacimiento (AAAA-MM-DD): ");
            String fecha_nacimiento = myObj.nextLine();
            if (fecha_nacimiento.length() == 0) {
                aux.fecha_nacimiento = actual.fecha_nacimiento;
            } else if (!validarFecha(fecha_nacimiento)) {
                throw new Exception("Fecha de nacimiento no valida");
            } else {
                aux.fecha_nacimiento = fecha_nacimiento;
            }

            System.out.print("Telefono: ");
            String telefono = myObj.nextLine();
            if (telefono.length() == 0) {
                aux.telefono = actual.telefono;
            } else {
                aux.telefono = telefono;
            }

            System.out.print("Genero (M/F): ");
            String genero = myObj.nextLine();
            if (genero.length() == 0) {
                aux.genero = actual.genero;
            } else if (!genero.equals("M") && !genero.equals("F")) {
                throw new Exception("Genero no valida");
            } else {
                aux.genero = genero;
            }

            URL url = new URL(URL_BASE + "/modifica_usuario");
            HttpURLConnection conexion = (HttpURLConnection) url.openConnection();

            conexion.setDoOutput(true);
            conexion.setRequestMethod("POST");
            conexion.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            String usuario = j.toJson(aux);
            String parametros = "usuario=" + URLEncoder.encode(usuario, "UTF-8");

            OutputStream os = conexion.getOutputStream();
            os.write(parametros.getBytes());
            os.flush();

            int aux1 = conexion.getResponseCode();
            System.out.println(aux1);
            if (aux1 == 200) {
                System.out.println("\nEl Usuario se ha modificado ");
            } else {
                BufferedReader br = new BufferedReader(new InputStreamReader(conexion.getErrorStream()));
                String respuesta = null;
                while ((respuesta = br.readLine()) != null)
                    System.out.println(respuesta);
            }

            conexion.disconnect();
        } catch (Exception e) {
            System.out.println("Error-Modifica usuario: " + e.getMessage());
        }
    }

    static void borrarUsuario() {
        try {
            String id_usuario = myObj.nextLine();

            URL url = new URL(URL_BASE + "/borra_usuario");
            HttpURLConnection conexion = (HttpURLConnection) url.openConnection();

            conexion.setDoOutput(true);
            conexion.setRequestMethod("POST");
            conexion.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            String parametros = "id_usuario=" + URLEncoder.encode(id_usuario, "UTF-8");

            OutputStream os = conexion.getOutputStream();
            os.write(parametros.getBytes());
            os.flush();

            if (conexion.getResponseCode() == 200) {
                System.out.println("\nEl usuario ha sido borrado");
            } else {
                BufferedReader br = new BufferedReader(new InputStreamReader(conexion.getErrorStream()));
                String respuesta = null;
                while ((respuesta = br.readLine()) != null)
                    System.out.println(respuesta);
            }

            conexion.disconnect();
        } catch (Exception e) {
            System.out.println("Error al borrar usuario");
        }
    }

    static boolean menu() {
        try {
            System.out.println("===========================");
            System.out.println("        Menu");
            System.out.println("===========================\n");

            System.out.println("Selecciona una opcion");
            System.out.println("a. Alta usuario");
            System.out.println("b. Consulta usuario");
            System.out.println("c. Borrar usuario");
            System.out.println("d. Salir");
            System.out.println("Opcion: ");
            String opcion = myObj.nextLine();
            System.out.println("\n===========================");

            if (opcion.equals("a") || opcion.equals("A")) {
                System.out.println("Alta Usuario");
                System.out.print("Ingrese los datos solicitados");
                altaUsuario();

            } else if (opcion.equals("b") || opcion.equals("B")) {
                System.out.println("Consulta usuario");
                System.out.print("Ingrese el id del usuario: ");
                consultaUsuario();

            } else if (opcion.equals("c") || opcion.equals("C")) {
                System.out.println("Eliminar Usuario");
                System.out.print("Ingrese el id del usuario: ");
                borrarUsuario();

            } else if (opcion.equals("d") || opcion.equals("D")) {
                System.out.println("Salir");
                return false;
            } else {
                System.out.println("Error-Ingresar de nuevo");
            }
        } catch (Exception e) {
            System.out.println("Error: Ingresar opcion");
        }
        return true;
    }

    public static void main(String[] args) {
        while (menu());
    }

}
