package interfaz;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import javax.swing.undo.*;
import java.awt.*;
import java.awt.event.*;

public class EditorPanel extends JPanel {
    private JTextArea textArea;
    private JTextArea lineNumbers;
    private JLabel statusLabel;
    private JScrollPane scrollPane;
    private UndoManager undoManager;
    
    public EditorPanel() {
        setLayout(new BorderLayout());
        initComponents();
    }
    
    private void initComponents() {
        // Crear área de texto principal
        textArea = new JTextArea();
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        textArea.setTabSize(4);
        
        // Configurar undo manager
        undoManager = new UndoManager();
        textArea.getDocument().addUndoableEditListener(undoManager);
        
        // Crear área de números de línea
        lineNumbers = new JTextArea("1");
        lineNumbers.setBackground(new Color(240, 240, 240));
        lineNumbers.setForeground(Color.GRAY);
        lineNumbers.setEditable(false);
        lineNumbers.setFont(new Font("Monospaced", Font.PLAIN, 14));
        lineNumbers.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        lineNumbers.setFocusable(false);
        
        // Configurar scroll pane con números de línea
        scrollPane = new JScrollPane(textArea);
        scrollPane.setRowHeaderView(lineNumbers);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        
        // Barra de estado
        statusLabel = new JLabel(" Línea: 1, Columna: 1 ");
        statusLabel.setBorder(BorderFactory.createEtchedBorder());
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        
        // Agregar componentes al panel
        add(scrollPane, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);
        
        // Configurar listeners
        setupListeners();
        updateLineNumbers();
    }
    
    private void setupListeners() {
        // Listener para actualizar números de línea
        textArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                SwingUtilities.invokeLater(() -> updateLineNumbers());
            }
            
            @Override
            public void removeUpdate(DocumentEvent e) {
                SwingUtilities.invokeLater(() -> updateLineNumbers());
            }
            
            @Override
            public void changedUpdate(DocumentEvent e) {
                SwingUtilities.invokeLater(() -> updateLineNumbers());
            }
        });
        
        // Listener para posición del cursor
        textArea.addCaretListener(new CaretListener() {
            @Override
            public void caretUpdate(CaretEvent e) {
                SwingUtilities.invokeLater(() -> updateStatusBar());
            }
        });
        
        // Configurar key bindings para mejor experiencia de edición
        setupKeyBindings();
    }
    
    private void setupKeyBindings() {
        // Agregar soporte para indentación con TAB
        textArea.getInputMap().put(KeyStroke.getKeyStroke("TAB"), "insertTab");
        textArea.getActionMap().put("insertTab", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textArea.replaceSelection("    "); // 4 espacios para indentación
            }
        });
        
        // Agregar soporte para SHIFT+TAB (de-indentación)
        textArea.getInputMap().put(KeyStroke.getKeyStroke("shift TAB"), "removeIndent");
        textArea.getActionMap().put("removeIndent", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int caretPos = textArea.getCaretPosition();
                    int lineStart = textArea.getLineStartOffset(textArea.getLineOfOffset(caretPos));
                    String lineText = textArea.getText(lineStart, caretPos - lineStart);
                    
                    if (lineText.startsWith("    ")) {
                        textArea.getDocument().remove(lineStart, 4);
                    } else if (lineText.startsWith(" ")) {
                        textArea.getDocument().remove(lineStart, 1);
                    }
                } catch (BadLocationException ex) {
                    ex.printStackTrace();
                }
            }
        });
        
        // Undo/Redo con Ctrl+Z y Ctrl+Y
        textArea.getInputMap().put(KeyStroke.getKeyStroke("ctrl Z"), "undo");
        textArea.getActionMap().put("undo", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                undo();
            }
        });
        
        textArea.getInputMap().put(KeyStroke.getKeyStroke("ctrl Y"), "redo");
        textArea.getActionMap().put("redo", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                redo();
            }
        });
    }
    
    private void updateLineNumbers() {
        try {
            int lineCount = textArea.getLineCount();
            StringBuilder numbers = new StringBuilder();
            
            for (int i = 1; i <= lineCount; i++) {
                numbers.append(i);
                if (i < lineCount) {
                    numbers.append("\n");
                }
            }
            
            lineNumbers.setText(numbers.toString());
            
            // Ajustar el ancho de los números de línea
            int maxWidth = Math.max(35, String.valueOf(lineCount).length() * 12 + 10);
            lineNumbers.setPreferredSize(new Dimension(maxWidth, lineNumbers.getPreferredSize().height));
            
            // Revalidar para aplicar cambios
            lineNumbers.revalidate();
            
        } catch (Exception ex) {
            // En caso de error, mantener números básicos
            lineNumbers.setText("1");
        }
    }
    
    private void updateStatusBar() {
        try {
            int caretPos = textArea.getCaretPosition();
            int lineNumber = textArea.getLineOfOffset(caretPos) + 1;
            int columnNumber = caretPos - textArea.getLineStartOffset(lineNumber - 1) + 1;
            
            String text = String.format(" Línea: %d, Columna: %d | Caracteres: %d | Líneas: %d ",
                    lineNumber, columnNumber, textArea.getText().length(), textArea.getLineCount());
            
            statusLabel.setText(text);
            
        } catch (Exception ex) {
            statusLabel.setText(" Línea: 1, Columna: 1 ");
        }
    }
    
    // Métodos públicos para interactuar con el editor
    
    public String getText() {
        return textArea.getText();
    }
    
    public void setText(String text) {
        textArea.setText(text);
        // Programar actualización para después de que se complete el setText
        SwingUtilities.invokeLater(() -> {
            updateLineNumbers();
            updateStatusBar();
        });
    }
    
    public void clear() {
        textArea.setText("");
        SwingUtilities.invokeLater(() -> {
            updateLineNumbers();
            updateStatusBar();
        });
    }
    
    public void append(String text) {
        textArea.append(text);
        SwingUtilities.invokeLater(() -> {
            updateLineNumbers();
            updateStatusBar();
        });
    }
    
    public int getCaretLine() {
        try {
            return textArea.getLineOfOffset(textArea.getCaretPosition()) + 1;
        } catch (Exception e) {
            return 1;
        }
    }
    
    public int getCaretColumn() {
        try {
            int caretPos = textArea.getCaretPosition();
            int line = textArea.getLineOfOffset(caretPos);
            return caretPos - textArea.getLineStartOffset(line) + 1;
        } catch (Exception e) {
            return 1;
        }
    }
    
    public void gotoLine(int lineNumber) {
        try {
            int lineStart = textArea.getLineStartOffset(lineNumber - 1);
            textArea.setCaretPosition(lineStart);
            textArea.grabFocus();
        } catch (Exception e) {
            // Línea no válida
        }
    }
    
    public void highlightLine(int lineNumber, Color color) {
        try {
            int start = textArea.getLineStartOffset(lineNumber - 1);
            int end = textArea.getLineEndOffset(lineNumber - 1);
            
            // Crear highlighter si no existe
            if (textArea.getHighlighter() == null) {
                textArea.setHighlighter(new DefaultHighlighter());
            }
            
            // Limpiar highlights anteriores
            textArea.getHighlighter().removeAllHighlights();
            
            // Agregar nuevo highlight
            textArea.getHighlighter().addHighlight(start, end, 
                new DefaultHighlighter.DefaultHighlightPainter(color));
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void clearHighlights() {
        if (textArea.getHighlighter() != null) {
            textArea.getHighlighter().removeAllHighlights();
        }
    }
    
    public JTextArea getTextArea() {
        return textArea;
    }
    
    public void setFontSize(int size) {
        Font currentFont = textArea.getFont();
        textArea.setFont(new Font(currentFont.getName(), currentFont.getStyle(), size));
        lineNumbers.setFont(new Font(currentFont.getName(), currentFont.getStyle(), size));
    }
    
    public void increaseFontSize() {
        setFontSize(textArea.getFont().getSize() + 1);
    }
    
    public void decreaseFontSize() {
        int newSize = textArea.getFont().getSize() - 1;
        if (newSize >= 8) {
            setFontSize(newSize);
        }
    }
    
    // Métodos para buscar texto
    public void findText(String text) {
        findText(text, 0);
    }
    
    public void findText(String text, int startPos) {
        String content = textArea.getText();
        int index = content.indexOf(text, startPos);
        
        if (index >= 0) {
            textArea.setCaretPosition(index);
            textArea.moveCaretPosition(index + text.length());
            textArea.grabFocus();
            
            // Highlight del texto encontrado
            highlightText(index, index + text.length(), new Color(255, 255, 0, 100));
        }
    }
    
    private void highlightText(int start, int end, Color color) {
        try {
            if (textArea.getHighlighter() == null) {
                textArea.setHighlighter(new DefaultHighlighter());
            }
            
            textArea.getHighlighter().removeAllHighlights();
            textArea.getHighlighter().addHighlight(start, end, 
                new DefaultHighlighter.DefaultHighlightPainter(color));
                
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // Métodos para undo/redo corregidos
    public void undo() {
        try {
            if (undoManager.canUndo()) {
                undoManager.undo();
            }
        } catch (CannotUndoException e) {
            // No se puede deshacer
        }
    }
    
    public void redo() {
        try {
            if (undoManager.canRedo()) {
                undoManager.redo();
            }
        } catch (CannotRedoException e) {
            // No se puede rehacer
        }
    }
    
    public boolean canUndo() {
        return undoManager.canUndo();
    }
    
    public boolean canRedo() {
        return undoManager.canRedo();
    }
}
