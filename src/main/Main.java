package main;

import interfaz.MainWindow;

public class Main {
    public static void main(String[] args) {
        // Mostrar la ventana principal
        java.awt.EventQueue.invokeLater(() -> {
            new MainWindow().setVisible(true);
        });
    }
}