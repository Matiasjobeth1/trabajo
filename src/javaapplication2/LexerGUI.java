/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package javaapplication2;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Analizador Léxico con Interfaz Gráfica
 * @author Jhoshua
 */
public class LexerGUI extends JFrame {
    private static final Map<String, Integer> palabrasReservadas = new HashMap<>();
    private static final Map<String, Integer> operadores = new HashMap<>();
    private static final Map<String, Integer> delimitadores = new HashMap<>();
    
    private JTextArea areaResultados;
    private JTextArea areaArchivo;
    private JLabel labelArchivo;
    private JButton btnSeleccionar;
    private JButton btnAnalizar;
    private JButton btnLimpiar;
    private JPanel panelEstadisticas;
    private JLabel lblTotalTokens;
    private JLabel lblPalabrasReservadas;
    private JLabel lblOperadores;
    private JLabel lblDelimitadores;
    private JLabel lblIdentificadores;
    private JLabel lblNumeros;
    private JLabel lblErrores;
    
    private File archivoSeleccionado;
    private int contadorTokens = 0;
    private int contadorPalabrasReservadas = 0;
    private int contadorOperadores = 0;
    private int contadorDelimitadores = 0;
    private int contadorIdentificadores = 0;
    private int contadorNumeros = 0;
    private int contadorErrores = 0;

    static {
        palabrasReservadas.put("chi", 100);
        palabrasReservadas.put("chino", 101);
        palabrasReservadas.put("repite", 102);
        palabrasReservadas.put("mientras", 103);
        palabrasReservadas.put("casos", 104);
        palabrasReservadas.put("rompe", 105);
        palabrasReservadas.put("ent", 200);
        palabrasReservadas.put("deci", 201);
        palabrasReservadas.put("vof", 202);
        palabrasReservadas.put("car", 203);
        palabrasReservadas.put("cad", 204);

        operadores.put("=", 500);
        operadores.put("+", 501);
        operadores.put("-", 502);
        operadores.put("*", 503);
        operadores.put("/", 504);
        operadores.put(">", 505);
        operadores.put("<", 506);
        operadores.put(">=", 507);
        operadores.put("<=", 508);
        operadores.put("==", 509);
        operadores.put("!=", 510);
        operadores.put("Y", 511);
        operadores.put("O", 512);
        operadores.put("!", 513);

        delimitadores.put("(", 600);
        delimitadores.put(")", 601);
        delimitadores.put("<<", 602);
        delimitadores.put(">>", 603);
        delimitadores.put("[", 604);
        delimitadores.put("]", 605);
        delimitadores.put(",", 607);
    }

    public LexerGUI() {
        inicializarInterfaz();
    }

    private void inicializarInterfaz() {
        setTitle("🔍 Analizador Léxico - Lenguaje Personalizado");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel panelSuperior = crearPanelSuperior();
        add(panelSuperior, BorderLayout.NORTH);

        JPanel panelCentral = crearPanelCentral();
        add(panelCentral, BorderLayout.CENTER);

        panelEstadisticas = crearPanelEstadisticas();
        add(panelEstadisticas, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(900, 600));
    }

    private JPanel crearPanelSuperior() {
        JPanel panel = new JPanel(new FlowLayout());
        panel.setBackground(new Color(240, 248, 255));
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), 
            "📁 Selección de Archivo", 
            TitledBorder.LEFT, 
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 12),
            new Color(25, 25, 112)
        ));

        btnSeleccionar = new JButton("🔍 Seleccionar Archivo");
        btnSeleccionar.setBackground(new Color(70, 130, 180));
        btnSeleccionar.setForeground(Color.WHITE);
        btnSeleccionar.setFont(new Font("Arial", Font.BOLD, 12));
        btnSeleccionar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                seleccionarArchivo();
            }
        });

        labelArchivo = new JLabel("Ningún archivo seleccionado");
        labelArchivo.setFont(new Font("Arial", Font.ITALIC, 11));
        labelArchivo.setForeground(Color.GRAY);

        btnAnalizar = new JButton("🚀 Analizar");
        btnAnalizar.setBackground(new Color(34, 139, 34));
        btnAnalizar.setForeground(Color.WHITE);
        btnAnalizar.setFont(new Font("Arial", Font.BOLD, 12));
        btnAnalizar.setEnabled(false);
        btnAnalizar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                analizarArchivo();
            }
        });

        btnLimpiar = new JButton("🧹 Limpiar");
        btnLimpiar.setBackground(new Color(220, 20, 60));
        btnLimpiar.setForeground(Color.WHITE);
        btnLimpiar.setFont(new Font("Arial", Font.BOLD, 12));
        btnLimpiar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                limpiarResultados();
            }
        });

        panel.add(btnSeleccionar);
        panel.add(Box.createHorizontalStrut(10));
        panel.add(labelArchivo);
        panel.add(Box.createHorizontalStrut(20));
        panel.add(btnAnalizar);
        panel.add(Box.createHorizontalStrut(10));
        panel.add(btnLimpiar);

        return panel;
    }

    private JPanel crearPanelCentral() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 10, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel panelArchivo = new JPanel(new BorderLayout());
        panelArchivo.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "📄 Contenido del Archivo",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 12),
            new Color(25, 25, 112)
        ));

        areaArchivo = new JTextArea();
        areaArchivo.setFont(new Font("Consolas", Font.PLAIN, 12));
        areaArchivo.setEditable(false);
        areaArchivo.setBackground(new Color(248, 248, 255));
        areaArchivo.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        JScrollPane scrollArchivo = new JScrollPane(areaArchivo);
        scrollArchivo.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        panelArchivo.add(scrollArchivo, BorderLayout.CENTER);

        JPanel panelResultados = new JPanel(new BorderLayout());
        panelResultados.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "🔬 Análisis Léxico",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 12),
            new Color(25, 25, 112)
        ));

        areaResultados = new JTextArea();
        areaResultados.setFont(new Font("Consolas", Font.PLAIN, 11));
        areaResultados.setEditable(false);
        areaResultados.setBackground(new Color(255, 255, 240));
        areaResultados.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        JScrollPane scrollResultados = new JScrollPane(areaResultados);
        scrollResultados.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        panelResultados.add(scrollResultados, BorderLayout.CENTER);

        panel.add(panelArchivo);
        panel.add(panelResultados);

        return panel;
    }

    private JPanel crearPanelEstadisticas() {
        JPanel panel = new JPanel(new GridLayout(2, 4, 10, 5));
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "📊 Estadísticas del Análisis",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 12),
            new Color(25, 25, 112)
        ));
        panel.setBackground(new Color(245, 245, 245));

        lblTotalTokens = crearEtiquetaEstadistica("🔢 Total Tokens:", "0", new Color(25, 25, 112));
        lblPalabrasReservadas = crearEtiquetaEstadistica("🔑 Palabras Reservadas:", "0", new Color(139, 0, 139));
        lblOperadores = crearEtiquetaEstadistica("⚙️ Operadores:", "0", new Color(255, 140, 0));
        lblDelimitadores = crearEtiquetaEstadistica("📋 Delimitadores:", "0", new Color(30, 144, 255));
        lblIdentificadores = crearEtiquetaEstadistica("🏷️ Identificadores:", "0", new Color(34, 139, 34));
        lblNumeros = crearEtiquetaEstadistica("🔢 Números:", "0", new Color(220, 20, 60));
        lblErrores = crearEtiquetaEstadistica("❌ Errores:", "0", new Color(178, 34, 34));

        panel.add(lblTotalTokens);
        panel.add(lblPalabrasReservadas);
        panel.add(lblOperadores);
        panel.add(lblDelimitadores);
        panel.add(lblIdentificadores);
        panel.add(lblNumeros);
        panel.add(lblErrores);
        panel.add(new JLabel()); 

        return panel;
    }

    private JLabel crearEtiquetaEstadistica(String texto, String valor, Color color) {
        JLabel label = new JLabel("<html><b>" + texto + "</b> <font color='rgb(" + 
                                 color.getRed() + "," + color.getGreen() + "," + color.getBlue() + 
                                 ")'>" + valor + "</font></html>");
        label.setFont(new Font("Arial", Font.PLAIN, 11));
        label.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
        return label;
    }

    private void seleccionarArchivo() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Archivos de texto (*.txt)", "txt"));
        
        int resultado = fileChooser.showOpenDialog(this);
        if (resultado == JFileChooser.APPROVE_OPTION) {
            archivoSeleccionado = fileChooser.getSelectedFile();
            labelArchivo.setText(archivoSeleccionado.getName());
            labelArchivo.setForeground(new Color(34, 139, 34));
            btnAnalizar.setEnabled(true);
            
            cargarContenidoArchivo();
        }
    }

    private void cargarContenidoArchivo() {
        try (BufferedReader br = new BufferedReader(new FileReader(archivoSeleccionado))) {
            StringBuilder contenido = new StringBuilder();
            String linea;
            int numeroLinea = 1;
            
            while ((linea = br.readLine()) != null) {
                contenido.append(String.format("%2d: %s\n", numeroLinea, linea));
                numeroLinea++;
            }
            
            areaArchivo.setText(contenido.toString());
            areaArchivo.setCaretPosition(0);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error al leer el archivo: " + e.getMessage(), 
                                        "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void analizarArchivo() {
        if (archivoSeleccionado == null) return;
        
        reiniciarContadores();
        
        StringBuilder resultados = new StringBuilder();
        resultados.append("═══════════════════════════════════════════════════════════════\n");
        resultados.append("           🔍 ANÁLISIS LÉXICO COMPLETADO 🔍\n");
        resultados.append("═══════════════════════════════════════════════════════════════\n\n");

        try (BufferedReader br = new BufferedReader(new FileReader(archivoSeleccionado))) {
            String linea;
            int numeroLinea = 0;
            
            while ((linea = br.readLine()) != null) {
                numeroLinea++;
                String resultado = procesarLinea(linea, numeroLinea);
                if (!resultado.isEmpty()) {
                    resultados.append(resultado);
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error al procesar el archivo: " + e.getMessage(), 
                                        "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        areaResultados.setText(resultados.toString());
        areaResultados.setCaretPosition(0);
        actualizarEstadisticas();
        
        JOptionPane.showMessageDialog(this, 
            "Análisis completado exitosamente!\n" +
            "Total de tokens procesados: " + contadorTokens,
            "Análisis Completado", 
            JOptionPane.INFORMATION_MESSAGE);
    }

    private String procesarLinea(String linea, int numeroLinea) {
        String lineaSinComentario = linea.split("//")[0].trim();
        if (lineaSinComentario.isEmpty()) {
            return ""; 
        }

        StringBuilder resultado = new StringBuilder();
        resultado.append("📝 Línea ").append(numeroLinea).append(": ").append(linea).append("\n");
        resultado.append("─".repeat(60)).append("\n");

        List<String> tokens = dividirTokens(lineaSinComentario);

        for (String token : tokens) {
            if (token.isEmpty()) continue;
            
            contadorTokens++;
            String tipoToken = "";
            String icono = "";
            Color color = Color.BLACK;

            if (palabrasReservadas.containsKey(token)) {
                tipoToken = "Palabra reservada";
                icono = "🔑";
                color = new Color(139, 0, 139);
                contadorPalabrasReservadas++;
                resultado.append(String.format("  %s [%s] → %d: %s\n", 
                    icono, token, palabrasReservadas.get(token), tipoToken));
            } else if (operadores.containsKey(token)) {
                tipoToken = "Operador";
                icono = "⚙️";
                color = new Color(255, 140, 0);
                contadorOperadores++;
                resultado.append(String.format("  %s [%s] → %d: %s\n", 
                    icono, token, operadores.get(token), tipoToken));
            } else if (delimitadores.containsKey(token)) {
                tipoToken = "Delimitador";
                icono = "📋";
                color = new Color(30, 144, 255);
                contadorDelimitadores++;
                resultado.append(String.format("  %s [%s] → %d: %s\n", 
                    icono, token, delimitadores.get(token), tipoToken));
            } else if (token.matches("\\d+")) {
                tipoToken = "Número";
                icono = "🔢";
                color = new Color(220, 20, 60);
                contadorNumeros++;
                resultado.append(String.format("  %s [%s] → 401: %s\n", 
                    icono, token, tipoToken));
            } else if (token.matches("[a-zA-Z_][a-zA-Z0-9_]*")) {
                tipoToken = "Identificador";
                icono = "🏷️";
                color = new Color(34, 139, 34);
                contadorIdentificadores++;
                resultado.append(String.format("  %s [%s] → 400: %s\n", 
                    icono, token, tipoToken));
            } else {
                tipoToken = "Error léxico (token no reconocido)";
                icono = "❌";
                color = new Color(178, 34, 34);
                contadorErrores++;
                resultado.append(String.format("  %s [%s] → 999: %s\n", 
                    icono, token, tipoToken));
            }
        }
        resultado.append("\n");
        return resultado.toString();
    }

    private void reiniciarContadores() {
        contadorTokens = 0;
        contadorPalabrasReservadas = 0;
        contadorOperadores = 0;
        contadorDelimitadores = 0;
        contadorIdentificadores = 0;
        contadorNumeros = 0;
        contadorErrores = 0;
    }

    private void actualizarEstadisticas() {
        lblTotalTokens.setText("<html><b>🔢 Total Tokens:</b> <font color='rgb(25,25,112)'>" + 
                              contadorTokens + "</font></html>");
        lblPalabrasReservadas.setText("<html><b>🔑 Palabras Reservadas:</b> <font color='rgb(139,0,139)'>" + 
                                     contadorPalabrasReservadas + "</font></html>");
        lblOperadores.setText("<html><b>⚙️ Operadores:</b> <font color='rgb(255,140,0)'>" + 
                             contadorOperadores + "</font></html>");
        lblDelimitadores.setText("<html><b>📋 Delimitadores:</b> <font color='rgb(30,144,255)'>" + 
                                contadorDelimitadores + "</font></html>");
        lblIdentificadores.setText("<html><b>🏷️ Identificadores:</b> <font color='rgb(34,139,34)'>" + 
                                  contadorIdentificadores + "</font></html>");
        lblNumeros.setText("<html><b>🔢 Números:</b> <font color='rgb(220,20,60)'>" + 
                          contadorNumeros + "</font></html>");
        lblErrores.setText("<html><b>❌ Errores:</b> <font color='rgb(178,34,34)'>" + 
                          contadorErrores + "</font></html>");
    }

    private void limpiarResultados() {
        areaResultados.setText("");
        areaArchivo.setText("");
        labelArchivo.setText("Ningún archivo seleccionado");
        labelArchivo.setForeground(Color.GRAY);
        btnAnalizar.setEnabled(false);
        archivoSeleccionado = null;
        reiniciarContadores();
        actualizarEstadisticas();
    }

    private List<String> dividirTokens(String linea) {
        List<String> tokens = new ArrayList<>();
        String simbolos = "=+-*/><!(),[]<>";
        StringBuilder token = new StringBuilder();

        for (int i = 0; i < linea.length(); i++) {
            char c = linea.charAt(i);

            if (simbolos.indexOf(c) != -1) {
                if (token.length() > 0) {
                    tokens.add(token.toString());
                    token.setLength(0);
                }

                if (i + 1 < linea.length()) {
                    String posibleOperador2 = "" + c + linea.charAt(i + 1);
                    if (operadores.containsKey(posibleOperador2) || delimitadores.containsKey(posibleOperador2)) {
                        tokens.add(posibleOperador2);
                        i++;
                        continue;
                    }
                }

                tokens.add("" + c);
            } else if (Character.isWhitespace(c)) {
                if (token.length() > 0) {
                    tokens.add(token.toString());
                    token.setLength(0);
                }
            } else {
                token.append(c);
            }
        }
        
        if (token.length() > 0) {
            tokens.add(token.toString());
        }
        
        return tokens;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new LexerGUI().setVisible(true);
            }
        });
    }
}

