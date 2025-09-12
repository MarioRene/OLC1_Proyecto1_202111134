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
    private ConsolePanel consolePanel;
    private JTabbedPane tabbedPane;
    private JLabel statusLabel;
    private JLabel positionLabel;
    private File currentFile;
    
    public MainWindow() {
        initComponents();
        setTitle("AutómataLab - OLC1 Proyecto 1");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        
        // Crear barra de menú
        createMenuBar();
        
        // Panel principal con división
        JSplitPane mainSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        mainSplitPane.setDividerLocation(500);
        mainSplitPane.setResizeWeight(0.7);
        
        // Panel superior: Editor
        editorPanel = new EditorPanel();
        JPanel editorContainer = new JPanel(new BorderLayout());
        editorContainer.setBorder(BorderFactory.createTitledBorder("Editor de Código (.atm)"));
        editorContainer.add(editorPanel, BorderLayout.CENTER);
        
        // Panel inferior: Consola
        consolePanel = new ConsolePanel();
        JPanel consoleContainer = new JPanel(new BorderLayout());
        consoleContainer.setBorder(BorderFactory.createTitledBorder("Consola de Salida"));
        consoleContainer.add(consolePanel, BorderLayout.CENTER);
        
        mainSplitPane.setTopComponent(editorContainer);
        mainSplitPane.setBottomComponent(consoleContainer);
        
        add(mainSplitPane, BorderLayout.CENTER);
        
        // Barra de estado
        createStatusBar();
        
        // Cargar ejemplo inicial
        cargarEjemploInicial();
        
        // Listeners para actualizar barra de estado
        setupStatusBarListeners();
    }
    
    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        // Menú Archivo
        JMenu archivoMenu = new JMenu("Archivo");
        addMenuItem(archivoMenu, "Nuevo", "Ctrl+N", e -> nuevoArchivo());
        addMenuItem(archivoMenu, "Abrir", "Ctrl+O", e -> abrirArchivo());
        addMenuItem(archivoMenu, "Guardar", "Ctrl+S", e -> guardarArchivo());
        addMenuItem(archivoMenu, "Guardar como...", "Ctrl+Shift+S", e -> guardarComoArchivo());
        archivoMenu.addSeparator();
        addMenuItem(archivoMenu, "Salir", "Alt+F4", e -> salir());
        
        // Menú Editar
        JMenu editarMenu = new JMenu("Editar");
        addMenuItem(editarMenu, "Cortar", "Ctrl+X", e -> editorPanel.getTextArea().cut());
        addMenuItem(editarMenu, "Copiar", "Ctrl+C", e -> editorPanel.getTextArea().copy());
        addMenuItem(editarMenu, "Pegar", "Ctrl+V", e -> editorPanel.getTextArea().paste());
        editarMenu.addSeparator();
        addMenuItem(editarMenu, "Buscar", "Ctrl+F", e -> mostrarDialogoBusqueda());
        
        // Menú Ejecutar
        JMenu ejecutarMenu = new JMenu("Ejecutar");
        addMenuItem(ejecutarMenu, "Ejecutar", "F5", e -> ejecutar());
        addMenuItem(ejecutarMenu, "Limpiar Consola", "Ctrl+L", e -> consolePanel.clear());
        
        // Menú Reportes
        JMenu reportesMenu = new JMenu("Reportes");
        addMenuItem(reportesMenu, "Tokens", null, e -> mostrarTokens());
        addMenuItem(reportesMenu, "Errores", null, e -> mostrarErrores());
        addMenuItem(reportesMenu, "Autómatas", null, e -> mostrarAutomatas());
        addMenuItem(reportesMenu, "Generar Gráfico", null, e -> mostrarGraficos());
        
        // Menú Ayuda
        JMenu ayudaMenu = new JMenu("Ayuda");
        addMenuItem(ayudaMenu, "Manual de Usuario", null, e -> mostrarManual());
        addMenuItem(ayudaMenu, "Acerca de", null, e -> mostrarAcercaDe());
        
        menuBar.add(archivoMenu);
        menuBar.add(editarMenu);
        menuBar.add(ejecutarMenu);
        menuBar.add(reportesMenu);
        menuBar.add(ayudaMenu);
        
        setJMenuBar(menuBar);
    }
    
    private void addMenuItem(JMenu menu, String text, String shortcut, ActionListener action) {
        JMenuItem item = new JMenuItem(text);
        if (shortcut != null) {
            item.setAccelerator(KeyStroke.getKeyStroke(shortcut));
        }
        item.addActionListener(action);
        menu.add(item);
    }
    
    private void createStatusBar() {
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBorder(BorderFactory.createEtchedBorder());
        
        statusLabel = new JLabel(" Listo para ejecutar");
        positionLabel = new JLabel(" Línea: 1, Columna: 1 ");
        
        statusBar.add(statusLabel, BorderLayout.WEST);
        statusBar.add(positionLabel, BorderLayout.EAST);
        
        add(statusBar, BorderLayout.SOUTH);
    }
    
    private void setupStatusBarListeners() {
        editorPanel.getTextArea().addCaretListener(e -> {
            int line = editorPanel.getCaretLine();
            int column = editorPanel.getCaretColumn();
            positionLabel.setText(" Línea: " + line + ", Columna: " + column + " ");
        });
    }
    
    private void cargarEjemploInicial() {
        String ejemplo = "// Ejemplo de AFD que acepta cadenas con número par de 0s\n" +
                        "<AFD Nombre=\"AFD_Par\">\n" +
                        "  N = {q0, q1};\n" +
                        "  T = {'0', '1'};\n" +
                        "  I = {q0};\n" +
                        "  A = {q0};\n" +
                        "  \n" +
                        "  Transiciones:\n" +
                        "    q0 -> '0', q1;\n" +
                        "    q0 -> '1', q0;\n" +
                        "    q1 -> '0', q0;\n" +
                        "    q1 -> '1', q1;\n" +
                        "</AFD>\n\n" +
                        "// Pruebas\n" +
                        "verAutomatas();\n" +
                        "desc(AFD_Par);\n" +
                        "AFD_Par(\"1010\");\n" +
                        "AFD_Par(\"100\");";
        
        editorPanel.setText(ejemplo);
        statusLabel.setText(" Ejemplo cargado - Listo para ejecutar");
    }
    
    private void nuevoArchivo() {
        if (hayModificacionesSinGuardar()) {
            int option = JOptionPane.showConfirmDialog(this, 
                    "¿Desea guardar los cambios actuales?", 
                    "Nuevo archivo", 
                    JOptionPane.YES_NO_CANCEL_OPTION);
            
            if (option == JOptionPane.YES_OPTION) {
                guardarArchivo();
            } else if (option == JOptionPane.CANCEL_OPTION) {
                return;
            }
        }
        
        editorPanel.clear();
        consolePanel.clear();
        Parser.automatas.clear();
        Parser.errores.clear();
        currentFile = null;
        setTitle("AutómataLab - Nuevo archivo");
        statusLabel.setText(" Nuevo archivo creado");
    }
    
    private void abrirArchivo() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Archivos ATM (*.atm)", "atm"));
        fileChooser.setDialogTitle("Abrir archivo .atm");
        
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                currentFile = fileChooser.getSelectedFile();
                BufferedReader reader = new BufferedReader(new FileReader(currentFile));
                StringBuilder content = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }
                reader.close();
                
                editorPanel.setText(content.toString());
                setTitle("AutómataLab - " + currentFile.getName());
                statusLabel.setText(" Archivo abierto: " + currentFile.getName());
                
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, 
                    "Error al abrir archivo: " + e.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
                statusLabel.setText(" Error al abrir archivo");
            }
        }
    }
    
    private void guardarArchivo() {
        if (currentFile == null) {
            guardarComoArchivo();
        } else {
            guardarArchivo(currentFile);
        }
    }
    
    private void guardarComoArchivo() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Archivos ATM (*.atm)", "atm"));
        fileChooser.setDialogTitle("Guardar archivo .atm");
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".atm")) {
                file = new File(file.getAbsolutePath() + ".atm");
            }
            guardarArchivo(file);
        }
    }
    
    private void guardarArchivo(File file) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write(editorPanel.getText());
            writer.close();
            
            currentFile = file;
            setTitle("AutómataLab - " + file.getName());
            statusLabel.setText(" Archivo guardado: " + file.getName());
            
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, 
                "Error al guardar archivo: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            statusLabel.setText(" Error al guardar archivo");
        }
    }
    
    private boolean hayModificacionesSinGuardar() {
        // Implementación simple - en una aplicación real se compararía con la última versión guardada
        return !editorPanel.getText().trim().isEmpty();
    }
    
    private void salir() {
        if (hayModificacionesSinGuardar()) {
            int option = JOptionPane.showConfirmDialog(this, 
                    "¿Desea guardar los cambios antes de salir?", 
                    "Salir", 
                    JOptionPane.YES_NO_CANCEL_OPTION);
            
            if (option == JOptionPane.YES_OPTION) {
                guardarArchivo();
            } else if (option == JOptionPane.CANCEL_OPTION) {
                return;
            }
        }
        System.exit(0);
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
        statusLabel.setText(" Ejecutando análisis...");
        consolePanel.clear();
        Parser.errores.clear();
        
        try {
            String input = editorPanel.getText();
            
            // Redirigir salida a consola
            System.setOut(consolePanel.getConsoleStream());
            System.setErr(consolePanel.getConsoleStream());
            
            consolePanel.append("=== INICIANDO ANÁLISIS ===\n");
            long startTime = System.currentTimeMillis();
            
            // Crear analizadores
            Lexer lexer = new Lexer(new StringReader(input));
            Parser parser = new Parser(lexer);
            
            // Ejecutar análisis
            parser.parse();
            
            long endTime = System.currentTimeMillis();
            consolePanel.append("\n=== ANÁLISIS COMPLETADO ===\n");
            consolePanel.append("Tiempo de ejecución: " + (endTime - startTime) + " ms\n");
            consolePanel.append("Autómatas definidos: " + Parser.automatas.size() + "\n");
            consolePanel.append("Errores encontrados: " + Parser.errores.size() + "\n");
            
            statusLabel.setText(" Análisis completado - " + Parser.automatas.size() + " autómatas, " + 
                               Parser.errores.size() + " errores");
            
        } catch (Exception e) {
            consolePanel.append("Error durante la ejecución: " + e.getMessage() + "\n");
            statusLabel.setText(" Error en ejecución");
            e.printStackTrace();
        }
    }
    
    private void mostrarTokens() {
        try {
            String input = editorPanel.getText();
            java.util.List<Token> tokens = ReporteTokens.analizarTokens(input);
            
            JDialog tokensDialog = new JDialog(this, "Reporte de Tokens", true);
            tokensDialog.setSize(800, 600);
            tokensDialog.setLocationRelativeTo(this);
            
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
            JScrollPane scrollPane = new JScrollPane(tokensTable);
            tokensDialog.add(scrollPane, BorderLayout.CENTER);
            
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
        if (Parser.errores.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "No se encontraron errores en el análisis.", 
                "Reporte de Errores", 
                JOptionPane.INFORMATION_MESSAGE);
        } else {
            JDialog erroresDialog = new JDialog(this, "Reporte de Errores", true);
            erroresDialog.setSize(700, 400);
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
        dialog.setSize(400, 250);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Seleccione autómata:"), gbc);
        
        gbc.gridx = 1;
        JComboBox<String> automataCombo = new JComboBox<>();
        for (String nombre : Parser.automatas.keySet()) {
            Object automata = Parser.automatas.get(nombre);
            if (automata instanceof AFD) {
                automataCombo.addItem(nombre + " (AFD)");
            }
        }
        panel.add(automataCombo, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Formato:"), gbc);
        
        gbc.gridx = 1;
        JComboBox<String> formatCombo = new JComboBox<>(new String[]{"png", "jpg", "svg"});
        panel.add(formatCombo, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        JButton generateButton = new JButton("Generar Gráfico");
        generateButton.addActionListener(e -> {
            String selected = (String) automataCombo.getSelectedItem();
            if (selected != null) {
                String nombre = selected.split(" ")[0];
                String formato = (String) formatCombo.getSelectedItem();
                
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setSelectedFile(new File(nombre + "." + formato));
                
                if (fileChooser.showSaveDialog(dialog) == JFileChooser.APPROVE_OPTION) {
                    File outputFile = fileChooser.getSelectedFile();
                    Object automata = Parser.automatas.get(nombre);
                    
                    if (automata instanceof AFD) {
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
        panel.add(generateButton, gbc);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    private void mostrarAcercaDe() {
        String acercaDe = "AutómataLab v1.0\n\n" +
                         "Organización de Lenguajes y Compiladores 1\n" +
                         "Primer Semestre 2025\n\n" +
                         "Desarrollado con Java, JFlex y CUP\n" +
                         "Copyright 2025 - Todos los derechos reservados";
        
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
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            new MainWindow().setVisible(true);
        });
    }
}