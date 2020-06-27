package com.gorim.motorJogo;

/**
 * Semente
 */
public class Semente extends Produto{


    public Semente(int id, String tipo, double custo, double poluicao){
        super(id, tipo, custo, poluicao);
    }
    
    public Semente(int id, String tipo, int custo, int poluicao){
        super(id, tipo, (double)custo, (double)poluicao);
    }

}