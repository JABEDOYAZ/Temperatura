import entidades.TemperaturaRegistro;
import servicios.ServicioTemperatura;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;

import datechooser.beans.DateChooserCombo;

public class FrmAnalisisTemperaturas extends JFrame {

    private DateChooserCombo dccDesde, dccHasta, dccFechaEspecifica;
    private JPanel pnlGrafica;
    private JTextArea taResultados;

    private List<TemperaturaRegistro> datos;

    public FrmAnalisisTemperaturas() {
        setTitle("Análisis de Temperaturas");
        setSize(1000, 550);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel pnlControles = new JPanel();
        pnlControles.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JLabel lblDesde = new JLabel("Desde:");
        dccDesde = new DateChooserCombo();
        JLabel lblHasta = new JLabel("Hasta:");
        dccHasta = new DateChooserCombo();
        JButton btnGraficar = new JButton("Mostrar Promedios");

        JLabel lblFechaEspecifica = new JLabel("Fecha Específica:");
        dccFechaEspecifica = new DateChooserCombo();
        JButton btnAnalizarFecha = new JButton("Analizar Fecha");

        pnlControles.add(lblDesde);
        pnlControles.add(dccDesde);
        pnlControles.add(lblHasta);
        pnlControles.add(dccHasta);
        pnlControles.add(btnGraficar);
        pnlControles.add(lblFechaEspecifica);
        pnlControles.add(dccFechaEspecifica);
        pnlControles.add(btnAnalizarFecha);

        pnlGrafica = new JPanel();
        pnlGrafica.setLayout(new BorderLayout());

        taResultados = new JTextArea(10, 40);
        JScrollPane spResultados = new JScrollPane(taResultados);

        JPanel pnlInferior = new JPanel(new BorderLayout());
        pnlInferior.add(spResultados, BorderLayout.CENTER);
        pnlInferior.setBorder(BorderFactory.createTitledBorder("Resultados"));

        add(pnlControles, BorderLayout.NORTH);
        add(pnlGrafica, BorderLayout.CENTER);
        add(pnlInferior, BorderLayout.SOUTH);

        cargarDatos();

        btnGraficar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                btnGraficarClick();
            }
        });

        btnAnalizarFecha.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                btnAnalizarFechaClick();
            }
        });
    }

    private void cargarDatos() {
        datos = ServicioTemperatura.getDatos(System.getProperty("user.dir") + "/src/datos/temperaturas.csv");
    }

    private void btnGraficarClick() {
        LocalDate desde = dccDesde.getSelectedDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate hasta = dccHasta.getSelectedDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        List<TemperaturaRegistro> datosFiltrados = ServicioTemperatura.filtrarPorFecha(desde, hasta, datos);
        Map<String, Double> promediosPorCiudad = ServicioTemperatura.calcularPromedioPorCiudad(datosFiltrados);

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (Map.Entry<String, Double> entry : promediosPorCiudad.entrySet()) {
            dataset.addValue(entry.getValue(), "Promedio", entry.getKey());
        }

        JFreeChart barChart = ChartFactory.createBarChart(
                "Promedio de Temperatura por Ciudad",
                "Ciudad",
                "Promedio Temperatura",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false);

        CategoryPlot plot = (CategoryPlot) barChart.getPlot();
        plot.setRangeGridlinePaint(Color.BLACK);

        ChartPanel chartPanel = new ChartPanel(barChart);
        pnlGrafica.removeAll();
        pnlGrafica.add(chartPanel, BorderLayout.CENTER);
        pnlGrafica.revalidate();
        pnlGrafica.repaint();
    }

    private void btnAnalizarFechaClick() {
        LocalDate fechaEspecifica = dccFechaEspecifica.getSelectedDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        String ciudadMasCalurosa = ServicioTemperatura.obtenerCiudadMasCalurosa(datos, fechaEspecifica);
        String ciudadMenosCalurosa = ServicioTemperatura.obtenerCiudadMenosCalurosa(datos, fechaEspecifica);

        taResultados.setText("");
        if (ciudadMasCalurosa != null) {
            taResultados.append("Ciudad más calurosa el " + fechaEspecifica + ": " + ciudadMasCalurosa + "\n");
        } else {
            taResultados.append("No hay datos de temperatura para el " + fechaEspecifica + "\n");
        }

        if (ciudadMenosCalurosa != null) {
            taResultados.append("Ciudad menos calurosa el " + fechaEspecifica + ": " + ciudadMenosCalurosa + "\n");
        }
    }

    public static void main(String[] args) throws Exception {
        new FrmAnalisisTemperaturas().setVisible(true);
    }
}