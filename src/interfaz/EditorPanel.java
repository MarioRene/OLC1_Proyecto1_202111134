package interfaz;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;

public class EditorPanel extends JPanel {
    private JTextArea textArea;
    private JTextArea lineNumbers;
    private JLabel statusLabel;
    private JScrollPane scrollPane;
    
    public EditorPanel() {
        setLayout(new BorderLayout());
        initComponents();
    }
    
    private void initComponents() {
        // Crear área de texto principal
        textArea = new JTextArea();
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        textArea.setTabSize(4);
        
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
                updateLineNumbers();
            }
            
            @Override
            public void removeUpdate(DocumentEvent e) {
                updateLineNumbers();
            }
            
            @Override
            public void changedUpdate(DocumentEvent e) {
                updateLineNumbers();
            }
        });
        
        // Listener para posición del cursor
        textArea.addCaretListener(new CaretListener() {
            @Override
            public void caretUpdate(CaretEvent e) {
                updateStatusBar();
            }
        });
        
        // Sincronizar scroll de números de línea con el texto
        textArea.getCaret().addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                lineNumbers.setCaretPosition(textArea.getCaretPosition());
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
    }
    
    private void updateLineNumbers() {
        SwingUtilities.invokeLater(() -> {
            try {
                int lineCount = textArea.getLineCount();
                StringBuilder numbers = new StringBuilder();
                
                for (int i = 1; i <= lineCount; i++) {
                    numbers.append(i).append("\n");
                }
                
                lineNumbers.setText(numbers.toString());
                lineNumbers.setCaretPosition(textArea.getCaretPosition());
                
                // Ajustar el ancho de los números de línea
                int maxWidth = Math.max(30, String.valueOf(lineCount).length() * 10 + 10);
                lineNumbers.setPreferredSize(new Dimension(maxWidth, lineNumbers.getPreferredSize().height));
                
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }
    
    private void updateStatusBar() {
        SwingUtilities.invokeLater(() -> {
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
        });
    }
    
    // Métodos públicos para interactuar con el editor
    
    public String getText() {
        return textArea.getText();
    }
    
    public void setText(String text) {
        textArea.setText(text);
        updateLineNumbers();
        updateStatusBar();
    }
    
    public void clear() {
        textArea.setText("");
        updateLineNumbers();
        updateStatusBar();
    }
    
    public void append(String text) {
        textArea.append(text);
        updateLineNumbers();
        updateStatusBar();
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
    
    // Métodos para undo/redo (si se implementa un Document con undo)
    public void undo() {
        try {
            if (textArea.getDocument() instanceof AbstractDocument) {
                ((AbstractDocument) textArea.getDocument()).undo();
            }
        } catch (Exception e) {
            // No soporta undo
        }
    }
    
    public void redo() {
        try {
            if (textArea.getDocument() instanceof AbstractDocument) {
                ((AbstractDocument) textArea.getDocument()).redo();
            }
        } catch (Exception e) {
            // No soporta redo
        }
    }
    
    // Método para configurar syntax highlighting básico
    public void configureSyntaxHighlighting() {
        // Esto es un esqueleto para syntax highlighting
        // Se podría expandir con un StyledDocument
        textArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                applyBasicHighlighting();
            }
            
            @Override
            public void removeUpdate(DocumentEvent e) {
                applyBasicHighlighting();
            }
            
            @Override
            public void changedUpdate(DocumentEvent e) {
                applyBasicHighlighting();
            }
            
            private void applyBasicHighlighting() {
                // Implementación básica de syntax highlighting
                // Se podría mejorar usando StyledDocument
            }
        });
    }
}