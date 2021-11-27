/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package raccoltaDatiAmbientali;

import java.util.Scanner;

/**
 *
 * @author daliaabbruciati
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {        
        // Variabile per memorizzare il tempo assoluto di inizio 
        long tempoInizio               = System.currentTimeMillis();
        //Inizializzazione dell'oggetto condiviso Cloud
        Cloud cloud                    = new Cloud(30, tempoInizio); 
        // Inizializzazione dell'oggetto condiviso Environment
        Environment environment        = new Environment(tempoInizio);
        // Inizializzazione dell'oggetto Waether Conditioner
        WeatherConditioner weatherCond = new WeatherConditioner("Weather Conditioner",
                                         environment, tempoInizio);        
        // Oggetto scanner per catturare l'input da tastiera. 
        Scanner input                  = new Scanner(System.in);        
        // Array contenente il numero di Thread Sensor
        Sensor sensori[];
        // Numero di Sensor scelto dall'utente
        int nSensors                   = 0;
        // Array contenente il numero di Thread User
        User utenti[];
        // Numero di User scelti dall'utente
        int nUsers                     = 0;
                        
               
        // Validazione stretta: rimango all'interno del while finché 
        // l'utente non inserisce un numero di Sensor maggiore o uguale a 0.        
        System.out.println("Inserire il numero di sensori:");
        do{
            nSensors = input.nextInt();
            if(nSensors <= 0)
                System.out.println("Valore non valido!\n"+
                        "Inserire nuovo valore maggiore di 0");
        }while(nSensors <= 0);
        
        // Validazione stretta: rimango all'interno del while finché 
        // l'utente non inserisce un numero di User maggiore o uguale a 0.        
        System.out.println("Inserire il numero di utenti:");            
        do{
            nUsers = input.nextInt();                
            if(nUsers <= 0)
                System.out.println("Valore non valido!\n"+
                        "Inserire nuovo valore maggiore di 0");                    
        }while(nUsers <= 0);
        
        
        System.out.println("--- INIZIO SIMULAZIONE ---");
        
        // Faccio partire l'oggetto WeatherConditioner
        weatherCond.start();
        
        // Creo l'istanza dei Sensor
        sensori = new Sensor[nSensors]; 
        for(int i = 0; i < sensori.length; i++){
            // creo i Thread Sensor
            sensori[i] = new Sensor("Sensore_"+i, cloud, environment,tempoInizio);            
            // faccio partire i Thread
            sensori[i].start();            
        }
                
        
        // Creo l'istanza degli User
        utenti = new User[nUsers];
        for(int i = 0; i < utenti.length; i++){
            // creo i Thread User
            utenti[i] = new User("Utente_"+i, cloud,tempoInizio);
            // faccio partire i Thread
            utenti[i].start();             
        }
        
              
        // Mi metto in attesa della terminazione dei Thread User
        try{
            for(int i = 0; i < nUsers; i++)                
                utenti[i].join();            
        }catch(InterruptedException e){
            System.out.println(e);
        }        
        
        
              
        // Mi metto in attesa della terminazione dei Thread Sensor
        try{
            for(int i = 0; i < nSensors; i++){
                sensori[i].interrupt();
                sensori[i].join();
            }
        }catch(InterruptedException e){
            System.out.println(e);
        }
        
                        
         // Mi metto in attesa della terminazione del Thread WeatherConditioner                 
        try{
            weatherCond.interrupt();            
            weatherCond.join();
        }catch(InterruptedException e){
            System.out.println(e);
        }
                                 
        System.out.println("\n--- SIMULAZIONE TERMINATA ---\n");
        
        
        // Al termine della simulazione vengono calcolati la media e la deviazione 
        // standard del tempo necessario per effettuare la lettura dei valori di 
        // temperatura e luminosità da parte degli utenti
        
        // calcolo la somma dei tempi di lettura di ogni utente
        double somma = 0;
        for(int i = 0; i < nUsers; i++)
            // somma di tutti i tempi di lettura di ogni utente
            somma += utenti[i].getSommaTempoLetturaTot();                             
        
        // stampo il tempo MEDIO di lettura per ogni singolo utente
        for(int i = 0; i < nUsers; i++){
        System.out.println("--> Tempo medio di lettura di Utente_"+
                    i+": "+utenti[i].getAvgTime()+"ms");
        }
        // MEDIA 
        double media = 0;
        // media delle letture totali per tutti gli utenti
        media = somma / (nUsers * 100);                 
        // stampo il tempo di lettura medio
        System.out.printf("\nTEMPO MEDIO TOTALE DELLE LETTURE: %.2fms\n",media); 
        
        // DEVIAZIONE STANDARD    
        double scarto = 0;        
        double varianza = 0;
        double deviazione = 0;               
        // scorro tutti gli utenti
        for(int i = 0; i < nUsers; i++){            
            // scorre tutte le letture del singolo utente
            for(int j = 0; j < 100; j++){
                // calcolo lo scarto
                scarto = utenti[i].tempoLettura[j] - media; 
                // calcolo la varianza
                varianza += Math.pow(scarto, 2);                                                 
            }                    
        }
        // calcolo la deviazione standard
        deviazione = Math.sqrt(varianza / (nUsers * 100)); 
        // stampo la deviazione
        System.out.printf("DEVIAZIONE STANDARD TOTALE: %.2fms\n",deviazione);   
                   
        
        /* Stampa per analisi sperimentale del punto 3 della specifica.
           Il sensore preso come riferimento è il numero 3.   
          
           System.out.println("\nTEMP;LIGHT");
           for(int i = 0; i < sensori[3].temp.size(); i++){
            System.out.println(sensori[3].temp.get(i)+";"+
                sensori[3].light.get(i)+";");
        }*/                
                                    
    }// Fine main
    
}// fine classe
