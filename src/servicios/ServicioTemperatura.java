package servicios;

import entidades.TemperaturaRegistro;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ServicioTemperatura {

    public static List<TemperaturaRegistro> getDatos(String nombreArchivo) {
        DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("d/M/yyyy");
        try {
            Stream<String> lineas = Files.lines(Paths.get(nombreArchivo));
            return lineas.skip(1)
                    .map(linea -> linea.split(","))
                    .map(textos -> new TemperaturaRegistro(textos[0].trim(), LocalDate.parse(textos[1].trim(), formatoFecha),
                            Double.parseDouble(textos[2].trim())))
                    .collect(Collectors.toList());

        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }

    public static List<String> getCiudades(List<TemperaturaRegistro> datos) {
        return datos.stream()
                .map(TemperaturaRegistro::getCiudad)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    public static List<TemperaturaRegistro> filtrarPorFecha(LocalDate desde, LocalDate hasta,
                                                             List<TemperaturaRegistro> datos) {
        return datos.stream()
                .filter(dato -> !dato.getFecha().isBefore(desde) && !dato.getFecha().isAfter(hasta))
                .collect(Collectors.toList());
    }

    public static Map<String, Double> calcularPromedioPorCiudad(List<TemperaturaRegistro> registros) {
        return registros.stream()
                .collect(Collectors.groupingBy(
                        TemperaturaRegistro::getCiudad,
                        Collectors.averagingDouble(TemperaturaRegistro::getTemperatura)
                ));
    }

    public static String obtenerCiudadMasCalurosa(List<TemperaturaRegistro> registros, LocalDate fecha) {
        return registros.stream()
                .filter(registro -> registro.getFecha().equals(fecha))
                .max(Comparator.comparingDouble(TemperaturaRegistro::getTemperatura))
                .map(TemperaturaRegistro::getCiudad)
                .orElse(null);
    }

    public static String obtenerCiudadMenosCalurosa(List<TemperaturaRegistro> registros, LocalDate fecha) {
        return registros.stream()
                .filter(registro -> registro.getFecha().equals(fecha))
                .min(Comparator.comparingDouble(TemperaturaRegistro::getTemperatura))
                .map(TemperaturaRegistro::getCiudad)
                .orElse(null);
    }
}