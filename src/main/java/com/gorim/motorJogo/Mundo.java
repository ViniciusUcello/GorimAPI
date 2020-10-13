package com.gorim.motorJogo;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.gorim.model.PessoaModel;
import com.gorim.model.ProdutoSimplifiedModel;
import com.gorim.model.forms.Venda;

public class Mundo {

    private int idPessoa;
    private int idParcelas;
    private int qntdParcelasPorAgricultor;
    private int quantidadeJogadores;
    private int rodada;
    private int etapa;
    private double poluicaoMundo;

    private Scanner scanner = new Scanner(System.in);

    private ArrayList<Empresario> empresarios;
    private ArrayList<Agricultor> agricultores;
    private ArrayList<FiscalAmbiental> fiscais;
    private ArrayList<Prefeito> prefeitos;
    private ArrayList<Vereador> vereadores;

    private String separadorCSV;
    private ArrayList<Double> saldosAnteriores;
    private ArrayList<JSONObject> transferenciasSent;
    private ArrayList<JSONObject> transferenciasReceived;
    
    private List<List<Venda>> vendas;
    //private ArrayList<Venda>[] vendas;

    public Mundo(int quantidadeJogadores) {
        this.idPessoa = 1;
        this.idParcelas = 1;
        this.qntdParcelasPorAgricultor = 6;
        this.quantidadeJogadores = quantidadeJogadores;
        this.rodada = 1;
        this.etapa = 1;
        this.poluicaoMundo = (double) 0.2;

        this.empresarios = new ArrayList<>();
        this.agricultores = new ArrayList<>();
        this.fiscais = new ArrayList<>();
        this.prefeitos = new ArrayList<>();
        this.vereadores = new ArrayList<>();

        this.separadorCSV = ";";
        this.saldosAnteriores = new ArrayList<>();
        
        //this.vendas = new ArrayList<ArrayList<Venda>>();

    }
    
    public int getRodada() {
    	return this.rodada;
    }
    
    public int getEtapa() {
    	return this.etapa;
    }
    
    public int getIdJogo() {
    	return 1;
    }
    
    public double calculaProdutividadeMundo() {
    	double peso = 0;
        if(this.poluicaoMundo < 0.3) peso = 1;
        else if(this.poluicaoMundo >= 0.3 && this.poluicaoMundo < 0.4) peso = 0.9;
        else if(this.poluicaoMundo >= 0.4 && this.poluicaoMundo < 0.5) peso = 0.8;
        else if(this.poluicaoMundo >= 0.5 && this.poluicaoMundo < 0.6) peso = 0.7;
        else if(this.poluicaoMundo >= 0.7 && this.poluicaoMundo < 0.8) peso = 0.6;
        else if(this.poluicaoMundo >= 0.8 && this.poluicaoMundo < 0.9) peso = 0.4;
        else if(this.poluicaoMundo >= 0.9 && this.poluicaoMundo < 0.99) peso = 0.2;
        else peso = 0;
        
        return peso;
    }
    
    public void iniciarJogo() {
    	String[] nomes = {"EmpSem", "EmpFer", "EmpMaq", "EmpAgr"};
    	String setor;
        for (int i = 1; i < 5; i++) {
            if (i == 1) {
                setor = "semente";
            } else if (i == 2) {
                setor = "fertilizante";
            } else if (i == 3) {
                setor = "maquina";
            } else {
                setor = "agrotoxico";
            }

            this.criaEmpresario(setor, nomes[i - 1]);
        }
        String nome;
        int numNome = 1;
        for (int i = 0; i < this.quantidadeJogadores - 4; i++) {
            if (i % 2 == 0) {
                nome = "AT" + numNome;
                this.criaAgricultor(nome, "Atlantis");
            } else {
                nome = "CD" + numNome;
                this.criaAgricultor(nome, "Cidadela");
                numNome++;
            }
        }
        
        this.criaCargosPoliticos();
        
        // Inicia Array auxiliar de orcamento
        this.vendas = new ArrayList<>();
        while(this.vendas.size() < this.quantidadeJogadores)
        	this.vendas.add(new ArrayList<Venda>());
        
        int qnt = this.quantidadeJogadores + 6;
        this.transferenciasSent = new ArrayList<JSONObject>(qnt);
        this.transferenciasReceived = new ArrayList<JSONObject>(qnt);

        for (int i = 0; i < qnt; i++) {
            this.transferenciasSent.add(new JSONObject());
            this.transferenciasReceived.add(new JSONObject());
        }

        this.criaHistoricoSaldos();
        
    }
    
    public void setPlayerQuantity(int quantity) {
    	this.quantidadeJogadores = quantity;
    }

    public double getPoluicaoMundo() {
        return this.poluicaoMundo;
    }
    
    public int getQuantidadeJogadores() {
    	return this.quantidadeJogadores;
    }

    public int getTipoPessoaById(int id) {
        int aux = this.quantidadeJogadores;
        if (id > 0 && id < 5) {
            return 1;
        } else if (id > 4 && id < aux + 1) {
            return 2;
        } else if (id > aux && id < aux + 3) {
            return 3;
        } else if (id > aux + 2 && id < aux + 5) {
            return 4;
        } else if (id > aux + 4 && id < aux + 7) {
            return 5;
        } else {
            return 0;
        }
    }

    /**
     * Metodos referentes a classe de Empresario
     */
    public void criaEmpresario(String setor, String nome) {
        Empresario emp;
        if (setor.equals("semente") || setor.equals("maquina")) {
            if (setor.equals("semente")) {
                emp = new Empresario(this.idPessoa, 0, nome, "Atlantis");
            } else {
                emp = new Empresario(this.idPessoa, 2, nome, "Atlantis");
            }
        } else {
            if (setor.equals("fertilizante")) {
                emp = new Empresario(this.idPessoa, 1, nome, "Cidadela");
            } else {
                emp = new Empresario(this.idPessoa, 3, nome, "Cidadela");
            }
        }
        this.empresarios.add(emp);
        this.idPessoa++;
    }

    public Empresario getEmpresarioById(int id) {
        for (Empresario emp : this.empresarios) {
            if (emp.getId() == id) {
                return emp;
            }
        }
        return null;
    }

    /*
    public Empresario getEmpresarioByIdJSON(int id) {
        for (Empresario emp : this.empresarios) {
            if (emp.getId() == id) {
                return emp;
            }
        }
        return null;
    }
    */

    public int getTipoProdutoById(int id) {
        if (id <= 0) {
            return 0;
        } else if (id < 4) {
            return 1;
        } else if (id < 7) {
            return 2;
        } else if (id < 11) {
            return 3;
        } else if (id < 14) {
            return 4;
        }
        return 0;
    }
    
    public List<ProdutoSimplifiedModel> getProdutosEmpresarios() {
    	List<ProdutoSimplifiedModel> produtos = new ArrayList<ProdutoSimplifiedModel>();

    	for (Empresario emp : this.empresarios) {
    		for (ProdutoSimplifiedModel prod : emp.getTipoPrecoProdutos()) {
				produtos.add(prod);
			}
		}
    	
    	return produtos;
    }

    /**
     * Metodos referentes a classe de Agricultor
     */
    public void criaAgricultor(String nome, String cidade) {
        Agricultor agr = new Agricultor(this.idPessoa, this.qntdParcelasPorAgricultor, nome, cidade, this.idParcelas);
        this.agricultores.add(agr);
        this.idPessoa++;
        this.idParcelas += this.qntdParcelasPorAgricultor;
    }

    public Agricultor getAgricultorById(int id) {
        for (Agricultor agr : this.agricultores) {
            if (agr.getId() == id) {
                return agr;
            }
        }
        return null;
    }

    /**
     * Metodos referentes a classe de FiscalAmbiental
     */
    public void criaFiscal(int idEleito, String nomeEleito) {
        FiscalAmbiental fis;
        if (idEleito % 2 != 0) {
            fis = new FiscalAmbiental(this.idPessoa, nomeEleito, "Atlantis", idEleito);
        } else {
            fis = new FiscalAmbiental(this.idPessoa, nomeEleito, "Cidadela", idEleito);
        }
        this.fiscais.add(fis);
        this.idPessoa++;
    }

    public FiscalAmbiental getFiscalById(int id) {
        for (FiscalAmbiental fis : this.fiscais) {
            if (fis.getId() == id) {
                return fis;
            }
        }
        return null;
    }

    /**
     * Cargos Politicos
     */
    public void criaCargosPoliticos() {
        this.fiscais.add(0, new FiscalAmbiental(this.idPessoa, "", "Atlantis", 0));
        this.idPessoa++;
        this.fiscais.add(1, new FiscalAmbiental(this.idPessoa, "", "Cidadela", 0));
        this.idPessoa++;
        this.prefeitos.add(0, new Prefeito(this.idPessoa, "", "Atlantis"));
        this.idPessoa++;
        this.prefeitos.add(1, new Prefeito(this.idPessoa, "", "Cidadela"));
        this.idPessoa++;
        this.vereadores.add(0, new Vereador(this.idPessoa, "", "Atlantis", 0));
        this.idPessoa++;
        this.vereadores.add(1, new Vereador(this.idPessoa, "", "Cidadela", 0));
        this.idPessoa++;
    }

    public boolean eleger(int idEleito, int cargo) {
        int tipo = 0;
        Pessoa pessoa = null;
        String cidade = "";
        String cargoString = "";

        tipo = getTipoPessoaById(idEleito);
        if(tipo == 1) {
            pessoa = getEmpresarioById(idEleito);
        }
        else if(tipo == 2) {
            pessoa = getAgricultorById(idEleito);
        }

        if (cargo == 0){
            if (idEleito % 2 != 0) {
                this.fiscais.get(0).eleger(pessoa.getId(), pessoa.getNome());
                cidade = "Atlantis";
            } else {
                this.fiscais.get(1).eleger(pessoa.getId(), pessoa.getNome());
                cidade = "Cidadela";
            }
            cargoString = "Fiscal";
        }
        else if (cargo == 1) { // Prefeito
            if (idEleito % 2 != 0) {
                this.prefeitos.get(0).eleger(pessoa.getId(), pessoa.getNome());
                cidade = "Atlantis";
            } else {
                this.prefeitos.get(1).eleger(pessoa.getId(), pessoa.getNome());
                cidade = "Cidadela";
            }
            cargoString = "Prefeito";
        }
        else if (cargo == 2){
            if (idEleito % 2 != 0) {
                this.vereadores.get(0).eleger(pessoa.getId(), pessoa.getNome());
                cidade = "Atlantis";
            } else {
                this.vereadores.get(1).eleger(pessoa.getId(), pessoa.getNome());
                cidade = "Cidadela";
            }
            cargoString = "Vereador";
        }

        this.colocaArquivoLog("" + pessoa.getNome().substring(0, 1).toUpperCase() + pessoa.getNome().substring(1).toLowerCase() + " eleito como " + cargoString + " na cidade de " + cidade + "");
        this.colocaLogCSV("eleicao" + this.separadorCSV + cargoString + this.separadorCSV + cidade + this.separadorCSV + pessoa.getNome());
        System.out.println("\"" + pessoa.getNome().substring(0, 1).toUpperCase() + pessoa.getNome().substring(1).toLowerCase() + "\" eleito como " + cargoString + " na cidade de " + cidade + ".");

        return true;
    }

    /**
     * Metodos referentes a classe de Prefeito
     */
    public Prefeito getPrefeitoById(int id) {
        for (Prefeito pref : this.prefeitos) {
            if (pref.getId() == id) {
                return pref;
            }
        }
        return null;
    }
    
    public void cobrarImpostos() {
        /*double imposto = 0;
        for (Empresario emp : this.empresarios) {
            if (emp.getCidade().equals("Atlantis")) {
                imposto = this.prefeitos.get(0).cobrarImposto(emp);

                this.colocaArquivoLog("Prefeito " + this.prefeitos.get(0).getNome() + " cobrou um imposto de D$" + imposto + " do empresario " + emp.getNome() + "");

                this.colocaLogCSV("imposto" + this.separadorCSV + "prefeito " + this.prefeitos.get(0).getNome() + this.separadorCSV + emp.getNome() + this.separadorCSV + imposto);
            } else {
                imposto = this.prefeitos.get(1).cobrarImposto(emp);
                this.colocaArquivoLog("Prefeito " + this.prefeitos.get(1).getNome() + " cobrou um imposto de D$" + imposto + " do empresario " + emp.getNome() + "");
                this.colocaLogCSV("imposto" + this.separadorCSV + "prefeito " + this.prefeitos.get(1).getNome() + this.separadorCSV + emp.getNome() + this.separadorCSV + imposto);
            }
        }
        for (Agricultor agr : this.agricultores) {
            if (agr.getCidade().equals("Atlantis")) {
                imposto = this.prefeitos.get(0).cobrarImposto(agr);
                this.colocaArquivoLog("Prefeito " + this.prefeitos.get(0).getNome() + " cobrou um imposto de D$" + imposto + " do agricultor " + agr.getNome() + "");
                this.colocaLogCSV("imposto" + this.separadorCSV + "prefeito " + this.prefeitos.get(0).getNome() + this.separadorCSV + agr.getNome() + this.separadorCSV + imposto);
            } else {
                imposto = this.prefeitos.get(1).cobrarImposto(agr);
                this.colocaArquivoLog("Prefeito " + this.prefeitos.get(1).getNome() + " cobrou um imposto de D$" + imposto + " do agricultor " + agr.getNome() + "");
                this.colocaLogCSV("imposto" + this.separadorCSV + "prefeito " + this.prefeitos.get(1).getNome() + this.separadorCSV + agr.getNome() + this.separadorCSV + imposto);
            }
        }*/
    }

    /**
     * Metodos referentes a classe de Vereador
     */
    public Vereador getVereadorById(int id) {
        for (Vereador ver : this.vereadores) {
            if (ver.getId() == id) {
                return ver;
            }
        }
        return null;
    }

    /**
     * Metodos em relacao do jogo
     */
    public void comecarJogo() {
        System.out.println("COMECANDO");

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String data = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss").format(timestamp.getTime());
        this.saveGame("\n\n" + data + "");
        this.saveGame("\n");

        String aux = "";
        boolean condicao = false;

        System.out.println("Qual a quantidade de jogadores? (minimo 10 - nao contar os cargos eleitorais)");
        do {
            aux = this.scanner.next();
            if (Pattern.matches("[0-9][0-9]", aux)) {
                this.quantidadeJogadores = Integer.parseInt(aux);
                if (this.quantidadeJogadores > 9) {
                    condicao = true;
                }
            } else {
                System.out.println("Quantidade ou valor invalidos. Por favor, tente novamente.");
            }
        } while (!condicao);
        condicao = false;

        this.saveGame("" + this.quantidadeJogadores + "");

        String[] nomes = {"EmpSem", "EmpFer", "EmpMaq", "EmpAgr"};

        System.out.println("Criando empresarios.....");
        String setor;
        for (int i = 1; i < 5; i++) {
            if (i == 1) {
                setor = "semente";
            } else if (i == 2) {
                setor = "fertilizante";
            } else if (i == 3) {
                setor = "maquina";
            } else {
                setor = "agrotoxico";
            }

            criaEmpresario(setor, nomes[i - 1]);
        }
        System.out.println("Empresarios criados.");

        int resposta = 0;
        System.out.println("Hora de criar os Agricultores.");

        System.out.println("A quantidade de parcelas a ser separada para cada um eh " + this.qntdParcelasPorAgricultor + " parcelas. Gostaria de mudar? (1 - Sim. 2 - Nao)");
        do {
            aux = this.scanner.next();
            if (Pattern.matches("[1-2]?", aux)) {
                resposta = Integer.parseInt(aux);
                if (resposta == 1) {
                    this.saveGame("1");
                    System.out.println("Qual a quantidade de parcelas desejadas? (no minimo 6, maximo 10)");
                    do {
                        aux = this.scanner.next();
                        if (Pattern.matches("[0-9]", aux) || Pattern.matches("[0-9][0-9]", aux)) {
                            this.qntdParcelasPorAgricultor = Integer.parseInt(aux);
                            if (qntdParcelasPorAgricultor > 5 && qntdParcelasPorAgricultor < 11) {
                                this.saveGame("" + this.qntdParcelasPorAgricultor + "");
                                condicao = true;
                            }
                        } else {
                            System.out.println("Quantidade ou valor invalidos. Por favor, tente novamente.");
                        }
                    } while (!condicao);
                } else if (resposta == 2) {
                    condicao = true;
                    this.saveGame("2");
                }
            } else {
                System.out.println("Quantidade ou valor invalidos. Por favor, tente novamente.");
            }
        } while (!condicao);
        condicao = false;

        String nome;
        int numNome = 1;
        for (int i = 0; i < this.quantidadeJogadores - 4; i++) {
            if (i % 2 == 0) {
                nome = "AT" + numNome;
                criaAgricultor(nome, "Atlantis");
            } else {
                nome = "CD" + numNome;
                criaAgricultor(nome, "Cidadela");
                numNome++;
            }
        }
        System.out.println("Agricultores criados.");

        this.colocaArquivoLog("\n===============================================\n" + data + "\nRodada:" + this.rodada);
        this.colocaLogCSV("rodada " + this.rodada);

        System.out.println("Fiscais, Prefeitos e Vereadores serao decididos por votacao pelos jogadores na primeira etapa da primeira rodada.\n");

        this.criaCargosPoliticos();
        this.criaHistoricoSaldos();
        this.limpaHistoricoTransferencias();
        System.out.println("Termino do processo de criacao do jogo. Tenha um otimo jogo!");
    }

    public void saveGame(String comando) {
        String fileName = "saves/saves.txt";

        BufferedWriter writer = null;

        try {
            writer = new BufferedWriter(new FileWriter(fileName, true));
            writer.write(comando + " ");
            writer.flush();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException ioe2) {
                    ioe2.printStackTrace();
                }
            }
        }
    }

    public void colocaArquivoLog(String comando) {
        String fileName = "arquivoslog/log.txt";

        BufferedWriter writer = null;

        try {
            writer = new BufferedWriter(new FileWriter(fileName, true));
            writer.write(comando);
            writer.newLine();
            writer.flush();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException ioe2) {
                    ioe2.printStackTrace();
                }
            }
        }
    }

    public void colocaLogCSV(String comando) {
        String fileName = "arquivoslog/log.csv";

        BufferedWriter writer = null;

        try {
            writer = new BufferedWriter(new FileWriter(fileName, true));
            writer.write(comando);
            writer.newLine();
            writer.flush();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException ioe2) {
                    ioe2.printStackTrace();
                }
            }
        }
    }

    public void fechaRodadaLog() {
        String infos = "";
        String infosCSV = "";
        for (Empresario emp : this.empresarios) {
            infos += emp.getNome() + ": " + ((emp.getPoluicao() / 10000) * 100) + " / D$ " + emp.getSaldo() + "\n";
            infosCSV += emp.getNome() + this.separadorCSV + ((emp.getPoluicao() / 10000) * 100) + this.separadorCSV + emp.getSaldo() + "\n";
        }
        for (Agricultor agr : this.agricultores) {
            infos += agr.getNome() + ": " + ((agr.getPoluicao() / 10000) * 100) + " / D$ " + agr.getSaldo() + "\n";
            infosCSV += agr.getNome() + this.separadorCSV + ((agr.getPoluicao() / 10000) * 100) + this.separadorCSV + agr.getSaldo() + "\n";
        }
        for (FiscalAmbiental fis : this.fiscais) {
            infos += "Fiscal " + fis.getNome() + ": D$ " + fis.getSaldo() + "\n";
            infosCSV += "fiscal " + fis.getNome() + this.separadorCSV + fis.getSaldo() + "\n";
        }
        for (Prefeito pref : this.prefeitos) {
            infos += "Prefeito " + pref.getNome() + ": D$ " + pref.getCaixa() + "\n";
            infosCSV += "prefeito " + pref.getNome() + this.separadorCSV + pref.getCaixa() + "\n";
        }
        int aux = 0;
        for (Vereador ver : this.vereadores) {
            infos += "Vereador " + ver.getNome() + ": D$ " + ver.getSaldo();
            infosCSV += "vereador " + ver.getNome() + this.separadorCSV + ver.getSaldo();
            if (aux == 0) {
                infos += "\n";
                infosCSV += "\n";
            }
            aux++;
        }
        this.colocaArquivoLog("Poluicoes/Saldo dos jogadores:\n" + infos);
        this.colocaLogCSV(infosCSV);
        this.colocaArquivoLog("Poluicao Mundial: " + (this.poluicaoMundo * 100));
        String poluicaoMundo = "mundo" + this.separadorCSV + (this.poluicaoMundo * 100);

        this.colocaLogCSV(poluicaoMundo);
    }

    // MÉTODOS PARA O TRATAMENTO DE TRANSAcoES E SALDOS

    private void criaHistoricoSaldos(){
        this.saldosAnteriores.clear();

        for (Empresario emp : this.empresarios) {
            this.saldosAnteriores.add(emp.getSaldo());
        }
        for (Agricultor agr : this.agricultores) {
            this.saldosAnteriores.add(agr.getSaldo());
        }
        for (FiscalAmbiental fis : this.fiscais) {
            this.saldosAnteriores.add(fis.getSaldo());
        }
        for (Prefeito pref : this.prefeitos) {
            this.saldosAnteriores.add(pref.getCaixa());
        }
        for (Vereador ver : this.vereadores) {
            this.saldosAnteriores.add(ver.getSaldo());
        }
    }

    private void limpaHistoricoTransferencias(){
        for (JSONObject transf : this.transferenciasSent) {
            transf.clear();
        }
        for (JSONObject transf : this.transferenciasReceived) {
            transf.clear();
        }
    }

    //ARQUIVOS DE SAIDA
    @SuppressWarnings({ "unchecked" })
	private JSONObject setArquivoEmpJSON(Empresario emp, int etapa) {
    	JSONObject rodada = new JSONObject();
    	
    	rodada.put("previousBalance", this.saldosAnteriores.get(emp.getId()-1));
    	rodada.put("selling", emp.getProdutividade());
    	rodada.put("taxes", emp.getImposto());
    	if(etapa == 2) rodada.put("fees", emp.getMulta());
    	
    	JSONObject transferencias = new JSONObject();
    	transferencias.put("sent", this.transferenciasSent.get((emp.getId()-1)));
    	transferencias.put("received", this.transferenciasReceived.get((emp.getId()-1)));
    	rodada.put("transfers", transferencias);
    	
    	rodada.put("currentBalance", emp.getSaldo());
    	rodada.put("personalPollution", emp.getPoluicao());
    	rodada.put("worldCausedPollution", (emp.getPoluicao() / 1000));
    	rodada.put("worldPollution", (this.poluicaoMundo/100));
    	
    	return rodada;
    	
    }

    @SuppressWarnings({ "unchecked" })
    private JSONObject setArquivoAgrJSON(Agricultor agr, int etapa) {
    	JSONObject rodada = new JSONObject();
    	
    	rodada.put("previousBalance", this.saldosAnteriores.get(agr.getId()-1));
    	rodada.put("productivity", agr.getProdutividade());
    	rodada.put("taxes", agr.getImposto());
    	if(etapa == 2) rodada.put("fees", agr.getMulta());
    	rodada.put("moneySpent", agr.getGastos());
    	    	
    	JSONObject transferencias = new JSONObject();
    	transferencias.put("sent", this.transferenciasSent.get((agr.getId()-1)));
    	transferencias.put("received", this.transferenciasReceived.get((agr.getId()-1)));
    	rodada.put("transfers", transferencias);
    	
    	rodada.put("currentBalance", agr.getSaldo());
    	rodada.put("PersonalPollution", agr.getPoluicao());
    	rodada.put("worldCausedPollution", (agr.getPoluicao() / 1000));
    	rodada.put("worldPollution", (this.poluicaoMundo/100));
    	
    	rodada.put("parcels", agr.contentParcelaJSON());
    	
    	return rodada;
    	
    }

    @SuppressWarnings({ "unchecked", "unused" })
    private JSONObject setArquivoFisJSON(FiscalAmbiental fis, int etapa) {
    	JSONObject rodada = new JSONObject();
    	
    	rodada.put("previousBalance", this.saldosAnteriores.get(fis.getId()-1));
    	
    	JSONObject transferencias = new JSONObject();
    	transferencias.put("sent", this.transferenciasSent.get((fis.getId()-1)));
    	transferencias.put("received", this.transferenciasReceived.get((fis.getId()-1)));
    	rodada.put("transfers", transferencias);

    	rodada.put("requests", fis.getPedidos());
    	rodada.put("currentBalance", fis.getSaldo());
    	rodada.put("worldPollution", (this.poluicaoMundo/100));
    	
    	JSONObject empresarios = new JSONObject();
    	for(Empresario emp : this.empresarios) {
    		if(emp.getCidade() == fis.getCidade()) {
    			JSONObject empresario = new JSONObject();
    			empresario.put("pollution", emp.getPoluicao());
    			empresario.put("productivity", emp.getProdutividade());
    			if(etapa == 2)  empresario.put("fee", emp.getMulta());
    			empresarios.put(emp.getNome(), empresario);
    		}
    	}
    	rodada.put("entrepreneurs", empresarios);
    	
    	JSONObject agricultores = new JSONObject();
    	for(Agricultor agr : this.agricultores) {
    		if(agr.getCidade() == fis.getCidade()) {
    			JSONObject agricultor = new JSONObject();
    			agricultor.put("avaragePollution", agr.getPoluicao());
    			if(etapa == 2)  agricultor.put("fee", agr.getMulta());
    			agricultor.put("parcels", agr.contentParcelaJSON());
        		agricultores.put(agr.getNome(), agricultor);
    		}
    	}
    	rodada.put("agriculturists", agricultores);
    	
    	return rodada;
    	
    }

    @SuppressWarnings({ "unchecked", "unused" })
    private JSONObject setArquivoPrefJSON(Prefeito pref, int etapa) {
    	JSONObject rodada = new JSONObject();
    	
    	double impostos = 0;
    	double multas = 0;
    	
    	JSONObject empresarios = new JSONObject();
    	for(Empresario emp : this.empresarios) {
    		if(emp.getCidade() == pref.getCidade()) {
    			JSONObject empresario = new JSONObject();
    			empresario.put("pollution", emp.getPoluicao());
    			empresario.put("productivity", emp.getProdutividade());
    			empresario.put("taxes", emp.getImposto());
    			impostos += emp.getImposto();
    			if(etapa == 2) {
    				empresario.put("fee", emp.getMulta());
    				multas += emp.getMulta();
    			}
    			empresarios.put(emp.getNome(), empresario);
    		}
    	}
    	
    	JSONObject agricultores = new JSONObject();
    	for(Agricultor agr : this.agricultores) {
    		if(agr.getCidade() == pref.getCidade()) {
    			JSONObject agricultor = new JSONObject();
    			agricultor.put("avaragePollution", agr.getPoluicao());
    			agricultor.put("taxes", agr.getImposto());
    			impostos += agr.getImposto();
    			if(etapa == 2) {
    				agricultor.put("fee", agr.getMulta());
    				multas += agr.getMulta();
    			}
    			agricultor.put("parcels", agr.contentParcelaJSON());
        		agricultores.put(agr.getNome(), agricultor);
    		}
    	}
    	
    	rodada.put("previousSafeBalance", this.saldosAnteriores.get(pref.getId()-1));
    	rodada.put("yearTaxes", impostos);
    	if(etapa == 2) rodada.put("yearFees", multas);
    	
    	JSONObject transferencias = new JSONObject();
    	transferencias.put("sent", this.transferenciasSent.get((pref.getId()-1)));
    	transferencias.put("received", this.transferenciasReceived.get((pref.getId()-1)));
    	rodada.put("transfers", transferencias);

    	rodada.put("currentSafeBalance", pref.getSaldo());
    	rodada.put("worldPollution", (this.poluicaoMundo/100));

    	rodada.put("entrepreneurs", empresarios);
    	rodada.put("agriculturists", agricultores);
    	
    	return rodada;
    	
    }

    @SuppressWarnings({ "unchecked", "unused" })
    private JSONObject setArquivoVerJSON(Vereador ver, int etapa) {
    	JSONObject rodada = new JSONObject();
    	
    	rodada.put("previousBalance", this.saldosAnteriores.get(ver.getId()-1));
    	
    	JSONObject transferencias = new JSONObject();
    	transferencias.put("sent", this.transferenciasSent.get((ver.getId()-1)));
    	transferencias.put("received", this.transferenciasReceived.get((ver.getId()-1)));
    	rodada.put("transfers", transferencias);
    	
    	if(etapa == 2) {
    		for(Prefeito pref : this.prefeitos) {
        		if(pref.getCidade() == ver.getCidade()) {
        			rodada.put("environmentalActionsUsed", pref.getAcoesUsadasJSON());
        			rodada.put("taxesChanged", pref.getTaxasMudadasJSON());
        		}
        	}
    	}

    	rodada.put("currentBalance", ver.getSaldo());
    	rodada.put("worldPollution", (this.poluicaoMundo/100));

    	
    	return rodada;
    	
    }
    
    
    /**
    * Para somente um arquivo de resumo, usar este método.
    * Para uma saida por papel, utilizar metodo setArquivosByRole.
    */
    public void setArquivos(int etapa) throws IOException {
    	String pageBreak = "\014";
        if (etapa == 1) {
            String fileContent = "";

            for (FiscalAmbiental fis : this.fiscais) {
                fileContent += "=================================================\n";
                //fileContent += this.setArquivoFiscal(fis, etapa);
                fileContent += "=================================================\n" + pageBreak;
            }
            for (Prefeito pre : this.prefeitos) {
                fileContent += "=================================================\n";
                //fileContent += this.setArquivoPrefeito(pre, etapa);
                fileContent += "=================================================\n" + pageBreak;
            }
            for (Vereador ver : this.vereadores) {
                fileContent += "=================================================\n";
                //fileContent += this.setArquivoVereador(ver, etapa);
                fileContent += "=================================================\n" + pageBreak;
            }

            String arq = "arquivosResumo/r" + this.rodada + "e" + etapa + ".txt";
            try (PrintWriter esc = new PrintWriter(arq, "UTF-8")) {
                esc.println(fileContent);
                esc.close();
            }
        }
        else {
            String fileContent = "";

            for (Empresario emp : this.empresarios) {
                fileContent += "=================================================\n";
                //fileContent += this.setArquivoEmp(emp, etapa);
                fileContent += "=================================================\n" + pageBreak;
            }
            for (Agricultor agr : this.agricultores) {
                fileContent += "=================================================\n";
                //fileContent += this.setArquivoAgr(agr, etapa);
                fileContent += "=================================================\n" + pageBreak;
            }
            for (FiscalAmbiental fis : this.fiscais) {
                fileContent += "=================================================\n";
                //fileContent += this.setArquivoFiscal(fis, etapa);
                fileContent += "=================================================\n" + pageBreak;
            }
            for (Prefeito pre : this.prefeitos) {
                fileContent += "=================================================\n";
                //fileContent += this.setArquivoPrefeito(pre, etapa);
                fileContent += "=================================================\n" + pageBreak;
            }
            for (Vereador ver : this.vereadores) {
                fileContent += "=================================================\n";
                //fileContent += this.setArquivoVereador(ver, etapa);
                fileContent += "=================================================\n" + pageBreak;
            }

            String arq = "arquivosResumo/r" + this.rodada + "e" + etapa + ".txt";
            try (PrintWriter esc = new PrintWriter(arq, "UTF-8")) {
                esc.println(fileContent);
                esc.close();
            }
        }
    }

    /**
    * Para criar um arquivo resumo por papel por etapa utilizar esse método.
    * Para utilizar um arquivo unico por etapa, utilizar setArquivos
    */
    public void setArquivosByRole(int etapa) throws IOException{
        String fileContent = "";
        /*
        if (etapa == 1) {

            for (FiscalAmbiental fis : this.fiscais) {
                fileContent = this.setArquivoFiscal(fis, etapa);
                String arq = "arquivosResumo/r" + this.rodada + "e" + etapa + "Fis" + fis.getCidade() + ".txt";
                try (PrintWriter esc = new PrintWriter(arq, "UTF-8")) {
                    esc.println(fileContent);
                    esc.close();
                }
            }
            for (Prefeito pre : this.prefeitos) {
                fileContent = this.setArquivoPrefeito(pre, etapa);
                String arq = "arquivosResumo/r" + this.rodada + "e" + etapa + "Pre" + pre.getCidade() + ".txt";
                try (PrintWriter esc = new PrintWriter(arq, "UTF-8")) {
                    esc.println(fileContent);
                    esc.close();
                }
            }
            for (Vereador ver : this.vereadores) {
                fileContent = this.setArquivoVereador(ver, etapa);
                String arq = "arquivosResumo/r" + this.rodada + "e" + etapa + "Ver" + ver.getCidade() + ".txt";
                try (PrintWriter esc = new PrintWriter(arq, "UTF-8")) {
                    esc.println(fileContent);
                    esc.close();
                }
            }
        }
        else {
		*/
            for (Empresario emp : this.empresarios) {
                this.escreveFinalArquivoJSON(
                		"arquivosResumo/empresario/"+ emp.getId() + ".json",
                		this.setArquivoEmpJSON(emp, etapa)
                );
            }
            for (Agricultor agr : this.agricultores) {
                this.escreveFinalArquivoJSON(
                		"arquivosResumo/agricultor/" + agr.getId() + ".json",
                		this.setArquivoAgrJSON(agr, etapa)
                );
            }
            /*
            for (FiscalAmbiental fis : this.fiscais) {
                fileContent = this.setArquivoFiscal(fis, etapa);
                String arq = "arquivosResumo/r" + this.rodada + "e" + etapa + "Fis" + fis.getCidade() + ".txt";
                try (PrintWriter esc = new PrintWriter(arq, "UTF-8")) {
                    esc.println(fileContent);
                    esc.close();
                }
            }
            for (Prefeito pre : this.prefeitos) {
                fileContent = this.setArquivoPrefeito(pre, etapa);
                String arq = "arquivosResumo/r" + this.rodada + "e" + etapa + "Pre" + pre.getCidade() + ".txt";
                try (PrintWriter esc = new PrintWriter(arq, "UTF-8")) {
                    esc.println(fileContent);
                    esc.close();
                }
            }
            for (Vereador ver : this.vereadores) {
                fileContent = this.setArquivoVereador(ver, etapa);
                String arq = "arquivosResumo/r" + this.rodada + "e" + etapa + "Ver" + ver.getCidade() + ".txt";
                try (PrintWriter esc = new PrintWriter(arq, "UTF-8")) {
                    esc.println(fileContent);
                    esc.close();
                }
            }
        }*/

    }
    
    public void escreveFinalArquivo(String arquivo, String escrita) throws IOException {
        String fileName = arquivo;

        BufferedWriter writer = null;

        try {
            writer = new BufferedWriter(new FileWriter(fileName, true));
            writer.write(escrita + "\n");
            writer.flush();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException ioe2) {
                    //
                }
            }
        }
    }
    
    @SuppressWarnings("unchecked")
	public void escreveFinalArquivoJSON(String arquivo, JSONObject novaRodada) throws IOException {
        String fileName = arquivo;

        JSONParser parser = new JSONParser();
        JSONObject arquivoNovo = new JSONObject();
        if( !( (this.rodada == 1) && (this.etapa == 1) ) ) {
        	try (Reader reader = new FileReader(fileName)) {

                JSONObject jsonObject = (JSONObject) parser.parse(reader);
                
                int r = 1;
                int e = 1;
                while(jsonObject.get(("r" + r + "e" + e)) != null) {
                	arquivoNovo.put(("r" + r + "e" + e), jsonObject.get(("r" + r + "e" + e)));
                	if(e == 2) {
                		r++;
                		e--;
                	}
                	else e++;                	
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        arquivoNovo.put(("r" + this.rodada + "e" + this.etapa), novaRodada);
        
        try (FileWriter file = new FileWriter(fileName)) {
            file.write(arquivoNovo.toJSONString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }


    // A partir daqui é API
    
    @SuppressWarnings("unchecked")
	public void transferirDinheiros(int idChamador, int idRecebedor, double quantity) {
    	Pessoa pessoaChamadora = null;
    	Pessoa pessoaRecebedora = null;
    	
    	String nomePessoaChamadora = "";
    	
    	int tipoPessoaChamadora = this.getTipoPessoaById(idChamador);
    	if (tipoPessoaChamadora == 1) {
            Empresario empresario = this.getEmpresarioById(idChamador);
            nomePessoaChamadora = empresario.getNome();
            pessoaChamadora = empresario;
        } else if (tipoPessoaChamadora == 2) {
            Agricultor agricultor = this.getAgricultorById(idChamador);
            nomePessoaChamadora = agricultor.getNome();
            pessoaChamadora = agricultor;
        } else if (tipoPessoaChamadora == 3) {
            FiscalAmbiental fis = this.getFiscalById(idChamador);
            nomePessoaChamadora = fis.getNome() + " (Fiscal Ambiental)";
            pessoaChamadora = fis;
        } else if (tipoPessoaChamadora == 4) {
            Prefeito prefeito = this.getPrefeitoById(idChamador);
            nomePessoaChamadora = prefeito.getNome() + " (Prefeito)";
            pessoaChamadora = prefeito;
        } else if (tipoPessoaChamadora == 5) {
            Vereador vereador = this.getVereadorById(idChamador);
            nomePessoaChamadora = vereador.getNome() + " (Vereador)";
            pessoaChamadora = vereador;
        }
    	
    	String nomePessoaRecebedora = "";    	
    	int tipoPessoaRecebedora = this.getTipoPessoaById(idRecebedor);
    	if (tipoPessoaRecebedora == 1) {
            Empresario empresario = this.getEmpresarioById(idRecebedor);
            nomePessoaRecebedora = empresario.getNome();
            pessoaRecebedora = empresario;
        } else if (tipoPessoaRecebedora == 2) {
            Agricultor agricultor = this.getAgricultorById(idRecebedor);
            nomePessoaRecebedora = agricultor.getNome();
            pessoaRecebedora = agricultor;
        } else if (tipoPessoaRecebedora == 3) {
            FiscalAmbiental fis = this.getFiscalById(idRecebedor);
            nomePessoaRecebedora = fis.getNome() + " (Fiscal Ambiental)";
            pessoaRecebedora = fis;
        } else if (tipoPessoaRecebedora == 4) {
            Prefeito prefeito = this.getPrefeitoById(idRecebedor);
            nomePessoaRecebedora = prefeito.getNome() + " (Prefeito)";
            pessoaRecebedora = prefeito;
        } else if (tipoPessoaRecebedora == 5) {
            Vereador vereador = this.getVereadorById(idRecebedor);
            nomePessoaRecebedora = vereador.getNome() + " (Vereador)";
            pessoaRecebedora = vereador;
        }
    	
    	pessoaChamadora.negociacaoCapital(quantity, pessoaRecebedora);
    	this.transferenciasSent.get(pessoaChamadora.getId()-1).put(nomePessoaRecebedora, quantity);
    	this.transferenciasReceived.get(pessoaRecebedora.getId()-1).put(nomePessoaChamadora, quantity);
    }
    
    public void venda(int idAgr, int numParcela, int idProduto, int preco) {
    	int tipoProduto = this.getTipoProdutoById(idProduto);
    	Agricultor agricultor = this.getAgricultorById(idAgr);
    	this.empresarios.get(tipoProduto-1).venderAlugar(
    			idProduto,
    			agricultor,
    			(numParcela-1),
    			preco,
    			this.poluicaoMundo
    	);
    }
    
    public double calcularPoluicaoCausada() {
    	double poluicaoCausada = 0;
    	for (Agricultor agr : this.agricultores) {
            agr.plantar(this.poluicaoMundo);
            poluicaoCausada += agr.calculaPoluicao();
        }
        for (Empresario emp : this.empresarios) {
            poluicaoCausada += emp.calculaPoluicao();
        }
        return poluicaoCausada;
    }
    
    public void finalizaEtapa() throws IOException {
	    if(this.etapa == 1) {
	    	// Realizar eleição se rodada divisivel por 2
	    	
	    	this.poluicaoMundo += this.calcularPoluicaoCausada();
	        
	        this.cobrarImpostos();
	        /*
	        for (Prefeito pref : this.prefeitos) {
	            pref.receberContribuicoes();
	            // System.out.println("Contribuicoes recebidas pelo Prefeito de " + pref.getCidade() + ".");
	        }
	        */
	
	        this.setArquivosByRole(this.etapa);
	        this.etapa = 2;
	        
	    }
	    else {
	    	
	    	double poluicaoReduzida = 0;
	    	/*
            for (Prefeito pref : this.prefeitos) {
                poluicaoReduzida += pref.usarAcoes();
                pref.receberContribuicoes();
            }
            */
            this.poluicaoMundo *= (1 - poluicaoReduzida);
            

            this.setArquivosByRole(this.etapa);
            
	    	this.rodada++;
            this.limpaHistoricoTransferencias();
            this.criaHistoricoSaldos();
            for (Empresario emp : this.empresarios) {
                emp.iniciaRodada();
            }
            for (Agricultor agr : this.agricultores) {
                agr.iniciaRodada();
            }
            /*
            for(Prefeito pref : this.prefeitos){
                pref.iniciaRodada();
                if( (this.rodada+1)%2 == 0 ) pref.setNome("");
            }
            */
            
            this.limpaVendas();
            
            this.etapa = 1;
	    }
    }
    
    public ArrayList<Empresario> getListaEmpresario(){
    	return this.empresarios;
    }
    
    public ArrayList<Agricultor> getListaAgricultor(){
    	return this.agricultores;
    }
    
    public List<PessoaModel> getInfoAgricultores(){
    	List<PessoaModel> agrs = new ArrayList<PessoaModel>();
    	for (Agricultor agricultor : this.agricultores) {
    		PessoaModel aux = new PessoaModel(agricultor.getNome(), agricultor.getId());
    		agrs.add(aux);
    	}
    	return agrs;
    }
    
    public ResponseEntity<ByteArrayResource> getFilePessoaById(int id) throws IOException {
    	int tipoPessoa = this.getTipoPessoaById(id);
    	File file = null;
    	HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");
        Path path;
        ByteArrayResource resource = null;
        
    	if(tipoPessoa == 1) {
    		file = new File("arquivosResumo/empresario/" + id + ".json");
            path = Paths.get(file.getAbsolutePath());
            resource = new ByteArrayResource(Files.readAllBytes(path));
            
    	}
    	else if(tipoPessoa == 2) {
    		file = new File("arquivosResumo/agricultor/" + id + ".json");
            path = Paths.get(file.getAbsolutePath());
            resource = new ByteArrayResource(Files.readAllBytes(path));
    	}
    	else if(tipoPessoa == 3) {
    		file = new File("arquivosResumo/fiscal/" + id + ".json");
            path = Paths.get(file.getAbsolutePath());
            resource = new ByteArrayResource(Files.readAllBytes(path));
    	}
    	else if(tipoPessoa == 4) {
    		file = new File("arquivosResumo/prefeito/" + id + ".json");
            path = Paths.get(file.getAbsolutePath());
            resource = new ByteArrayResource(Files.readAllBytes(path));
    	}
    	else if(tipoPessoa == 5) {
    		file = new File("arquivosResumo/vereador/" + id + ".json");
            path = Paths.get(file.getAbsolutePath());
            resource = new ByteArrayResource(Files.readAllBytes(path));
    	}
    	return ResponseEntity.ok()
                .headers(headers)
                .contentLength(file.length())
                .contentType(MediaType.valueOf(MediaType.TEXT_PLAIN_VALUE))
                .body(resource);
    }
    
    public void setPedidoFiscal(int idAgr, String pedido) {
    	Agricultor agr = getAgricultorById(idAgr);
    	
    	int cidade = (agr.getCidade().equals("Atlantis")) ? 0 : 1;
    	
    	this.fiscais.get(cidade).adicionaPedido(agr.getNome(), pedido);
    }
    
    public void adicionaOrcamentoById(Venda venda) {
    	System.out.println("Entrou Mundo.adicionaOrcamentoById()");
    	venda.setNomeAgr(this.agricultores.get(venda.getIdAgr()-1-4).getNome());
    	venda.setNomeEmp(this.empresarios.get(venda.getIdEmp()-1).getNome());
    	
    	venda.setNomeProduto(
    			this.empresarios.get(venda.getIdEmp()-1).getTipoProdutoById(venda.getIdProduto())
    	);
    	
    	this.vendas.get(venda.getIdAgr()-1).add(venda);
    }
    
    public List<Venda> getOrcamentos(int idAgr){
    	System.out.println("Entrou Mundo.getOrcamentos()");
    	return this.vendas.get(idAgr-1);
    }
    
    public void adicionaVendaById(Venda venda) {
    	System.out.println("Entrou Mundo.adicionaVendaById()");
    	this.vendas.get(venda.getIdEmp()-1).add(venda);
    }
    
    public void removeOrcamentoById(int idAgr, int idEmp, int idOrcamento) {
    	System.out.println("Entrou Mundo.removeOrcamentoById()");
    	System.out.println(this.vendas.get(idAgr-1).size());
    	for (Venda orcamento : this.vendas.get(idAgr-1)) {
			if(orcamento.getIdOrcamento() == idOrcamento && orcamento.getIdEmp() == idEmp) {
				this.vendas.get(idAgr-1).remove(orcamento);
				break;
			}
		}
    	System.out.println(this.vendas.get(idAgr-1).size());
    	//this.vendas.get(venda.getIdAgr()-1).remove(venda.getIdJava());
    }
    
    public List<Venda> getVendas(int idEmp){
    	System.out.println("Entrou Mundo.getVendas()");
    	return this.vendas.get(idEmp-1);
    }
    
    public void limpaVendas() {
    	this.vendas.forEach(
			x -> x.clear()
    	);
    }
    
}
