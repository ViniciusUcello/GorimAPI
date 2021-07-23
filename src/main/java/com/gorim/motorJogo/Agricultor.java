package com.gorim.motorJogo;

import org.json.simple.JSONArray;

/**
 * Agricultor
 */
public class Agricultor extends Pessoa {

    private int qntdParcelas;
    private int idParcelaInicio;
    private Parcela[] parcelas;

    private int produtividade;
    private double poluicao;

    private double imposto;
    private double multa;

    private double gastos;

    public Agricultor(int id, int qntdParcelas, String nome, String cidade, int idParcelaInicio) {
        super(id, nome, cidade, (double) 600);

        this.qntdParcelas = qntdParcelas;
        this.idParcelaInicio = idParcelaInicio;
        this.parcelas = new Parcela[qntdParcelas];
        for (int i = 0; i < qntdParcelas; i++) {
            parcelas[i] = new Parcela(this.idParcelaInicio + i, this.id);
        }

        this.produtividade = 0;
        this.poluicao = 0;
        this.imposto = 0;
        this.multa = 0;

        this.gastos = 0;

    }

	public Parcela[] getParcelas() {
       	return this.parcelas;
    }
	
	public Parcela getParcela(int num) {
		return this.parcelas[num-1];
	}

    public void setProdutoParcela(int parcela, String setor, Produto produto, double preco, Empresario emp) {
        this.gastos += preco;
        this.negociacaoCapital(preco, emp);

        if (setor.equals(ConstantesGorim.c_Semente)) {
            this.parcelas[parcela].setSemente(produto);
        } else if (setor.equals(ConstantesGorim.c_Fertilizante)) {
            this.parcelas[parcela].setFetilizante(produto);
        } else if (produto.getTipo().equals("pulverizador")) {
            this.parcelas[parcela].setPulverizador(true);
        } else if (setor.equals(ConstantesGorim.c_Agrotoxico)) {
            this.parcelas[parcela].setAgrMaq(produto);
        } else if (setor.equals(ConstantesGorim.c_Maquina)) {
            this.parcelas[parcela].setAgrMaq(produto);
        }

    }

    public boolean checkProdutoParcela(int parcela, int tipoProduto) {
        if (tipoProduto == 1 && this.parcelas[parcela].getProdutoByTipo(tipoProduto)) {
            return true;
        } else if (tipoProduto == 2 && this.parcelas[parcela].getProdutoByTipo(tipoProduto)) {
            return true;
        } else if (tipoProduto == 3 && this.parcelas[parcela].getProdutoByTipo(tipoProduto)) {
            return true;
        } else if (tipoProduto == 4 && this.parcelas[parcela].getProdutoByTipo(tipoProduto)) {
            return true;
        }
        return false;
    }

    public void devolveProdutoParcela(int produto, int parcela, Empresario emp, double precoCompra) {
        //System.out.println("devolveProdutoParcela(" + produto + ", " + parcela + ", " + emp.getNome() + ", " + precoCompra + ");");
        emp.recebeProduto(this.parcelas[parcela].getIdProduto(produto), this, precoCompra);
        this.gastos += precoCompra;

        this.parcelas[parcela].removeProduto(produto);
    }

    public String[] criaListaProdutosParcela(int comeco, int parcela) {
        String[] lista = new String[4];

        lista[0] = "";
        lista[1] = "Nao ha produtos nessa parcela.";
        lista[2] = "";

        for (int i = 0; i < 3; i++) {
            if (!this.parcelas[parcela].getNomeProduto(i).equals("")) {
                String nomeProd = this.parcelas[parcela].getNomeProduto(i);
                lista[0] += "(" + comeco + ") " + nomeProd.substring(0, 1).toUpperCase() + nomeProd.substring(1).toLowerCase() + "\n";
                lista[1] = "";
            } else {
                lista[0] += "(" + comeco + ") -\n";
            }
            comeco++;
        }

        if (this.parcelas[parcela].getPulverizador() == true) {
            lista[0] += "(" + comeco + ") Pulverizador\n";
            comeco++;
            lista[1] = "";
        } else {
            lista[0] += "(" + comeco + ") -\n";
            comeco++;
        }

        lista[2] = Integer.toString(comeco);

        return lista;
    }

    public boolean getPulverizadorParcela(int parcela) {
        return this.parcelas[parcela].getPulverizador();
    }

    public void plantar(double poluicaoMundo) {
        int prod = 0;
        double polu = 0;

        for (int i = 0; i < this.qntdParcelas; i++) {
            this.parcelas[i].calculaProdutividade();
            prod += this.parcelas[i].getProdutividade();

            this.parcelas[i].calculaPoluicao();
            polu += this.parcelas[i].getPoluicao();
        }

        double peso = 0;
        if(poluicaoMundo < 0.3) peso = 1;
        else if(poluicaoMundo >= 0.3 && poluicaoMundo < 0.4) peso = 0.9;
        else if(poluicaoMundo >= 0.4 && poluicaoMundo < 0.5) peso = 0.8;
        else if(poluicaoMundo >= 0.5 && poluicaoMundo < 0.6) peso = 0.7;
        else if(poluicaoMundo >= 0.7 && poluicaoMundo < 0.8) peso = 0.6;
        else if(poluicaoMundo >= 0.8 && poluicaoMundo < 0.9) peso = 0.4;
        else if(poluicaoMundo >= 0.9 && poluicaoMundo < 0.99) peso = 0.2;
        else peso = 0;

        prod *= peso;

        this.produtividade = prod;
        this.poluicao = polu / this.qntdParcelas;
    }

    public void setProdutividade(int produtividade) {
        this.produtividade = produtividade;
    }

    public int getProdutividade() {
        return this.produtividade;
    }

    public int getProdutividadeParcela(int parcela) {
        return this.parcelas[parcela].getProdutividade();
    }

    public double getPoluicaoParcela(int parcela) {
        return this.parcelas[parcela].getPoluicao();
    }

    public void setPoluicao(double poluicao) {
        this.poluicao = poluicao;
    }

    public double getPoluicao() {
        return this.poluicao;
    }

    public void setMulta(double multa) {
        this.multa = multa;
    }

    public double getMulta() {
        return this.multa;
    }

    public int getQntdParcelas() {
        return this.qntdParcelas;
    }

    public void printContentParcela(int parcela) {
        this.parcelas[parcela].printProdutosParcela();
    }

    public void printContentParcelas() {
        for (int i = 0; i < qntdParcelas; i++) {
            System.out.println("====");
            this.parcelas[i].printProdutosParcela();
        }
    }

    public double getCustoProdutoParcela(int produto, int parcela) {
        return this.parcelas[parcela].getCustoProduto(produto);
    }

    public void printAgrParcela(int parcela) {
        this.parcelas[parcela].printAgr();
    }

    public boolean checkAgrParcela(int parcela) {
        if (this.parcelas[parcela].checkAgr()) {
            return true;
        }
        return false;
    }

    public void setSeloVerdeParcela(int parcela, boolean selo) {
        this.parcelas[parcela-1].setSeloVerde(selo);
    }

    public boolean getSeloVerdeParcela(int parcela) {
        return this.parcelas[parcela].getSeloVerde();
    }

    public int getIdParcelaInicio() {
        return this.idParcelaInicio;
    }

    public int getIdProdutoParcela(int produto, int parcela) {
        if (this.parcelas[parcela].checkProduto(produto)) {
            return this.parcelas[parcela].getIdProduto(produto);
        }
        return 0;
    }

    public void pagarMulta(double multa, Prefeito prefeito) {
        this.negociacaoCapital(multa, prefeito);
        this.setMulta(multa);
    }

    public void pagarImposto(double imposto, Prefeito prefeito) {
        this.negociacaoCapital(imposto, prefeito);
        this.imposto = imposto;
    }

    public double getImposto() {
        return this.imposto;
    }

    public double calculaPoluicao() {
        return (this.poluicao / 10000);
    }

    public double getGastos(){
        return this.gastos;
    }

    public void consultaDados() {
        System.out.println("------\nID: " + this.id + "; Nome: " + this.nome + "; Cidade: " + this.cidade + "; Saldo: " + this.saldo + "; Id da primeira parcela: " + this.idParcelaInicio + ".");
        this.printContentParcelas();
        System.out.println();
    }

    public String printContentParcelasString(int arquivo) {
        String dados = "";

        for (int i = 0; i < qntdParcelas; i++) {
            dados += (i != 0)? "---\n" : "";
            dados += "Parcela: " + (i+1) + "\n" + this.parcelas[i].printProdutosParcelaString(arquivo);
        }

        return dados;
    }

    public String consultaDadosString() {
        String dados = "";
        dados += "ID: " + this.id + "\n";
        dados += "Nome: " + this.nome + "\n";
        dados += "Cidade: " + this.cidade + "\n";
        dados += "Saldo: " + this.saldo + "\n";
        dados += "Id da primeira parcela: " + this.idParcelaInicio + "\n";
        dados += "Produtividade:" + this.produtividade + "\n";
        dados += this.printContentParcelasString(2);
        dados += "---\n";
        return dados;
    }

    public void iniciaRodada(){
        for (int i = 0; i < qntdParcelas; i++) {
            this.parcelas[i].zerarProdutos();
        }
        this.setProdutividade(0);
        this.setPoluicao(0);
        this.setMulta(0);
        this.gastos = 0;
    }

    public void finalizarSaldo() {
        this.setSaldo(this.getSaldo() + this.getProdutividade());
    }

    @SuppressWarnings( "unchecked" )
	public JSONArray contentParcelaJSON() {
    	JSONArray content = new JSONArray();
    	
    	for(Parcela parcela : this.parcelas) {
    		content.add(parcela.produtoParcelaJSON());
    	}
    	
    	return content;
    }
}
