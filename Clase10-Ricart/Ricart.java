import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

enum Estado {
    normal, esperandoRecurso, adquirioRecurso;
}

public class Ricart {
    static String[] hosts;
    static int[] puertos;
    static int num_nodos = 0;
    static int nodo = 0;

    static long reloj_logico = 0;
    static Object lock = new Object();

    static LinkedList<Integer> cola = new LinkedList<Integer>();

    static int num_ok_recibidos = 0;
    static long tiempo_logico_enviado = 0;

    static Estado estado = Estado.normal;

    static void envia_mensaje(String host, int puerto) throws Exception {
        Socket conexion = null;
        for (;;) {
            try {
                conexion = new Socket(host, puerto);
                break;
            } catch (Exception e) {
                // System.out.println("envia_mensaje-Error: " + e.getMessage());
                Thread.sleep(1000);
            }
        }
        System.out.println("Barrera - Conexion al: " + host + ":" + puerto);
        DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());
        salida.writeUTF("HELLO");
        conexion.close();
    }

    static void envia_peticion(int id_recurso, int nodo, long tiempo_logico, String host, int puerto) {
        try {
            Socket conexion = new Socket(host, puerto);

            DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());
            salida.writeUTF("REQUEST");
            salida.writeInt(id_recurso);
            salida.writeInt(nodo);
            salida.writeLong(tiempo_logico);

            conexion.close();
        } catch (Exception e) {
            System.out.println("envia_peticion-Error: " + e.getMessage());
        }
    }

    static void envia_ok(long tiempo_logico, String host, int puerto) {
        try {
            Socket conexion = new Socket(host, puerto);

            DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());
            salida.writeUTF("OK");
            salida.writeLong(tiempo_logico);

            conexion.close();
        } catch (Exception e) {
            System.out.println("envia_ok-Error: " + e.getMessage());
        }
    }

    static void bloquea() throws Exception {
        System.out.println("Bloquea Recurso");
        estado = Estado.esperandoRecurso;

        num_ok_recibidos = 0;

        synchronized (lock) {
            tiempo_logico_enviado = reloj_logico;
        }

        for (int i = 0; i < num_nodos; ++i) {
            if (i != nodo)
                envia_peticion(1, nodo, tiempo_logico_enviado, hosts[i], puertos[i]);
        }
    }

    static void desbloquea() throws Exception {
        System.out.println("Desbleoquea Recurso");
        estado = Estado.normal;

        while (cola.size() > 0) {
            int nodo = cola.removeFirst();
            synchronized (lock) {
                envia_ok(reloj_logico, hosts[nodo], puertos[nodo]);
            }
        }
    }

    static void ajusta_reloj(long tiempo_recibido) {
        if (tiempo_recibido > 0) {
            synchronized (lock) {
                if (tiempo_recibido >= reloj_logico) {
                    reloj_logico = tiempo_recibido + 1;
                }
            }
        }
    }

    static class Reloj extends Thread {
        public void run() {
            try {
                for (;;) {
                    synchronized (lock) {
                        System.out.println("Reloj logico: " + reloj_logico);
                        if (nodo == 0)
                            reloj_logico += 4;
                        else if (nodo == 1)
                            reloj_logico += 5;
                        else if (nodo == 2)
                            reloj_logico += 6;
                    }
                    Thread.sleep(10000);
                }
            } catch (Exception e) {
                System.out.println("Reloj-Error:" + e.getMessage());
            }
        }
    }

    static class Worker extends Thread {
        Socket conexion;

        Worker(Socket conexion) {
            this.conexion = conexion;
        }

        public void run() {
            System.out.println("----Worker----");
            try {
                DataInputStream entrada = new DataInputStream(conexion.getInputStream());
                String comando = entrada.readUTF();
                System.out.println("Comando: " + comando);

                if (!comando.equals("HELLO")) {
                    if (comando.equals("REQUEST")) {
                        int id_recurso = entrada.readInt();
                        int nodo_recibido = entrada.readInt();
                        long tiempo_recibido = entrada.readLong();

                        System.out.println("ID recurso: " + id_recurso);
                        System.out.println("Nodo recibido: " + nodo_recibido);
                        System.out.println("Tiempo recibido: " + tiempo_recibido);
                        synchronized (lock) {
                            System.out.println("Reloj logico: " + reloj_logico);
                        }
                        System.out.println("Estado: " + estado);

                        ajusta_reloj(tiempo_recibido);

                        if (estado == Estado.normal) {
                            synchronized (lock) {
                                envia_ok(reloj_logico, hosts[nodo_recibido], puertos[nodo_recibido]);
                            }
                        } else if (estado == Estado.adquirioRecurso) {
                            cola.add(nodo_recibido);

                        } else if (estado == Estado.esperandoRecurso) {
                            if (tiempo_logico_enviado < tiempo_recibido) {
                                cola.add(nodo_recibido);

                            } else if (tiempo_logico_enviado > tiempo_recibido) {
                                synchronized (lock) {
                                    envia_ok(reloj_logico, hosts[nodo_recibido], puertos[nodo_recibido]);
                                }

                            } else {
                                if (nodo < nodo_recibido) {
                                    cola.add(nodo_recibido);
                                } else {
                                    synchronized (lock) {
                                        envia_ok(reloj_logico, hosts[nodo_recibido], puertos[nodo_recibido]);
                                    }
                                }
                            }
                        }
                    } else if (comando.equals("OK")) {
                        ++num_ok_recibidos;
                        long tiempo_recibido = entrada.readLong();

                        ajusta_reloj(tiempo_recibido);

                        if (num_ok_recibidos == num_nodos - 1) {
                            System.out.println("Adquirio el recurso");
                            estado = Estado.adquirioRecurso;
                        }
                    }
                }

            } catch (Exception e) {
                System.out.println("Worker-Error:" + e.getMessage());
            }
        }
    }

    static class Servidor extends Thread {
        public void run() {
            Socket conexion = null;

            try {
                ServerSocket servidor = new ServerSocket(puertos[nodo]);
                for (;;) {
                    conexion = servidor.accept();
                    Worker w = new Worker(conexion);
                    w.start();
                }
            } catch (Exception e) {
                System.out.println("Servidor-Error:" + e.getMessage());
            }
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.out.println("Ingrese los parametros[ID_nodo, IP-nodos(separados por :)]");
            System.exit(0);
        }

        nodo = Integer.parseInt(args[0]);
        num_nodos = args.length - 1;

        hosts = new String[num_nodos];
        puertos = new int[num_nodos];

        for (int i = 0; i < num_nodos; i++) {
            hosts[i] = args[i + 1].split(":")[0];
            puertos[i] = Integer.parseInt(args[i + 1].split(":")[1]);
        }

        Servidor servidor = new Servidor();
        servidor.start();

        for (int i = 0; i < num_nodos; i++) {
            if (i != nodo)
                envia_mensaje(hosts[i], puertos[i]);
        }

        Reloj reloj = new Reloj();
        reloj.start();

        Thread.sleep(1000);
        bloquea();

        while (estado != Estado.adquirioRecurso)
            Thread.sleep(100);

        Thread.sleep(3000);
        desbloquea();

        servidor.join();
    }

}

// 0 localhost:50000 localhost:50001 localhost:50002
// 1 localhost:50000 localhost:50001 localhost:50002
// 2 localhost:50000 localhost:50001 localhost:50002
