package uvm;
package com.cazador;

import javax.swing.*;
import java.awt.*;
import java.util.logging.Logger;

public class HiloCazador extends Thread {
    
    private static final Logger LOGGER = Logger.getLogger(HiloCazador.class.getName());

    private final char[][] matrizDatos;
    private final JLabel[][] etiquetasMatriz;
    private final JLabel lblContador;
    private int contadorSimbolos;

    public HiloCazador(char[][] matrizDatos, JLabel[][] etiquetasMatriz, JLabel lblContador) {
        this.matrizDatos = matrizDatos;
        this.etiquetasMatriz = etiquetasMatriz;
        this.lblContador = lblContador;
        this.contadorSimbolos = 0;
    }

    @Override
    public void run() {
        int filas = matrizDatos.length;
        int columnas = matrizDatos[0].length;

        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                
                if (matrizDatos[i][j] == '@') {
                    etiquetasMatriz[i][j].setForeground(Color.RED);
                    
                    try {
                        etiquetasMatriz[i][j].setVisible(false);
                        Thread.sleep(500); 
                        etiquetasMatriz[i][j].setVisible(true);
                        Thread.sleep(500); 
                    } catch (InterruptedException e) {
                        LOGGER.warning("Error en el hilo: " + e.getMessage());
                        // Restablece el estado de interrupción del hilo (SonarQube rule java:S2142)
                        Thread.currentThread().interrupt();
                    }

                    matrizDatos[i][j] = 'X';
                    etiquetasMatriz[i][j].setText("X");
                    etiquetasMatriz[i][j].setForeground(Color.BLACK);

                    contadorSimbolos++;
                    lblContador.setText("Símbolos '@' encontrados: " + contadorSimbolos);
                }

                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    LOGGER.warning("Error en la pausa de escaneo: " + e.getMessage());
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    public static void main(String[] args) {
        char[][] matriz = {
            {'.', '@', '.', '.'},
            {'.', '.', '@', '.'},
            {'@', '.', '.', '.'},
            {'.', '.', '.', '@'}
        };

        int filas = matriz.length;
        int columnas = matriz[0].length;

        JFrame ventana = new JFrame("Cazador de @");
        // Se utiliza el acceso estático correcto mediante WindowConstants (SonarQube rule java:S3252)
        ventana.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        ventana.setSize(400, 450);
        ventana.setLayout(new BorderLayout());

        JLabel lblContador = new JLabel("Símbolos '@' encontrados: 0", SwingConstants.CENTER);
        lblContador.setFont(new Font("Arial", Font.BOLD, 16));
        ventana.add(lblContador, BorderLayout.NORTH);

        JPanel panelMatriz = new JPanel();
        panelMatriz.setLayout(new GridLayout(filas, columnas));

        JLabel[][] etiquetas = new JLabel[filas][columnas];

        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                etiquetas[i][j] = new JLabel(String.valueOf(matriz[i][j]), SwingConstants.CENTER);
                etiquetas[i][j].setFont(new Font("Arial", Font.BOLD, 26));
                panelMatriz.add(etiquetas[i][j]);
            }
        }

        ventana.add(panelMatriz, BorderLayout.CENTER);
        ventana.setLocationRelativeTo(null); 
        ventana.setVisible(true);

        HiloCazador hilo = new HiloCazador(matriz, etiquetas, lblContador);
        hilo.start();
    }
}
