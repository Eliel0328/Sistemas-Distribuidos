import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Pruebas {
    private static String[] hosts;
    private static Integer[] ports;
    private static int nodo;
    private static int num_nodos;

    private static int coordinador_actual;

    static class Worker extends Thread {
        Socket Conexion;

        Worker(Socket Conexion) {
            this.Conexion = Conexion;
        }

        public void run() {
            try {
                // Buffers
                DataInputStream entrada = new DataInputStream(Conexion.getInputStream());
                DataOutputStream salida = new DataOutputStream(Conexion.getOutputStream());
                // ** Cuerpo del programa ** //
                String mensaje = entrada.readUTF();
                if (mensaje.equals("Eleccion")) {
                    salida.writeUTF("Ok");
                    eleccion(nodo);
                }
                if (mensaje.equals("Coordinador")) {
                    coordinador_actual = entrada.readInt();
                    System.out.println("Nodo coordinador recibido :" + coordinador_actual);
                }
                // close buffer and conection
                entrada.close();
                Conexion.close();
            } catch (IOException ex) {
            } catch (Exception e) {
            }
        }

    }

    static class Servidor extends Thread {
        public void run() {
            System.out.println("Servidor iniciado");
            ServerSocket servidor;
            try {
                servidor = new ServerSocket(ports[nodo]);
                for (;;) {
                    Socket cliente = servidor.accept();
                    Worker x = new Worker(cliente);
                    x.start();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        if (args.length < 2) {
            System.err.print("Uso:");
            System.err.print("java ExclusionMutua <nodo> <ip1:puerto1> <ip2:puerto2>  ... <ipN:puertoN>");
            System.exit(0);
        }
        num_nodos = (args.length - 1);
        nodo = Integer.parseInt(args[0]);
        System.out.println(nodo);
        hosts = new String[args.length];
        ports = new Integer[args.length];
        for (int i = 1; i < args.length; i++) {
            hosts[i - 1] = args[i].split(":")[0];
            ports[i - 1] = Integer.parseInt(args[i].split(":")[1]);
        }
        Servidor s = new Servidor();
        s.start();

        if (revisaConexiones()) {
            System.out.println("Barrera superada");
            Thread.sleep(3000);
            if (nodo == 7) {
                System.exit(0);
            }
            if (nodo == 4) {
                eleccion(nodo);
            }
            Thread.sleep(5000);
            s.join();
        }
        System.out.println("Coordinador:" + coordinador_actual);

    }

    private static boolean revisaConexiones() {
        for (int i = 0; i < num_nodos; i++) {
            envia_mensaje_Barrera(hosts[i], ports[i]);
        }
        return true;
    }

    private static void envia_mensaje_Barrera(String host, Integer port) {
        Socket cliente = null;
        for (;;) {
            try {
                cliente = new Socket(host, port);
                System.out.println("Conecte con " + host + port);
                try {
                    DataOutputStream salida = new DataOutputStream(cliente.getOutputStream());
                    salida.writeUTF("Barrera");
                } catch (Exception e) {
                    System.out.println(e);
                } finally {
                    cliente.close();
                }
                break;
            } catch (IOException e1) {
            }
        }
    }

    private static void envia_mensaje_coordinador(String host, int port) {
        Socket cliente = null;
        try {
            cliente = new Socket(host, port);
            try {
                DataOutputStream salida = new DataOutputStream(cliente.getOutputStream());
                salida.writeUTF("Coordinador");
                salida.writeInt(nodo);
                salida.close();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                cliente.close();
            }
        } catch (IOException e1) {
        }
    }

    private static String envia_mensaje_eleccion(String host, int port) {
        Socket cliente = null;
        String recibida = "";
        try {
            cliente = new Socket(host, port);
            try {
                DataInputStream entrada = new DataInputStream(cliente.getInputStream());
                DataOutputStream salida = new DataOutputStream(cliente.getOutputStream());
                salida.writeUTF("Eleccion");
                recibida = entrada.readUTF();
                entrada.close();
                salida.close();
            } catch (Exception e) {
                return recibida;
            } finally {
                cliente.close();
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return recibida;
    }

    private static void eleccion(int nodo) {
        ArrayList<Integer> arrayEnteros = new ArrayList<Integer>();
        for (int i = nodo + 1; i < num_nodos; i++) {
            String mensaje = envia_mensaje_eleccion(hosts[i], ports[i]);
            if (mensaje.equals("Ok")) {
                arrayEnteros.add(i);
            }
        }
        if (arrayEnteros.isEmpty()) {
            for (int i = 0; i < nodo; i++) {
                envia_mensaje_coordinador(hosts[i], ports[i]);
            }
        }
    }

}

