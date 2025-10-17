package com.crudzaso.crudpark.ui.panels;

import com.crudzaso.crudpark.dao.OperadorDAO;
import com.crudzaso.crudpark.model.Operador;
import com.crudzaso.crudpark.model.Ticket;
import com.crudzaso.crudpark.service.AuthService;
import com.crudzaso.crudpark.service.TicketService;
import com.crudzaso.crudpark.util.PrinterHelper;
import com.crudzaso.crudpark.util.QRGenerator;
import com.crudzaso.crudpark.util.TicketPrinter;
import com.crudzaso.crudpark.util.ValidationUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Panel para registrar el ingreso de vehículos
 * ACTUALIZADO con visualización de QR
 */
public class IngresoVehiculoPanel extends JPanel {
    private final AuthService authService;
    private final TicketService ticketService;
    private final TicketPrinter ticketPrinter;
    private final PrinterHelper printerHelper;
    private final OperadorDAO operadorDAO;
    private final QRGenerator qrGenerator;

    private JTextField placaField;
    private JButton registrarButton;
    private JButton imprimirButton;
    private JTextArea resultadoArea;
    private JLabel qrLabel;
    private Ticket ultimoTicket;

    public IngresoVehiculoPanel(AuthService authService) {
        this.authService = authService;
        this.ticketService = new TicketService();
        this.ticketPrinter = new TicketPrinter();
        this.printerHelper = new PrinterHelper();
        this.operadorDAO = new OperadorDAO();
        this.qrGenerator = new QRGenerator();
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel titleLabel = new JLabel("Registro de Ingreso de Vehículo");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        add(titleLabel, BorderLayout.NORTH);

        // Panel principal dividido en dos: izquierda (form + ticket) y derecha (QR)
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));

        // Panel izquierdo
        JPanel leftPanel = new JPanel(new BorderLayout(10, 10));

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createTitledBorder("Datos del Vehículo"));

        JPanel placaPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        placaPanel.add(new JLabel("Placa:"));
        placaField = new JTextField(10);
        placaField.setFont(new Font("Arial", Font.BOLD, 16));
        placaPanel.add(placaField);

        JLabel formatoLabel = new JLabel("(Carro: ABC123 | Moto: ABC12A)");
        formatoLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        formatoLabel.setForeground(Color.GRAY);
        placaPanel.add(formatoLabel);

        formPanel.add(placaPanel);
        formPanel.add(Box.createVerticalStrut(15));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        registrarButton = new JButton("Registrar Ingreso");
        registrarButton.setPreferredSize(new Dimension(180, 40));
        registrarButton.addActionListener(e -> handleRegistroIngreso());
        buttonPanel.add(registrarButton);

        imprimirButton = new JButton("Imprimir Ticket");
        imprimirButton.setPreferredSize(new Dimension(150, 40));
        imprimirButton.setEnabled(false);
        imprimirButton.addActionListener(e -> imprimirTicket());
        buttonPanel.add(imprimirButton);

        formPanel.add(buttonPanel);

        leftPanel.add(formPanel, BorderLayout.NORTH);

        JPanel resultadoPanel = new JPanel(new BorderLayout());
        resultadoPanel.setBorder(BorderFactory.createTitledBorder("Ticket Generado"));

        resultadoArea = new JTextArea(12, 45);
        resultadoArea.setEditable(false);
        resultadoArea.setFont(new Font("Courier New", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(resultadoArea);

        resultadoPanel.add(scrollPane, BorderLayout.CENTER);
        leftPanel.add(resultadoPanel, BorderLayout.CENTER);

        // Panel derecho para el QR
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createTitledBorder("Código QR"));
        rightPanel.setPreferredSize(new Dimension(250, 0));

        qrLabel = new JLabel("", SwingConstants.CENTER);
        qrLabel.setPreferredSize(new Dimension(220, 220));
        qrLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        qrLabel.setBackground(Color.WHITE);
        qrLabel.setOpaque(true);

        rightPanel.add(qrLabel, BorderLayout.CENTER);

        JLabel qrInfo = new JLabel("<html><center>El código QR aparecerá<br>después de registrar</center></html>",
                                    SwingConstants.CENTER);
        qrInfo.setFont(new Font("Arial", Font.ITALIC, 11));
        qrInfo.setForeground(Color.GRAY);
        rightPanel.add(qrInfo, BorderLayout.SOUTH);

        mainPanel.add(leftPanel, BorderLayout.CENTER);
        mainPanel.add(rightPanel, BorderLayout.EAST);

        add(mainPanel, BorderLayout.CENTER);

        placaField.addActionListener(e -> handleRegistroIngreso());
    }

    private void handleRegistroIngreso() {
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
                    "Formato de placa inválido.\nCarro: ABC123 (3 letras + 3 números)\nMoto: ABC12A (3 letras + 2 números + 1 letra)",
                    "Placa inválida",
                    JOptionPane.ERROR_MESSAGE
            );
            placaField.requestFocus();
            return;
        }

        Ticket ticketExistente = ticketService.buscarTicketAbierto(placa);
        if (ticketExistente != null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Ya existe un ticket abierto para esta placa.\nTicket #" + ticketExistente.getId() +
                    "\nFolio: " + ticketExistente.getFolio(),
                    "Ticket duplicado",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        Integer ticketId = ticketService.registrarIngreso(
                placa,
                authService.getOperadorActual().getId()
        );

        if (ticketId != null) {
            ultimoTicket = ticketService.buscarTicketPorId(ticketId);
            Operador operador = operadorDAO.findById(ultimoTicket.getOperadorId());

            String ticketFormateado = ticketPrinter.generarTicketParaVista(ultimoTicket, operador);
            resultadoArea.setText(ticketFormateado);

            // Generar y mostrar el QR
            long timestamp = ultimoTicket.getFechaIngreso().getTime();
            BufferedImage qrImage = qrGenerator.generarQR(
                    ultimoTicket.getId(),
                    ultimoTicket.getPlaca(),
                    timestamp
            );

            if (qrImage != null) {
                ImageIcon qrIcon = new ImageIcon(qrImage);
                qrLabel.setIcon(qrIcon);
                qrLabel.setText("");
            }

            imprimirButton.setEnabled(true);

            JOptionPane.showMessageDialog(
                    this,
                    "Ingreso registrado exitosamente.\n" +
                    "Folio: " + ultimoTicket.getFolio() + "\n" +
                    "Ticket #" + ticketId + "\n" +
                    "Tipo Cliente: " + ultimoTicket.getTipoCliente().getDescripcion() + "\n" +
                    "Vehículo: " + ultimoTicket.getTipoVehiculo().name(),
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE
            );

            placaField.setText("");
            placaField.requestFocus();
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "Error al registrar el ingreso",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void imprimirTicket() {
        if (ultimoTicket != null) {
            Operador operador = operadorDAO.findById(ultimoTicket.getOperadorId());

            // Intentar imprimir en Xprinter XP-58IIT primero
            boolean impreso = printerHelper.imprimirEnXprinter(ultimoTicket, operador);

            if (impreso) {
                JOptionPane.showMessageDialog(
                        this,
                        "Ticket impreso en Xprinter XP-58IIT",
                        "Impresión Exitosa",
                        JOptionPane.INFORMATION_MESSAGE
                );
            } else {
                // Si no se encuentra Xprinter, mostrar diálogo de selección
                int opcion = JOptionPane.showConfirmDialog(
                        this,
                        "No se encontró la impresora Xprinter XP-58IIT.\n" +
                        "¿Desea seleccionar otra impresora?",
                        "Impresora no encontrada",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );

                if (opcion == JOptionPane.YES_OPTION) {
                    boolean impresoManual = printerHelper.imprimirConDialogo(ultimoTicket, operador);
                    if (impresoManual) {
                        JOptionPane.showMessageDialog(
                                this,
                                "Ticket enviado a la impresora seleccionada",
                                "Impresión Exitosa",
                                JOptionPane.INFORMATION_MESSAGE
                        );
                    } else {
                        JOptionPane.showMessageDialog(
                                this,
                                "Error al imprimir o impresión cancelada",
                                "Error",
                                JOptionPane.ERROR_MESSAGE
                        );
                    }
                }
            }
        }
    }
}
