package uvm;
import javax.swing.JLabel;
import java.awt.Color;
import javax.swing.SwingUtilities;


public class HiloClima extends Thread {

    private JLabel[][] matriz;
    private int activacionesPendientes = 0;

    public HiloClima( JLabel[][] matriz ){
        this.matriz = matriz;
    }
    

    public synchronized void activar(){
        this.activacionesPendientes++;
        notifyAll();
    }

    @Override
    public void run(){
        while(true){
            synchronized(this){
            while(activacionesPendientes==0){
                try {
                    wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
            this.activacionesPendientes--;
        }

        for(int i=0;i<matriz.length;i++){
            for(int j=0;j<matriz[i].length;j++){
                JLabel celda=matriz[i][j];
                SwingUtilities.invokeLater(()-> {if( celda.getForeground() == Color.BLACK){celda.setForeground(Color.RED);
                }else if(celda.getForeground() == Color.RED){
                    celda.setForeground(Color.BLUE);
                }else if (celda.getForeground() == Color.BLUE){
                     celda.setForeground(Color.BLACK);}
                }); 
            }
        }
    
        
        }
    }
}
