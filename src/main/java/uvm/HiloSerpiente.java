package uvm;

import java.awt.Color;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

/*
Recorre la matriz celda por celda en forma de serpiente (izquierda-derecha, abajo, derecha-izquierda)
con letras de color azul y escribiendo números consecutivos (1, 2, 3...).
Puede recibir una señal para Terminar y una señal de pausa y continuar.
*/
public class HiloSerpiente extends Thread {

    private static final Logger LOGGER = Logger.getLogger(HiloSerpiente.class.getName());

    private JLabel[][] matriz;
    private int retardo = 150;
    private boolean pausado = false;
    private boolean terminado = false;

    public HiloSerpiente(JLabel[][] matriz) {
        this.matriz = matriz;
    }

    public HiloSerpiente(JLabel[][] matriz, int retardo) {
        this.matriz = matriz;
        this.retardo = retardo;
    }

    @Override
    public void run() {
        if (matriz == null) {
            LOGGER.log(Level.WARNING, "HiloSerpiente: La matriz no ha sido asignada.");
            return;
        }

        try {
            while (!terminado) {
                recorrerMatriz();
            }
        } catch (InterruptedException e) {
            LOGGER.log(Level.INFO, "HiloSerpiente interrumpido.");
            Thread.currentThread().interrupt();
        }
    }

    private void recorrerMatriz() throws InterruptedException {
        int contador = 1;
        for (int fila = 0; fila < matriz.length && !terminado; fila++) {
            contador = recorrerFila(fila, contador);
        }
    }

    private int recorrerFila(int fila, int contador) throws InterruptedException {
        int columnas = matriz[fila].length;
        if (fila % 2 == 0) {
            // Fila par: izquierda a derecha ->
            for (int col = 0; col < columnas && !terminado; col++) {
                procesarCelda(fila, col, contador++);
            }
        } else {
            // Fila impar: derecha a izquierda <-
            for (int col = columnas - 1; col >= 0 && !terminado; col--) {
                procesarCelda(fila, col, contador++);
            }
        }
        return contador;
    }

    private void procesarCelda(int fila, int col, int valor) throws InterruptedException {
        verificarPausa();

        if (terminado) {
            return;
        }

        JLabel celda = matriz[fila][col];
        SwingUtilities.invokeLater(() -> {
            celda.setForeground(Color.BLUE);
            celda.setText(String.valueOf(valor));
        });

        Thread.sleep(retardo);
    }

    private synchronized void verificarPausa() throws InterruptedException {
        while (pausado && !terminado) {
            wait();
        }
    }

    public synchronized void pausar() {
        pausado = true;
    }

    public synchronized void continuar() {
        pausado = false;
        notifyAll();
    }

    public synchronized void alternarPausa() {
        if (pausado) {
            continuar();
        } else {
            pausar();
        }
    }

    public synchronized void terminar() {
        terminado = true;
        pausado = false;
        notifyAll();
        interrupt();
    }

    public synchronized boolean isPausado() {
        return pausado;
    }

    public synchronized boolean isTerminado() {
        return terminado;
    }

    public JLabel[][] getMatriz() {
        return matriz;
    }

    public void setMatriz(JLabel[][] matriz) {
        this.matriz = matriz;
    }

    public int getRetardo() {
        return retardo;
    }

    public void setRetardo(int retardo) {
        this.retardo = retardo;
    }
}
