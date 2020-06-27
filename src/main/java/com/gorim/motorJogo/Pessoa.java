package com.gorim.motorJogo;

public abstract class Pessoa {

    protected int id;
    protected String nome;
    protected String cidade;
    protected double saldo;
    
    
    public Pessoa(int id, String nome, String cidade, double saldo) {
        this.id = id;
        this.nome = nome;
        this.cidade = cidade;
        this.saldo = saldo;
        
    }

    public int getId() {
        return this.id;
    }

    public String getNome() {
        return this.nome;
    }

    public String getCidade() {
        return this.cidade;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }

    public double getSaldo() {
        return this.saldo;
    }

    public abstract void consultaDados();

    public boolean negociacaoCapital(double dinheiros, Pessoa pessoa) {
        this.saldo -= dinheiros;
        pessoa.setSaldo(pessoa.getSaldo() + dinheiros);
        return true;
    }
    
}

