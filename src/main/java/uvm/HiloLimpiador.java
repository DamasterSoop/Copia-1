package uvm;

import java.awt.Color;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

public class HiloLimpiador extends Thread {

    private static final Logger LOGGER = Logger.getLogger(HiloLimpiador.class.getName());

    private JLabel[][] matriz;
    private int retardo = 200;
    private boolean pausado = false;
    private boolean terminado = false;

    public HiloLimpiador(JLabel[][] matriz) {
        this.matriz = matriz;
    }

    public HiloLimpiador(JLabel[][] matriz, int retardo) {
        this.matriz = matriz;
        this.retardo = retardo;
    }

    @Override
    public void run() {
        if (matriz == null) {
            LOGGER.log(Level.WARNING, "HiloLimpiador: La matriz no ha sido asignada.");
            return;
        }

        try {
            while (!terminado) {
                for (int fila = 0; fila < matriz.length && !terminado; fila++) {
                    for (int col = 0; col < matriz[fila].length && !terminado; col++) {
                        limpiarCelda(fila, col);
                    }
                }
            }
        } catch (InterruptedException e) {
            LOGGER.log(Level.INFO, "HiloLimpiador interrumpido.");
            Thread.currentThread().interrupt();
        }
    }

    private void limpiarCelda(int fila, int col) throws InterruptedException {
        verificarPausa();

        if (terminado) {
            return;
        }

        JLabel celda = matriz[fila][col];
        SwingUtilities.invokeLater(() -> {
            celda.setText("-");
            celda.setForeground(Color.BLACK);
            celda.setBackground(Color.WHITE);
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
