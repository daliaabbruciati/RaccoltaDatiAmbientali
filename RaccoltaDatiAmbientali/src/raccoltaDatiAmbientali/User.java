/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package raccoltaDatiAmbientali;

import java.util.Random;
/**
 *
 * @author daliaabbruciati
 */
public class User extends Thread{
    // Attributi interni dell'oggetto
    
    // Riferimento all'oggetto condiviso Cloud
    private Cloud mioCloud;
    // Variabile per memorizzare il tempo di inizio 
    private long tempoInizio;
    // Generatore di numeri casuali per simulare il tempo di attesa
    private Random rnd;
    // Array per memorizzare il tempo di lettura di un singolo utente
    public long[] tempoLettura;
    // Variabile per memorizzare i tempi di attesa totali dei Thread
    private double tempoLetturaTot; 
    
        
    // Costruttore della classe User. 
    public User(String name, Cloud cl, long tempoInizio){
        super(name);
        this.mioCloud        = cl;
        this.tempoInizio     = tempoInizio;
        this.rnd             = new Random();
        this.tempoLettura    = new long[100];
        this.tempoLetturaTot = 0;         
    }// fine costruttore
     
    
    // Metodo che implementa il comportamento del Thread User. 
    @Override
    public void run(){
        System.out.println("("+getTempoTrascorso()+") --> "+"[U]"+super.getName()+
                " inizia l'esecuzione");
        for(int i = 0; i < 100; i ++){
            try{
                // attende per un tempo casuale estratto nell'intervallo [0,99]ms
                Thread.sleep(this.rnd.nextInt(100));  
                
                // salvo il tempo prima di affettuare le letture
                long tempoStart = System.currentTimeMillis();
                System.out.println("("+getTempoTrascorso()+") --> "+
                        "[U]"+super.getName()+" INIZIA LA LETTURA DEI DATI"); 
                // legge i valori nel buffer Temperatura
                this.mioCloud.readAverageTemp(this);
                // legge i valori nel buffer Luminosità
                this.mioCloud.readAverageLight(this);
                System.out.println("("+getTempoTrascorso()+") --> "+
                        "[U]"+super.getName()+" FINISCE LA LETTURA DEI DATI"); 
                
                // salvo il tempo dopo aver effettuato le letture
                long tempoEnd = System.currentTimeMillis(); 
                
                // calcolo il tempo di lettura totale                              
                this.tempoLettura[i] = (tempoEnd - tempoStart); 
                
                // sommo i tempi di lettura di un singolo utente
                // fatti durante tutta la durata della simulazione
                this.tempoLetturaTot += tempoLettura[i];
                System.out.println("("+getTempoTrascorso()+") --> "+
                        "[U]"+super.getName()+" ha impiegato per la lettura "
                        +tempoLettura[i]+"ms");
            }catch(InterruptedException e){
                System.out.println(e);                
            }            
        }               
        System.out.println("("+getTempoTrascorso()+") --> "+"Il thread "
                +super.getName()+" termina esecuzione");        
    }// fine metodo run
    
    
    // Metodo per ricavare il tempo medio di lettura del singolo Thread User. 
    public double getAvgTime(){
        return this.tempoLetturaTot / 100;
    }// fine metodo getAvgTime
    
                
    // Metodo che ritorna la somma dei tempi totali di lettura 
    // di un singolo Thread User. 
    public double getSommaTempoLetturaTot(){
       return this.tempoLetturaTot;
    }// fine metodo getSommaTempoLetturaTot        
    
    
    // Metodo interno per calcolare il tempo trascorso dall'inizio della
    // simulazione. 
    private String getTempoTrascorso(){
        return System.currentTimeMillis() - this.tempoInizio+" ms";
    }// fine metodo getTempoTrascorso
               
}// Fine classe
