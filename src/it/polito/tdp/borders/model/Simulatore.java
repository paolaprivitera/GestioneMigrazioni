package it.polito.tdp.borders.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;

public class Simulatore {
	
	//Modello -> Stato del sistema ad ogni passo
	private Graph<Country, DefaultEdge> grafo; // creato nel punto 1.

	//Tipi di evento/coda prioritaria
	// 1 solo evento
	// L'unico evento da modellare e' quello che succede al tempo T
	// cioe' il fatto che i migranti a partire da uno stato si spostano
	// (non ci sono migranti che tornano indietro etc.)
	
	private PriorityQueue<Evento> queue;
	
	//Parametri della simulazione
	private int N_MIGRANTI = 1000; // di cui andiamo ad analizzare il flusso
	private Country partenza; 
	
	//Valori in output
	private int T;
	private Map<Country, Integer> stanziali;
	
	// il metodo init() riceve sicuramente i parametri della simulazione quindi partenza
	// mentre N_MIGRANTI e' fisso
	// e ovviamente il riferimento al grafo
	
	public void init(Country partenza, Graph<Country,DefaultEdge> grafo) {
		// ricevo i parametri
		this.partenza = partenza;
		this.grafo = grafo;
		
		// impostazione dello stato iniziale
		this.T = 1;
		stanziali = new HashMap<Country, Integer>();
		for(Country c : this.grafo.vertexSet()) {
			stanziali.put(c, 0); // inizializzo la mappa stanziali con il country e il numero di persone pari a 0
		}
		queue = new PriorityQueue<Evento>();
		
		// inserisco il primo evento -> evento di partenza
		this.queue.add(new Evento(T, N_MIGRANTI,partenza));
	}
	
	public void run() {
		// Estraggo un evento per volta dalla coda e lo eseguo,
		// finche' la coda non si svuota
		Evento e;

		while((e = queue.poll()) != null){ // while(!queue.isEmpty()) e poi fare la poll dentro il while
			
			// ESEGUO L'EVENTO
			
			this.T = e.getT(); // tengo traccia dell'ultimo T a cui siamo arrivati
			
			int nPersone = e.getN();
			Country stato = e.getStato();
			List<Country> confinanti = Graphs.neighborListOf(this.grafo, stato);
			int migranti = (nPersone/2) / confinanti.size();
			// poiche' la variabile migranti e' intera
			// l'approssimazione per difetto la fa gia' la divisione
			
			if(migranti > 0) { // ci sono abbastanza migranti per essere spostati
				//le persone si possono muovere
				for(Country confinante : confinanti) // aggiungo un evento per ogni stato confinante
					queue.add(new Evento(e.getT() +1,migranti,confinante));
			}
			
			int stanziali = nPersone - migranti* confinanti.size();
			this.stanziali.put(stato, this.stanziali.get(stato) + stanziali);
		}	
	}

	public int getLastT() {
		return T;
	}
	
	public Map<Country, Integer> getStanziali(){
		return this.stanziali;
	}

}
