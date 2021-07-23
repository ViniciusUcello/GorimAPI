package com.gorim.motorJogo;

import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Prefeito extends Pessoa {

    private double caixa;
    private int idEleito;
    private double[] taxas;
    private boolean[] usarAcoes;
    private ArrayList<AcoesAmbientais> acoesAmbientais;

    private double[] mudancaTaxas;

    public Prefeito(int id, String nome, String cidade) {
		super(id, nome, cidade, 0);
        this.caixa = 1000;
        this.idEleito = -1;
        this.taxas = new double[3];
        this.taxas[0] = (double)10;
        this.taxas[1] = (double)0.1;
        this.taxas[2] = (double)0.3;

        this.usarAcoes = new boolean[3];
        this.usarAcoes[0] = false;
        this.usarAcoes[1] = false;
        this.usarAcoes[2] = false;

        this.acoesAmbientais = new ArrayList<>();
        this.acoesAmbientais.add(0, new AcoesAmbientais(0, "Água", 800, (double) 0.05));
        this.acoesAmbientais.add(1, new AcoesAmbientais(1, "Lixo", 1600, (double) 0.10));
        this.acoesAmbientais.add(2, new AcoesAmbientais(2, "Esgoto", 2400, (double) 0.15));

        this.mudancaTaxas = new double[3];
        this.mudancaTaxas[0] = 0;
        this.mudancaTaxas[1] = 0;
        this.mudancaTaxas[2] = 0;
    }

    public void setNome(String nome){
        this.nome = nome;
    }

    public void setUsarAcao(int acao, double poluicaoMundo){
    	if(acao > -1 && acao < 3) {
    		this.usarAcoes[acao] = true;
            double preco = this.acoesAmbientais.get(acao).getCusto();

            if(poluicaoMundo >= 0.3) preco = (poluicaoMundo - 0.2)*100 + preco; 

            this.setCaixa(this.getCaixa() - preco);
    	}
    }

    public String getTipoAcao(int acao){
        return this.acoesAmbientais.get(acao).getTipo();
    }

    public double getCaixa() {
        return this.caixa;
    }
    
    public double[] getTaxas() {
    	return this.taxas;
    }
    
    public ArrayList<AcoesAmbientais> getAcoesAmbientais() {
    	return this.acoesAmbientais;
    }

    public void setCaixa(double novo_saldo) {
        this.caixa = novo_saldo;
    }

    public double cobrarImposto(Empresario emp){
        double cobranca = 0;
        int produtividadeEmp = emp.getProdutividade();
        if(produtividadeEmp == 0) cobranca = this.taxas[0];
        else if(produtividadeEmp < 201) cobranca = produtividadeEmp*this.taxas[1];
        else if(produtividadeEmp > 200) cobranca = produtividadeEmp*this.taxas[2];

        emp.pagarImposto(cobranca, this);
        return cobranca;
    }

    public double cobrarImposto(Agricultor agr){
        double cobranca = 0;
        int produtividadeAgr = agr.getProdutividade();
        double desconto = 0;
        for (int i = 0; i < agr.getQntdParcelas(); i++) {
            if(agr.getSeloVerdeParcela(i)) desconto += ((double)0.5/agr.getQntdParcelas());
        }

        if(produtividadeAgr == 0) cobranca = this.taxas[0];
        else if(produtividadeAgr < 121) cobranca = produtividadeAgr*this.taxas[1];
        else if(produtividadeAgr > 120) cobranca = produtividadeAgr*this.taxas[2];

        desconto = 1 - desconto;

        cobranca = cobranca*desconto;

        agr.pagarImposto(cobranca, this);
        return cobranca;
    }
    
    public void receberContribuicoes(){
        double contr = this.getSaldo();
        this.setCaixa(this.getCaixa() + contr);
        this.saldo = 0;
    }

    public int getIdEleito() {
        return this.idEleito;
    }
    
    public void eleger(int idEleito, String nomeEleito) {
    	this.idEleito = idEleito;
        this.nome = nomeEleito;
    }

    public double usarAcoes() {
        
        double reducao_total = 0;

        int i = 0;
        for (boolean acao : this.usarAcoes) {
            if(acao){
                reducao_total += this.acoesAmbientais.get(i).getReducaoDaPoluicao();
            }
            i++;
        }

        
        return reducao_total;
    }

    public void mudarTaxa(int tipoTaxa, double novaTaxa){
        if(tipoTaxa > 0 && tipoTaxa < 4){
            this.taxas[tipoTaxa-1] = novaTaxa;
            this.mudancaTaxas[tipoTaxa-1] = novaTaxa;
            System.out.println("this.taxas[" + (tipoTaxa-1) + "] = " + novaTaxa);
            System.out.println("this.mudancaTaxas[" + (tipoTaxa-1) + "] = " + novaTaxa);
        }
    }

    public void Caixa_para_contaPessoal(double valor) {
        this.caixa -= valor;
        super.saldo += valor;
    }
    
    public void consultaAcoes() {
    	for(AcoesAmbientais acoes : this.acoesAmbientais) {
    		acoes.consultaDados();
    	}
    }
    
    public void consultaDados() {
    	System.out.println("Id do eleito prefeito: "+this.idEleito+"\nDados do prefeito: ID: " + this.id + ";\n Nome: " + this.nome + ";\n Cidade: " + this.cidade + ";\n Dinheiros recebidos de contribuicoes nesta rodada: " + this.saldo + ";\n Caixa Prefeitura (total): " + this.caixa + "\n");
    	System.out.println("Acoes tomadas na rodada: ");
    	this.consultaAcoes();
    }
    
    public String criaListaAcoes(int comeco){
		String dados = "";
		for(AcoesAmbientais acoes : this.acoesAmbientais){
			dados += "(" + comeco + ") " + acoes.getTipo() + ". D$ " + acoes.getCusto() + "\n";
			comeco++;
		}
		return dados;
	}
    
    @Override
    public boolean negociacaoCapital(double dinheiros, Pessoa pessoa) {
        this.caixa -= dinheiros;
        pessoa.setSaldo(pessoa.getSaldo() + dinheiros);
        return true;
    }

    //Metodo Log

    public String getAcoesUsadasString(){
        String dados = "";
        int i = 0;
        for(AcoesAmbientais acoes : this.acoesAmbientais){
			if(this.usarAcoes[i] == true){
                dados += acoes.getTipo() + " (" + acoes.getCusto() + ")\n";
            }
			i++;
        }
        if(dados.equals("")) dados = "Nenhuma Acao Ambiental foi usada\n";
        return dados;
    }

    public String getTaxasMudadasString(){
        String dados = "";
        for(int i = 0; i < 3; i++){
            if(this.mudancaTaxas[i] != 0){
                dados += "Taxa do tipo " + (i+1) + " mudada para ";
                if(i == 0){
                    dados += "D$" + this.taxas[i] + "\n";
                }
                else{
                    dados += (this.taxas[i]*100) + "%\n";
                }
            }
        }
        if(dados.equals("")) dados += "Nao foi feita nenhuma mudanca de taxas";
        dados += "\n";

        return dados;
    }
    
    public String consultaAcoesString() {
    	String dados = "";
    	for(AcoesAmbientais acoes : this.acoesAmbientais) {
    		dados += acoes.consultaDadosString();
    	}
    	return dados;
    }
    
    public String consultaDadosString() {
    	String acoes = "";
    	acoes += consultaAcoesString();
    	String dados = "\nId do eleito prefeito: "+this.idEleito+"\nDados do prefeito: : ID: " + this.id + ";\n Nome: " + this.nome + ";\n Cidade: " + this.cidade + ";\n Dinheiros recebidos de contribuicoes nesta rodada: " + this.saldo + ";\n Caixa Prefeitura (total): " + this.caixa + "\nAcoes tomadas na rodada: "+ acoes+".\n";
    	return dados;
    }

    public void iniciaRodada(){
        this.setSaldo(0);
        for (int i = 0; i < 3; i++) {
            this.usarAcoes[i] = false;
        }
    }
    
    @SuppressWarnings("unchecked")
	public JSONArray getAcoesUsadasJSON(){
        JSONArray acoesUsadas = new JSONArray();
        int i = 0;
        for(AcoesAmbientais acoes : this.acoesAmbientais){
			if(this.usarAcoes[i] == true) acoesUsadas.add(acoes.getTipo());
			i++;
        }
        return acoesUsadas;
    }

    @SuppressWarnings("unchecked")
    public JSONArray getTaxasMudadasJSON(){
    	JSONArray taxasMudadas = new JSONArray();
        for(int i = 0; i < 3; i++) {
        	if(this.mudancaTaxas[i] != 0) {
        		JSONObject aux = new JSONObject();
        		aux.put("tipo", i+1);
        		aux.put("taxa", this.taxas[i]);
        		taxasMudadas.add(aux);
        	}
        }

        return taxasMudadas;
    }

}
