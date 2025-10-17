package com.crudzaso.crudpark.ui;

import com.crudzaso.crudpark.service.AuthService;
import com.crudzaso.crudpark.ui.panels.DashboardPanel;
import com.crudzaso.crudpark.ui.panels.IngresoVehiculoPanel;
import com.crudzaso.crudpark.ui.panels.SalidaVehiculoPanel;

import javax.swing.*;
import java.awt.*;

/**
 * Ventana principal de la aplicación
 */
public class MainFrame extends JFrame {
    private final AuthService authService;
    private JTabbedPane tabbedPane;
    private JLabel operadorLabel;

    public MainFrame(AuthService authService) {
        this.authService = authService;
        initComponents();
    }

    private void initComponents() {
        setTitle("CrudPark - Sistema de Parqueadero");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());

        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        tabbedPane = new JTabbedPane();

        DashboardPanel dashboardPanel = new DashboardPanel(authService);
        tabbedPane.addTab("Dashboard", dashboardPanel);

        IngresoVehiculoPanel ingresoPanel = new IngresoVehiculoPanel(authService);
        tabbedPane.addTab("Ingreso de Vehículo", ingresoPanel);

        SalidaVehiculoPanel salidaPanel = new SalidaVehiculoPanel(authService);
        tabbedPane.addTab("Salida de Vehículo", salidaPanel);

        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        add(mainPanel);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        headerPanel.setBackground(new Color(52, 152, 219));

        JLabel titleLabel = new JLabel("CrudPark - Sistema de Parqueadero");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        infoPanel.setOpaque(false);

        operadorLabel = new JLabel("Operador: " + authService.getOperadorActual().getNombre());
        operadorLabel.setForeground(Color.WHITE);
        operadorLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        infoPanel.add(operadorLabel);

        JButton logoutButton = new JButton("Cerrar Sesión");
        logoutButton.addActionListener(e -> handleLogout());
        infoPanel.add(logoutButton);

        headerPanel.add(infoPanel, BorderLayout.EAST);

        return headerPanel;
    }

    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "¿Está seguro que desea cerrar sesión?",
                "Confirmar cierre de sesión",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            authService.logout();
            dispose();

            SwingUtilities.invokeLater(() -> {
                LoginFrame loginFrame = new LoginFrame();
                loginFrame.setVisible(true);
            });
        }
    }
}
