package com.crudzaso.crudpark.ui.panels;

import com.crudzaso.crudpark.dao.TicketDAO;
import com.crudzaso.crudpark.model.Ticket;
import com.crudzaso.crudpark.service.AuthService;
import com.crudzaso.crudpark.util.DateUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel de dashboard que muestra información en tiempo real
 */
public class DashboardPanel extends JPanel {
    private final AuthService authService;
    private final TicketDAO ticketDAO;
    private JTable ticketsTable;
    private DefaultTableModel tableModel;
    private JLabel vehiculosDentroLabel;
    private JButton refreshButton;

    public DashboardPanel(AuthService authService) {
        this.authService = authService;
        this.ticketDAO = new TicketDAO();
        initComponents();
        cargarDatos();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Dashboard - Vehículos en el Parqueadero");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        vehiculosDentroLabel = new JLabel("Vehículos dentro: 0");
        vehiculosDentroLabel.setFont(new Font("Arial", Font.BOLD, 14));
        statsPanel.add(vehiculosDentroLabel);

        refreshButton = new JButton("Actualizar");
        refreshButton.addActionListener(e -> cargarDatos());
        statsPanel.add(refreshButton);

        headerPanel.add(statsPanel, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        String[] columnNames = {"ID", "Placa", "Tipo", "Hora de Ingreso", "Tiempo Transcurrido"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        ticketsTable = new JTable(tableModel);
        ticketsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ticketsTable.getTableHeader().setResizingAllowed(false);
        ticketsTable.setRowHeight(25);

        JScrollPane scrollPane = new JScrollPane(ticketsTable);
        add(scrollPane, BorderLayout.CENTER);

        JPanel infoPanel = new JPanel();
        infoPanel.setBorder(BorderFactory.createTitledBorder("Información"));
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.add(new JLabel("Operador actual: " + authService.getOperadorActual().getNombre()));
        infoPanel.add(Box.createVerticalStrut(10));
        infoPanel.add(new JLabel("Los tickets se actualizan automáticamente cada vez que presiones Actualizar"));

        add(infoPanel, BorderLayout.SOUTH);
    }

    private void cargarDatos() {
        tableModel.setRowCount(0);

        List<Ticket> ticketsAbiertos = ticketDAO.findTicketsAbiertos();

        vehiculosDentroLabel.setText("Vehículos dentro: " + ticketsAbiertos.size());

        for (Ticket ticket : ticketsAbiertos) {
            int minutosTranscurridos = (int) ((System.currentTimeMillis() -
                    ticket.getFechaIngreso().getTime()) / (1000 * 60));

            Object[] row = {
                    ticket.getId(),
                    ticket.getPlaca(),
                    ticket.getTipoCliente().getDescripcion(),
                    DateUtils.formatearParaTicket(ticket.getFechaIngreso()),
                    DateUtils.formatearTiempoEstadia(minutosTranscurridos)
            };

            tableModel.addRow(row);
        }
    }

    public void refresh() {
        cargarDatos();
    }
}
