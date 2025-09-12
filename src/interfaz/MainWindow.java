package interfaz;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import analizadores.*;
import modelos.*;
import reportes.*;
import java_cup.runtime.Symbol;

public class MainWindow extends JFrame {
    private EditorPanel editorPanel;
    private JTextArea consola;
    private JTabbedPane tabbedPane;
    
    public MainWindow() {
        initComponents();
        setTitle("AutómataLab - OLC1 Proyecto 1");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
    }
    
    private void initComponents() {
        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // Barra de menú
        JMenuBar menuBar = new JMenuBar();
        
        // Menú Archivo
        JMenu archivoMenu = new JMenu("Archivo");
        JMenuItem nuevoItem = new JMenuItem("Nuevo");
        JMenuItem abrirItem = new JMenuItem("Abrir");
        JMenuItem guardarItem = new JMenuItem("Guardar");
        JMenuItem guardarComoItem = new JMenuItem("Guardar como...");
        JMenuItem salirItem = new JMenuItem("Salir");
        
        archivoMenu.add(nuevoItem);
        archivoMenu.add(abrirItem);
        archivoMenu.add(guardarItem);
        archivoMenu.add(guardarComoItem);
        archivoMenu.addSeparator();
        archivoMenu.add(salirItem);
        
        // Menú Editar
        JMenu editarMenu = new JMenu("Editar");
        JMenuItem deshacerItem = new JMenuItem("Deshacer");
        JMenuItem rehacerItem = new JMenuItem("Rehacer");
        JMenuItem cortarItem = new JMenuItem("Cortar");
        JMenuItem copiarItem = new JMenuItem("Copiar");
        JMenuItem pegarItem = new JMenuItem("Pegar");
        JMenuItem buscarItem = new JMenuItem("Buscar");
        
        editarMenu.add(deshacerItem);
        editarMenu.add(rehacerItem);
        editarMenu.addSeparator();
        editarMenu.add(cortarItem);
        editarMenu.add(copiarItem);
        editarMenu.add(pegarItem);
        editarMenu.addSeparator();
        editarMenu.add(buscarItem);
        
        // Menú Ver
        JMenu verMenu = new JMenu("Ver");
        JMenuItem aumentarFuenteItem = new JMenuItem("Aumentar fuente");
        JMenuItem disminuirFuenteItem = new JMenuItem("Disminuir fuente");
        
        verMenu.add(aumentarFuenteItem);
        verMenu.add(disminuirFuenteItem);
        
        // Menú Reportes
        JMenu reportesMenu = new JMenu("Reportes");
        JMenuItem tokensItem = new JMenuItem("Reporte de Tokens");
        JMenuItem erroresItem = new JMenuItem("Reporte de Errores");
        JMenuItem automatasItem = new JMenuItem("Lista de Autómatas");
        JMenuItem graficosItem = new JMenuItem("Generar Gráficos");
        JMenuItem pasosItem = new JMenuItem("Reporte de Pasos");
        
        reportesMenu.add(tokensItem);
        reportesMenu.add(erroresItem);
        reportesMenu.add(automatasItem);
        reportesMenu.add(graficosItem);
        reportesMenu.add(pasosItem);
        
        // Menú Ejecutar
        JMenu ejecutarMenu = new JMenu("Ejecutar");
        JMenuItem ejecutarItem = new JMenuItem("Ejecutar");
        JMenuItem limpiarConsolaItem = new JMenuItem("Limpiar Consola");
        
        ejecutarMenu.add(ejecutarItem);
        ejecutarMenu.add(limpiarConsolaItem);
        
        // Menú Ayuda
        JMenu ayudaMenu = new JMenu("Ayuda");
        JMenuItem acercaDeItem = new JMenuItem("Acerca de");
        JMenuItem manualItem = new JMenuItem("Manual de Usuario");
        
        ayudaMenu.add(acercaDeItem);
        ayudaMenu.add(manualItem);
        
        // Agregar menús a la barra
        menuBar.add(archivoMenu);
        menuBar.add(editarMenu);
        menuBar.add(verMenu);
        menuBar.add(reportesMenu);
        menuBar.add(ejecutarMenu);
        menuBar.add(ayudaMenu);
        
        setJMenuBar(menuBar);
        
        // Editor y consola
        tabbedPane = new JTabbedPane();
        
        // Usar EditorPanel en lugar de JTextArea
        editorPanel = new EditorPanel();
        JScrollPane editorScroll = new JScrollPane(editorPanel);
        editorScroll.setBorder(BorderFactory.createTitledBorder("Editor de Código (.atm)"));
        tabbedPane.addTab("Editor", editorScroll);
        
        consola = new JTextArea();
        consola.setFont(new Font("Monospaced", Font.PLAIN, 12));
        consola.setEditable(false);
        consola.setBackground(Color.BLACK);
        consola.setForeground(Color.WHITE);
        JScrollPane consolaScroll = new JScrollPane(consola);
        consolaScroll.setBorder(BorderFactory.createTitledBorder("Consola de Salida"));
        tabbedPane.addTab("Consola", consolaScroll);
        
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        
        // Barra de estado
        JPanel statusBar = new JPanel(new BorderLayout());
        JLabel statusLabel = new JLabel(" Listo para ejecutar");
        statusLabel.setBorder(BorderFactory.createEtchedBorder());
        statusBar.add(statusLabel, BorderLayout.WEST);
        
        // Indicador de posición del cursor
        JLabel positionLabel = new JLabel(" Línea: 1, Columna: 1 ");
        positionLabel.setBorder(BorderFactory.createEtchedBorder());
        statusBar.add(positionLabel, BorderLayout.EAST);
        
        mainPanel.add(statusBar, BorderLayout.SOUTH);
        
        // Listeners para menús
        nuevoItem.addActionListener(e -> nuevoArchivo());
        abrirItem.addActionListener(e -> abrirArchivo());
        guardarItem.addActionListener(e -> guardarArchivo());
        guardarComoItem.addActionListener(e -> guardarComoArchivo());
        salirItem.addActionListener(e -> salir());
        
        deshacerItem.addActionListener(e -> editorPanel.undo());
        rehacerItem.addActionListener(e -> editorPanel.redo());
        cortarItem.addActionListener(e -> editorPanel.getTextArea().cut());
        copiarItem.addActionListener(e -> editorPanel.getTextArea().copy());
        pegarItem.addActionListener(e -> editorPanel.getTextArea().paste());
        buscarItem.addActionListener(e -> mostrarDialogoBusqueda());
        
        aumentarFuenteItem.addActionListener(e -> editorPanel.increaseFontSize());
        disminuirFuenteItem.addActionListener(e -> editorPanel.decreaseFontSize());
        
        ejecutarItem.addActionListener(e -> ejecutar());
        limpiarConsolaItem.addActionListener(e -> consola.setText(""));
        
        tokensItem.addActionListener(e -> mostrarTokens());
        erroresItem.addActionListener(e -> mostrarErrores());
        automatasItem.addActionListener(e -> mostrarAutomatas());
        graficosItem.addActionListener(e -> mostrarGraficos());
        pasosItem.addActionListener(e -> mostrarReportePasos());
        
        acercaDeItem.addActionListener(e -> mostrarAcercaDe());
        manualItem.addActionListener(e -> mostrarManual());
        
        add(mainPanel);
        
        // Actualizar etiqueta de posición en tiempo real
        editorPanel.getTextArea().addCaretListener(e -> {
            int line = editorPanel.getCaretLine();
            int column = editorPanel.getCaretColumn();
            positionLabel.setText(" Línea: " + line + ", Columna: " + column + " ");
        });
        
        // Cargar ejemplo inicial
        cargarEjemploInicial();
    }
    
    private void cargarEjemploInicial() {
        String ejemplo = "// Ejemplo de AFD que acepta cadenas con número par de 0s\n" +
                        "<AFD Nombre=\"AFD_Par\">\n" +
                        "  N = {q0, q1};\n" +
                        "  T = {0, 1};\n" +
                        "  I = {q0};\n" +
                        "  A = {q0};\n" +
                        "  \n" +
                        "  Transiciones:\n" +
                        "    q0 -> 0, q1 | 1, q0;\n" +
                        "    q1 -> 0, q0 | 1, q1;\n" +
                        "</AFD>\n\n" +
                        "// Ejemplo de AP para lenguaje a^n b^n\n" +
                        "<AP Nombre=\"AP_AnBn\">\n" +
                        "  N = {q0, q1, q2};\n" +
                        "  T = {a, b};\n" +
                        "  P = {A, Z};\n" +
                        "  I = {q0};\n" +
                        "  A = {q2};\n" +
                        "  \n" +
                        "  Transiciones:\n" +
                        "    q0 (a) -> (Z), q1 : (A Z);\n" +
                        "    q1 (a) -> (A), q1 : (A A);\n" +
                        "    q1 (b) -> (A), q2 : ($);\n" +
                        "    q2 (b) -> (A), q2 : ($);\n" +
                        "</AP>\n\n" +
                        "// Pruebas\n" +
                        "verAutomatas();\n" +
                        "desc(AFD_Par);\n" +
                        "AFD_Par(\"1010\");\n" +
                        "AFD_Par(\"100\");\n" +
                        "AP_AnBn(\"aabb\");";
        
        editorPanel.setText(ejemplo);
    }
    
    private void nuevoArchivo() {
        int option = JOptionPane.showConfirmDialog(this, 
                "¿Desea guardar los cambios actuales?", 
                "Nuevo archivo", 
                JOptionPane.YES_NO_CANCEL_OPTION);
        
        if (option == JOptionPane.YES_OPTION) {
            guardarArchivo();
        }
        
        if (option != JOptionPane.CANCEL_OPTION) {
            editorPanel.clear();
            consola.setText("");
            Parser.automatas.clear();
            Parser.errores.clear();
            setTitle("AutómataLab - Nuevo archivo");
        }
    }
    
    private void abrirArchivo() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Archivos ATM", "atm"));
        fileChooser.setDialogTitle("Abrir archivo .atm");
        
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(fileChooser.getSelectedFile()));
                StringBuilder content = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }
                reader.close();
                
                editorPanel.setText(content.toString());
                setTitle("AutómataLab - " + fileChooser.getSelectedFile().getName());
                
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, 
                    "Error al abrir archivo: " + e.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void guardarArchivo() {
        // Implementación básica - en una aplicación real se guardaría el nombre del archivo actual
        guardarComoArchivo();
    }
    
    private void guardarComoArchivo() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Archivos ATM", "atm"));
        fileChooser.setDialogTitle("Guardar archivo .atm");
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                String filename = fileChooser.getSelectedFile().getAbsolutePath();
                if (!filename.toLowerCase().endsWith(".atm")) {
                    filename += ".atm";
                }
                
                BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
                writer.write(editorPanel.getText());
                writer.close();
                
                setTitle("AutómataLab - " + new File(filename).getName());
                JOptionPane.showMessageDialog(this, 
                    "Archivo guardado exitosamente", 
                    "Éxito", 
                    JOptionPane.INFORMATION_MESSAGE);
                
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, 
                    "Error al guardar archivo: " + e.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void salir() {
        int option = JOptionPane.showConfirmDialog(this, 
                "¿Está seguro que desea salir?", 
                "Salir", 
                JOptionPane.YES_NO_OPTION);
        
        if (option == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }
    
    private void mostrarDialogoBusqueda() {
        String searchText = JOptionPane.showInputDialog(this, 
                "Texto a buscar:", 
                "Buscar", 
                JOptionPane.QUESTION_MESSAGE);
        
        if (searchText != null && !searchText.trim().isEmpty()) {
            editorPanel.findText(searchText.trim());
        }
    }
    
    private void ejecutar() {
        consola.setText(""); // Limpiar consola
        Parser.errores.clear();
        
        try {
            String input = editorPanel.getText();
            
            // Redirigir salida estándar a la consola
            PrintStream originalOut = System.out;
            PrintStream originalErr = System.err;
            
            PrintStream consoleStream = new PrintStream(new OutputStream() {
                @Override
                public void write(int b) {
                    consola.append(String.valueOf((char) b));
                    consola.setCaretPosition(consola.getDocument().getLength());
                }
                
                @Override
                public void write(byte[] b, int off, int len) {
                    consola.append(new String(b, off, len));
                    consola.setCaretPosition(consola.getDocument().getLength());
                }
            });
            
            System.setOut(consoleStream);
            System.setErr(consoleStream);
            
            // Crear analizador léxico
            Lexer lexer = new Lexer(new StringReader(input));
            
            // Crear analizador sintáctico
            Parser parser = new Parser(lexer);
            
            // Ejecutar análisis
            consola.append("=== EJECUTANDO ANÁLISIS ===\n");
            long startTime = System.currentTimeMillis();
            parser.parse();
            long endTime = System.currentTimeMillis();
            
            consola.append("\n=== ANÁLISIS COMPLETADO ===\n");
            consola.append("Tiempo de ejecución: " + (endTime - startTime) + " ms\n");
            
            // Restaurar salida estándar
            System.setOut(originalOut);
            System.setErr(originalErr);
            
            // Cambiar a la pestaña de consola
            tabbedPane.setSelectedIndex(1);
            
        } catch (Exception e) {
            consola.append("Error durante la ejecución: " + e.getMessage() + "\n");
            e.printStackTrace();
        }
    }
    
    private void mostrarTokens() {
        try {
            String input = editorPanel.getText();
            List<Token> tokens = ReporteTokens.analizarTokens(input);
            
            // Crear diálogo para mostrar tokens
            JDialog tokensDialog = new JDialog(this, "Reporte de Tokens", true);
            tokensDialog.setSize(800, 600);
            tokensDialog.setLayout(new BorderLayout());
            tokensDialog.setLocationRelativeTo(this);
            
            // Crear tabla de tokens
            String[] columnNames = {"#", "Lexema", "Tipo", "Línea", "Columna"};
            Object[][] data = new Object[tokens.size()][5];
            
            for (int i = 0; i < tokens.size(); i++) {
                Token token = tokens.get(i);
                data[i][0] = token.getNumero();
                data[i][1] = token.getLexema();
                data[i][2] = token.getTipo();
                data[i][3] = token.getLinea();
                data[i][4] = token.getColumna();
            }
            
            JTable tokensTable = new JTable(data, columnNames);
            tokensTable.setFont(new Font("Monospaced", Font.PLAIN, 12));
            tokensTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            
            JScrollPane scrollPane = new JScrollPane(tokensTable);
            tokensDialog.add(scrollPane, BorderLayout.CENTER);
            
            // Botón para cerrar
            JButton closeButton = new JButton("Cerrar");
            closeButton.addActionListener(e -> tokensDialog.dispose());
            
            JPanel buttonPanel = new JPanel();
            buttonPanel.add(closeButton);
            tokensDialog.add(buttonPanel, BorderLayout.SOUTH);
            
            tokensDialog.setVisible(true);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error al generar tokens: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void mostrarErrores() {
        ReporteErrores.mostrarErroresConsola();
        
        if (Parser.errores.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "No se encontraron errores en el análisis.", 
                "Reporte de Errores", 
                JOptionPane.INFORMATION_MESSAGE);
        } else {
            // Crear diálogo para mostrar errores
            JDialog erroresDialog = new JDialog(this, "Reporte de Errores", true);
            erroresDialog.setSize(700, 400);
            erroresDialog.setLayout(new BorderLayout());
            erroresDialog.setLocationRelativeTo(this);
            
            JTextArea erroresArea = new JTextArea();
            erroresArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
            erroresArea.setEditable(false);
            erroresArea.setForeground(Color.RED);
            
            StringBuilder content = new StringBuilder("ERRORES ENCONTRADOS:\n");
            content.append("====================\n\n");
            
            for (int i = 0; i < Parser.errores.size(); i++) {
                content.append((i + 1) + ". ").append(Parser.errores.get(i)).append("\n\n");
            }
            
            erroresArea.setText(content.toString());
            
            JScrollPane scrollPane = new JScrollPane(erroresArea);
            erroresDialog.add(scrollPane, BorderLayout.CENTER);
            
            // Botón para cerrar
            JButton closeButton = new JButton("Cerrar");
            closeButton.addActionListener(e -> erroresDialog.dispose());
            
            JPanel buttonPanel = new JPanel();
            buttonPanel.add(closeButton);
            erroresDialog.add(buttonPanel, BorderLayout.SOUTH);
            
            erroresDialog.setVisible(true);
        }
    }
    
    private void mostrarAutomatas() {
        if (Parser.automatas.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "No hay autómatas definidos. Ejecute el análisis primero.", 
                "Autómatas", 
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        JDialog dialog = new JDialog(this, "Autómatas Definidos", true);
        dialog.setSize(600, 400);
        dialog.setLayout(new BorderLayout());
        dialog.setLocationRelativeTo(this);
        
        JTextArea automatasArea = new JTextArea();
        automatasArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        automatasArea.setEditable(false);
        
        StringBuilder content = new StringBuilder();
        content.append("AUTÓMATAS DEFINIDOS:\n");
        content.append("=====================\n\n");
        
        for (String nombre : Parser.automatas.keySet()) {
            Object automata = Parser.automatas.get(nombre);
            if (automata instanceof AFD) {
                content.append(((AFD)automata).getDescripcionCompleta()).append("\n");
            } else if (automata instanceof AP) {
                content.append("AP: ").append(nombre).append(" (Autómata de Pila)\n");
                // Aquí podrías agregar más detalles del AP si lo deseas
            }
            content.append("----------------------------------------\n");
        }
        
        automatasArea.setText(content.toString());
        
        JScrollPane scrollPane = new JScrollPane(automatasArea);
        dialog.add(scrollPane, BorderLayout.CENTER);
        
        JButton closeButton = new JButton("Cerrar");
        closeButton.addActionListener(e -> dialog.dispose());
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(closeButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.setVisible(true);
    }
    
    private void mostrarGraficos() {
        if (Parser.automatas.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "No hay autómatas definidos para graficar.", 
                "Gráficos", 
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        JDialog dialog = new JDialog(this, "Generar Gráficos", true);
        dialog.setSize(400, 300);
        dialog.setLayout(new BorderLayout());
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel titleLabel = new JLabel("Seleccione autómata para graficar:");
        panel.add(titleLabel);
        
        JComboBox<String> automataCombo = new JComboBox<>();
        for (String nombre : Parser.automatas.keySet()) {
            Object automata = Parser.automatas.get(nombre);
            if (automata instanceof AFD) {
                automataCombo.addItem(nombre + " (AFD)");
            }
        }
        panel.add(automataCombo);
        
        JComboBox<String> formatCombo = new JComboBox<>(new String[]{"png", "jpg", "svg", "pdf"});
        panel.add(new JLabel("Formato:"));
        panel.add(formatCombo);
        
        JButton generateButton = new JButton("Generar Gráfico");
        generateButton.addActionListener(e -> {
            String selected = (String) automataCombo.getSelectedItem();
            if (selected != null) {
                String nombre = selected.split(" ")[0];
                String formato = (String) formatCombo.getSelectedItem();
                
                Object automata = Parser.automatas.get(nombre);
                if (automata instanceof AFD) {
                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.setSelectedFile(new File(nombre + "." + formato));
                    fileChooser.setDialogTitle("Guardar gráfico como");
                    
                    if (fileChooser.showSaveDialog(dialog) == JFileChooser.APPROVE_OPTION) {
                        File outputFile = fileChooser.getSelectedFile();
                        boolean success = Graphviz.generarImagenAFD((AFD)automata, formato, outputFile.getAbsolutePath());
                        
                        if (success) {
                            JOptionPane.showMessageDialog(dialog, 
                                "Gráfico generado exitosamente: " + outputFile.getName(), 
                                "Éxito", 
                                JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(dialog, 
                                "Error al generar gráfico. Verifique que Graphviz esté instalado.", 
                                "Error", 
                                JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            }
        });
        
        panel.add(generateButton);
        
        dialog.add(panel, BorderLayout.CENTER);
        dialog.setVisible(true);
    }
    
    private void mostrarReportePasos() {
        JOptionPane.showMessageDialog(this, 
            "Función de reporte de pasos en desarrollo.", 
            "Reporte de Pasos", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void mostrarAcercaDe() {
        String acercaDe = "AutómataLab v1.0\n\n" +
                         "Organización de Lenguajes y Compiladores 1\n" +
                         "Primer Semestre 2025\n\n" +
                         "Desarrollado con Java, JFlex y CUP\n" +
                         "© 2025 - Todos los derechos reservados";
        
        JOptionPane.showMessageDialog(this, 
            acercaDe, 
            "Acerca de AutómataLab", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void mostrarManual() {
        JOptionPane.showMessageDialog(this, 
            "Consulte el archivo MANUAL_USUARIO.md para más información.", 
            "Manual de Usuario", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    public static void main(String[] args) {
        // Establecer look and feel del sistema
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Mostrar la ventana principal
        java.awt.EventQueue.invokeLater(() -> {
            new MainWindow().setVisible(true);
        });
    }
}