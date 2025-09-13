package interfaz;

import javax.swing.*;
import javax.swing.border.TitledBorder;
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
    private JLabel statusLabel;
    private JLabel positionLabel;
    private File currentFile;
    private JTabbedPane reportTabs;
    private JPanel graphPanel;
    
    public MainWindow() {
        initComponents();
        setTitle("AutómataLab - OLC1 Proyecto 1");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 900);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        
        // Configurar look and feel mejorado
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        
        // Crear barra de menú mejorada
        createMenuBar();
        
        // Crear toolbar
        createToolBar();
        
        // Panel principal con división triple
        JSplitPane mainSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        mainSplitPane.setDividerLocation(400);
        mainSplitPane.setResizeWeight(0.5);
        
        // Panel superior: Editor con mejoras visuales
        editorPanel = new EditorPanel();
        JPanel editorContainer = createStyledPanel("Editor de Código (.atm)", editorPanel);
        
        // Panel inferior dividido: Consola y Reportes
        JSplitPane bottomSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        bottomSplitPane.setDividerLocation(600);
        bottomSplitPane.setResizeWeight(0.6);
        
        // Consola con mejoras
        consolePanel = new ConsolePanel();
        JPanel consoleContainer = createStyledPanel("Consola de Salida", consolePanel);
        
        // Panel de reportes con pestañas
        createReportPanel();
        JPanel reportContainer = createStyledPanel("Reportes y Visualización", reportTabs);
        
        bottomSplitPane.setLeftComponent(consoleContainer);
        bottomSplitPane.setRightComponent(reportContainer);
        
        mainSplitPane.setTopComponent(editorContainer);
        mainSplitPane.setBottomComponent(bottomSplitPane);
        
        add(mainSplitPane, BorderLayout.CENTER);
        
        // Barra de estado mejorada
        createStatusBar();
        
        // Cargar ejemplo inicial
        cargarEjemploInicial();
        
        // Listeners para actualizar barra de estado
        setupStatusBarListeners();
    }
    
    private JPanel createStyledPanel(String title, Component content) {
        JPanel panel = new JPanel(new BorderLayout());
        TitledBorder border = BorderFactory.createTitledBorder(title);
        border.setTitleFont(new Font("SansSerif", Font.BOLD, 12));
        border.setTitleColor(new Color(70, 130, 180));
        panel.setBorder(border);
        panel.add(content, BorderLayout.CENTER);
        return panel;
    }
    
    private void createToolBar() {
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.setBorder(BorderFactory.createEtchedBorder());
        
        // Botones de archivo
        addToolBarButton(toolBar, "Nuevo", "⊞", e -> nuevoArchivo());
        addToolBarButton(toolBar, "Abrir", "📁", e -> abrirArchivo());
        addToolBarButton(toolBar, "Guardar", "💾", e -> guardarArchivo());
        
        toolBar.addSeparator();
        
        // Botones de ejecución
        JButton ejecutarBtn = addToolBarButton(toolBar, "Ejecutar", "▶", e -> ejecutar());
        ejecutarBtn.setBackground(new Color(46, 125, 50));
        ejecutarBtn.setForeground(Color.WHITE);
        ejecutarBtn.setOpaque(true);
        
        addToolBarButton(toolBar, "Limpiar", "🗑", e -> {
            consolePanel.clear();
            clearAllReports();
        });
        
        toolBar.addSeparator();
        
        // Botones de reportes
        addToolBarButton(toolBar, "Tokens", "🔤", e -> mostrarTokens());
        addToolBarButton(toolBar, "Errores", "⚠", e -> mostrarErrores());
        addToolBarButton(toolBar, "Gráficos", "📊", e -> generarGraficos());
        
        add(toolBar, BorderLayout.NORTH);
    }
    
    private JButton addToolBarButton(JToolBar toolBar, String tooltip, String icon, ActionListener action) {
        JButton button = new JButton(icon);
        button.setToolTipText(tooltip);
        button.addActionListener(action);
        button.setFocusable(false);
        button.setPreferredSize(new Dimension(35, 35));
        toolBar.add(button);
        return button;
    }
    
    private void createReportPanel() {
        reportTabs = new JTabbedPane();
        reportTabs.setFont(new Font("SansSerif", Font.PLAIN, 11));
        
        // Pestaña de autómatas
        JTextArea automatasArea = new JTextArea();
        automatasArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        automatasArea.setEditable(false);
        automatasArea.setBackground(new Color(248, 248, 248));
        JScrollPane automatasScroll = new JScrollPane(automatasArea);
        reportTabs.addTab("Autómatas", automatasScroll);
        
        // Pestaña de tokens
        JTable tokensTable = new JTable();
        tokensTable.setFont(new Font("SansSerif", Font.PLAIN, 11));
        tokensTable.setGridColor(Color.LIGHT_GRAY);
        tokensTable.setRowHeight(20);
        JScrollPane tokensScroll = new JScrollPane(tokensTable);
        reportTabs.addTab("Tokens", tokensScroll);
        
        // Pestaña de errores
        JTextArea erroresArea = new JTextArea();
        erroresArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        erroresArea.setEditable(false);
        erroresArea.setForeground(Color.RED);
        erroresArea.setBackground(new Color(255, 248, 248));
        JScrollPane erroresScroll = new JScrollPane(erroresArea);
        reportTabs.addTab("Errores", erroresScroll);
        
        // Pestaña de gráficos
        graphPanel = new JPanel(new BorderLayout());
        graphPanel.setBackground(Color.WHITE);
        JLabel graphLabel = new JLabel("Los gráficos aparecerán aquí después de ejecutar", SwingConstants.CENTER);
        graphLabel.setForeground(Color.GRAY);
        graphPanel.add(graphLabel, BorderLayout.CENTER);
        reportTabs.addTab("Gráficos", graphPanel);
    }
    
    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBorder(BorderFactory.createEtchedBorder());
        
        // Menú Archivo
        JMenu archivoMenu = new JMenu("Archivo");
        addMenuItem(archivoMenu, "Nuevo", "ctrl N", e -> nuevoArchivo());
        addMenuItem(archivoMenu, "Abrir", "ctrl O", e -> abrirArchivo());
        addMenuItem(archivoMenu, "Guardar", "ctrl S", e -> guardarArchivo());
        addMenuItem(archivoMenu, "Guardar como...", "ctrl shift S", e -> guardarComoArchivo());
        archivoMenu.addSeparator();
        addMenuItem(archivoMenu, "Exportar Reportes", null, e -> exportarReportes());
        archivoMenu.addSeparator();
        addMenuItem(archivoMenu, "Salir", "alt F4", e -> salir());
        
        // Menú Editar
        JMenu editarMenu = new JMenu("Editar");
        addMenuItem(editarMenu, "Cortar", "ctrl X", e -> editorPanel.getTextArea().cut());
        addMenuItem(editarMenu, "Copiar", "ctrl C", e -> editorPanel.getTextArea().copy());
        addMenuItem(editarMenu, "Pegar", "ctrl V", e -> editorPanel.getTextArea().paste());
        addMenuItem(editarMenu, "Deshacer", "ctrl Z", e -> editorPanel.undo());
        addMenuItem(editarMenu, "Rehacer", "ctrl Y", e -> editorPanel.redo());
        editarMenu.addSeparator();
        addMenuItem(editarMenu, "Buscar", "ctrl F", e -> mostrarDialogoBusqueda());
        addMenuItem(editarMenu, "Ir a línea", "ctrl G", e -> mostrarDialogoIrLinea());
        
        // Menú Ejecutar
        JMenu ejecutarMenu = new JMenu("Ejecutar");
        addMenuItem(ejecutarMenu, "Ejecutar", "F5", e -> ejecutar());
        addMenuItem(ejecutarMenu, "Compilar", "F7", e -> compilar());
        addMenuItem(ejecutarMenu, "Limpiar Consola", "ctrl L", e -> consolePanel.clear());
        addMenuItem(ejecutarMenu, "Limpiar Todo", "ctrl shift L", e -> limpiarTodo());
        
        // Menú Reportes
        JMenu reportesMenu = new JMenu("Reportes");
        addMenuItem(reportesMenu, "Análisis Léxico", null, e -> mostrarTokens());
        addMenuItem(reportesMenu, "Errores", null, e -> mostrarErrores());
        addMenuItem(reportesMenu, "Autómatas", null, e -> mostrarAutomatas());
        reportesMenu.addSeparator();
        addMenuItem(reportesMenu, "Generar Gráficos", null, e -> generarGraficos());
        addMenuItem(reportesMenu, "Exportar HTML", null, e -> exportarHTML());
        
        // Menú Ver
        JMenu verMenu = new JMenu("Ver");
        addMenuItem(verMenu, "Aumentar fuente", "ctrl PLUS", e -> editorPanel.increaseFontSize());
        addMenuItem(verMenu, "Disminuir fuente", "ctrl MINUS", e -> editorPanel.decreaseFontSize());
        verMenu.addSeparator();
        addMenuItem(verMenu, "Mostrar números de línea", null, e -> toggleLineNumbers());
        
        // Menú Ayuda
        JMenu ayudaMenu = new JMenu("Ayuda");
        addMenuItem(ayudaMenu, "Manual de Usuario", "F1", e -> mostrarManual());
        addMenuItem(ayudaMenu, "Ejemplos", null, e -> mostrarEjemplos());
        addMenuItem(ayudaMenu, "Acerca de", null, e -> mostrarAcercaDe());
        
        menuBar.add(archivoMenu);
        menuBar.add(editarMenu);
        menuBar.add(ejecutarMenu);
        menuBar.add(reportesMenu);
        menuBar.add(verMenu);
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
        statusBar.setPreferredSize(new Dimension(0, 25));
        
        statusLabel = new JLabel(" Listo para ejecutar");
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        
        positionLabel = new JLabel(" Línea: 1, Columna: 1 ");
        positionLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        
        JLabel timeLabel = new JLabel();
        timeLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        
        // Actualizar tiempo cada segundo
        Timer timer = new Timer(1000, e -> {
            timeLabel.setText(" " + java.time.LocalTime.now().format(
                java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss")) + " ");
        });
        timer.start();
        
        statusBar.add(statusLabel, BorderLayout.WEST);
        statusBar.add(positionLabel, BorderLayout.CENTER);
        statusBar.add(timeLabel, BorderLayout.EAST);
        
        add(statusBar, BorderLayout.SOUTH);
    }
    
    private void setupStatusBarListeners() {
        editorPanel.getTextArea().addCaretListener(e -> {
            int line = editorPanel.getCaretLine();
            int column = editorPanel.getCaretColumn();
            int chars = editorPanel.getText().length();
            int lines = editorPanel.getTextArea().getLineCount();
            positionLabel.setText(String.format(" Línea: %d, Columna: %d | Caracteres: %d | Líneas: %d ", 
                    line, column, chars, lines));
        });
    }
    
    private void cargarEjemploInicial() {
        String ejemplo = 
                "// Ejemplo 1: AFD que acepta cadenas con número par de 0s\n" +
                "<AFD Nombre=\"AFD_Par_Ceros\">\n" +
                "  N = {q0, q1};\n" +
                "  T = {'0', '1'};\n" +
                "  I = {q0};\n" +
                "  A = {q0};\n" +
                "  \n" +
                "  Transiciones:\n" +
                "    q0 -> '0', q1 | '1', q0;\n" +
                "    q1 -> '0', q0 | '1', q1;\n" +
                "</AFD>\n" +
                "\n" +
                "// Ejemplo 2: AP que acepta L = {a^n b^n | n >= 1}\n" +
                "<AP Nombre=\"AP_AnBn\">\n" +
                "  N = {q0, q1, q2};\n" +
                "  T = {'a', 'b'};\n" +
                "  P = {'A', 'Z'};\n" +
                "  I = {q0};\n" +
                "  A = {q2};\n" +
                "  \n" +
                "  Transiciones:\n" +
                "    q0 ('a') -> ('Z'), q1 : ('A');\n" +
                "    q1 ('a') -> ('A'), q1 : ('A') | ('b') -> ('A'), q1 : ('$');\n" +
                "    q1 ('$') -> ('Z'), q2 : ('$');\n" +
                "</AP>\n" +
                "\n" +
                "// Comandos de prueba\n" +
                "verAutomatas();\n" +
                "desc(AFD_Par_Ceros);\n" +
                "AFD_Par_Ceros(\"1010\");\n" +
                "AFD_Par_Ceros(\"100\");\n" +
                "AP_AnBn(\"aabb\");\n" +
                "AP_AnBn(\"aaabbb\");\n";
        
        editorPanel.setText(ejemplo);
        statusLabel.setText(" Ejemplos cargados - Listo para ejecutar");
    }
    
    // Métodos mejorados de funcionalidad
    
    private void ejecutar() {
        statusLabel.setText(" Ejecutando análisis...");
        consolePanel.clear();
        Parser.errores.clear();
        clearAllReports();
        
        SwingUtilities.invokeLater(() -> {
            try {
                String input = editorPanel.getText();
                
                // Redirigir salida a consola
                System.setOut(consolePanel.getConsoleStream());
                System.setErr(consolePanel.getConsoleStream());
                
                consolePanel.append("=== INICIANDO ANÁLISIS LÉXICO Y SINTÁCTICO ===\n");
                consolePanel.append("Archivo: " + (currentFile != null ? currentFile.getName() : "Sin título") + "\n");
                consolePanel.append("Tamaño: " + input.length() + " caracteres, " + 
                                  editorPanel.getTextArea().getLineCount() + " líneas\n");
                consolePanel.append("Hora: " + java.time.LocalDateTime.now().format(
                    java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "\n");
                consolePanel.append("===============================================\n\n");
                
                long startTime = System.currentTimeMillis();
                
                // Crear analizadores
                Lexer lexer = new Lexer(new StringReader(input));
                Parser parser = new Parser(lexer);
                
                // Ejecutar análisis
                Symbol result = parser.parse();
                
                long endTime = System.currentTimeMillis();
                
                consolePanel.append("\n===============================================\n");
                consolePanel.append("=== ANÁLISIS COMPLETADO ===\n");
                consolePanel.append("Tiempo de ejecución: " + (endTime - startTime) + " ms\n");
                consolePanel.append("Autómatas definidos: " + Parser.automatas.size() + "\n");
                consolePanel.append("Errores encontrados: " + Parser.errores.size() + "\n");
                
                if (Parser.errores.isEmpty()) {
                    consolePanel.append("Estado: ✓ ANÁLISIS EXITOSO\n");
                    statusLabel.setText(" ✓ Análisis exitoso - " + Parser.automatas.size() + " autómatas definidos");
                } else {
                    consolePanel.append("Estado: ✗ ERRORES ENCONTRADOS\n");
                    statusLabel.setText(" ✗ Errores encontrados - Revise la pestaña de errores");
                }
                consolePanel.append("===============================================\n");
                
                // Actualizar reportes automáticamente
                actualizarReportes();
                
            } catch (Exception e) {
                consolePanel.append("\n❌ ERROR CRÍTICO DURANTE LA EJECUCIÓN:\n");
                consolePanel.append(e.getMessage() + "\n");
                statusLabel.setText(" ❌ Error crítico en ejecución");
                e.printStackTrace(consolePanel.getConsoleStream());
            }
        });
    }
    
    private void compilar() {
        statusLabel.setText(" Compilando...");
        try {
            String input = editorPanel.getText();
            Lexer lexer = new Lexer(new StringReader(input));
            Parser parser = new Parser(lexer);
            parser.parse();
            
            if (Parser.errores.isEmpty()) {
                statusLabel.setText(" ✓ Compilación exitosa");
                JOptionPane.showMessageDialog(this, 
                    "Compilación exitosa\n" + Parser.automatas.size() + " autómatas definidos", 
                    "Compilación", 
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                statusLabel.setText(" ✗ Errores de compilación");
                mostrarErrores();
            }
        } catch (Exception e) {
            statusLabel.setText(" ❌ Error de compilación");
            JOptionPane.showMessageDialog(this, 
                "Error de compilación: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void actualizarReportes() {
        // Actualizar pestaña de autómatas
        actualizarAutomatas();
        // Actualizar pestaña de tokens
        actualizarTokens();
        // Actualizar pestaña de errores
        actualizarErrores();
        // Generar gráficos automáticamente
        generarGraficos();
    }
    
    private void actualizarAutomatas() {
        JScrollPane scrollPane = (JScrollPane) reportTabs.getComponentAt(0);
        JTextArea automatasArea = (JTextArea) scrollPane.getViewport().getView();
        
        StringBuilder content = new StringBuilder();
        if (Parser.automatas.isEmpty()) {
            content.append("No hay autómatas definidos.\n");
            content.append("Ejecute el análisis para ver los autómatas creados.");
        } else {
            content.append("AUTÓMATAS DEFINIDOS (" + Parser.automatas.size() + "):\n");
            content.append("═".repeat(50)).append("\n\n");
            
            int count = 1;
            for (String nombre : Parser.automatas.keySet()) {
                Object automata = Parser.automatas.get(nombre);
                content.append(count++).append(". ");
                
                if (automata instanceof AFD) {
                    AFD afd = (AFD) automata;
                    content.append("📊 AFD: ").append(nombre).append("\n");
                    content.append("   Estados: ").append(afd.getEstados().size()).append("\n");
                    content.append("   Alfabeto: ").append(afd.getAlfabeto().size()).append(" símbolos\n");
                    content.append("   Estado inicial: ").append(afd.getEstadoInicial()).append("\n");
                    content.append("   Estados finales: ").append(afd.getEstadosAceptacion().size()).append("\n");
                } else if (automata instanceof AP) {
                    AP ap = (AP) automata;
                    content.append("🏗️ AP: ").append(nombre).append("\n");
                    content.append("   Estados: ").append(ap.getEstados().size()).append("\n");
                    content.append("   Alfabeto: ").append(ap.getAlfabeto().size()).append(" símbolos\n");
                    content.append("   Símbolos de pila: ").append(ap.getSimbolosPila().size()).append("\n");
                }
                content.append("\n");
            }
        }
        
        automatasArea.setText(content.toString());
        automatasArea.setCaretPosition(0);
    }
    
    private void actualizarTokens() {
        try {
            String input = editorPanel.getText();
            java.util.List<Token> tokens = ReporteTokens.analizarTokens(input);
            
            JScrollPane scrollPane = (JScrollPane) reportTabs.getComponentAt(1);
            JTable tokensTable = (JTable) scrollPane.getViewport().getView();
            
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
            
            tokensTable.setModel(new javax.swing.table.DefaultTableModel(data, columnNames) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            });
            
            // Ajustar ancho de columnas
            tokensTable.getColumnModel().getColumn(0).setPreferredWidth(50);
            tokensTable.getColumnModel().getColumn(1).setPreferredWidth(150);
            tokensTable.getColumnModel().getColumn(2).setPreferredWidth(120);
            tokensTable.getColumnModel().getColumn(3).setPreferredWidth(60);
            tokensTable.getColumnModel().getColumn(4).setPreferredWidth(80);
            
        } catch (Exception e) {
            statusLabel.setText(" Error al generar tokens");
        }
    }
    
    private void actualizarErrores() {
        JScrollPane scrollPane = (JScrollPane) reportTabs.getComponentAt(2);
        JTextArea erroresArea = (JTextArea) scrollPane.getViewport().getView();
        
        StringBuilder content = new StringBuilder();
        if (Parser.errores.isEmpty()) {
            content.append("✅ No se encontraron errores.\n\n");
            content.append("El análisis léxico y sintáctico fue exitoso.\n");
            content.append("Todos los autómatas fueron procesados correctamente.");
            erroresArea.setForeground(new Color(0, 128, 0));
        } else {
            content.append("❌ ERRORES ENCONTRADOS (").append(Parser.errores.size()).append("):\n");
            content.append("═".repeat(50)).append("\n\n");
            
            for (int i = 0; i < Parser.errores.size(); i++) {
                content.append((i + 1)).append(". ").append(Parser.errores.get(i)).append("\n\n");
            }
            erroresArea.setForeground(Color.RED);
        }
        
        erroresArea.setText(content.toString());
        erroresArea.setCaretPosition(0);
    }
    
    private void generarGraficos() {
        graphPanel.removeAll();
        
        if (Parser.automatas.isEmpty()) {
            JLabel noDataLabel = new JLabel("No hay autómatas para graficar", SwingConstants.CENTER);
            noDataLabel.setForeground(Color.GRAY);
            noDataLabel.setFont(new Font("SansSerif", Font.ITALIC, 14));
            graphPanel.add(noDataLabel, BorderLayout.CENTER);
        } else {
            // Crear panel con lista de autómatas y área de visualización
            JPanel graphContent = new JPanel(new BorderLayout());
            
            // Lista de autómatas
            DefaultListModel<String> listModel = new DefaultListModel<>();
            for (String nombre : Parser.automatas.keySet()) {
                Object automata = Parser.automatas.get(nombre);
                String tipo = (automata instanceof AFD) ? "AFD" : "AP";
                listModel.addElement(tipo + ": " + nombre);
            }
            
            JList<String> automatasList = new JList<>(listModel);
            automatasList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            automatasList.setFont(new Font("SansSerif", Font.PLAIN, 12));
            
            // Área de código DOT
            JTextArea dotArea = new JTextArea();
            dotArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
            dotArea.setEditable(false);
            dotArea.setBackground(new Color(245, 245, 245));
            
            // Listener para mostrar DOT del autómata seleccionado
            automatasList.addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting()) {
                    String selected = automatasList.getSelectedValue();
                    if (selected != null) {
                        String nombre = selected.substring(5); // Remover "AFD: " o "AP: "
                        Object automata = Parser.automatas.get(nombre);
                        if (automata instanceof AFD) {
                            AFD afd = (AFD) automata;
                            dotArea.setText(afd.generarDot());
                        } else {
                            dotArea.setText("// Gráficos de AP no implementados aún\n" +
                                          "// Seleccione un AFD para ver su representación DOT");
                        }
                    }
                }
            });
            
            // Panel izquierdo: lista
            JPanel leftPanel = new JPanel(new BorderLayout());
            leftPanel.setBorder(BorderFactory.createTitledBorder("Autómatas"));
            leftPanel.add(new JScrollPane(automatasList), BorderLayout.CENTER);
            leftPanel.setPreferredSize(new Dimension(200, 0));
            
            // Panel derecho: código DOT
            JPanel rightPanel = new JPanel(new BorderLayout());
            rightPanel.setBorder(BorderFactory.createTitledBorder("Código DOT (Graphviz)"));
            rightPanel.add(new JScrollPane(dotArea), BorderLayout.CENTER);
            
            // Botones de acción
            JPanel buttonPanel = new JPanel(new FlowLayout());
            JButton exportDotBtn = new JButton("Exportar DOT");
            JButton generateImageBtn = new JButton("Generar Imagen");
            
            exportDotBtn.addActionListener(e -> exportarDOT(dotArea.getText()));
            generateImageBtn.addActionListener(e -> mostrarInfoGraphviz());
            
            buttonPanel.add(exportDotBtn);
            buttonPanel.add(generateImageBtn);
            rightPanel.add(buttonPanel, BorderLayout.SOUTH);
            
            JSplitPane graphSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
            graphSplit.setLeftComponent(leftPanel);
            graphSplit.setRightComponent(rightPanel);
            graphSplit.setDividerLocation(200);
            
            graphContent.add(graphSplit, BorderLayout.CENTER);
            graphPanel.add(graphContent, BorderLayout.CENTER);
            
            // Seleccionar el primer autómata por defecto
            if (listModel.getSize() > 0) {
                automatasList.setSelectedIndex(0);
            }
        }
        
        graphPanel.revalidate();
        graphPanel.repaint();
        
        // Cambiar a la pestaña de gráficos
        reportTabs.setSelectedIndex(3);
    }
    
    private void clearAllReports() {
        // Limpiar todas las pestañas de reportes
        for (int i = 0; i < reportTabs.getTabCount(); i++) {
            Component comp = reportTabs.getComponentAt(i);
            if (comp instanceof JScrollPane) {
                Component view = ((JScrollPane) comp).getViewport().getView();
                if (view instanceof JTextArea) {
                    ((JTextArea) view).setText("");
                } else if (view instanceof JTable) {
                    ((JTable) view).setModel(new javax.swing.table.DefaultTableModel());
                }
            }
        }
        
        // Limpiar panel de gráficos
        graphPanel.removeAll();
        JLabel label = new JLabel("Los gráficos aparecerán aquí después de ejecutar", SwingConstants.CENTER);
        label.setForeground(Color.GRAY);
        graphPanel.add(label, BorderLayout.CENTER);
        graphPanel.revalidate();
        graphPanel.repaint();
    }
    
    // Métodos de archivo mejorados
    
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
        limpiarTodo();
        currentFile = null;
        setTitle("AutómataLab - Nuevo archivo");
        statusLabel.setText(" Nuevo archivo creado");
    }
    
    private void abrirArchivo() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
            "Archivos AutómataLab (*.atm)", "atm"));
        fileChooser.setDialogTitle("Abrir archivo .atm");
        
        if (currentFile != null) {
            fileChooser.setCurrentDirectory(currentFile.getParentFile());
        }
        
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
                limpiarTodo();
                
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, 
                    "Error al abrir archivo:\n" + e.getMessage(), 
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
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
            "Archivos AutómataLab (*.atm)", "atm"));
        fileChooser.setDialogTitle("Guardar archivo .atm");
        
        if (currentFile != null) {
            fileChooser.setCurrentDirectory(currentFile.getParentFile());
            fileChooser.setSelectedFile(new File(currentFile.getName()));
        } else {
            fileChooser.setSelectedFile(new File("automata.atm"));
        }
        
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
                "Error al guardar archivo:\n" + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            statusLabel.setText(" Error al guardar archivo");
        }
    }
    
    private boolean hayModificacionesSinGuardar() {
        return !editorPanel.getText().trim().isEmpty();
    }
    
    private void limpiarTodo() {
        consolePanel.clear();
        Parser.automatas.clear();
        Parser.errores.clear();
        clearAllReports();
        statusLabel.setText(" Todo limpio - Listo para trabajar");
    }
    
    // Métodos de diálogo mejorados
    
    private void mostrarDialogoBusqueda() {
        JDialog searchDialog = new JDialog(this, "Buscar", true);
        searchDialog.setSize(400, 150);
        searchDialog.setLocationRelativeTo(this);
        searchDialog.setLayout(new BorderLayout());
        
        JPanel inputPanel = new JPanel(new FlowLayout());
        JLabel label = new JLabel("Buscar:");
        JTextField searchField = new JTextField(20);
        JCheckBox caseSensitive = new JCheckBox("Coincidir mayúsculas");
        
        inputPanel.add(label);
        inputPanel.add(searchField);
        inputPanel.add(caseSensitive);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton findBtn = new JButton("Buscar");
        JButton cancelBtn = new JButton("Cancelar");
        
        findBtn.addActionListener(e -> {
            String text = searchField.getText();
            if (!text.isEmpty()) {
                editorPanel.findText(text);
                searchDialog.dispose();
            }
        });
        
        cancelBtn.addActionListener(e -> searchDialog.dispose());
        
        buttonPanel.add(findBtn);
        buttonPanel.add(cancelBtn);
        
        searchDialog.add(inputPanel, BorderLayout.CENTER);
        searchDialog.add(buttonPanel, BorderLayout.SOUTH);
        
        searchField.addActionListener(e -> findBtn.doClick());
        searchDialog.setVisible(true);
        searchField.requestFocus();
    }
    
    private void mostrarDialogoIrLinea() {
        String lineStr = JOptionPane.showInputDialog(this, 
                "Ir a línea (1-" + editorPanel.getTextArea().getLineCount() + "):", 
                "Ir a línea", 
                JOptionPane.QUESTION_MESSAGE);
        
        if (lineStr != null && !lineStr.trim().isEmpty()) {
            try {
                int line = Integer.parseInt(lineStr.trim());
                editorPanel.gotoLine(line);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, 
                    "Número de línea inválido", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    // Métodos de reportes mejorados
    
    private void mostrarTokens() {
        actualizarTokens();
        reportTabs.setSelectedIndex(1);
        statusLabel.setText(" Mostrando análisis léxico");
    }
    
    private void mostrarErrores() {
        actualizarErrores();
        reportTabs.setSelectedIndex(2);
        statusLabel.setText(" Mostrando errores");
    }
    
    private void mostrarAutomatas() {
        actualizarAutomatas();
        reportTabs.setSelectedIndex(0);
        statusLabel.setText(" Mostrando autómatas definidos");
    }
    
    private void exportarReportes() {
        if (Parser.automatas.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "No hay reportes para exportar.\nEjecute el análisis primero.", 
                "Exportar", 
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Exportar reportes a directorio");
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File dir = fileChooser.getSelectedFile();
            try {
                exportarTodosLosReportes(dir);
                JOptionPane.showMessageDialog(this, 
                    "Reportes exportados exitosamente a:\n" + dir.getAbsolutePath(), 
                    "Exportación completada", 
                    JOptionPane.INFORMATION_MESSAGE);
                statusLabel.setText(" Reportes exportados a " + dir.getName());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Error al exportar reportes:\n" + e.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void exportarTodosLosReportes(File dir) throws Exception {
        String timestamp = java.time.LocalDateTime.now().format(
            java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        
        // Exportar tokens
        java.util.List<Token> tokens = ReporteTokens.analizarTokens(editorPanel.getText());
        ReporteTokens.generarReporteHTML(tokens, 
            new File(dir, "tokens_" + timestamp + ".html").getAbsolutePath());
        
        // Exportar errores
        ReporteErrores.generarReporteHTML(
            new File(dir, "errores_" + timestamp + ".html").getAbsolutePath());
        
        // Exportar DOT de autómatas
        for (String nombre : Parser.automatas.keySet()) {
            Object automata = Parser.automatas.get(nombre);
            if (automata instanceof AFD) {
                AFD afd = (AFD) automata;
                File dotFile = new File(dir, nombre + "_" + timestamp + ".dot");
                try (FileWriter writer = new FileWriter(dotFile)) {
                    writer.write(afd.generarDot());
                }
            }
        }
    }
    
    private void exportarHTML() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
            "Archivos HTML (*.html)", "html"));
        fileChooser.setDialogTitle("Exportar reporte HTML");
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".html")) {
                file = new File(file.getAbsolutePath() + ".html");
            }
            
            try {
                generarReporteHTMLCompleto(file);
                statusLabel.setText(" Reporte HTML exportado");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Error al exportar HTML: " + e.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void generarReporteHTMLCompleto(File file) throws Exception {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>\n<html>\n<head>\n")
            .append("<title>Reporte AutómataLab</title>\n")
            .append("<meta charset='UTF-8'>\n")
            .append("<style>\n")
            .append("body { font-family: Arial, sans-serif; margin: 20px; }\n")
            .append("h1 { color: #2c3e50; }\n")
            .append("h2 { color: #34495e; border-bottom: 2px solid #ecf0f1; }\n")
            .append("table { border-collapse: collapse; width: 100%; margin: 10px 0; }\n")
            .append("th, td { border: 1px solid #bdc3c7; padding: 8px; text-align: left; }\n")
            .append("th { background-color: #ecf0f1; }\n")
            .append("pre { background-color: #f8f9fa; padding: 10px; border-radius: 5px; }\n")
            .append(".error { color: #e74c3c; }\n")
            .append(".success { color: #27ae60; }\n")
            .append("</style>\n</head>\n<body>\n");
        
        html.append("<h1>Reporte de Análisis - AutómataLab</h1>\n");
        html.append("<p>Generado: ").append(java.time.LocalDateTime.now()).append("</p>\n");
        
        // Sección de autómatas
        html.append("<h2>Autómatas Definidos</h2>\n");
        if (Parser.automatas.isEmpty()) {
            html.append("<p>No hay autómatas definidos.</p>\n");
        } else {
            for (String nombre : Parser.automatas.keySet()) {
                Object automata = Parser.automatas.get(nombre);
                if (automata instanceof AFD) {
                    AFD afd = (AFD) automata;
                    html.append("<h3>AFD: ").append(nombre).append("</h3>\n");
                    html.append("<pre>").append(afd.getDescripcionCompleta()).append("</pre>\n");
                }
            }
        }
        
        html.append("</body>\n</html>");
        
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(html.toString());
        }
    }
    
    private void exportarDOT(String dotContent) {
        if (dotContent.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "No hay código DOT para exportar.\nSeleccione un autómata primero.", 
                "Exportar DOT", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
            "Archivos DOT (*.dot)", "dot"));
        fileChooser.setDialogTitle("Exportar código DOT");
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".dot")) {
                file = new File(file.getAbsolutePath() + ".dot");
            }
            
            try {
                FileWriter writer = new FileWriter(file);
                writer.write(dotContent);
                writer.close();
                statusLabel.setText(" Código DOT exportado");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, 
                    "Error al exportar DOT: " + e.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void mostrarInfoGraphviz() {
        String mensaje = "Para generar imágenes desde código DOT:\n\n" +
                        "1. Instale Graphviz desde: https://graphviz.org/\n" +
                        "2. Use el comando: dot -Tpng archivo.dot -o imagen.png\n" +
                        "3. Formatos disponibles: png, svg, pdf, jpg\n\n" +
                        "El código DOT se puede exportar usando el botón 'Exportar DOT'.";
        
        JOptionPane.showMessageDialog(this, mensaje, 
            "Generación de imágenes", JOptionPane.INFORMATION_MESSAGE);
    }
    
    // Métodos de vista
    
    private void toggleLineNumbers() {
        // Esta funcionalidad ya está implementada en EditorPanel
        JOptionPane.showMessageDialog(this, 
            "Los números de línea están siempre visibles en el editor.", 
            "Números de línea", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    // Métodos de ayuda
    
    private void mostrarEjemplos() {
        String[] ejemplos = {
            "AFD - Números pares de ceros",
            "AFD - Cadenas que terminan en 01", 
            "AP - Lenguaje a^n b^n",
            "AP - Paréntesis balanceados"
        };
        
        String seleccion = (String) JOptionPane.showInputDialog(this,
            "Seleccione un ejemplo para cargar:",
            "Ejemplos",
            JOptionPane.QUESTION_MESSAGE,
            null,
            ejemplos,
            ejemplos[0]);
        
        if (seleccion != null) {
            cargarEjemplo(seleccion);
        }
    }
    
    private void cargarEjemplo(String tipo) {
        String ejemplo = switch (tipo) {
            case "AFD - Números pares de ceros" -> """
                // AFD que acepta cadenas con número par de ceros
                <AFD Nombre="ParCeros">
                  N = {q0, q1};
                  T = {'0', '1'};
                  I = {q0};
                  A = {q0};
                  
                  Transiciones:
                    q0 -> '0', q1 | '1', q0;
                    q1 -> '0', q0 | '1', q1;
                </AFD>
                
                verAutomatas();
                desc(ParCeros);
                ParCeros("1010");
                ParCeros("100");
                """;
                
            case "AFD - Cadenas que terminan en 01" -> """
                // AFD que acepta cadenas que terminan en "01"
                <AFD Nombre="TerminaEn01">
                  N = {q0, q1, q2};
                  T = {'0', '1'};
                  I = {q0};
                  A = {q2};
                  
                  Transiciones:
                    q0 -> '0', q1 | '1', q0;
                    q1 -> '0', q1 | '1', q2;
                    q2 -> '0', q1 | '1', q0;
                </AFD>
                
                verAutomatas();
                TerminaEn01("1001");
                TerminaEn01("101");
                TerminaEn01("110");
                """;
                
            case "AP - Lenguaje a^n b^n" -> """
                // AP que acepta L = {a^n b^n | n >= 1}
                <AP Nombre="AnBn">
                  N = {q0, q1, q2};
                  T = {'a', 'b'};
                  P = {'A', 'Z'};
                  I = {q0};
                  A = {q2};
                  
                  Transiciones:
                    q0 ('a') -> ('Z'), q1 : ('A');
                    q1 ('a') -> ('A'), q1 : ('A') | ('b') -> ('A'), q1 : (');
                    q1 (') -> ('Z'), q2 : (');
                </AP>
                
                verAutomatas();
                desc(AnBn);
                AnBn("ab");
                AnBn("aabb");
                AnBn("aaabbb");
                """;
                
            case "AP - Paréntesis balanceados" -> """
                // AP que acepta paréntesis balanceados
                <AP Nombre="Parentesis">
                  N = {q0, q1};
                  T = {'(', ')'};
                  P = {'P', 'Z'};
                  I = {q0};
                  A = {q1};
                  
                  Transiciones:
                    q0 ('(') -> ('Z'), q0 : ('P');
                    q0 ('(') -> ('P'), q0 : ('P');
                    q0 (')') -> ('P'), q0 : (');
                    q0 (') -> ('Z'), q1 : (');
                </AP>
                
                verAutomatas();
                Parentesis("()");
                Parentesis("(())");
                Parentesis("((()))");
                """;
                
            default -> cargarEjemploInicial(); return;
        };
        
        editorPanel.setText(ejemplo);
        statusLabel.setText(" Ejemplo cargado: " + tipo);
    }
    
    private void mostrarManual() {
        String manual = """
            MANUAL RÁPIDO - AutómataLab
            ============================
            
            SINTAXIS AFD:
            <AFD Nombre="MiAFD">
              N = {q0, q1, q2};          // Estados
              T = {'a', 'b'};            // Alfabeto
              I = {q0};                  // Estado inicial
              A = {q2};                  // Estados finales
              
              Transiciones:
                q0 -> 'a', q1;           // Transición simple
                q1 -> 'a', q1 | 'b', q2; // Múltiples transiciones
            </AFD>
            
            SINTAXIS AP:
            <AP Nombre="MiAP">
              N = {q0, q1};              // Estados
              T = {'a', 'b'};            // Alfabeto entrada
              P = {'A', 'Z'};            // Símbolos de pila
              I = {q0};                  // Estado inicial
              A = {q1};                  // Estados finales
              
              Transiciones:
                q0 ('a') -> ('Z'), q0 : ('A');   // (entrada) -> (extrae), destino : (inserta)
                q0 (') -> ('Z'), q1 : (');   // $ = lambda (cadena vacía)
            </AP>
            
            COMANDOS:
            verAutomatas();              // Listar autómatas
            desc(NombreAutomata);        // Describir autómata
            NombreAutomata("cadena");    // Validar cadena
            
            ATAJOS DE TECLADO:
            F5          - Ejecutar
            Ctrl+N      - Nuevo
            Ctrl+O      - Abrir
            Ctrl+S      - Guardar
            Ctrl+F      - Buscar
            Ctrl+G      - Ir a línea
            """;
        
        JDialog manualDialog = new JDialog(this, "Manual de Usuario", true);
        manualDialog.setSize(700, 500);
        manualDialog.setLocationRelativeTo(this);
        
        JTextArea manualArea = new JTextArea(manual);
        manualArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        manualArea.setEditable(false);
        manualArea.setBackground(new Color(250, 250, 250));
        
        manualDialog.add(new JScrollPane(manualArea), BorderLayout.CENTER);
        
        JButton closeBtn = new JButton("Cerrar");
        closeBtn.addActionListener(e -> manualDialog.dispose());
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(closeBtn);
        manualDialog.add(buttonPanel, BorderLayout.SOUTH);
        
        manualDialog.setVisible(true);
    }
    
    private void mostrarAcercaDe() {
        String acercaDe = """
            AutómataLab v2.0
            
            Herramienta para definición y análisis de:
            • Autómatas Finitos Deterministas (AFD)
            • Autómatas de Pila (AP)
            
            Características:
            • Editor con resaltado de sintaxis
            • Análisis léxico y sintáctico
            • Validación de cadenas
            • Generación de reportes
            • Exportación a múltiples formatos
            • Visualización con Graphviz
            
            Desarrollado para:
            Organización de Lenguajes y Compiladores 1
            Universidad de San Carlos de Guatemala
            
            Tecnologías utilizadas:
            • Java Swing
            • JFlex (Análisis Léxico)
            • CUP (Análisis Sintáctico)
            • Graphviz (Visualización)
            """;
        
        JOptionPane.showMessageDialog(this, acercaDe, 
            "Acerca de AutómataLab", JOptionPane.INFORMATION_MESSAGE);
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