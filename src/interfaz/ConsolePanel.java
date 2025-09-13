package interfaz;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.awt.datatransfer.StringSelection;

public class ConsolePanel extends JPanel {
    private JTextArea textArea;
    private PrintStream consoleStream;
    private PrintStream originalOut;
    private PrintStream originalErr;
    
    public ConsolePanel() {
        setLayout(new BorderLayout());
        
        textArea = new JTextArea();
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        textArea.setEditable(false);
        textArea.setBackground(Color.BLACK);
        textArea.setForeground(Color.WHITE);
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        add(scrollPane, BorderLayout.CENTER);
        
        // Guardar los streams originales
        originalOut = System.out;
        originalErr = System.err;
        
        // Crear stream para redirigir salida
        consoleStream = new PrintStream(new OutputStream() {
            private StringBuilder buffer = new StringBuilder();
            
            @Override
            public void write(int b) {
                if (b == '\r') return; // Ignorar carriage return
                
                if (b == '\n') {
                    String text = buffer.toString();
                    buffer.setLength(0);
                    SwingUtilities.invokeLater(() -> {
                        textArea.append(text + "\n");
                        textArea.setCaretPosition(textArea.getDocument().getLength());
                    });
                } else {
                    buffer.append((char) b);
                }
            }
            
            @Override
            public void write(byte[] b, int off, int len) {
                String text = new String(b, off, len);
                SwingUtilities.invokeLater(() -> {
                    textArea.append(text);
                    textArea.setCaretPosition(textArea.getDocument().getLength());
                });
            }
        });
    }
    
    public PrintStream getConsoleStream() {
        return consoleStream;
    }
    
    public void clear() {
        textArea.setText("");
    }
    
    public void append(String text) {
        textArea.append(text);
        textArea.setCaretPosition(textArea.getDocument().getLength());
    }
    
    public void redirectSystemStreams() {
        System.setOut(consoleStream);
        System.setErr(consoleStream);
    }
    
    public void restoreSystemStreams() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }
    
    public String getText() {
        return textArea.getText();
    }
    
    public void setText(String text) {
        textArea.setText(text);
        textArea.setCaretPosition(0);
    }
    
    public void copyToClipboard() {
        StringSelection selection = new StringSelection(textArea.getText());
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);
    }
    
    public void saveToFile(File file) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(textArea.getText());
        }
    }
}