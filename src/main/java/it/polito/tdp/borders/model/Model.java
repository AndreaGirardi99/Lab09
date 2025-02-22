package it.polito.tdp.borders.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.jgrapht.Graphs;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.traverse.DepthFirstIterator;

import it.polito.tdp.borders.db.BordersDAO;

public class Model {
	BordersDAO dao = new BordersDAO();
	private SimpleGraph<Country, DefaultEdge> graph;
	List<Country> countries;

	public Model() {
	}

	public void creaGrafo(int anno) {
		graph = new SimpleGraph<Country, DefaultEdge>(DefaultEdge.class);
		// countries = dao.getCountryByYears(anno);

		for (Border b : dao.getCountryPairs(anno)) {
			graph.addVertex(b.getC1());
			graph.addVertex(b.getC2());
			graph.addEdge(b.getC1(), b.getC2());
			aggiornaDegree();
		}
		countries = new ArrayList<>(graph.vertexSet());

		System.out.println("ARCHI: ");
		System.out.println(graph.edgeSet());
		System.out.println("\nVERTICI e grado: ");
		System.out.println(this.getV());
		System.out.println("NUM VERTICI:" + graph.vertexSet().size());
		System.out.println("NUM ARCHI:" + graph.edgeSet().size());
		System.out.println("NUM COMP CONNESSE:" + this.getNumberOfConnectedComponents());
	}

	public String getV() {
		String s = "";
		for (Country c : graph.vertexSet()) {
			s += c.getNomeDegree();
		}
		return s;
	}

	public void aggiornaDegree() {
		for (Country c : graph.vertexSet()) {
			c.setDegree(graph.degreeOf(c));
		}
	}

	public int getNumberOfConnectedComponents() {
		if (graph == null) {
			throw new RuntimeException("Grafo non esistente");
		}

		ConnectivityInspector<Country, DefaultEdge> ci = new ConnectivityInspector<Country, DefaultEdge>(graph);
		return ci.connectedSets().size();
	}

	public List<Country> getCountries() {
		return countries;
	}

	public List<Country> doIteratore(Country start) {
		List<Country> result = new ArrayList<Country>();
		DepthFirstIterator<Country, DefaultEdge> it = new DepthFirstIterator<Country, DefaultEdge>(graph, start);
		while (it.hasNext()) {
			result.add(it.next());
		}
		return result;
	}

	/*
	 * VERSIONE RICORSIVA
	 */
	private List<Country> doRicorsiva(Country start) {
		List<Country> visited = new LinkedList<Country>();
		visitaRicorsiva(start, visited);
		return visited;
	}

	private void visitaRicorsiva(Country n, List<Country> visited) {
		// Do always
		visited.add(n);
		// cycle
		for (Country c : Graphs.neighborListOf(graph, n)) {
			// filter
			if (!visited.contains(c))
				visitaRicorsiva(c, visited);
			// DO NOT REMOVE!! (no backtrack)
		}
	}
}