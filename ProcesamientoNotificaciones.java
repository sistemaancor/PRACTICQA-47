import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

interface GeneradorCarta {
    String generarCarta(String nombreProveedor, String numeroNulidad, String nombreEmpresa, double totalServicios, List<String> listaPagoServicios);
}

class CartaNotificacionAnulacionTipo1 implements GeneradorCarta {
    @Override
    public String generarCarta(String nombreProveedor, String numeroNulidad, String nombreEmpresa, double totalServicios, List<String> listaPagoServicios) {
        StringBuilder carta = new StringBuilder();
        carta.append("¡Hola ").append(nombreProveedor).append("!\n\n");
        carta.append("¡Te escribo para contarte que ya hemos preparado la nulidad ").append(numeroNulidad).append(" para cuadrar lo del pago de los servicios de tu empresa ").append(nombreEmpresa).append("! El total a pagar es de ").append(totalServicios).append(", y aquí te dejo el desglose:\n\n");
        for (String pago : listaPagoServicios) {
            carta.append(pago).append("\n");
        }
        carta.append("\nCuando puedas, échale un vistazo y confírmame que todo está como debe ser. Solo necesitas mandarme un correo a [correo_empresa_nulidad] diciendo que todo está ok.\n\n");
        carta.append("¡Gracias y un saludo!\n\n");
        carta.append("[Juan Fernandez]");
        return carta.toString();
    }
}

class CartaNotificacionAnulacionTipo2 implements GeneradorCarta {
    @Override
    public String generarCarta(String nombreProveedor, String numeroNulidad, String nombreEmpresa, double totalServicios, List<String> listaPagoServicios) {
        StringBuilder carta = new StringBuilder();
        carta.append("Estimado/a ").append(nombreProveedor).append(",\n\n");
        carta.append("Por medio de la presente, le informamos que hemos procedido a la creación de la nulidad ").append(numeroNulidad).append(". Esta acción corresponde al proceso de compensación económica por los servicios prestados por su compañía ").append(nombreEmpresa).append(", cuyo monto asciende a ").append(totalServicios).append(". A continuación, encontrará el detalle de los pagos correspondientes:\n\n");
        for (String pago : listaPagoServicios) {
            carta.append(pago).append("\n");
        }
        carta.append("\nEs imperativo que nos confirme la recepción de este documento y la aceptación de los términos contenidos en él. Para ello, solicitamos que se comunique con nosotros a través de nuestro correo electrónico [correo_empresa_nulidad] a la mayor brevedad posible.\n\n");
        carta.append("Agradecemos de antemano su colaboración y comprensión.\n\n");
        carta.append("Atentamente,\n\n");
        carta.append("[juan fernandez]\n");
        carta.append("[Director del grupo de progrmacion en la star of death]\n");
        carta.append("[Imperio]");
        return carta.toString();
    }
}

public class ProcesamientoNotificaciones {
    private static final String SEPARADOR_DATOS = "\t";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String archivo = "Nulidad.txt";
        List<String[]> datosProveedores = leerArchivo(archivo);
        if (datosProveedores != null) {
            double sumaTotal = mostrarSumaTotal(datosProveedores);
            System.out.println("Suma total de nulidades: " + sumaTotal);
            System.out.println("Seleccione el número de nulidades a procesar:");
            int numNulidades = scanner.nextInt();
            procesarNotificaciones(datosProveedores, numNulidades);
        } else {
            System.out.println("Error al leer el archivo de datos.");
        }
    }

    public static void procesarNotificaciones(List<String[]> datosProveedores, int numNulidades) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Seleccione el tipo de correo a enviar:");
        System.out.println("1. Notificación de anulación tipo 1");
        System.out.println("2. Notificación de anulación tipo 2");
        int opcion = scanner.nextInt();
        scanner.nextLine(); // Consumir la nueva línea

        GeneradorCarta generadorCarta;
        if (opcion == 1) {
            generadorCarta = new CartaNotificacionAnulacionTipo1();
        } else if (opcion == 2) {
            generadorCarta = new CartaNotificacionAnulacionTipo2();
        } else {
            System.out.println("Opción no válida.");
            return;
        }

        for (int i = 0; i < numNulidades && i < datosProveedores.size(); i++) {
            String[] proveedor = datosProveedores.get(i);
            if (proveedor.length < 8) {
                System.out.println("Error: Datos insuficientes para el proveedor.");
                continue;
            }
            String numeroNulidad = proveedor[0];
            String nombreEmpresa = proveedor[1];
            List<String> pagos = new ArrayList<>();
            for (int j = 2; j < proveedor.length; j++) {
                if (esNumeroValido(proveedor[j])) {
                    pagos.add(proveedor[j]);
                }
            }
            // Generar carta de notificación
            String carta = generadorCarta.generarCarta(proveedor[8], numeroNulidad, nombreEmpresa, calcularTotal(pagos), pagos);
            // Enviar el correo electrónico (simulado)
            System.out.println("Enviando correo a " + proveedor[8] + ":\n" + carta);
        }
    }

    public static List<String[]> leerArchivo(String archivo) {
        List<String[]> datosProveedores = new ArrayList<>();
        try (BufferedReader lector = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = lector.readLine()) != null) {
                String[] datos = linea.split(SEPARADOR_DATOS);
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
}