package entidades;

import java.time.LocalDate;

public class TemperaturaRegistro {
    private String ciudad;
    private LocalDate fecha;
    private double temperatura;

    public TemperaturaRegistro(String ciudad, LocalDate fecha, double temperatura) {
        this.ciudad = ciudad;
        this.fecha = fecha;
        this.temperatura = temperatura;
    }

    public String getCiudad() {
        return ciudad;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public double getTemperatura() {
        return temperatura;
    }

    public String setCiudad(String ciudad) {
        return this.ciudad = ciudad;
    }

    public LocalDate setFecha(LocalDate fecha) {
        return this.fecha = fecha;
    }
    
    public double setTemperatura(double temperatura) {
        return this.temperatura = temperatura;
    }
}