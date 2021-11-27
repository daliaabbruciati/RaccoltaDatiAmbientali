# Raccolta dati ambientali
<h4>Progetto in java per il corso di "Sistemi operativi". </h4>

<ul>
  <li>
    <strong><h4>Specifiche del progetto</h4></strong> 
    Scrivere un programma multithread che simuli il funzionamento di un sistema Internet of Things (IoT) per la raccolta di informazioni ambientali e l’aggregazione su cloud. In particolare, il sistema sarà costituito da un numero nSensors di sensori in grado di accedere all’ambiente e misurare temperatura e luminosità. Le condizioni ambientali saranno controllate da un thread (WeatherConditioner) che avrà il compito di modificare periodicamente i valori di temperatura e luminosità presenti nell’ambiente secondo la relazione descritta in seguito.
I sensori, dopo aver effettuato la misurazione, invieranno i dati in cloud dove saranno memorizzati utilizzando dei buffer circolari a capacità prefissata agendo, in questo modo, come dei produttori. Saranno, inoltre, presenti nUsers utenti che sottometteranno richieste per ottenere i valori medi di temperatura e luminosità registrati in cloud. Ogni lettura da parte di un utente sarà ottenuta mediando 4 valori cariati nel buffer in ordine FIFO. La lettura comporterà la rimozione dei 4 valori letti come avviene per un normale consumatore. Avendo il buffer una capacità limitata i consumatori dovranno attendere la presenza di dati nel buffer ed i produttori dovranno attendere in caso di mancanza di spazio. I sensori in attesa di scrivere e gli utenti in attesa di leggere saranno gestiti in ordine FIFO. La simulazione terminerà  quando tutti gli utenti avranno effettuato 100 letture ciascuno.
  </li>
</ul>



