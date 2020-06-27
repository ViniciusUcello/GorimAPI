package com.gorim.motorJogo;

import java.util.ArrayList;

import org.json.simple.JSONArray;

/**
 * Empresario
 */
public class Empresario extends Pessoa {

    private String setor;
    private ArrayList<Produto> produtos;
    private double poluicao;
    private int produtividade;

    private double imposto;
    private double multa;

    public Empresario(int id, int setor, String nome, String cidade) {
        super(id, nome, cidade, (double) 100);

        this.produtos = new ArrayList<>();
        this.poluicao = 0;
        this.produtividade = 0;

        this.multa = 0;

        switch (setor) {
            case 0:
                this.setor = "semente";
                this.produtos.add(new Semente(1, "hortalica", 10, 1));
                this.produtos.add(new Semente(2, "arroz", 20, 2));
                this.produtos.add(new Semente(3, "soja", 30, 3));
                break;
            case 1:
                this.setor = "fertilizante";
                this.produtos.add(new Fertilizante(4, "comum", 30, 9));
                this.produtos.add(new Fertilizante(5, "premium", 60, 6));
                this.produtos.add(new Fertilizante(6, "super premium", 90, 3));
                break;
            case 2:
                this.setor = "maquina";
                this.produtos.add(new Maquina(7, "maquinas 1", 30, 3));
                this.produtos.add(new Maquina(8, "maquinas 2", 60, 6));
                this.produtos.add(new Maquina(9, "maquinas 3", 90, 9));
                this.produtos.add(new Maquina(10, "pulverizador", 400, 40));
                break;
            case 3:
                this.setor = "agrotoxico";
                this.produtos.add(new Agrotoxico(11, "comum", 10, 3));
                this.produtos.add(new Agrotoxico(12, "premium", 20, 2));
                this.produtos.add(new Agrotoxico(13, "super premium", 30, 1));
                break;
            default:
                System.out.println("Setor invalido!");
        }

    }

    public String getSetor() {
        return this.setor;
    }

    public double getPoluicao() {
        return this.poluicao;
    }

    public void setPoluicao(int poluicao) {
        this.poluicao = poluicao;
    }

    public void setProdutividade(int produtividade) {
        this.produtividade = produtividade;
    }

    public int getProdutividade() {
        return this.produtividade;
    }

    public void setMulta(double multa) {
        this.multa = multa;
    }

    public double getMulta() {
        return this.multa;
    }

    public double getCustoProduto(int produto) {
        return this.produtos.get(produto).getCusto();
    }

    public void printProdutos() {
        System.out.println("Lista de produtos do empresario de " + this.setor);
        int count = 1;
        for (Produto elemento : this.produtos) {
            System.out.println("(" + count + ") " + elemento.getTipo().substring(0, 1).toUpperCase() + elemento.getTipo().substring(1).toLowerCase() + ". D$ = " + elemento.getCusto());
            count++;
        }
    }

    public int getIndiceProdutoById(int id) {
        if (id < 0) {
            return 0;
        } else if (id < 14) {
            return (id - this.produtos.get(0).getId());
        }
        return 0;
    }

    public void venderAlugar(int produto, Agricultor agricultor, int parcela, int preco, double poluicaoMundo) {
        Produto prod = this.produtos.get(this.getIndiceProdutoById(produto));

        this.poluicao += prod.getPoluicao();

        double diferenca = (double) 0;
        if (preco == 0) {
            diferenca = (double) -5;
        } else if (preco == 2) {
            diferenca = (double) 5;
        }

        double produtividade = prod.getCusto() + diferenca;
        double valorFinalProduto = produtividade;

        double peso = 0;
        if(poluicaoMundo < 0.3) peso = 1;
        else if(poluicaoMundo >= 0.3 && poluicaoMundo < 0.4) peso = 0.9;
        else if(poluicaoMundo >= 0.4 && poluicaoMundo < 0.5) peso = 0.8;
        else if(poluicaoMundo >= 0.5 && poluicaoMundo < 0.6) peso = 0.7;
        else if(poluicaoMundo >= 0.7 && poluicaoMundo < 0.8) peso = 0.6;
        else if(poluicaoMundo >= 0.8 && poluicaoMundo < 0.9) peso = 0.4;
        else if(poluicaoMundo >= 0.9 && poluicaoMundo < 0.99) peso = 0.2;
        else peso = 0;

        produtividade *= peso;

        this.produtividade += produtividade;

        agricultor.setProdutoParcela(parcela, this.setor, prod, valorFinalProduto, this);

    }

    public void recebeProduto(int produto, Agricultor agr, double precoCompra) {
        double precoFinal = this.getCustoProduto(this.getIndiceProdutoById(produto)) - precoCompra;
        this.negociacaoCapital(precoFinal, agr);

        this.poluicao -= this.produtos.get(this.getIndiceProdutoById(produto)).getPoluicao();
        this.produtividade -= precoFinal;
    }

    public void pagarMulta(double multa, Prefeito prefeito) {
        this.negociacaoCapital(multa, prefeito);
        this.multa = multa;
    }

    public void pagarImposto(double imposto, Prefeito prefeito) {
        this.negociacaoCapital(imposto, prefeito);
        this.imposto = imposto;
    }

    public double getImposto() {
        return this.imposto;
    }

    public int getIdProduto(int produto) {
        return this.produtos.get(produto).getId();
    }

    public String getTipoProduto(int produto) {
        return this.produtos.get(produto).getTipo().substring(0, 1).toUpperCase() + this.produtos.get(produto).getTipo().substring(1).toLowerCase();
    }

    public double calculaPoluicao() {
        return (this.poluicao / 10000);
    }

    public void consultaDados() {
        System.out.println("ID: " + this.id + "; Setor: " + this.setor + "; Nome: " + this.nome + "; Cidade: " + this.cidade + "; Saldo: " + this.saldo + ".");
        this.printProdutos();
    }

    /**
     * Metodos feitos para a impressao das informacoes no log
     */
    public String printProdutosString() {
        String dados = "---\n";
        int count = 1;
        for (Produto prod : this.produtos) {
            String tipo = prod.getTipo();
            tipo = tipo.substring(0, 1).toUpperCase() + tipo.substring(1).toLowerCase();
            dados += "" + count + " => " + tipo + "\n";
            count++;
        }
        return dados;
    }

    public String consultaDadosString() {
        String dados = "";
        dados += "ID: " + this.id + "\nSetor: " + this.setor + "\nNome: " + this.nome + "\nCidade: " + this.cidade + "\nSaldo: " + this.saldo + ".\n";
        dados += this.printProdutosString();
        return dados;
    }

    public void iniciaRodada(){
        this.setProdutividade(0);
        this.setPoluicao(0);
        this.setMulta(0);

    }
    
    @SuppressWarnings( "unchecked" )
    public JSONArray getProdutos() {
    	JSONArray produtos = new JSONArray();
    	
    	for(Produto prod: this.produtos) {
    		produtos.add(prod.infoJSON());
    	}
    	
    	return produtos;
    }

}
