package interfaz;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.List;
import java.util.Map;

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
            try {
                int caretPos = editorPanel.getTextArea().getCaretPosition();
                int line = editorPanel.getTextArea().getLineOfOffset(caretPos) + 1;
                int column = caretPos - editorPanel.getTextArea().getLineStartOffset(line - 1) + 1;
                int chars = editorPanel.getText().length();
                int lines = editorPanel.getTextArea().getLineCount();
                
                positionLabel.setText(String.format(" Línea: %d, Columna: %d | Caracteres: %d | Líneas: %d ", 
                        line, column, chars, lines));
            } catch (Exception ex) {
                positionLabel.setText(" Línea: 1, Columna: 1 ");
            }
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
        statusLabel.setText(" Ejecutando análisis según especificación OLC1...");
        consolePanel.clear();
        
        // Limpiar datos anteriores
        Parser.errores.clear();
        Parser.automatas.clear();
        clearAllReports();
        
        SwingUtilities.invokeLater(() -> {
            try {
                String input = editorPanel.getText();
                
                if (input.trim().isEmpty()) {
                    consolePanel.append("❌ Error: No hay código para analizar.\n");
                    statusLabel.setText(" Error: Código vacío");
                    return;
                }
                
                // Redirigir salida a consola
                consolePanel.redirectSystemStreams();
                
                consolePanel.append("=== AUTOMATALAB - PROYECTO 1 OLC1 ===\n");
                consolePanel.append("Análisis Léxico y Sintáctico\n");
                consolePanel.append("Archivo: " + (currentFile != null ? currentFile.getName() : "Sin título") + "\n");
                consolePanel.append("Tamaño: " + input.length() + " caracteres, " + 
                                  editorPanel.getTextArea().getLineCount() + " líneas\n");
                consolePanel.append("Fecha: " + java.time.LocalDateTime.now().format(
                    java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) + "\n");
                consolePanel.append("===============================================\n\n");
                
                long startTime = System.currentTimeMillis();
                
                // Crear analizadores
                Lexer lexer = new Lexer(new StringReader(input));
                Parser parser = new Parser(lexer);
                
                // Ejecutar análisis
                Symbol result = parser.parse();
                
                long endTime = System.currentTimeMillis();
                
                // Restaurar streams originales antes de mostrar reportes
                consolePanel.restoreSystemStreams();
                
                consolePanel.append("\n===============================================\n");
                consolePanel.append("=== RESULTADO DEL ANÁLISIS ===\n");
                consolePanel.append("Tiempo de ejecución: " + (endTime - startTime) + " ms\n");
                consolePanel.append("Autómatas procesados: " + Parser.automatas.size() + "\n");
                consolePanel.append("Errores detectados: " + Parser.errores.size() + "\n");
                
                // Mostrar lista de autómatas según especificación
                if (!Parser.automatas.isEmpty()) {
                    consolePanel.append("\n--- Autómatas Definidos ---\n");
                    for (String nombre : Parser.automatas.keySet()) {
                        Object automata = Parser.automatas.get(nombre);
                        String tipo = (automata instanceof AFD) ? "Autómata Finito Determinista" : "Autómata de Pila";
                        consolePanel.append("✓ " + nombre + " - " + tipo + "\n");
                    }
                }
                
                // Mostrar errores si los hay
                if (!Parser.errores.isEmpty()) {
                    consolePanel.append("\n--- Errores Detectados ---\n");
                    for (int i = 0; i < Parser.errores.size(); i++) {
                        consolePanel.append((i+1) + ". " + Parser.errores.get(i) + "\n");
                    }
                }
                
                consolePanel.append("\n===============================================\n");
                
                // Mostrar resultados en las pestañas según especificación
                SwingUtilities.invokeLater(() -> {
                    try {
                        // Generar reportes según orden de especificación
                        mostrarAutomatas();           // Primero autómatas
                        mostrarTokens();             // Luego tokens
                        mostrarErrores();            // Luego errores
                        
                        // Si hay autómatas, generar gráficos automáticamente
                        if (!Parser.automatas.isEmpty()) {
                            generarGraficos();
                            reportTabs.setSelectedIndex(0); // Mostrar autómatas primero
                        } else if (!Parser.errores.isEmpty()) {
                            reportTabs.setSelectedIndex(2); // Mostrar errores si los hay
                        } else {
                            reportTabs.setSelectedIndex(1); // Mostrar tokens por defecto
                        }
                        
                    } catch (Exception e) {
                        consolePanel.append("Error al generar reportes: " + e.getMessage() + "\n");
                        e.printStackTrace();
                    }
                });
                
                // Actualizar estado según especificación
                if (Parser.errores.isEmpty() && !Parser.automatas.isEmpty()) {
                    statusLabel.setText(" ✅ Análisis exitoso - " + Parser.automatas.size() + 
                                      " autómata(s) procesado(s) correctamente");
                } else if (!Parser.errores.isEmpty()) {
                    statusLabel.setText(" ⚠️ Análisis con errores - " + Parser.errores.size() + 
                                      " error(es) detectado(s)");
                } else {
                    statusLabel.setText(" ℹ️ Análisis completado - Sin autómatas definidos");
                }
                
            } catch (Exception ex) {
                // Asegurarse de restaurar streams incluso en caso de error
                consolePanel.restoreSystemStreams();
                
                consolePanel.append("\n❌ ERROR CRÍTICO DURANTE EL ANÁLISIS\n");
                consolePanel.append("Tipo: " + ex.getClass().getSimpleName() + "\n");
                consolePanel.append("Mensaje: " + ex.getMessage() + "\n");
                
                // Agregar error al reporte de errores
                Parser.errores.add("Error crítico: " + ex.getMessage());
                
                statusLabel.setText(" ❌ Error crítico durante el análisis");
                
                // Mostrar errores en la pestaña correspondiente
                SwingUtilities.invokeLater(() -> {
                    mostrarErrores();
                    reportTabs.setSelectedIndex(2); // Pestaña de errores
                });
            }
        });
    }
    
    private void mostrarTokens() {
        try {
            String input = editorPanel.getText();
            List<Token> tokens = ReporteTokens.analizarTokens(input);
            
            JScrollPane tokensTab = (JScrollPane) reportTabs.getComponentAt(1);
            JTable tokensTable = (JTable) tokensTab.getViewport().getView();
            
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
            
            tokensTable.setModel(new DefaultTableModel(data, columnNames) {
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
            
            reportTabs.setSelectedIndex(1);
            statusLabel.setText(" Tokens generados");
            
        } catch (Exception e) {
            statusLabel.setText(" Error al generar tokens");
            JOptionPane.showMessageDialog(this, "Error al analizar tokens: " + e.getMessage(), 
                                        "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void mostrarErrores() {
        JScrollPane erroresTab = (JScrollPane) reportTabs.getComponentAt(2);
        JTextArea erroresArea = (JTextArea) erroresTab.getViewport().getView();
        
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
        reportTabs.setSelectedIndex(2);
    }
    
    private void mostrarAutomatas() {
        try {
            JScrollPane automatasTab = (JScrollPane) reportTabs.getComponentAt(0);
            JTextArea automatasArea = (JTextArea) automatasTab.getViewport().getView();
            
            StringBuilder content = new StringBuilder();
            
            // Debug: verificar contenido de Parser.automatas
            System.out.println("[DEBUG] mostrarAutomatas() - Cantidad de autómatas: " + Parser.automatas.size());
            for (String key : Parser.automatas.keySet()) {
                System.out.println("[DEBUG] Autómata encontrado: " + key + " - " + Parser.automatas.get(key).getClass().getSimpleName());
            }
            
            if (Parser.automatas.isEmpty()) {
                content.append("📋 No hay autómatas definidos.\n\n");
                content.append("Para ver autómatas aquí:\n");
                content.append("1. Escriba la definición de un autómata en el editor\n");
                content.append("2. Use la sintaxis correcta (AFD o AP)\n");
                content.append("3. Haga clic en 'Ejecutar' o presione F5\n\n");
                content.append("Ejemplo de AFD:\n");
                content.append("<AFD Nombre=\"MiAFD\">\n");
                content.append("  N = {q0, q1};\n");
                content.append("  T = {'a', 'b'};\n");
                content.append("  I = {q0};\n");
                content.append("  A = {q1};\n");
                content.append("  Transiciones:\n");
                content.append("    q0 -> 'a', q1;\n");
                content.append("</AFD>");
            } else {
                content.append("🤖 AUTÓMATAS DEFINIDOS (").append(Parser.automatas.size()).append(")\n");
                content.append("═".repeat(60)).append("\n\n");
                
                int count = 1;
                for (Map.Entry<String, Object> entry : Parser.automatas.entrySet()) {
                    String nombre = entry.getKey();
                    Object automata = entry.getValue();
                    
                    content.append("[").append(count++).append("] ");
                    
                    if (automata instanceof AFD) {
                        AFD afd = (AFD) automata;
                        content.append("📊 AFD: ").append(nombre).append("\n");
                        content.append("    ├─ Estados: ").append(afd.getEstados().size())
                               .append(" → ").append(afd.getEstados()).append("\n");
                        content.append("    ├─ Alfabeto: ").append(afd.getAlfabeto().size())
                               .append(" símbolos → ").append(afd.getAlfabeto()).append("\n");
                        content.append("    ├─ Estado inicial: ").append(afd.getEstadoInicial()).append("\n");
                        content.append("    ├─ Estados finales: ").append(afd.getEstadosAceptacion().size())
                               .append(" → ").append(afd.getEstadosAceptacion()).append("\n");
                        content.append("    ├─ Transiciones: ").append(afd.getTransiciones().values().stream()
                               .mapToInt(Map::size).sum()).append("\n");
                        content.append("    ├─ Completo: ").append(afd.esCompleto() ? "✅ Sí" : "❌ No").append("\n");
                        content.append("    └─ Estados alcanzables: ").append(afd.getEstadosAlcanzables().size())
                               .append("/").append(afd.getEstados().size()).append("\n");
                        
                    } else if (automata instanceof AP) {
                        AP ap = (AP) automata;
                        content.append("🏗️ AP: ").append(nombre).append("\n");
                        content.append("    ├─ Estados: ").append(ap.getEstados().size())
                               .append(" → ").append(ap.getEstados()).append("\n");
                        content.append("    ├─ Alfabeto: ").append(ap.getAlfabeto().size())
                               .append(" símbolos → ").append(ap.getAlfabeto()).append("\n");
                        content.append("    ├─ Símbolos de pila: ").append(ap.getSimbolosPila().size())
                               .append(" → ").append(ap.getSimbolosPila()).append("\n");
                        content.append("    ├─ Estado inicial: ").append(ap.getEstadoInicial()).append("\n");
                        content.append("    ├─ Estados finales: ").append(ap.getEstadosAceptacion().size())
                               .append(" → ").append(ap.getEstadosAceptacion()).append("\n");
                        content.append("    └─ Transiciones: ").append(ap.getTransiciones().size()).append("\n");
                    } else {
                        content.append("❓ Tipo desconocido: ").append(nombre)
                               .append(" (").append(automata.getClass().getSimpleName()).append(")\n");
                    }
                    content.append("\n");
                }
                
                content.append("═".repeat(60)).append("\n");
                content.append("📈 ESTADÍSTICAS GENERALES:\n");
                
                long afdCount = Parser.automatas.values().stream()
                    .filter(a -> a instanceof AFD).count();
                long apCount = Parser.automatas.values().stream()
                    .filter(a -> a instanceof AP).count();
                
                content.append("  • AFDs definidos: ").append(afdCount).append("\n");
                content.append("  • APs definidos: ").append(apCount).append("\n");
                content.append("  • Total: ").append(Parser.automatas.size()).append(" autómatas\n");
                
                content.append("\n💡 ACCIONES DISPONIBLES:\n");
                content.append("  • Use desc(NombreAutomata); para ver detalles\n");
                content.append("  • Use NombreAutomata(\"cadena\"); para validar\n");
                content.append("  • Vea la pestaña 'Gráficos' para visualización\n");
            }
            
            automatasArea.setText(content.toString());
            automatasArea.setCaretPosition(0);
            
            // Solo cambiar a la pestaña si hay autómatas para mostrar
            if (!Parser.automatas.isEmpty()) {
                reportTabs.setSelectedIndex(0);
                statusLabel.setText(" 📊 Mostrando " + Parser.automatas.size() + " autómatas definidos");
            }
            
            System.out.println("[DEBUG] mostrarAutomatas() completado exitosamente");
            
        } catch (Exception e) {
            System.err.println("[ERROR] Error en mostrarAutomatas(): " + e.getMessage());
            e.printStackTrace();
            
            // Mostrar error en la pestaña
            try {
                JScrollPane automatasTab = (JScrollPane) reportTabs.getComponentAt(0);
                JTextArea automatasArea = (JTextArea) automatasTab.getViewport().getView();
                automatasArea.setText("❌ Error al mostrar autómatas: " + e.getMessage() + 
                                    "\n\nVerifique la consola para más detalles.");
            } catch (Exception e2) {
                System.err.println("[ERROR] Error crítico en mostrarAutomatas(): " + e2.getMessage());
            }
        }
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
        statusLabel.setText(" Gráficos generados");
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
                    ((JTable) view).setModel(new DefaultTableModel());
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
        statusLabel.setText(" Reportes limpiados");
    }
    
    // Métodos de archivo mejorados
    
    private void nuevoArchivo() {
        if (confirmarGuardado()) {
            editorPanel.setText("");
            currentFile = null;
            setTitle("AutómataLab - Nuevo Archivo");
            statusLabel.setText(" Nuevo archivo creado");
        }
    }
    
    private void abrirArchivo() {
        if (confirmarGuardado()) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Abrir archivo de autómata");
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                    "Archivos de Autómata (.atm)", "atm"));
            
            if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                try {
                    File file = fileChooser.getSelectedFile();
                    BufferedReader reader = new BufferedReader(new FileReader(file));
                    StringBuilder content = new StringBuilder();
                    String line;
                    
                    while ((line = reader.readLine()) != null) {
                        content.append(line).append("\n");
                    }
                    
                    reader.close();
                    editorPanel.setText(content.toString());
                    currentFile = file;
                    setTitle("AutómataLab - " + file.getName());
                    statusLabel.setText(" Archivo cargado: " + file.getName());
                    
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, 
                            "Error al abrir el archivo: " + ex.getMessage(), 
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
    
    private void guardarArchivo() {
        if (currentFile == null) {
            guardarComoArchivo();
        } else {
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(currentFile));
                writer.write(editorPanel.getText());
                writer.close();
                statusLabel.setText(" Archivo guardado: " + currentFile.getName());
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, 
                        "Error al guardar el archivo: " + ex.getMessage(), 
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void guardarComoArchivo() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar archivo de autómata");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Archivos de Autómata (.atm)", "atm"));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".atm")) {
                file = new File(file.getAbsolutePath() + ".atm");
            }
            
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                writer.write(editorPanel.getText());
                writer.close();
                currentFile = file;
                setTitle("AutómataLab - " + file.getName());
                statusLabel.setText(" Archivo guardado: " + file.getName());
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, 
                        "Error al guardar el archivo: " + ex.getMessage(), 
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private boolean confirmarGuardado() {
        if (editorPanel.isModified()) {
            int result = JOptionPane.showConfirmDialog(this, 
                    "¿Desea guardar los cambios antes de continuar?", 
                    "Confirmar", JOptionPane.YES_NO_CANCEL_OPTION);
            
            if (result == JOptionPane.YES_OPTION) {
                guardarArchivo();
                return true;
            } else if (result == JOptionPane.NO_OPTION) {
                return true;
            } else {
                return false;
            }
        }
        return true;
    }
    
    // Métodos de utilidad
    
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
                editorPanel.goToLine(line);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, 
                    "Número de línea inválido", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void toggleLineNumbers() {
        editorPanel.toggleLineNumbers();
    }
    
    private void mostrarManual() {
        String manual = 
            "MANUAL RÁPIDO - AutómataLab\n" +
            "============================\n" +
            "\n" +
            "SINTAXIS AFD:\n" +
            "<AFD Nombre=\"MiAFD\">\n" +
            "  N = {q0, q1, q2};          // Estados\n" +
            "  T = {'a', 'b'};            // Alfabeto\n" +
            "  I = {q0};                  // Estado inicial\n" +
            "  A = {q2};                  // Estados finales\n" +
            "  \n" +
            "  Transiciones:\n" +
            "    q0 -> 'a', q1;           // Transición simple\n" +
            "    q1 -> 'a', q1 | 'b', q2; // Múltiples transiciones\n" +
            "</AFD>\n" +
            "\n" +
            "SINTAXIS AP:\n" +
            "<AP Nombre=\"MiAP\">\n" +
            "  N = {q0, q1};              // Estados\n" +
            "  T = {'a', 'b'};            // Alfabeto entrada\n" +
            "  P = {'A', 'Z'};            // Símbolos de pila\n" +
            "  I = {q0};                  // Estado inicial\n" +
            "  A = {q1};                  // Estados finales\n" +
            "  \n" +
            "  Transiciones:\n" +
            "    q0 ('a') -> ('Z'), q0 : ('A');   // (entrada) -> (extrae), destino : (inserta)\n" +
            "    q0 (') -> ('Z'), q1 : (');   // $ = lambda (cadena vacía)\n" +
            "</AP>\n" +
            "\n" +
            "COMANDOS:\n" +
            "verAutomatas();              // Listar autómatas\n" +
            "desc(NombreAutomata);        // Describir autómata\n" +
            "NombreAutomata(\"cadena\");    // Validar cadena\n" +
            "\n" +
            "ATAJOS DE TECLADO:\n" +
            "F5          - Ejecutar\n" +
            "Ctrl+N      - Nuevo\n" +
            "Ctrl+O      - Abrir\n" +
            "Ctrl+S      - Guardar\n" +
            "Ctrl+F      - Buscar\n" +
            "Ctrl+G      - Ir a línea\n";
        
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
        String ejemplo;
        switch (tipo) {
            case "AFD - Números pares de ceros":
                ejemplo =
                    "// AFD que acepta cadenas con número par de ceros\n" +
                    "<AFD Nombre=\"ParCeros\">\n" +
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
                    "verAutomatas();\n" +
                    "desc(ParCeros);\n" +
                    "ParCeros(\"1010\");\n" +
                    "ParCeros(\"100\");\n";
                break;
            case "AFD - Cadenas que terminan en 01":
                ejemplo =
                    "// AFD que acepta cadenas que terminan en \"01\"\n" +
                    "<AFD Nombre=\"TerminaEn01\">\n" +
                    "  N = {q0, q1, q2};\n" +
                    "  T = {'0', '1'};\n" +
                    "  I = {q0};\n" +
                    "  A = {q2};\n" +
                    "  \n" +
                    "  Transiciones:\n" +
                    "    q0 -> '0', q1 | '1', q0;\n" +
                    "    q1 -> '0', q1 | '1', q2;\n" +
                    "    q2 -> '0', q1 | '1', q0;\n" +
                    "</AFD>\n" +
                    "\n" +
                    "verAutomatas();\n" +
                    "TerminaEn01(\"1001\");\n" +
                    "TerminaEn01(\"101\");\n" +
                    "TerminaEn01(\"110\");\n";
                break;
            case "AP - Lenguaje a^n b^n":
                ejemplo =
                    "// AP que acepta L = {a^n b^n | n >= 1}\n" +
                    "<AP Nombre=\"AnBn\">\n" +
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
                    "verAutomatas();\n" +
                    "desc(AnBn);\n" +
                    "AnBn(\"ab\");\n" +
                    "AnBn(\"aabb\");\n" +
                    "AnBn(\"aaabbb\");\n";
                break;
            case "AP - Paréntesis balanceados":
                ejemplo =
                    "// AP que acepta paréntesis balanceados\n" +
                    "<AP Nombre=\"Parentesis\">\n" +
                    "  N = {q0, q1};\n" +
                    "  T = {'(', ')'};\n" +
                    "  P = {'P', 'Z'};\n" +
                    "  I = {q0};\n" +
                    "  A = {q1};\n" +
                    "  \n" +
                    "  Transiciones:\n" +
                    "    q0 ('(') -> ('Z'), q0 : ('P');\n" +
                    "    q0 ('(') -> ('P'), q0 : ('P');\n" +
                    "    q0 (')') -> ('P'), q0 : ('$');\n" +
                    "    q0 (')') -> ('Z'), q1 : ('$');\n" +
                    "</AP>\n" +
                    "\n" +
                    "verAutomatas();\n" +
                    "Parentesis(\"()\");\n" +
                    "Parentesis(\"(())\");\n" +
                    "Parentesis(\"((()))\");\n";
                break;
            default:
                cargarEjemploInicial();
                return;
        }

        editorPanel.setText(ejemplo);
        statusLabel.setText(" Ejemplo cargado: " + tipo);
    }
    
    private void mostrarAcercaDe() {
        String acercaDe =
            "AutómataLab v2.0\n"
            + "\n"
            + "Herramienta para definición y análisis de:\n"
            + "• Autómatas Finitos Deterministas (AFD)\n"
            + "• Autómatas de Pila (AP)\n"
            + "\n"
            + "Características:\n"
            + "• Editor con resaltado de sintaxis\n"
            + "• Análisis léxico y sintáctico\n"
            + "• Validación de cadenas\n"
            + "• Generación de reportes\n"
            + "• Exportación a múltiples formatos\n"
            + "• Visualización con Graphviz\n"
            + "\n"
            + "Desarrollado para:\n"
            + "Organización de Lenguajes y Compiladores 1\n"
            + "Universidad de San Carlos de Guatemala\n"
            + "\n"
            + "Tecnologías utilizadas:\n"
            + "• Java Swing\n"
            + "• JFlex (Análisis Léxico)\n"
            + "• CUP (Análisis Sintáctico)\n"
            + "• Graphviz (Visualización)\n";
        
        JOptionPane.showMessageDialog(this, acercaDe, 
            "Acerca de AutómataLab", JOptionPane.INFORMATION_MESSAGE);
    }
    
    // Métodos placeholder para funcionalidades no implementadas
    
    private void compilar() {
        JOptionPane.showMessageDialog(this, 
                "La compilación generaría código intermedio o ejecutable.\n" +
                "Esta funcionalidad está planeada para una versión futura.", 
                "Compilar", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void limpiarTodo() {
        if (JOptionPane.showConfirmDialog(this, 
                "¿Está seguro de que desea limpiar todo el contenido?\n" +
                "Esto borrará el editor, la consola y todos los reportes.", 
                "Confirmar limpieza", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            editorPanel.setText("");
            consolePanel.clear();
            clearAllReports();
            statusLabel.setText(" Todo limpiado");
        }
    }
    
    private void exportarReportes() {
        JOptionPane.showMessageDialog(this, 
                "Los reportes se exportarían a formato PDF o HTML.\n" +
                "Esta funcionalidad está planeada para una versión futura.", 
                "Exportar Reportes", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void exportarHTML() {
        JOptionPane.showMessageDialog(this, 
                "Los autómatas se exportarían a formato HTML para publicación web.\n" +
                "Esta funcionalidad está planeada para una versión futura.", 
                "Exportar HTML", JOptionPane.INFORMATION_MESSAGE);
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
    
    private void salir() {
        if (confirmarGuardado()) {
            System.exit(0);
        }
    }
    
    // Método principal
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            MainWindow window = new MainWindow();
            window.setVisible(true);
        });
    }
}