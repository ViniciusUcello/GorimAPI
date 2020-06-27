package com.gorim.motorJogo;

public class AcoesAmbientais {

    private final int id;
    private final String tipo;
    private final double custo;
    private final double reducaoDaPoluicao;

    public AcoesAmbientais(int id, String tipo, double custo, double reducaoDaPoluicao) {
        this.id = id;
        this.tipo = tipo;
        this.custo = custo;
        this.reducaoDaPoluicao = reducaoDaPoluicao;
    }

    public int getID() {
        return this.id;
    }

    public String getTipo() {
        return this.tipo;
    }

    public double getCusto() {
        return this.custo;
    }

    public double getReducaoDaPoluicao() {
        return this.reducaoDaPoluicao;
    }
    
    public void consultaDados() {
    	System.out.println("Id: "+ this.id + ";\nTipo: " + this.tipo + ";\nCusto: " + this.custo + ";\nReducao da poluicao: " + this.reducaoDaPoluicao + ".");
    }
    
    public String consultaDadosString() {
    	String dados = "";
    	dados += "Id: "+ this.id + ";\nTipo: " + this.tipo + ";\nCusto: " + this.custo + ";\nReducao da poluicao: " + this.reducaoDaPoluicao + ".";
    	return dados;
    }

}
