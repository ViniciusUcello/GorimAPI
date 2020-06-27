package com.gorim.motorJogo;

/**
 * Maquina
 */
public class Maquina extends Produto {


    public Maquina(int id, String tipo, double custo, double poluicao){
        super(id, tipo, custo, poluicao);
    }
    
    public Maquina(int id, String tipo, int custo, int poluicao){
        super(id, tipo, (double)custo, (double)poluicao);
    }
    
}