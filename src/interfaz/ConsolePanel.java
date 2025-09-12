package interfaz;

import javax.swing.*;
import java.awt.*;
import java.io.*;

public class ConsolePanel extends JPanel {
    private JTextArea textArea;
    private PrintStream consoleStream;
    
    public ConsolePanel() {
        setLayout(new BorderLayout());
        
        textArea = new JTextArea();
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        textArea.setEditable(false);
        textArea.setBackground(Color.BLACK);
        textArea.setForeground(Color.WHITE);
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        add(scrollPane, BorderLayout.CENTER);
        
        // Crear stream para redirigir salida
        consoleStream = new PrintStream(new OutputStream() {
            @Override
            public void write(int b) {
                textArea.append(String.valueOf((char) b));
                textArea.setCaretPosition(textArea.getDocument().getLength());
            }
            
            @Override
            public void write(byte[] b, int off, int len) {
                textArea.append(new String(b, off, len));
                textArea.setCaretPosition(textArea.getDocument().getLength());
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
}