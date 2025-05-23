/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package javaapplication2;

import java.io.*;
import java.util.*;
/**
 *
 * @author Jhoshua
 */
public class Lexer {
	// Mapas para almacenar palabras reservadas, operadores y delimitadores
	private static final Map<String, Integer> palabrasReservadas = new HashMap<>();
	private static final Map<String, Integer> operadores = new HashMap<>();
	private static final Map<String, Integer> delimitadores = new HashMap<>();

	static {
    	// Palabras reservadas
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

    	// Operadores
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

    	// Delimitadores
    	delimitadores.put("<", 600);	// Paréntesis apertura según tu tabla (aunque < usualmente es operador)
    	delimitadores.put(">", 601);	// Paréntesis cierre
    	delimitadores.put("<<", 602);   // Llave apertura
    	delimitadores.put(">>", 603);   // Llave cierre
    	delimitadores.put("[", 604);
    	delimitadores.put("]", 605);
    	delimitadores.put(",", 607);
	}

	// Método para leer y procesar el archivo
	public void leerArchivo(String archivo) {
    	try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
        	String linea;
        	int numeroLinea = 0;
        	while ((linea = br.readLine()) != null) {
            	numeroLinea++;
            	procesarLinea(linea, numeroLinea);
        	}
    	} catch (IOException e) {
        	e.printStackTrace();
    	}
	}

	// Método para procesar cada línea del archivo
	public void procesarLinea(String linea, int numeroLinea) {
    	// Ignorar comentarios que comienzan con //
    	String lineaSinComentario = linea.split("//")[0].trim();
    	if(lineaSinComentario.isEmpty()) {
        	return; // línea vacía o solo comentario, no mostrar nada
    	}

    	System.out.println("Linea " + numeroLinea + ": " + linea);

    	// Separar tokens por espacios y también dividir algunos símbolos especiales para detectarlos
    	List<String> tokens = dividirTokens(lineaSinComentario);

    	for (String token : tokens) {
        	if(token.isEmpty()) continue;

        	// Verificar si es palabra reservada
        	if (palabrasReservadas.containsKey(token)) {
            	System.out.println("[" + token + "] -> " + palabrasReservadas.get(token) + ": Palabra reservada");
        	}
        	// Verificar si es operador
        	else if (operadores.containsKey(token)) {
            	System.out.println("[" + token + "] -> " + operadores.get(token) + ": Operador");
        	}
        	// Verificar si es delimitador
        	else if (delimitadores.containsKey(token)) {
            	System.out.println("[" + token + "] -> " + delimitadores.get(token) + ": Delimitador");
        	}
        	// Verificar si es número (solo enteros aquí)
        	else if (token.matches("\\d+")) {
            	System.out.println("[" + token + "] -> 401: Numero");
        	}
        	// Verificar si es identificador (empieza con letra o _ y puede contener letras, números y _)
        	else if (token.matches("[a-zA-Z_][a-zA-Z0-9_]*")) {
            	System.out.println("[" + token + "] -> 400: Identificador");
        	}
        	// Si no se reconoce
        	else {
            	System.out.println("[" + token + "] -> 999: Error lexico (token no reconocido)");
        	}
    	}
    	System.out.println();
	}

	// Método para dividir tokens tomando en cuenta operadores y delimitadores que pueden ser símbolos pegados
	private List<String> dividirTokens(String linea) {
    	// Aquí se puede mejorar para separar símbolos juntos
    	// Por simplicidad, vamos a separar espacios y también dividir símbolos comunes

    	List<String> tokens = new ArrayList<>();

    	// Definimos los símbolos que queremos separar individualmente o como operadores compuestos
    	String simbolos = "=+-*/><!(),[]<>";
    	// Vamos a recorrer el string caracter por caracter y separar tokens
    	StringBuilder token = new StringBuilder();

    	for (int i = 0; i < linea.length(); i++) {
        	char c = linea.charAt(i);

        	// Checar si c es parte de un símbolo especial
        	if (simbolos.indexOf(c) != -1) {
            	// Si hay token acumulado, guardarlo
            	if (token.length() > 0) {
                	tokens.add(token.toString());
                	token.setLength(0);
            	}

            	// Para operadores que tienen dos caracteres, chequeamos el siguiente carácter
            	if (i + 1 < linea.length()) {
                	String posibleOperador2 = "" + c + linea.charAt(i + 1);
                	if (operadores.containsKey(posibleOperador2) || delimitadores.containsKey(posibleOperador2)) {
                    	tokens.add(posibleOperador2);
                    	i++; // saltar el siguiente carácter porque ya lo usamos
                    	continue;
                	}
            	}

            	// Agregar el símbolo solo
            	tokens.add("" + c);
        	} else if (Character.isWhitespace(c)) {
            	// Si hay token acumulado, guardarlo
            	if (token.length() > 0) {
                	tokens.add(token.toString());
                	token.setLength(0);
            	}
        	} else {
            	// Es parte de un token normal
            	token.append(c);
        	}
    	}
    	// Agregar último token si queda
    	if (token.length() > 0) {
        	tokens.add(token.toString());
    	}
    	return tokens;
	}

	public static void main(String[] args) {
    	Lexer lexer = new Lexer();
    	// Cambia la ruta a donde tengas tu archivo de programa
    	lexer.leerArchivo("E:\\juegos\\programa.txt");
	}
}

