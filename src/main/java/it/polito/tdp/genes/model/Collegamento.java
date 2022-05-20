package it.polito.tdp.genes.model;

public class Collegamento {
	
	private String localization1;
	private String localization2;
	private int peso;
	public Collegamento(String localization1, String localization2, int peso) {
		super();
		this.localization1 = localization1;
		this.localization2 = localization2;
		this.peso = peso;
	}
	public String getLocalization1() {
		return localization1;
	}
	public String getLocalization2() {
		return localization2;
	}
	public int getPeso() {
		return peso;
	}
	
	

}
