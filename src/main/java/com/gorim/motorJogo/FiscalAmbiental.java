package com.gorim.motorJogo;

import org.json.simple.JSONObject;

/**
 *
 * @author mathe
 */
public class FiscalAmbiental extends Pessoa {
    private int idFiscal_ambiental;
    private JSONObject pedidos;
    
    public FiscalAmbiental(int id, String nome, String cidade, int idFiscal_ambiental) {
        super(id, nome, cidade, 0);
        this.idFiscal_ambiental = idFiscal_ambiental;
        this.pedidos = new JSONObject();
        
    }

    public void eleger(int idEleito, String nomeEleito){
        this.idFiscal_ambiental = idEleito;
        this.nome = nomeEleito;
    }
    
    public int getIdEleito() {
        return this.idFiscal_ambiental;
    }
    
    public void fiscalizar(Agricultor agricultor) {
        // Nao faz nada computacionalmente
    }

    public boolean setSeloVerde(Agricultor agricultor, int parcela, boolean selo){
	    agricultor.setSeloVerdeParcela(parcela, selo);
	    return true;
    }

    public void notificar(Pessoa pessoa) {
        System.out.println("NOTIFICACAO enviada a " + pessoa.getNome());
    }

    public double multar(Agricultor agricultor, Prefeito prefeito, int tipoMulta) {

        double poluicao = agricultor.getPoluicao();
        int coeficiente = 0;

        if (tipoMulta == 1) coeficiente = 1;
        else if (tipoMulta == 2)  coeficiente = 2;
        else if (tipoMulta == 3) coeficiente = 3;

        double multa = poluicao * coeficiente;
        agricultor.pagarMulta(multa, prefeito);
        
        return multa;
    }

    public double multar(Empresario empresario, Prefeito prefeito, int tipoMulta) {
        double poluicao = empresario.getPoluicao();
        int coeficiente = 0;

        if (tipoMulta == 1) coeficiente = 1;
        else if (tipoMulta == 2)  coeficiente = 2;
        else if (tipoMulta == 3) coeficiente = 3;

        double multa = poluicao * coeficiente;
        empresario.pagarMulta(multa, prefeito);
        
        return multa;

    }
    
    public void consultaDados() {
    	System.out.println("Id do eleito fiscal: " + this.idFiscal_ambiental +"\n"+ "Dados do fiscal:\nID: " + this.id + "\nNome: " + this.nome + "\nCidade: " + this.cidade + "\nSaldo: " + this.saldo + ".\n");
    }

    @SuppressWarnings("unchecked")
    public void adicionaPedido(String nomePedinte, String pedido){
        this.pedidos.put(nomePedinte, pedido);
    }

    public JSONObject getPedidos(){
        return this.pedidos;
    }
    
    //Metodo Log
    
    public String consultaDadosString() {
    	return "Id do eleito fiscal: " + this.idFiscal_ambiental +"\n"+ "Dados do fiscal:\nID: " + this.id + "\nNome: " + this.nome + "\nCidade: " + this.cidade + "\nSaldo: " + this.saldo + ".\n";
    }

    void finalizarRodada(Pessoa pessoa){
        pessoa.setSaldo(pessoa.getSaldo() + this.getSaldo());
        this.setSaldo(0);
        if(this.pedidos.size() > 0) {
        	for (int i = this.pedidos.size()-1; i >= 0; i--) {
    			this.pedidos.remove(i);
    		}
        }
    }

}
