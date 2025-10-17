package com.crudzaso.crudpark.ui.panels;

import com.crudzaso.crudpark.model.Ticket;
import com.crudzaso.crudpark.model.TipoCliente;
import com.crudzaso.crudpark.model.TipoVehiculo;
import com.crudzaso.crudpark.service.AuthService;
import com.crudzaso.crudpark.service.TarifaCalculator;
import com.crudzaso.crudpark.service.TicketService;
import com.crudzaso.crudpark.util.DateUtils;
import com.crudzaso.crudpark.util.ValidationUtils;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Panel para registrar la salida de vehículos y gestionar cobros
 */
public class SalidaVehiculoPanel extends JPanel {
    private final AuthService authService;
    private final TicketService ticketService;
    private final TarifaCalculator tarifaCalculator;

    private JTextField placaField;
    private JButton buscarButton;
    private JLabel ticketIdLabel;
    private JLabel tipoClienteLabel;
    private JLabel fechaIngresoLabel;
    private JLabel tiempoEstadiaLabel;
    private JLabel montoLabel;
    private JComboBox<String> metodoPagoCombo;
    private JComboBox<TipoVehiculo> tipoVehiculoCombo;
    private JButton registrarSalidaButton;

    private Ticket ticketActual;

    public SalidaVehiculoPanel(AuthService authService) {
        this.authService = authService;
        this.ticketService = new TicketService();
        this.tarifaCalculator = new TarifaCalculator();
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel titleLabel = new JLabel("Registro de Salida de Vehículo");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        add(titleLabel, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        JPanel busquedaPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        busquedaPanel.setBorder(BorderFactory.createTitledBorder("Buscar Ticket"));
        busquedaPanel.add(new JLabel("Placa:"));
        placaField = new JTextField(10);
        placaField.setFont(new Font("Arial", Font.BOLD, 16));
        busquedaPanel.add(placaField);

        buscarButton = new JButton("Buscar");
        buscarButton.addActionListener(e -> buscarTicket());
        busquedaPanel.add(buscarButton);

        mainPanel.add(busquedaPanel);

        JPanel infoPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        infoPanel.setBorder(BorderFactory.createTitledBorder("Información del Ticket"));

        infoPanel.add(new JLabel("Ticket ID:"));
        ticketIdLabel = new JLabel("-");
        ticketIdLabel.setFont(new Font("Arial", Font.BOLD, 14));
        infoPanel.add(ticketIdLabel);

        infoPanel.add(new JLabel("Tipo de Cliente:"));
        tipoClienteLabel = new JLabel("-");
        infoPanel.add(tipoClienteLabel);

        infoPanel.add(new JLabel("Fecha de Ingreso:"));
        fechaIngresoLabel = new JLabel("-");
        infoPanel.add(fechaIngresoLabel);

        infoPanel.add(new JLabel("Tiempo de Estadía:"));
        tiempoEstadiaLabel = new JLabel("-");
        infoPanel.add(tiempoEstadiaLabel);

        infoPanel.add(new JLabel("Tipo de Vehículo:"));
        tipoVehiculoCombo = new JComboBox<>(TipoVehiculo.values());
        tipoVehiculoCombo.addActionListener(e -> calcularMonto());
        infoPanel.add(tipoVehiculoCombo);

        infoPanel.add(new JLabel("Monto a Cobrar:"));
        montoLabel = new JLabel("-");
        montoLabel.setFont(new Font("Arial", Font.BOLD, 16));
        montoLabel.setForeground(new Color(0, 128, 0));
        infoPanel.add(montoLabel);

        infoPanel.add(new JLabel("Método de Pago:"));
        metodoPagoCombo = new JComboBox<>(new String[]{"Efectivo", "Tarjeta", "Transferencia"});
        infoPanel.add(metodoPagoCombo);

        mainPanel.add(infoPanel);

        mainPanel.add(Box.createVerticalStrut(20));

        registrarSalidaButton = new JButton("Registrar Salida");
        registrarSalidaButton.setPreferredSize(new Dimension(200, 40));
        registrarSalidaButton.setEnabled(false);
        registrarSalidaButton.addActionListener(e -> registrarSalida());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(registrarSalidaButton);
        mainPanel.add(buttonPanel);

        add(mainPanel, BorderLayout.CENTER);

        placaField.addActionListener(e -> buscarTicket());
    }

    private void buscarTicket() {
        String placa = placaField.getText().trim().toUpperCase();

        if (placa.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Por favor ingrese una placa",
                    "Campo vacío",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        if (!ValidationUtils.isPlacaValida(placa)) {
            JOptionPane.showMessageDialog(
                    this,
                    "Formato de placa inválido. Use el formato ABC123",
                    "Placa inválida",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        ticketActual = ticketService.buscarTicketAbierto(placa);

        if (ticketActual == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "No se encontró un ticket abierto para la placa: " + placa,
                    "Ticket no encontrado",
                    JOptionPane.WARNING_MESSAGE
            );
            limpiarCampos();
            return;
        }

        mostrarInformacionTicket();
    }

    private void mostrarInformacionTicket() {
        ticketIdLabel.setText("#" + ticketActual.getId());
        tipoClienteLabel.setText(ticketActual.getTipoCliente().getDescripcion());
        fechaIngresoLabel.setText(DateUtils.formatearParaTicket(ticketActual.getFechaIngreso()));

        int minutos = ticketService.calcularTiempoEstadia(
                ticketActual.getFechaIngreso(),
                Timestamp.valueOf(java.time.LocalDateTime.now())
        );

        tiempoEstadiaLabel.setText(DateUtils.formatearTiempoEstadia(minutos));

        calcularMonto();

        registrarSalidaButton.setEnabled(true);
    }

    private void calcularMonto() {
        if (ticketActual == null) {
            return;
        }

        if (ticketActual.getTipoCliente() == TipoCliente.MENSUALIDAD) {
            montoLabel.setText("$0.00 (Mensualidad)");
            metodoPagoCombo.setEnabled(false);
            tipoVehiculoCombo.setEnabled(false);
            return;
        }

        metodoPagoCombo.setEnabled(true);
        tipoVehiculoCombo.setEnabled(true);

        int minutos = ticketService.calcularTiempoEstadia(
                ticketActual.getFechaIngreso(),
                Timestamp.valueOf(java.time.LocalDateTime.now())
        );

        TipoVehiculo tipoVehiculo = (TipoVehiculo) tipoVehiculoCombo.getSelectedItem();
        BigDecimal monto = tarifaCalculator.calcularMonto(minutos, tipoVehiculo);

        montoLabel.setText("$" + monto);

        if (monto.compareTo(BigDecimal.ZERO) == 0) {
            montoLabel.setText("$0.00 (Tiempo de gracia)");
        }
    }

    private void registrarSalida() {
        if (ticketActual == null) {
            return;
        }

        BigDecimal monto = BigDecimal.ZERO;
        String metodoPago = null;

        if (ticketActual.getTipoCliente() == TipoCliente.INVITADO) {
            int minutos = ticketService.calcularTiempoEstadia(
                    ticketActual.getFechaIngreso(),
                    Timestamp.valueOf(java.time.LocalDateTime.now())
            );

            TipoVehiculo tipoVehiculo = (TipoVehiculo) tipoVehiculoCombo.getSelectedItem();
            monto = tarifaCalculator.calcularMonto(minutos, tipoVehiculo);
            metodoPago = (String) metodoPagoCombo.getSelectedItem();
        }

        boolean exito = ticketService.registrarSalida(
                ticketActual.getId(),
                monto.compareTo(BigDecimal.ZERO) > 0 ? monto : null,
                metodoPago,
                authService.getOperadorActual().getId()
        );

        if (exito) {
            String mensaje = "Salida registrada exitosamente.\nTicket #" + ticketActual.getId();
            if (monto.compareTo(BigDecimal.ZERO) > 0) {
                mensaje += "\nMonto cobrado: $" + monto;
            }

            JOptionPane.showMessageDialog(
                    this,
                    mensaje,
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE
            );

            limpiarCampos();
            placaField.setText("");
            placaField.requestFocus();
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "Error al registrar la salida",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void limpiarCampos() {
        ticketActual = null;
        ticketIdLabel.setText("-");
        tipoClienteLabel.setText("-");
        fechaIngresoLabel.setText("-");
        tiempoEstadiaLabel.setText("-");
        montoLabel.setText("-");
        registrarSalidaButton.setEnabled(false);
        metodoPagoCombo.setEnabled(true);
        tipoVehiculoCombo.setEnabled(true);
    }
}
