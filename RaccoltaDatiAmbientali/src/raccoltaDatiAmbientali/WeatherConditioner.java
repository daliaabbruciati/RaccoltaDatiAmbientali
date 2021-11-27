/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package raccoltaDatiAmbientali;

/**
 *
 * @author daliaabbruciati
 */
public class WeatherConditioner extends Thread{
    // Attributi interni
    private Environment mioEnvironment;
    // Variabile per memorizzare il tempo di inizio 
    private long tempoInizio;
    
    // Costruttore della classe. 
    public WeatherConditioner(String name, Environment env, long tempoInizio){        
        super(name);
        this.mioEnvironment = env;
        this.tempoInizio    = tempoInizio;
    }// fine costruttore
    
    
    // Metodo che implementa il comportamento del Thread WeatherConditioner. 
    @Override
    public void run(){  
         System.out.println("("+getTempoTrascorso()+") --> "+"[WC]"+super.getName()+
                " inizia l'esecuzione");
        // variabile booleana per gestire la terminazione deferita
        boolean isAlive = true;        
        while(isAlive){
             try{                
                // si sospende per 400ms 
                Thread.sleep(400);                
                
                // quando si risveglia aggiorna i parametri 
                System.out.println("("+getTempoTrascorso()+") --> "+
                        "[WC]"+super.getName()+" aggiorna i parametri");                
                this.mioEnvironment.updateParameters();
                
            }catch(InterruptedException e){
                System.out.println(e);
                isAlive = false;
            }            
        }
        System.out.println("("+getTempoTrascorso()+") --> "+
                "Il thread "+super.getName()+" termina esecuzione");        
    }// fine metodo run
        
    
    // Metodo interno per calcolare il tempo trascorso dall'inizio della
    // simulazione.
    private String getTempoTrascorso(){
        return System.currentTimeMillis() - this.tempoInizio+" ms";
    }// fine metodo getTempoTrascorso
    
}// Fine classe
