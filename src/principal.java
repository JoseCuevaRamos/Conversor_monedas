import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Map;
import java.util.Scanner;

public class principal {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        HttpClient client = HttpClient.newHttpClient();

        while (true) {
            System.out.println("Menu:");
            System.out.println("1. Convertir moneda");
            System.out.println("2. Salir");
            System.out.print("Seleccione una opción: ");
            int opcion;
            try {
                opcion = scanner.nextInt();
                scanner.nextLine(); // Consume newline
            } catch (InputMismatchException e) {
                System.out.println("Opción no válida. Por favor, ingrese un número.");
                scanner.nextLine(); // Clear the invalid input
                continue;
            }

            if (opcion == 1) {
                System.out.println("Ingrese la moneda a convertir entre ARS, BOB, BRL, CLP, COP, USD");
                String input = scanner.nextLine().toUpperCase();

                String direccion = "https://v6.exchangerate-api.com/v6/98743aad26da9f4c106986da/latest/" + input;
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(direccion))
                        .build();
                HttpResponse<String> response;
                try {
                    response = client.send(request, HttpResponse.BodyHandlers.ofString());
                } catch (IOException | InterruptedException e) {
                    System.out.println("Error al realizar la solicitud a la API: " + e.getMessage());
                    continue;
                }

                JsonObject jsonResponse;
                try {
                    jsonResponse = JsonParser.parseString(response.body()).getAsJsonObject();
                } catch (JsonSyntaxException e) {
                    System.out.println("Error al analizar la respuesta de la API: " + e.getMessage());
                    continue;
                }

                JsonObject conversionRates = jsonResponse.getAsJsonObject("conversion_rates");

                ArrayList<String> monedas = new ArrayList<>();
                monedas.add("ARS");
                monedas.add("BOB");
                monedas.add("BRL");
                monedas.add("CLP");
                monedas.add("COP");
                monedas.add("USD");

                Map<String, Double> valor = new HashMap<>();
                for (String moneda : monedas) {
                    valor.put(moneda, conversionRates.get(moneda).getAsDouble());
                }

                if (!valor.containsKey(input)) {
                    System.out.println("Moneda no válida.");
                    continue;
                }

                System.out.println("Ingrese la moneda a la que desea convertir (ARS, BOB, BRL, CLP, COP, USD): ");
                String targetCurrency = scanner.nextLine().toUpperCase();

                if (!valor.containsKey(targetCurrency)) {
                    System.out.println("Moneda no válida.");
                    continue;
                }

                double conversionRate = valor.get(targetCurrency);

                System.out.println("Ingrese la cantidad de " + input + " a convertir");
                double cantidad;
                try {
                    cantidad = scanner.nextDouble();
                } catch (InputMismatchException e) {
                    System.out.println("Cantidad no válida. Por favor, ingrese un número.");
                    scanner.nextLine(); // Clear the invalid input
                    continue;
                }

                System.out.println("Convertir " + cantidad + " " + input + " en " + targetCurrency + " es: " + convertirMoneda(cantidad, conversionRate) + " " + targetCurrency);
            } else if (opcion == 2) {
                System.out.println("Saliendo del programa...");
                break;
            } else {
                System.out.println("Opción no válida.");
            }
        }
    }

    private static double convertirMoneda(double cantidad, double conversionRate) {
        return cantidad * conversionRate;
    }
}