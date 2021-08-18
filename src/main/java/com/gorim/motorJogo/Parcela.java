package com.gorim.motorJogo;

import org.json.simple.JSONObject;

/**
 * Parcela
 */
public class Parcela {
    private int id;
    private boolean seloVerde;
    private Produto[] produtos;
    private int owner; // id do dono da parcela
    private boolean usaPulverizador;
    
    private double poluicao;
    private int produtividade;

    public Parcela(int id, int owner){
        this.id = id;
        this.seloVerde = false;
        this.poluicao = 0;
        this.produtividade = 0;

        this.produtos = new Produto[3];
        this.produtos[0] = null;
        this.produtos[1] = null;
        this.produtos[2] = null;

        this.owner = owner;
        this.usaPulverizador = false;
    }
    
    public int getId(){
    	return this.id;
    }
    
    public int getOwner(){
    	return this.owner;
    }

    public void setSeloVerde(boolean seloVerde){
        this.seloVerde = seloVerde;
    }

    public boolean getSeloVerde(){
        return this.seloVerde;
    }

    public void setSemente(Produto semente){
        this.produtos[0] = semente;
    }

    public void setFetilizante(Produto fertilizante){
        this.produtos[1] = fertilizante;
    }
    
    public Produto[] getProdutos() {
    	return this.produtos;
    }
    
    public Produto getProduto(int prod) {
    	return this.produtos[prod-1];
    }

    public void setAgrMaq(Produto prod){
        if(this.produtos[2] == null) this.produtos[2] = prod;
    }

    public void removeProduto(int prod){
        if(prod == 3) this.usaPulverizador = false;
        else this.produtos[prod] = null;
    }

    public boolean checkAgr(){
        if(
            (this.produtos[2] != null) &&
            (this.produtos[2].getTipo().equals(ConstantesGorim.c_TipoAgrotoxicoA) || this.produtos[2].getTipo().contains(ConstantesGorim.c_TipoAgrotoxicoB))
        )
            return true;
        return false;
    }

    public boolean checkProduto(int produto){
        if( this.produtos[produto] != null ) return true;
        return false;
    }

    public void setPulverizador(Boolean bool){
        this.usaPulverizador = bool;
    }

    public boolean getPulverizador(){
        return this.usaPulverizador;
    }

    public boolean getProdutoByTipo(int tipoProduto){
        if( (tipoProduto < 4) && (this.produtos[tipoProduto-1] != null) ) return true;
        else if((tipoProduto == 4) && (this.produtos[2] != null) ) return true;
        return false;
    }
    
    public void printAgr() {
    	this.produtos[2].imprimeCaracteristicas();
    }

    public void printProdutosParcela(){
    	System.out.println("Parcela com ID: " + this.id);
    	if(this.produtos[0] != null) {
    		System.out.print("Semente: "); this.produtos[0].imprimeCaracteristicas();
    	}
    	
    	if(this.produtos[1] != null) {
        	System.out.print("Fertilizante: "); this.produtos[1].imprimeCaracteristicas();
        }

        if(this.produtos[2] != null) {
            System.out.println(this.produtos[2].getTipo());
	        String type = this.produtos[2].getTipo();
	        System.out.println(type);
	        if(type.contains(ConstantesGorim.c_TipoAgrotoxicoB) || type.contains(ConstantesGorim.c_TipoAgrotoxicoA)){
	            System.out.print("Agrotoxico: "); this.produtos[2].imprimeCaracteristicas();
	        }	
	        else if(
                    type.contains("maquinas") ||
                    type.contains("pulverizador")
                    ){
	            System.out.print("Maquina: " ); this.produtos[2].imprimeCaracteristicas();
            }
    	}

        if(this.usaPulverizador) System.out.println("Pulverizador? Sim.");
        else System.out.println("Pulverizador? Nao.");
        
        if(this.seloVerde) System.out.println("Selo Verde? Sim.");
        else System.out.println("Selo Verde? Nao.");

        System.out.println("Poluicao: " + this.poluicao);
        System.out.println("Produtividade: " + this.produtividade);
    }

    public String printProdutosParcelaString(int arquivo){
        String dados = "";
        if(arquivo == 0){ // Fiscal/Prefeito primeira etapa
            if(this.produtos[0] != null) {
            	dados += "Semente: " + this.produtos[0].getTipo() + "\n";
            }
            
            if(this.produtos[1] != null) {
            	dados += "Fertilizante: " + this.produtos[1].getTipo() + "\n";
            }
            
            if(this.produtos[2] != null) {
                String type = this.produtos[2].getTipo();
                if(type.contains(ConstantesGorim.c_TipoAgrotoxicoB) || type.contains(ConstantesGorim.c_TipoAgrotoxicoA)){
                    dados += "Agrotoxico: " + this.produtos[2].getTipo() + "\n";
                }
        
                else{
                    dados += "Maquina: " + this.produtos[2].getTipo() + "\n";
                }
            }

            if(this.usaPulverizador) dados += "Pulverizador? Sim\n";
            else dados += "Pulverizador? Nao\n";
            
            if(this.seloVerde) dados += "Selo Verde? Sim\n";
            else dados += "Selo Verde? Nao\n";

            dados += "Poluicao: " + this.poluicao + "\n";
            dados += "Produtividade: " + this.produtividade + "\n";
        }
        else if(arquivo == 1){ // Fiscal segunda etapa
            if(this.seloVerde) dados += "Selo Verde? Sim\n";
            else dados += "Selo Verde? Nao\n";
        }
        else if(arquivo == 2){ // Agricultor
            dados += "";
            if(this.produtos[0] != null) {
            	dados += "Semente: " + this.produtos[0].getTipo() + "\n";
            }
            
            if(this.produtos[1] != null) {
            	dados += "Fertilizante: " + this.produtos[1].getTipo() + "\n";
            }
            
            if(this.produtos[2] != null) {
                String type = this.produtos[2].getTipo();
                if(type.contains(ConstantesGorim.c_TipoAgrotoxicoB) || type.contains(ConstantesGorim.c_TipoAgrotoxicoA)){
                    dados += "Agrotoxico: " + this.produtos[2].getTipo() + "\n";
                }
        
                else{
                    dados += "Maquina: " + this.produtos[2].getTipo() + "\n";
                }
            }

            if(this.usaPulverizador) dados += "Pulverizador? Sim\n";
            else dados += "Pulverizador? Nao\n";
            
            if(this.seloVerde) dados += "Selo Verde? Sim\n";
            else dados += "Selo Verde? Nao\n";

            dados += "Poluicao: " + this.poluicao + "\n";
            dados += "Produtividade: " + this.produtividade + "\n";
        }

        return dados;
    }

    public void calculaProdutividade(){
    	this.produtividade = 0;
    	if( (this.produtos[0] != null) ) {
	        int prod = 10;
	    	
	    	if (this.produtos[1] != null) {
	    		String fert = this.produtos[1].getTipo();
	    		if(fert.equals(ConstantesGorim.c_TipoFertilizanteA)) prod *= 2;
	            else if(fert.equals(ConstantesGorim.c_TipoFertilizanteB)) prod *= 3;
	            else if(fert.equals(ConstantesGorim.c_TipoFertilizanteC)) prod *= 4;
	    	}
	    	
	    	if (this.produtos[2] != null) {
	    		String agrMaq = this.produtos[2].getTipo();
	    		if(agrMaq.equals(ConstantesGorim.c_TipoAgrotoxicoA) || agrMaq.equals(ConstantesGorim.c_TipoMaquinaA)) prod *= 3;
	            else if(agrMaq.equals(ConstantesGorim.c_TipoAgrotoxicoB) || agrMaq.equals(ConstantesGorim.c_TipoMaquinaB)) prod *= 6;
	            else if(agrMaq.equals(ConstantesGorim.c_TipoAgrotoxicoC) || agrMaq.equals(ConstantesGorim.c_TipoMaquinaC)) prod *= 10;
	    		
	    		String sem = this.produtos[0].getTipo();
	    		
	            if(
	        		agrMaq.equals(ConstantesGorim.c_TipoAgrotoxicoA) ||
	        		agrMaq.contains(ConstantesGorim.c_TipoAgrotoxicoB)
	        	){
	                if(sem.equals(ConstantesGorim.c_TipoSementeB)) prod *= 2;
	                else if(sem.equals(ConstantesGorim.c_TipoSementeC)) prod *= 3;
	            }
	    	}
	        this.produtividade = prod;
    	}
    }

    public int getProdutividade(){
        return this.produtividade;
    }

    public void calculaPoluicao(){
    	double polu = 0;
        if(this.produtos[0] != null) {
        	
        	String sem = this.produtos[0].getTipo();
        	
        	if(sem.equals(ConstantesGorim.c_TipoSementeA)) polu = 10;
            else if(sem.equals(ConstantesGorim.c_TipoSementeB)) polu = 20;
            else if(sem.equals(ConstantesGorim.c_TipoSementeC)) polu = 30;
        	
        	if( this.produtos[2] != null ) {
                String agr = this.produtos[2].getTipo();
                
                if(agr.equals(ConstantesGorim.c_TipoAgrotoxicoA)) polu *= 10;
                else if(agr.equals(ConstantesGorim.c_TipoAgrotoxicoB)) polu *= 6;
                else if(agr.equals(ConstantesGorim.c_TipoAgrotoxicoC)) polu *= 3;
        	}

            if (usaPulverizador) polu /= 2;
        }
    	this.poluicao = polu;
    	
    }

    public double getPoluicao(){
        return this.poluicao;
    }

    public void zerarProdutos(){
        for (int i = 0; i < 3; i++) this.produtos[i] = null;
        this.usaPulverizador = false;
        this.produtividade = 0;
        this.poluicao = 0;
    }

    public String getNomeProduto(int produto){
        if(this.produtos[produto] != null) return this.produtos[produto].getTipo();
        else return "";
    }

    public double getCustoProduto(int tipoProduto){
        if(this.produtos[tipoProduto] != null) return this.produtos[tipoProduto].getCusto();
        else return 0;
    }

    public int getIdProduto(int tipoProduto){
        if(tipoProduto == 3) return 10;
        if(this.produtos[tipoProduto] != null) return this.produtos[tipoProduto].getId();
        else return 0;
    }
    
    @SuppressWarnings("unchecked")
	public JSONObject produtoParcelaJSON() {
    	JSONObject content = new JSONObject();
    	
    	if(this.produtos[0] != null) content.put(ConstantesGorim.c_Semente, this.produtos[0].getTipo());
    	else content.put(ConstantesGorim.c_Semente, null);
    	
    	if(this.produtos[1] != null) content.put(ConstantesGorim.c_Fertilizante, this.produtos[1].getTipo());
    	else content.put(ConstantesGorim.c_Fertilizante, null);
    	
    	if(this.produtos[2] != null) {
    		content.put("maqAgr", this.produtos[2].getTipo());
        }
    	else content.put("maqAgr", null);
    	
    	content.put(ConstantesGorim.c_TipoMaquinaD.toLowerCase(), this.usaPulverizador);
    	content.put("seloVerde", this.seloVerde);
    	
    	return content;
    }

}