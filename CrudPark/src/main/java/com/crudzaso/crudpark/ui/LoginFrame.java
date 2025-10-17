package com.crudzaso.crudpark.ui;

import com.crudzaso.crudpark.model.Operador;
import com.crudzaso.crudpark.service.AuthService;

import javax.swing.*;
import java.awt.*;

/**
 * Ventana de inicio de sesión para operadores
 */
public class LoginFrame extends JFrame {
    private JTextField usuarioField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private final AuthService authService;

    public LoginFrame() {
        this.authService = new AuthService();
        initComponents();
    }

    private void initComponents() {
        setTitle("CrudPark - Iniciar Sesión");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 250);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("CrudPark - Sistema de Parqueadero", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Usuario:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        usuarioField = new JTextField(20);
        formPanel.add(usuarioField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Contraseña:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        passwordField = new JPasswordField(20);
        formPanel.add(passwordField, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        loginButton = new JButton("Iniciar Sesión");
        loginButton.setPreferredSize(new Dimension(150, 35));
        loginButton.addActionListener(e -> handleLogin());
        buttonPanel.add(loginButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        passwordField.addActionListener(e -> handleLogin());
        usuarioField.addActionListener(e -> passwordField.requestFocus());
    }

    private void handleLogin() {
        String usuario = usuarioField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (usuario.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Por favor ingrese usuario y contraseña",
                    "Campos vacíos",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        Operador operador = authService.login(usuario, password);

        if (operador != null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Bienvenido, " + operador.getNombre(),
                    "Login exitoso",
                    JOptionPane.INFORMATION_MESSAGE
            );

            dispose();

            SwingUtilities.invokeLater(() -> {
                MainFrame mainFrame = new MainFrame(authService);
                mainFrame.setVisible(true);
            });
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "Usuario o contraseña incorrectos",
                    "Error de autenticación",
                    JOptionPane.ERROR_MESSAGE
            );
            passwordField.setText("");
            usuarioField.requestFocus();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            LoginFrame frame = new LoginFrame();
            frame.setVisible(true);
        });
    }
}
