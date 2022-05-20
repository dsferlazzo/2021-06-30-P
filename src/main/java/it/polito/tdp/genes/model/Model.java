package it.polito.tdp.genes.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm.SingleSourcePaths;
import org.jgrapht.alg.shortestpath.AllDirectedPaths;
import org.jgrapht.alg.shortestpath.BellmanFordShortestPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.genes.db.GenesDao;

public class Model {
	
	GenesDao dao;
	List<String> localizations;
	Graph<String, DefaultWeightedEdge> grafo;
	List<Collegamento> edges;	//LISTA DELL'OGGETTO RAPPRESENTANTE L'ARCO DEL GRAFO
	
	
	public List<String> getAllLocalizations(){
		this.dao = new GenesDao();
		localizations = this.dao.getAllLocalizations();
		return localizations;
	}
	
	public List<Collegamento> getAllCollegamenti(){
		this.dao=new GenesDao();
		return this.dao.getAllCollegamenti();
	}
	
	public void creaGrafo() {
		grafo = new SimpleWeightedGraph<String, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		Graphs.addAllVertices(grafo, localizations);
		
		//AGGIUNGO GLI ARCHI
		edges = this.getAllCollegamenti();
		for(Collegamento c : edges) {
			Graphs.addEdgeWithVertices(grafo, c.getLocalization1(), c.getLocalization2(), c.getPeso());
		}
		
		
	}
	
	public String infoGrafo() {
		return "Grafo creato: " + grafo.vertexSet().size() + " vertici, " +
				grafo.edgeSet().size() + " archi\n";
	}
	
	public String statistiche(String localizzazione) {
		
		String result = "Adiacenti a: " + localizzazione+"\n";
		List<String> adiacenti = Graphs.neighborListOf(grafo, localizzazione);
		int peso;
		
		
		for(String v : adiacenti) {
			if(grafo.getEdge(v, localizzazione)!=null)
				peso = (int) grafo.getEdgeWeight(grafo.getEdge(v, localizzazione));
			else peso = (int) grafo.getEdgeWeight(grafo.getEdge(localizzazione, v));
		result += v + "     " + peso + "\n";
		}
		return result + "\n";
		
	}
	
	public List<String> getMaxPath(String localizzazione){
		
		//0) CREARE IL GRAFO SIMPLEDIRECTEDWEIGHTEDGRAPH
		Graph<String, DefaultWeightedEdge> sottoGrafo = new SimpleDirectedWeightedGraph<String, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		
		//1) CERCO LA COMPONENTE CONNESSA DEL VERTICE PASSATO COME PARAMETRO
		ConnectivityInspector<String, DefaultWeightedEdge> ci = 
				new ConnectivityInspector<String, DefaultWeightedEdge>(grafo);
		Set<String> vertici = ci.connectedSetOf(localizzazione);
		//for(String v : vertici)	System.out.println(v + "\n");	//DEBUGGING
		
		
		//2) AGGIUNGO VERTICE E COMPONENTE CONNESSA A GRAFO (NOTA: VERTICE PARAMETRO COMPRESO IN CC, MI BASTA AGGIUNGERE I VERTICI IN CC)
		Graphs.addAllVertices(sottoGrafo, vertici);
		
		//3) RIPRENDERE GLI ARCHI DEL GRAFO, ED AGGIUNGERLI BIDIREZIONALMENTE AL GRAFO; NEL CASO IN CUI L'ARCO NON PUO ESSERE INSERITO NEL SOTTO-GRAFO, LA FUNZIONE DI INSERIMENTO RITORNERA NULL
		for(Collegamento c : edges)
		{
			Graphs.addEdgeWithVertices(sottoGrafo, c.getLocalization1(), c.getLocalization2(), c.getPeso());
			Graphs.addEdgeWithVertices(sottoGrafo, c.getLocalization2(), c.getLocalization1(), c.getPeso());
		}
		
		
		//4) USARE ALLDIRECTEDPATHS.GETALLPATHS TRA VERTICE PARAMETRO A LIST<VERTICE> COMPONENTECONNESSA
		AllDirectedPaths<String, DefaultWeightedEdge> adp = 
				new AllDirectedPaths<String, DefaultWeightedEdge>(sottoGrafo);
		Set<String> targetVertices = sottoGrafo.vertexSet();
		Set<String> sourceVertex = new HashSet<String>();
		sourceVertex.add(localizzazione);
		//targetVertices.remove(localizzazione);
		
		List<GraphPath<String, DefaultWeightedEdge>> percorsi =
				adp.getAllPaths(sourceVertex, targetVertices, true, null);
		//System.out.println(percorsi.get(0).getVertexList());	//DEBUGGING
		System.out.println("Numero percorsi trovati: " + percorsi.size());	//DEBUGGING
		
		//5) TROVARE IL PERCORSO DI LUNGHEZZA MASSIMA, maxPath
		int maxLength = 0;
		GraphPath<String, DefaultWeightedEdge> maxPath = null;
		
		for(GraphPath<String, DefaultWeightedEdge> gp : percorsi) {
			if(gp.getLength()>maxLength) {
				maxPath = gp;
				maxLength = gp.getLength();
				//System.out.println(maxPath.getLength());	//DEBUGGING
			}
		}
		//System.out.println(maxPath.getLength());	//DEBUGGING
		
		
		//6) RITORNARE IL PERCORSO DI MASSIMA LUNGHEZZA COSI TROVATO
		System.out.println("Lunghezza massima: " + maxPath.getLength());
		return maxPath.getVertexList();
	}
	
	public List<String> getMaxPathClear(String localizzazione){
		
		Graph<String, DefaultWeightedEdge> sottoGrafo = new SimpleDirectedWeightedGraph(DefaultWeightedEdge.class);
		ConnectivityInspector ci = new ConnectivityInspector(grafo);
		Set<String> subVertices = ci.connectedSetOf(localizzazione);
		
		Graphs.addAllVertices(sottoGrafo, subVertices);
		
		for(Collegamento c : edges) {
			Graphs.addEdgeWithVertices(sottoGrafo, c.getLocalization1(), c.getLocalization2(), c.getPeso());
			Graphs.addEdgeWithVertices(sottoGrafo, c.getLocalization2(), c.getLocalization1(), c.getPeso());
		}
		
		AllDirectedPaths adp = new AllDirectedPaths(sottoGrafo);
		
		Set<String> sourceVertex = new HashSet<String>();
		sourceVertex.add(localizzazione);
		Set<String> targetVertex = sottoGrafo.vertexSet();
		
		List<GraphPath> percorsi = adp.getAllPaths(sourceVertex, targetVertex, true, null);
		int maxLength = 0;
		List<String> maxPath = null;
		
		for(GraphPath gp : percorsi)
			if(gp.getLength()>maxLength)
			{
				maxPath = gp.getVertexList();
				maxLength = gp.getLength();
			}
		
		return maxPath;
	}

}