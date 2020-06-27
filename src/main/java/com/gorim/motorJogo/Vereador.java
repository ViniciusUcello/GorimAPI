/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gorim.motorJogo;

public class Vereador extends Pessoa {

    private int idEleito;

    public Vereador(int id, String nome, String cidade, double saldo) {
        super(id, nome, cidade, saldo);
        this.idEleito = 0;
    }
    
    public int getIdEleito() {
    	return this.idEleito;
    }
    
    public void eleger(int idEleito, String nomeEleito) {
    	this.idEleito = idEleito;
    	this.nome = nomeEleito;
    }
    
    public void consultaDados() {
    	System.out.println("Id do eleito vereador: " + this.idEleito +"\n"+ "Dados do vereador: \nID: " + this.id + ";\nNome: " + this.nome + ";\nCidade: " + this.cidade + ";\nSaldo: " + this.saldo + ".\n");
    }
    
    //Metodo Log
    
    public String consultaDadosString() {
    	return "Id do eleito vereador: " + this.idEleito +"\n"+ "Dados do vereador: \nID: " + this.id + ";\nNome: " + this.nome + ";\nCidade: " + this.cidade + ";\nSaldo: " + this.saldo + ".\n";
    }

    void finalizarRodada(Pessoa pessoa){
        pessoa.setSaldo(pessoa.getSaldo() + this.getSaldo());
        this.setSaldo(0);
    }
}
