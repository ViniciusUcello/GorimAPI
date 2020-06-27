package com.gorim.motorJogo;

/**
 * Fertilizante
 */
public class Fertilizante extends Produto {


    public Fertilizante(int id, String tipo, double custo, double poluicao){
        super(id, tipo, custo, poluicao);
    }
    
    public Fertilizante(int id, String tipo, int custo, int poluicao){
        super(id, tipo, (double)custo, (double)poluicao);
    }

}