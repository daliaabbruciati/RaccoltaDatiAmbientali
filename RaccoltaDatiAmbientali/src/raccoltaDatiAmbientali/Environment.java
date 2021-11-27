/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package raccoltaDatiAmbientali;

import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author daliaabbruciati
 */

// Oggetto condiviso dai thread Sensor e WeatherConditioner. 
public class Environment {
    // Attributi interni
    
    // Array per memorizzare i dati di temperatura e luminosità
    private double[] letture;
    // Variabile in cui memorizzare i valori di temperatura
    private double temp;
    // Variabile in cui memorizzare i valori di luminosità
    private double light;  
    // Variabile per memorizzare il tempo di inizio 
    private long tempoInizio;
    
    // Attributi di sincronizzazione
    
    // Lock per la mutua esclusione a guardia delle variabili
    // temp e light     
    private ReentrantLock mutexEnv;
              
    // Costruttore della classe
    public Environment(long tempoInizio){        
        this.letture     = new double[2];
        this.light       = 0;
        this.tempoInizio = tempoInizio;
        this.mutexEnv    = new ReentrantLock();
    }// end costruttore
    
            
    // Metodo che cattura i valori di temperatura e luminosità, invocato
    // dai Thread Sensor. 
    public double[] measureParameters(Sensor s) throws InterruptedException{                
        // INIZIO SEZIONE CRITICA
        this.mutexEnv.lock();
        try{         
            // memorizza i dati di temperatura nella prima cella
            letture[0] = temp;
            System.out.println("("+getTempoTrascorso()+") --> "+
                    "Valore TEMP misurato nell'Environment: "+temp+
                    ", dal "+s.getName()+" con errore: "+s.errore+"%");
            // memorizza i dati di luminosità nella seconda cella
            letture[1] = light;
            System.out.println("("+getTempoTrascorso()+") --> "+
                    "Valore LIGHT misurato nell'Environment: "+light+
                    ", dal "+s.getName()+" con errore: "+s.errore+"%");
        }finally{
            this.mutexEnv.unlock();
            // FINE SEZIONE CRITICA
        }    
    return letture;    
    }// fine metodo measureParameters
    
    
    // Metodo invocato da WeatherConditioner per modificare
    // i valori di temperatura e luminosità secondo la formula indicata
    // nella specifica del progetto.
    public void updateParameters() throws InterruptedException{
        // INIZIO SEZIONE CRITICA
        this.mutexEnv.lock();
        try{
            this.light = light + 1000;
            this.temp = 10 + (0.00022 * this.light);
        }finally{
            this.mutexEnv.unlock();
            // FINE SEZIONE CRITICA
        }
    }// fine metodo updateParameters
    
    
    // Metodo interno per calcolare il tempo trascorso dall'inizio della
    // simulazione. 
    private String getTempoTrascorso(){
        return System.currentTimeMillis() - this.tempoInizio+" ms";
    }// fine metodo getTempoTrascorso
    
}// Fine classe
