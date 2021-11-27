/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package raccoltaDatiAmbientali;

import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author daliaabbruciati
 */

// Oggetto condiviso dai thread Sensor e User
public class Cloud {
    // Attributi funzionali
    
    // Buffer contenente i valori di temperatura
    private double bufferTemp[];
    // Buffer contenente i valori di luminosità
    private double bufferLight[];
    // Contatore elementi nel buffer temperatura
    private int countBufferTemp;
    // Contatore elementi nel buffer luminosità
    private int countBufferLight;
    // Puntatori logici al buffer temperatura
    private int inTemp, outTemp;
    // Puntatori logici al buffer luminosità
    private int inLight, outLight;    
    // Variabile per memorizzare il tempo di inizio 
    private long tempoInizio;
    
    // Attributi di sincronizzazione
    
    // Semaforo binario per garantire la mutua esclusione 
    // a guardia delle variabili condivise dai Sensor e User
    private ReentrantLock mutexCloud;
    // Semaforo contatore per sospendere i Thread Sensor in buffer Temp
    private Semaphore notFullTemp; 
    // Semaforo contatore per sospendere i Thread Sensor in buffer Light
    private Semaphore notFullLight; 
    // Semaforo contatore per sospendere i Thread User in buffer Temp
    private Semaphore notEmptyTemp;
    // Semaforo contatore per sospendere i Thread User in buffer Light
    private Semaphore notEmptyLight;
    // Contatore permessi di buffer temperatura
    private int itemBufferTemp;
    // Contatore permessi di buffer luminosità
    private int itemBufferLight;
    
    
    // Costruttore dell'oggetto Cloud. 
    public Cloud(int size, long tempoInizio){
        this.bufferTemp        = new double[size];
        this.bufferLight       = new double[size];
        this.countBufferTemp   = 0;
        this.countBufferLight  = 0;
        this.inTemp            = 0;
        this.outTemp           = 0;
        this.inLight           = 0;
        this.outLight          = 0;        
        this.tempoInizio       = tempoInizio;
        this.mutexCloud        = new ReentrantLock();
        this.notFullTemp       = new Semaphore(this.bufferTemp.length,true);
        this.notEmptyTemp      = new Semaphore(0,true);                                
        this.notFullLight      = new Semaphore(this.bufferLight.length,true);
        this.notEmptyLight     = new Semaphore(0,true);                                
        this.itemBufferTemp    = 0;
        this.itemBufferLight   = 0;
    }// fine costruttore
    
        
    // Metodo invocato dai thread Sensor per scrivere i dati
    // nei due buffer. 
    public void writeData(Sensor s, double dataTemp, double dataLight) throws InterruptedException{               
        // se i sensori non hanno almeno uno spazio libero in entrambi i buffer
         // si sospendono sul proprio semaforo        
            this.notFullTemp.acquire();
            this.notFullLight.acquire();       
              
        try{                                               
            // se sono qua significa che hanno almeno uno spazio libero e
            // possono iniziare ad inserire i dati negli appositi buffer.
            // Per farlo il sensore deve essere in mutua esclusione con 
            // l'oggetto User 
            
            // INIZIO SEZIONE CRITICA    
            this.mutexCloud.lock();  
                        
            // aumenta il numero di permessi nel buffer temperatura
            this.itemBufferTemp ++;
            // il sensore inserisce dati nel buffer della temperatura
            this.bufferTemp[this.inTemp] = dataTemp;  
            System.out.println("("+getTempoTrascorso()+") --> "+"[S]"+s.getName()
                    +" inserisce dato ["+dataTemp+"] nel buffer Temp"); 
            // aggiorna il puntatore logico input
            this.inTemp = (this.inTemp+1)%this.bufferTemp.length;
            // incrementa il numero di elementi nel buffer temperatura
            this.countBufferTemp++;                  
            
            // aumenta il numero di permessi nel buffer luminosità
            this.itemBufferLight++;
            // il sensore inserisce i dati nel buffer della luminosità
            this.bufferLight[this.inLight] = dataLight;    
            System.out.println("("+getTempoTrascorso()+") --> "+"[S]"+s.getName()+
                    " inserisce dato ["+dataLight+"] nel buffer Light"); 
            // aggiorna il puntatore logico input               
            this.inLight = (this.inLight+1)%this.bufferLight.length;
            // incrementa il numero di elementi nel buffer luminosità
            this.countBufferLight++;
                        
            // stampa il numero di elemeti in entrambi i buffer
            System.out.println("("+getTempoTrascorso()+") --> "+
                "Numero di elementi in buffer Temperatura: "+this.countBufferTemp+
                "\n("+getTempoTrascorso()+") --> "
                +"Numero di elementi in buffer Luminosità: "+this.countBufferLight);

            // i sensori sveglieranno gli utenti solo quando ci saranno 
            // 4 elementi da leggere nei buffer
            if(this.itemBufferTemp >= 4){
                this.notEmptyTemp.release(); 
                // una volta letti i dati, essi verranno rimossi decrementando 
                // il contatore del buffer temperatura
                this.itemBufferTemp -=4;
            }
            if(this.itemBufferLight >= 4){ 
                this.notEmptyLight.release(); 
                // una volta letti i dati, essi verranno rimossi decrementando 
                // il contatore del buffer luminosità
                this.itemBufferLight -=4;
            }                    
        }finally{
            this.mutexCloud.unlock();
            // FINE SEZIONE CRITICA
        }        
    }// fine metodo writeData
    
        
    // Metodo invocato dagli User per leggere il valore medio
    // ricavato dai dati presenti nel buffer della temperatura. 
    public double readAverageTemp(User u) throws InterruptedException{
        double avgTemp = -1;               
        // finchè nel buffer non sono presenti 4 valori
        // il Thread User si sospende in attesa di essi                        
        this.notEmptyTemp.acquire();         
        
        try{
            // se sono qua significa che ci sono valori da leggere nel buffer.
            // Per farlo l'utente deve essere in mutua esclusione con 
            // l'oggetto Sensor            
            // INIZIO SEZIONE CRITICA
            this.mutexCloud.lock();
            
            System.out.println("("+getTempoTrascorso()+") --> "+"[U]"+u.getName()+
                    " legge i valori da buffer Temp: ");
            // stampa i 4 valori che devo leggere dal buffer temperatura
            for(int i = 0; i < 4; i++)                
                System.out.println("("+getTempoTrascorso()+") --> "+
                        this.bufferTemp[(outTemp+i)%this.bufferTemp.length]);
                        
            // ricava il valore medio dei dati tramite l'algoritmo e lo stampa
            avgTemp = this.getAvgDataTemp(outTemp);
            System.out.println("("+getTempoTrascorso()+") --> "+
                    "Valore medio dei dati TEMP: "+avgTemp);
            
            // aggiorna il puntatore logico di output
            this.outTemp = (this.outTemp+4)%this.bufferTemp.length; 
            // decrementa il numero di elementi letti
            this.countBufferTemp -=4;
            
            // segnala ai sensori che ci sono 4 nuovi posti disponibili 
            // nel buffer temperaura
            this.notFullTemp.release(4);                         
        }finally{
            this.mutexCloud.unlock();
            // FINE SEZIONE CRITICA
        }                       
        return avgTemp;        
    }// fine metodo readAverageTemp
    
    
    // Metodo invocato dagli User per leggere il valore medio
    // ricavato dai dati presenti nel buffer della luminosità. 
    public double readAverageLight(User u) throws InterruptedException{
        double avgLight = -1;                
        // finchè nel buffer non sono presenti 4 valori
        // il Thread User si sospende in attesa di essi                                        
        this.notEmptyLight.acquire();          
        
        try{
            // se sono qua significa che ci sono valori da leggere nel buffer.
            // Per farlo l'utente deve essere in mutua esclusione con 
            // l'oggetto Sensor
            
            // INIZIO SEZIONE CRITICA
            this.mutexCloud.lock();
            
            System.out.println("("+getTempoTrascorso()+") --> "+"[U]"+u.getName()+
                    " legge i valori da buffer Light: ");
            // stampa i 4 valori che devo leggere dal buffer
            for(int i = 0; i < 4; i++)                
                System.out.println("("+getTempoTrascorso()+") --> "+
                        this.bufferLight[(outLight+i)%this.bufferLight.length]); 
            
            // ricava il valore medio dei dati tramite l'algoritmo
            avgLight = this.getAvgDataLight(outLight);
            System.out.println("("+getTempoTrascorso()+") --> "+
                    "Valore medio dei dati LIGHT: "+avgLight);
            
            // aggiorna il puntatore logico di output
            this.outLight = (this.outLight+4)%this.bufferLight.length;
            // decremento il numero di elementi letti
            this.countBufferLight -=4;
            
            // segnala ai sensori che ci sono 4 nuovi posti disponibili 
            // nel buffer luminosità
            this.notFullLight.release(4);                   
        }finally{
            this.mutexCloud.unlock();
            // FINE SEZIONE CRITICA
        }                               
        return avgLight;        
    }// fine metodo readAverageLight
    
        
    // Metodo per ricavare il valore medio dai dati di temperatura. 
    private double getAvgDataTemp(int out){
        double sumData = 0;
        double avgData = 0;
        // leggo i 4 dati nel buffer
        for(int i = 0; i < 4; i++){
            // faccio la somma dei 4 dati letti
            sumData += this.bufferTemp[(out+i)%bufferTemp.length];
            // divido la somma dei dati per 4
            avgData = sumData / 4;
        }                
        return avgData;        
    }// fine metodo getAvgDataTemp
    
    
    // Metodo per ricavare il valore medio dai dati di luminosità. 
    private double getAvgDataLight(int out){
        double sumData = 0;
        double avgData = 0;
        // leggo i 4 dati nel buffer
        for(int i = 0; i < 4; i++){
            // faccio la soma dei dati letti
            sumData += this.bufferLight[(out+i)%bufferLight.length];
            // divido la somma per 4
            avgData = sumData / 4;
        }                
        return avgData;        
    }// fine metodo getAvgDataLight
            
    
    // Metodo interno per calcolare il tempo trascorso dall'inizio della
    // simulazione. 
    private String getTempoTrascorso(){
        return System.currentTimeMillis() - this.tempoInizio+" ms";
    }// fine metodo getTempoTrascorso
        
}// Fine classe
