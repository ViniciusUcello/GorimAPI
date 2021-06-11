package com.gorim.motorJogo;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;

import com.gorim.model.ProdutoSimplifiedModel;

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
                this.setor = ConstantesGorim.c_Semente;
                this.produtos.add(new Semente(1, ConstantesGorim.c_TipoSementeA, ConstantesGorim.c_CustoSementeA, ConstantesGorim.c_PoluicaoSementeA));
                this.produtos.add(new Semente(2, ConstantesGorim.c_TipoSementeB, ConstantesGorim.c_CustoSementeB, ConstantesGorim.c_PoluicaoSementeB));
                this.produtos.add(new Semente(3, ConstantesGorim.c_TipoSementeC, ConstantesGorim.c_CustoSementeC, ConstantesGorim.c_PoluicaoSementeC));
                break;
            case 1:
                this.setor = ConstantesGorim.c_Fertilizante;
                this.produtos.add(new Fertilizante(4, ConstantesGorim.c_TipoFertilizanteA, ConstantesGorim.c_CustoFertilizanteA, ConstantesGorim.c_PoluicaoFertilizanteA));
                this.produtos.add(new Fertilizante(5, ConstantesGorim.c_TipoFertilizanteB, ConstantesGorim.c_CustoFertilizanteB, ConstantesGorim.c_PoluicaoFertilizanteB));
                this.produtos.add(new Fertilizante(6, ConstantesGorim.c_TipoFertilizanteC, ConstantesGorim.c_CustoFertilizanteC, ConstantesGorim.c_PoluicaoFertilizanteC));
                break;
            case 2:
                this.setor = ConstantesGorim.c_Maquina;
                this.produtos.add(new Maquina(7, ConstantesGorim.c_TipoMaquinaA, ConstantesGorim.c_CustoMaquinaA, ConstantesGorim.c_PoluicaoMaquinaA));
                this.produtos.add(new Maquina(8, ConstantesGorim.c_TipoMaquinaB, ConstantesGorim.c_CustoMaquinaB, ConstantesGorim.c_PoluicaoMaquinaB));
                this.produtos.add(new Maquina(9, ConstantesGorim.c_TipoMaquinaC, ConstantesGorim.c_CustoMaquinaC, ConstantesGorim.c_PoluicaoMaquinaC));
                this.produtos.add(new Maquina(10, ConstantesGorim.c_TipoMaquinaD, ConstantesGorim.c_CustoMaquinaD, ConstantesGorim.c_PoluicaoMaquinaD));
                break;
            case 3:
                this.setor = ConstantesGorim.c_Agrotoxico;
                this.produtos.add(new Agrotoxico(11, ConstantesGorim.c_TipoAgrotoxicoA, ConstantesGorim.c_CustoAgrotoxicoA, ConstantesGorim.c_PoluicaoAgrotoxicoA));
                this.produtos.add(new Agrotoxico(12, ConstantesGorim.c_TipoAgrotoxicoB, ConstantesGorim.c_CustoAgrotoxicoB, ConstantesGorim.c_PoluicaoAgrotoxicoB));
                this.produtos.add(new Agrotoxico(13, ConstantesGorim.c_TipoAgrotoxicoC, ConstantesGorim.c_CustoAgrotoxicoC, ConstantesGorim.c_PoluicaoAgrotoxicoC));
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
    
    public String getTipoProdutoById(int id) {
    	String aux = "";
    	for (Produto produto : this.produtos) {
			if(produto.getId() == id) aux = produto.getTipo().substring(0, 1).toUpperCase() + produto.getTipo().substring(1).toLowerCase();
		}
    	return aux;
    }
    
    public List<ProdutoSimplifiedModel> getTipoPrecoProdutos(){
    	List<ProdutoSimplifiedModel> produtos = new ArrayList<ProdutoSimplifiedModel>();
    	
    	for (Produto prod : this.produtos) {
    		produtos.add(new ProdutoSimplifiedModel(
    				prod.getTipo(),
    				prod.getCusto(),
    				this.setor
    				));
		}
    	
    	return produtos;
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
