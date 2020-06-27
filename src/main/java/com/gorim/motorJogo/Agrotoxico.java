package com.gorim.motorJogo;

/**
 * Agrotoxico
 */
public class Agrotoxico extends Produto {


    public Agrotoxico(int id, String tipo, double custo, double poluicao){
        super(id, tipo, custo, poluicao);
    }
    
    public Agrotoxico(int id, String tipo, int custo, int poluicao){
        super(id, tipo, (double)custo, (double)poluicao);
    }
}