/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package raccoltaDatiAmbientali;

import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author daliaabbruciati
 */
public class Sensor extends Thread{
    // Attributi interni
    
    // Riferimento all'oggetto condiviso Cloud
    private Cloud mioCloud;
    // Riferimento all'oggetto condiviso Environment
    private Environment mioEnvironment;
    // Variabile per memorizzare il tempo di inizio 
    private long tempoInizio;
    // Metodo per generare in modo casuale il valore dell'errore
    private Random rnd;
    // Variabile dell'errore
    public int errore;
    // Array per contenere le letture dai valori dai due buffer Temp e Light
    private double[] letture;
    /*
    // ArrayList per contenere tutti i valori di temperatura inseriti 
    // dal sensore n per la simulazione nell'analisi sperimentale dei dati
    public ArrayList<Double> temp;          
    // ArrayList per contenere tutti i valori di luminosità inseriti 
    // dal sensore n per la simulazione nell'analisi sperimentale dei dati
    public ArrayList<Double> light; 
    */        
    
    
    // Costruttore della classe Sensor. 
    public Sensor(String name, Cloud cl, Environment env, long tempoInizio){
        super(name);
        this.mioCloud       = cl;
        this.mioEnvironment = env;
        this.tempoInizio    = tempoInizio;
        this.rnd            = new Random();
        this.errore         = this.rnd.nextInt(21)-10;
        /* this.temp        = new ArrayList<Double>(); */
        /* this.light       = new ArrayList<Double>(); */
    }// fine costruttore
    
    
    // Metodo che implementa il comportamento del Thread Sensor. 
    @Override
    public void run(){   
        // stampo il valore di errore iniziale
        System.out.println("("+getTempoTrascorso()+") --> "+"[S]"+super.getName()+
                " generato con errore pari a: "+this.errore+"%");
        // uso la variabile booleana per gestire la terminazione deferita
        boolean isAlive = true;
        while(isAlive){   
            try{
                // misura i parametri ambientali                        
                letture = this.mioEnvironment.measureParameters(this); 
                                
                // applica l'errore iniziale ai valori di temperatura
                letture[0] = this.setErrore(errore, letture[0]);
                
                /* Aggiungo i valori inseriti nella coda dei valori temperatura
                 temp.add(letture[0]); */     
                // applica l'errore iniziale ai valori di luminosità
                letture[1] = this.setErrore(errore, letture[1]); 
                
                /* Aggiungo i valori inseriti nella coda dei valori luminosità                
                 light.add(letture[1]); */ 
                // invia i dati all'oggetto cloud
                this.mioCloud.writeData(this, letture[0],letture[1]);               
                
                // si sospende per 400 millisecondi
                Thread.sleep(400);
                
            }catch(InterruptedException e){
                System.out.println(e);
                isAlive = false;
            }            
        }// fine while
        System.out.println("("+getTempoTrascorso()+") --> "+
                "Il thread "+super.getName()+" termina esecuzione");           
    }// fine metodo run
    
    
    // Metodo che applica l'errore generato inizialmente, ai valori letti
    // nell'Environment. 
    private double setErrore(int errore, double lettura){
        double toRet;
        // calcolo la percentuale dell'errore sul totale della lettura.
        double margine = (lettura * errore)/100;
        if(errore > 0){
            // se l'errore generato è positivo,
            // allora aumenterò il valore delle letture in positivo
            toRet = lettura + margine;
        }else{
            // se l'errore generato è negativo,
            // allora decrementerò il valore delle letture in negativo
            toRet = lettura - margine;
        }
        return toRet;        
    }// fine metodo setErrore
    
    
    // Metodo interno per calcolare il tempo trascorso dall'inizio della
    // simulazione. 
    private String getTempoTrascorso(){
        return System.currentTimeMillis() - this.tempoInizio+" ms";
    }// fine metodo getTempoTrascorso
    
}// Fine classe
