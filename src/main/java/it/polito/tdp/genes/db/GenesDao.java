package it.polito.tdp.genes.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.polito.tdp.genes.model.Collegamento;
import it.polito.tdp.genes.model.Genes;
import it.polito.tdp.genes.model.Interactions;


public class GenesDao {
	
	public List<Genes> getAllGenes(){
		String sql = "SELECT DISTINCT GeneID, Essential, Chromosome FROM Genes";
		List<Genes> result = new ArrayList<Genes>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Genes genes = new Genes(res.getString("GeneID"), 
						res.getString("Essential"), 
						res.getInt("Chromosome"));
				result.add(genes);
			}
			res.close();
			st.close();
			conn.close();
			return result;
			
		} catch (SQLException e) {
			throw new RuntimeException("Database error", e) ;
		}
	}
	/**
	 * Ritorna la lista di tutte le stringhe localiations, all'interno della tabella classification
	 * @return
	 */
	public List<String> getAllLocalizations(){
		String sql = "SELECT DISTINCT localization "
				+ "FROM classification";
		List<String> result = new ArrayList<String>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				result.add(res.getString("localization"));
			}
			res.close();
			st.close();
			conn.close();
			return result;
			
		} catch (SQLException e) {
			throw new RuntimeException("Database error", e) ;
		}
	}
	/**
	 * ottiene dal database la lista di tutti i collegamenti tra due localization, insieme al loro peso
	 * @return
	 */
	public List<Collegamento> getAllCollegamenti(){
		String sql = "SELECT c1.Localization AS l1, c2.Localization AS l2, COUNT(DISTINCT i.`Type`) AS n "
				+ "FROM classification c1, classification c2, interactions i "
				+ "WHERE ((c1.GeneID=i.GeneID1 AND c2.GeneID=i.GeneID2) OR (c2.GeneID=i.GeneID1 AND c1.GeneID=i.GeneID2)) AND c1.Localization<>c2.Localization "
				+ "GROUP BY c1.Localization, c2.Localization";
		List<Collegamento> result = new ArrayList<Collegamento>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Collegamento c = new Collegamento(res.getString("l1"), res.getString("l2"),
						res.getInt("n"));
				result.add(c);
			}
			res.close();
			st.close();
			conn.close();
			return result;
			
		} catch (SQLException e) {
			throw new RuntimeException("Database error", e) ;
		}
	}
	


	
}
