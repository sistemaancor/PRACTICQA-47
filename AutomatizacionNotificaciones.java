import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// Definición de la interfaz para enviar notificaciones
interface Notificador {
    void enviarNotificacion(String destinatario, String mensaje);
}

// Implementación básica del notificador por email
class EmailNotificador implements Notificador {
    @Override
    public void enviarNotificacion(String destinatario, String mensaje) {
        System.out.println("Enviando correo electrónico a " + destinatario + ":");
        System.out.println(mensaje);
    }
}

// Implementación básica del notificador por fax
class FaxNotificador implements Notificador {
    @Override
    public void enviarNotificacion(String destinatario, String mensaje) {
        System.out.println("Enviando fax a " + destinatario + ":");
        System.out.println(mensaje);
    }
}

// Clase para representar una entidad gubernamental
class EntidadGubernamental {
    private String nombre;
    private Notificador notificador;

    public EntidadGubernamental(String nombre, Notificador notificador) {
        this.nombre = nombre;
        this.notificador = notificador;
    }

    public String getNombre() {
        return nombre;
    }

    public Notificador getNotificador() {
        return notificador;
    }
}

// Clase principal del programa
public class AutomatizacionNotificaciones {

    private static final String CARPETA_CARTAS = "CARPETA_CARTAS/";

    private static List<EntidadGubernamental> entidadesGubernamentales = new ArrayList<>();

    static {
        // Configurar las entidades gubernamentales con su notificador
        entidadesGubernamentales.add(new EntidadGubernamental("Entidad 1", new EmailNotificador()));
        entidadesGubernamentales.add(new EntidadGubernamental("Entidad 2", new FaxNotificador()));
        // Agregar más entidades si es necesario
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Seleccione la entidad gubernamental:");
        for (int i = 0; i < entidadesGubernamentales.size(); i++) {
            System.out.println((i + 1) + ". " + entidadesGubernamentales.get(i).getNombre());
        }
        int opcionEntidad = scanner.nextInt();
        scanner.nextLine(); // Consumir la nueva línea

        String archivo = "Nulidad.txt";
        List<String[]> datosProveedores = leerArchivo(archivo);
        if (datosProveedores != null) {
            double sumaTotal = mostrarSumaTotal(datosProveedores);
            System.out.println("Suma total de nulidades: " + sumaTotal);
            System.out.println("Seleccione el número de nulidades a procesar:");
            int numNulidades = scanner.nextInt();
            procesarNotificaciones(datosProveedores, numNulidades, opcionEntidad);
        } else {
            System.out.println("Error al leer el archivo de datos.");
        }
    }

    public static void procesarNotificaciones(List<String[]> datosProveedores, int numNulidades, int opcionEntidad) {
        EntidadGubernamental entidad = entidadesGubernamentales.get(opcionEntidad - 1);

        for (int i = 0; i < numNulidades && i < datosProveedores.size(); i++) {
            String[] proveedor = datosProveedores.get(i);
            if (proveedor.length < 8) {
                System.out.println("Error: Datos insuficientes para el proveedor.");
                continue;
            }
            String codigoNulidad = proveedor[0];
            String nombreEmpresa = proveedor[1];
            List<String> pagos = new ArrayList<>();
            for (int j = 2; j < proveedor.length; j++) {
                if (esNumeroValido(proveedor[j])) {
                    pagos.add(proveedor[j]);
                }
            }
            // Generar carta de notificación
            String carta = generarCarta(entidad.getNombre(), proveedor[8], codigoNulidad, nombreEmpresa, calcularTotal(pagos), pagos);
            // Archivar la carta en un archivo de texto
            archivarCarta(carta, codigoNulidad);
            // Enviar notificación
            entidad.getNotificador().enviarNotificacion(proveedor[8], carta);
        }
    }

    public static List<String[]> leerArchivo(String archivo) {
        List<String[]> datosProveedores = new ArrayList<>();
        try (BufferedReader lector = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = lector.readLine()) != null) {
                String[] datos = linea.split("\t");
                datosProveedores.add(datos);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return datosProveedores;
    }

    public static double mostrarSumaTotal(List<String[]> datosProveedores) {
        double sumaTotal = 0;
        for (String[] proveedor : datosProveedores) {
            if (proveedor.length >= 8) {
                for (int i = 2; i < proveedor.length; i++) {
                    if (esNumeroValido(proveedor[i])) {
                        sumaTotal += Double.parseDouble(proveedor[i].replace(",", "").trim());
                    }
                }
            }
        }
        return sumaTotal;
    }

    public static boolean esNumeroValido(String str) {
        // Verifica si la cadena es un número válido
        try {
            Double.parseDouble(str.replace(",", "").trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static double calcularTotal(List<String> pagos) {
        double total = 0;
        for (String pago : pagos) {
            try {
                total += Double.parseDouble(pago.replace(",", "").trim());
            } catch (NumberFormatException e) {
                e.printStackTrace();
                return 0; // Si hay un error, devolver 0 como total
            }
        }
        return total;
    }

    public static String generarCarta(String nombreEntidad, String nombreProveedor, String codigoNulidad, String nombreEmpresa, double totalServicios, List<String> listaPagoServicios) {
        // Estructura del mensaje de la carta
        StringBuilder carta = new StringBuilder();
        carta.append("Estimado/a ").append(nombreProveedor).append(",\n\n");
        carta.append("Por medio de la presente, le informamos que hemos procedido a la creación de la nulidad ").append(codigoNulidad).append(". Esta acción corresponde al proceso de compensación económica por los servicios prestados por su compañía ").append(nombreEmpresa).append(", cuyo monto asciende a ").append(totalServicios).append(". A continuación, encontrará el detalle de los pagos correspondientes:\n\n");
        for (String pago : listaPagoServicios) {
            carta.append(pago).append("\n");
        }
        carta.append("\nEs imperativo que nos confirme la recepción de este documento y la aceptación de los términos contenidos en él. Para ello, solicitamos que se comunique con nosotros a través de nuestro correo electrónico [correo_empresa_nulidad] a la mayor brevedad posible.\n\n");
        carta.append("Agradecemos de antemano su colaboración y comprensión.\n\n");
        carta.append("Atentamente,\n\n");
        carta.append("[Nombre del remitente]\n");
        carta.append("[Cargo del remitente]\n");
        carta.append("[Entidad: ").append(nombreEntidad).append("]\n");
        carta.append("[Dirección de la entidad]\n");
        carta.append("[Teléfono de la entidad]\n");
        return carta.toString();
    }

    public static void archivarCarta(String carta, String codigoNulidad) {
        String nombreArchivo = CARPETA_CARTAS + codigoNulidad + ".txt";
        File carpetaCartas = new File(CARPETA_CARTAS);
        if (!carpetaCartas.exists()) {
            if (!carpetaCartas.mkdirs()) {
                System.err.println("Error: No se pudo crear la carpeta de cartas.");
                return;
            }
        }
        try (BufferedWriter escritor = new BufferedWriter(new FileWriter(nombreArchivo))) {
            escritor.write(carta);
            System.out.println("Carta archivada correctamente en: " + nombreArchivo);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}