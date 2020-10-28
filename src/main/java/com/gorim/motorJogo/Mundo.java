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
import java.util.Random;
import java.util.Scanner;
import java.util.regex.Pattern;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.gorim.model.PessoaModel;
import com.gorim.model.ProdutoSimplifiedModel;
import com.gorim.model.forms.AgricultorForm;
import com.gorim.model.forms.EmpresarioForm;
import com.gorim.model.forms.Parcela;
import com.gorim.model.forms.Produto;
import com.gorim.model.forms.Transfer;
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
    private ArrayList<JSONArray> transferenciasSent;
    private ArrayList<JSONArray> transferenciasReceived;
    
    private ArrayList<JSONArray> acoesAmbientaisExecutadas;
    
    private List<List<Venda>> vendas;
    private ArrayList<Transfer> transferencias;

	private boolean[] et1;
	private boolean[] et2;
	private boolean fimEtapa;

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
        
        this.acoesAmbientaisExecutadas = new ArrayList<>(2);
        
        this.transferencias = new ArrayList<>();
        
		this.et1 = new boolean[this.quantidadeJogadores];
		this.et2 = new boolean[6];
		this.fimEtapa = false;

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
        this.primeiraEleicao();
        
        // Inicia Array auxiliar de orcamento
        this.vendas = new ArrayList<>();
        while(this.vendas.size() < this.quantidadeJogadores)
        	this.vendas.add(new ArrayList<Venda>());
        
        int qnt = this.quantidadeJogadores + 6;
        this.transferenciasSent = new ArrayList<JSONArray>(qnt);
        this.transferenciasReceived = new ArrayList<JSONArray>(qnt);

        for (int i = 0; i < qnt; i++) {
            this.transferenciasSent.add(new JSONArray());
            this.transferenciasReceived.add(new JSONArray());
        }
        
        while(this.acoesAmbientaisExecutadas.size() < 2)
        	this.acoesAmbientaisExecutadas.add(new JSONArray());

        this.criaHistoricoSaldos();
        
    }
    
    public void primeiraEleicao() {
    	Random rand = new Random();

    	// Eleger em Atlantis
    	int idFis = rand.nextInt(5)*2 + 1;
    	
    	int idPref;
    	do {
    		idPref = rand.nextInt(5)*2 + 1;
    	} while(idPref == idFis);
    	
    	int idVer;
    	do {
    		idVer = rand.nextInt(5)*2 + 1;
    	} while( (idVer == idFis) || (idVer == idPref) );
    	
    	this.eleger(idFis, 0);
    	this.eleger(idPref, 1);
    	this.eleger(idVer, 2);
    	
    	// Eleger em Cidadela
    	idFis = (1+rand.nextInt(5))*2;
    	
    	do {
    		idPref = (1+rand.nextInt(5))*2;
    	} while(idPref == idFis);
    	
    	do {
    		idVer = (1+rand.nextInt(5))*2;
    	} while( (idVer == idFis) || (idVer == idPref) );
    	
    	this.eleger(idFis, 0);
    	this.eleger(idPref, 1);
    	this.eleger(idVer, 2);
    	
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
    
    public void changeFlagFimEtapa() {
    	this.fimEtapa = !this.fimEtapa;
    }
	
	@SuppressWarnings("unused")
	private void setJaJogou(int tipoJogador, int idJogador) {
		if(tipoJogador < 3) this.et1[idJogador-1] = true;
		else this.et2[idJogador-this.quantidadeJogadores] = true;
	}
	
	@SuppressWarnings("unused")
	private void limpaEts() {
		for (boolean et : this.et1) {
			et = false;
		}
		
		for (boolean et : this.et2) {
			et = false;
		}
	}
	
	public boolean[] verificaFinalizados(int etapa) {
		if(etapa == 1) return this.et1;
		else if(etapa == 2) return this.et2;
		return null;
	}
	
	/*
	 * Verifica a situação do término de jogada dos jogadores de uma determinada etapa.
	 * 
	 * Caso 0: todos marcaram que terminaram a jogada.
	 * Caso 1: tem alguns que terminaram e outros que não.
	 * Caso 2: nenhum terminou ainda
	 * 
	 */
	public int hasUnfinishedPlayers(int etapa) {
		int terminaram = 0;
		boolean todosTerminaram = false;
		
		if(etapa == 1) {
			for (boolean jogadaPessoa : this.et1) {
				if(jogadaPessoa) terminaram++;
			}
			if(terminaram == this.quantidadeJogadores) todosTerminaram = true;
		}
		else {
			for (boolean jogadaPessoa : this.et2) {
				if(jogadaPessoa) terminaram++;
			}
			if(terminaram == 6) todosTerminaram = true;
		}
		
		if(terminaram == 0) return 2;
		else if(todosTerminaram) return 0;
		return 1;
		
	}
	
	/*
	 * Verifica qual a situação da etapa.
	 * 
	 * Caso 0: Significa que mestre acionou que terminou jogada e todos os jogadores da
	 * etapa foram marcados que acabaram a jogada também.
	 * Caso 1: Significa que está começando a etapa, ou seja, a etapa anterior foi finalizada,
	 * mas nem todos os jogadores abriram a janela da nova etapa ainda.
	 * Caso 2: Significa que todos os jogadores entraram na nova janela, mas o serviço
	 * do mestre ainda não tirou a flag de término da jogada.
	 * Caso 3: Significa que todos os jogadores terminaram a jogada e o mestre ainda não
	 * apertou o botão de acabar a etapa.
	 * Caso 4: Significa que está no meio da etapa, nem todos os jogadores terminaram a jogada,
	 * e nem o mestre apertou o botão de finalizar etapa.
	 * Caso 5: Significa que se iniciou a etapa. Se rodada == 1 & etapa == 2 || rodada > 1: todos
	 * os jogadores entraram nas novas janelas e a flag de fimEtapa foi desativada pelo masterService.
	 * 
	 * Os botões de finalizar jogada só devem ser habilitados caso o retorno seja > 2.
	 * 
	 */
	public int verificaFimEtapa(int etapa) {
		int situacaoEtapa;
		
		if(this.fimEtapa) {
			if(this.hasUnfinishedPlayers(etapa) == 0) situacaoEtapa = 0;
			else if(this.hasUnfinishedPlayers(etapa) == 1) situacaoEtapa = 1;
			else situacaoEtapa = 2;
		}
		else{
			if(this.hasUnfinishedPlayers(etapa) == 0) situacaoEtapa = 3;
			else if(this.hasUnfinishedPlayers(etapa) == 1) situacaoEtapa = 4;
			else situacaoEtapa = 5;
		}
		
		
		return situacaoEtapa;
	}
	
	/*
	 * Retorna o id do do papel da pessoa na segunda etapa e o papel que ela vai jogar.
	 * @param idPessoa: id da pessoa na primeira etapa
	 * 
	 * Caso 0: a pessoa do id informado não fará nenhum papel na segunda etapa.
	 * Caso outro: retorno/10 = id do papel na segunda etapa. retorno%10 = papel da
	 * pessoa na segunda etapa.
	 * 		0: fiscal ambiental
	 * 		1: prefeito
	 * 		2: vereador
	 */
	public int papelSegundaEtapa(int idPessoa) {
		int idSegundaEtapa = 0;
		
		for (FiscalAmbiental fis : this.fiscais) {
			if(idPessoa == fis.getIdEleito()) {
				idSegundaEtapa = fis.getId()*10;
				break;
			}
		}
		
		if(idSegundaEtapa == 0) {
			for (Prefeito pref : this.prefeitos) {
				if(idPessoa == pref.getIdEleito()) {
					idSegundaEtapa = pref.getId()*10 + 1;
					break;
				}
			}
		}
		
		if(idSegundaEtapa == 0) {
			for (Vereador ver : this.vereadores) {
				if(idPessoa == ver.getIdEleito()) {
					idSegundaEtapa = ver.getId()*10 + 2;
					break;
				}
			}
		}
		
		return idSegundaEtapa;
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
    
    public void adicionaTransferencia(Transfer transferencia) {
    	this.transferencias.add(transferencia);
    }
    
    public void executaTransferencias() {
    	if(!this.transferencias.isEmpty())
    		for(Transfer transfer: this.transferencias)
				this.transferirDinheiros(
					transfer.getRemetente(),
					transfer.getDestinatario(),
					transfer.getQuantia()
    			);
    }
    
    public void limpaTransferencias() {
    	for (JSONArray array : this.transferenciasSent) {
			array.clear();
		}
    	for (JSONArray array : this.transferenciasReceived) {
			array.clear();
		}
    }
    
    public void limpaTransfers() {
    	this.transferencias.clear();
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
    
    /*
     * Retorna o objeto Empresario do id requerido
     * 
     * @param id: Id do empresario requerido
     * @param chamadaFront: Caso a chamada de informações venha do
     * frontend. Nesse caso, diz-se que começou uma nova etapa, logo
     * marca "false" no vetor de jogadas.
     */
    public Empresario getEmpresarioById(int id, boolean chamadaFront) {
        for (Empresario emp : this.empresarios) {
            if (emp.getId() == id) {
            	if(chamadaFront) this.et1[id-1] = false;
                return emp;
            }
        }
        return null;
    }


    public void processaJogadaEmpresario(int idEmp, EmpresarioForm empForm) {
		this.et1[idEmp-1] = true;
    }

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
    			if(prod.getSetor() == "fertilizante") prod.setTipo("F. " + prod.getTipo());
    			if(prod.getSetor() == "agrotoxico") prod.setTipo("A. " + prod.getTipo());
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
    
    /*
     * Retorna o objeto Agricultor do id requerido
     * 
     * @param id: Id do agricultor requerido
     * @param chamadaFront: Caso a chamada de informações venha do
     * frontend. Nesse caso, diz-se que começou uma nova etapa, logo
     * marca "false" no vetor de jogadas.
     */
    public Agricultor getAgricultorById(int id, boolean chamadaFront) {
        for (Agricultor agr : this.agricultores) {
            if (agr.getId() == id) {
            	if(chamadaFront) this.et1[id-1] = false;
                return agr;
            }
        }
        return null;
    }
    
    public void processaJogadaAgricultor(int idAgr, AgricultorForm agrForm) {
    	int i = 1;
		for (Parcela parcela : agrForm.getParcelas()) {
			for (Produto produto : parcela.getProdutos()) {
				if(produto.getId() != 0)
					this.venda(
							idAgr,
							i,
							produto.getId(),
							produto.getPreco()
						);
			}
			i++;
		}
		this.et1[idAgr-1] = true;
		
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
            pessoa = getEmpresarioById(idEleito, false);
        }
        else if(tipo == 2) {
            pessoa = getAgricultorById(idEleito, false);
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

        //this.colocaArquivoLog("" + pessoa.getNome().substring(0, 1).toUpperCase() + pessoa.getNome().substring(1).toLowerCase() + " eleito como " + cargoString + " na cidade de " + cidade + "");
        //this.colocaLogCSV("eleicao" + this.separadorCSV + cargoString + this.separadorCSV + cidade + this.separadorCSV + pessoa.getNome());
        //System.out.println("\"" + pessoa.getNome().substring(0, 1).toUpperCase() + pessoa.getNome().substring(1).toLowerCase() + "\" eleito como " + cargoString + " na cidade de " + cidade + ".");

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
        this.limpaTransfers();
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

    //ARQUIVOS DE SAIDA
    @SuppressWarnings({ "unchecked" })
	private JSONObject setArquivoEmpJSON(Empresario emp, int etapa) {
    	JSONObject rodada = new JSONObject();
    	
    	rodada.put("rodada", this.rodada);
    	rodada.put("saldoAnterior", this.saldosAnteriores.get(emp.getId()-1));
    	rodada.put("produtividade", emp.getProdutividade());
    	rodada.put("imposto", emp.getImposto());
    	if(etapa == 2) rodada.put("multa", emp.getMulta());
    	
    	JSONObject transferencias = new JSONObject();
    	transferencias.put("enviado", this.transferenciasSent.get((emp.getId()-1)));
    	transferencias.put("recebido", this.transferenciasReceived.get((emp.getId()-1)));
    	rodada.put("transferencias", transferencias);
    	
    	switch(emp.getCidade()) {
	    	case "Atlantis":
	    		rodada.put("acoesUtilizadas", this.prefeitos.get(0).getAcoesUsadasJSON());
	    		break;
	    		
	    	case "Cidadela":
	    		rodada.put("acoesUtilizadas", this.prefeitos.get(1).getAcoesUsadasJSON());
	    		break;
    	}
    	
    	rodada.put("saldoAtual", emp.getSaldo());
    	rodada.put("poluicaoPessoal", emp.getPoluicao());
    	rodada.put("poluicaoCausadaMundo", (emp.getPoluicao()*1000));
    	rodada.put("poluicaoMundial", (this.poluicaoMundo*100));
    	
    	return rodada;
    	
    }

    @SuppressWarnings({ "unchecked" })
    private JSONObject setArquivoAgrJSON(Agricultor agr, int etapa) {
    	JSONObject rodada = new JSONObject();
    	
    	rodada.put("rodada", this.rodada);
    	rodada.put("saldoAnterior", this.saldosAnteriores.get(agr.getId()-1));
    	rodada.put("produtividade", agr.getProdutividade());
    	rodada.put("imposto", agr.getImposto());
    	if(etapa == 2) rodada.put("multa", agr.getMulta());
    	rodada.put("gastos", agr.getGastos());
    	    	
    	JSONObject transferencias = new JSONObject();
    	transferencias.put("enviado", this.transferenciasSent.get((agr.getId()-1)));
    	transferencias.put("recebido", this.transferenciasReceived.get((agr.getId()-1)));
    	rodada.put("transferencias", transferencias);
    	
    	switch(agr.getCidade()) {
	    	case "Atlantis":
	    		rodada.put("acoesUtilizadas", this.prefeitos.get(0).getAcoesUsadasJSON());
	    		break;
	    		
	    	case "Cidadela":
	    		rodada.put("acoesUtilizadas", this.prefeitos.get(1).getAcoesUsadasJSON());
	    		break;
    	}    	
    	
    	rodada.put("saldoAtual", agr.getSaldo());
    	rodada.put("poluicaoPessoal", agr.getPoluicao());
    	rodada.put("poluicaoCausadaMundo", (agr.getPoluicao()*1000));
    	rodada.put("poluicaoMundial", (this.poluicaoMundo*100));
    	
    	rodada.put("parcelas", agr.contentParcelaJSON());
    	
    	return rodada;
    	
    }

    @SuppressWarnings({ "unchecked", "unused" })
    private JSONObject setArquivoFisJSON(FiscalAmbiental fis, int etapa) {
    	JSONObject rodada = new JSONObject();
    	
    	rodada.put("saldoAnterior", this.saldosAnteriores.get(fis.getId()-1));
    	
    	JSONObject transferencias = new JSONObject();
    	transferencias.put("enviado", this.transferenciasSent.get((fis.getId()-1)));
    	transferencias.put("recebido", this.transferenciasReceived.get((fis.getId()-1)));
    	rodada.put("transferencias", transferencias);

    	rodada.put("pedidos", fis.getPedidos());
    	rodada.put("saldoAtual", fis.getSaldo());
    	rodada.put("poluicaoMundial", (this.poluicaoMundo*100));
    	
    	JSONArray empresarios = new JSONArray();
    	for(Empresario emp : this.empresarios) {
    		if(emp.getCidade() == fis.getCidade()) {
    			JSONObject empresario = new JSONObject();
    			empresario.put("poluicao", emp.getPoluicao());
    			empresario.put("produtividade", emp.getProdutividade());
    			if(etapa == 2)  empresario.put("multa", emp.getMulta());
    			empresario.put("nome", emp.getNome());
    			empresarios.add(empresario);
    		}
    	}
    	rodada.put("empresarios", empresarios);
    	
    	JSONArray agricultores = new JSONArray();
    	for(Agricultor agr : this.agricultores) {
    		if(agr.getCidade() == fis.getCidade()) {
    			JSONObject agricultor = new JSONObject();
    			agricultor.put("policaoMedia", agr.getPoluicao());
    			if(etapa == 2)  agricultor.put("multa", agr.getMulta());
    			agricultor.put("parcelas", agr.contentParcelaJSON());
    			agricultor.put("nome", agr.getNome());
        		agricultores.add(agricultor);
    		}
    	}
    	rodada.put("agricultores", agricultores);
    	
    	return rodada;
    	
    }

    @SuppressWarnings({ "unchecked", "unused" })
    private JSONObject setArquivoPrefJSON(Prefeito pref, int etapa) {
    	JSONObject rodada = new JSONObject();
    	
    	double impostos = 0;
    	double multas = 0;
    	
    	JSONArray empresarios = new JSONArray();
    	for(Empresario emp : this.empresarios) {
    		if(emp.getCidade() == pref.getCidade()) {
    			JSONObject empresario = new JSONObject();
    			empresario.put("poluicao", emp.getPoluicao());
    			empresario.put("produtividade", emp.getProdutividade());
    			empresario.put("imposto", emp.getImposto());
    			impostos += emp.getImposto();
    			if(etapa == 2) {
    				empresario.put("multa", emp.getMulta());
    				multas += emp.getMulta();
    			}
    			empresario.put("nome", emp.getNome());
    			empresarios.add(empresario);
    		}
    	}
    	
    	JSONArray agricultores = new JSONArray();
    	for(Agricultor agr : this.agricultores) {
    		if(agr.getCidade() == pref.getCidade()) {
    			JSONObject agricultor = new JSONObject();
    			agricultor.put("poluicaoMedia", agr.getPoluicao());
    			agricultor.put("imposto", agr.getImposto());
    			impostos += agr.getImposto();
    			if(etapa == 2) {
    				agricultor.put("multa", agr.getMulta());
    				multas += agr.getMulta();
    			}
    			agricultor.put("parcelas", agr.contentParcelaJSON());
    			agricultor.put("nome", agr.getNome());
        		agricultores.add(agricultor);
    		}
    	}
    	
    	rodada.put("saldoAnterior", this.saldosAnteriores.get(pref.getId()-1));
    	rodada.put("impostos", impostos);
    	if(etapa == 2) rodada.put("multas", multas);
    	
    	JSONObject transferencias = new JSONObject();
    	transferencias.put("enviado", this.transferenciasSent.get((pref.getId()-1)));
    	transferencias.put("recebido", this.transferenciasReceived.get((pref.getId()-1)));
    	rodada.put("transferencias", transferencias);

    	rodada.put("saldoAtual", pref.getSaldo());
    	rodada.put("poluicaoMundial", (this.poluicaoMundo*100));

    	rodada.put("empresarios", empresarios);
    	rodada.put("agricultores", agricultores);
    	
    	return rodada;
    	
    }

    @SuppressWarnings({ "unchecked", "unused" })
    private JSONObject setArquivoVerJSON(Vereador ver, int etapa) {
    	JSONObject rodada = new JSONObject();
    	
    	rodada.put("saldoAnterior", this.saldosAnteriores.get(ver.getId()-1));
    	
    	JSONObject transferencias = new JSONObject();
    	transferencias.put("enviado", this.transferenciasSent.get((ver.getId()-1)));
    	transferencias.put("recebido", this.transferenciasReceived.get((ver.getId()-1)));
    	rodada.put("transferencias", transferencias);
    	
    	if(etapa == 2) {
    		for(Prefeito pref : this.prefeitos) {
        		if(pref.getCidade() == ver.getCidade()) {
        			rodada.put("acoesAmbientaisUsadas", pref.getAcoesUsadasJSON());
        			rodada.put("impostosModificados", pref.getTaxasMudadasJSON());
        			break;
        		}
        	}
    	}

    	rodada.put("saldoAtual", ver.getSaldo());
    	rodada.put("poluicaoMundial", (this.poluicaoMundo*100));

    	
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
        
        if (etapa == 1) {

//            for (FiscalAmbiental fis : this.fiscais) {
//                fileContent = this.setArquivoFiscal(fis, etapa);
//                String arq = "arquivosResumo/r" + this.rodada + "e" + etapa + "Fis" + fis.getCidade() + ".txt";
//                try (PrintWriter esc = new PrintWriter(arq, "UTF-8")) {
//                    esc.println(fileContent);
//                    esc.close();
//                }
//            }
//            for (Prefeito pre : this.prefeitos) {
//                fileContent = this.setArquivoPrefeito(pre, etapa);
//                String arq = "arquivosResumo/r" + this.rodada + "e" + etapa + "Pre" + pre.getCidade() + ".txt";
//                try (PrintWriter esc = new PrintWriter(arq, "UTF-8")) {
//                    esc.println(fileContent);
//                    esc.close();
//                }
//            }
//            for (Vereador ver : this.vereadores) {
//                fileContent = this.setArquivoVereador(ver, etapa);
//                String arq = "arquivosResumo/r" + this.rodada + "e" + etapa + "Ver" + ver.getCidade() + ".txt";
//                try (PrintWriter esc = new PrintWriter(arq, "UTF-8")) {
//                    esc.println(fileContent);
//                    esc.close();
//                }
//            }
        }
        else {
            for (Empresario emp : this.empresarios) {
                this.escreveFinalArquivoJSON(
                		"arquivosResumo/empresario/"+ emp.getId() + ".json",
                		this.setArquivoEmpJSON(emp, etapa),
                		1,
                		emp.getNome()
                );
            }
            for (Agricultor agr : this.agricultores) {
                this.escreveFinalArquivoJSON(
                		"arquivosResumo/agricultor/" + agr.getId() + ".json",
                		this.setArquivoAgrJSON(agr, etapa),
                		2,
                		agr.getNome()
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
            }*/
        }

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
	public void escreveFinalArquivoJSON(String arquivo, JSONObject novaRodada, int papel, String nomePessoa) throws IOException {
        String fileName = arquivo;

        JSONParser parser = new JSONParser();
        JSONObject arquivoNovo = new JSONObject();
        JSONArray rodadasNovo = new JSONArray();
        
        if(papel > 3) {
        	if( !( (this.rodada == 1) && (this.etapa == 1) ) ) {
            	try (Reader reader = new FileReader(fileName)) {

            		JSONArray jsonArray = (JSONArray) parser.parse(reader);
            		  
//            		for (Object object : jsonArray) {
//            			rodadasNovo.add(object);
//    				}
//                   
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        	
        	// Colocar aqui pra adicionar nome e o array com etapa 1
        }
        else {
        	if( !( (this.rodada == 1) && (this.etapa == 2) ) ) {
            	try (Reader reader = new FileReader(fileName)) {

            		JSONObject jsonObject = (JSONObject) parser.parse(reader);
            		
            		JSONArray jsonArray = (JSONArray) jsonObject.get("rodadas");
            		
            		for (Object object : jsonArray) {
            			rodadasNovo.add(object);
    				}
            		
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        	// colocar aqui pra adicionar nome e array com rodada 1
        	rodadasNovo.add(novaRodada);
        	
        	arquivoNovo.put("nome", nomePessoa);
        	arquivoNovo.put("rodadas", rodadasNovo);
        }

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
            Empresario empresario = this.getEmpresarioById(idChamador, false);
            nomePessoaChamadora = empresario.getNome();
            pessoaChamadora = empresario;
        } else if (tipoPessoaChamadora == 2) {
            Agricultor agricultor = this.getAgricultorById(idChamador, false);
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
            Empresario empresario = this.getEmpresarioById(idRecebedor, false);
            nomePessoaRecebedora = empresario.getNome();
            pessoaRecebedora = empresario;
        } else if (tipoPessoaRecebedora == 2) {
            Agricultor agricultor = this.getAgricultorById(idRecebedor, false);
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
    	JSONObject aux = new JSONObject();
    	aux.put("nome", nomePessoaRecebedora);
    	aux.put("valor", quantity);
    	this.transferenciasSent.get(pessoaChamadora.getId()-1).add(aux);
    	aux.clear();
    	aux.put("nome", nomePessoaChamadora);
    	aux.put("valor", quantity);
    	this.transferenciasReceived.get(pessoaRecebedora.getId()-1).add(aux);
    }
    
    public void venda(int idAgr, int numParcela, int idProduto, int preco) {
    	int tipoProduto = this.getTipoProdutoById(idProduto);
    	Agricultor agricultor = this.getAgricultorById(idAgr, false);
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
    
    public void finalizarEtapa() throws IOException {
    	this.fimEtapa = true;
	    if(this.etapa == 1) {
	    	// Realizar eleição se rodada divisivel por 2
	    	
	    	for (int i = 0; i < et1.length; i++) {
				this.et1[i] = true;
			}
	    	//this.poluicaoMundo += this.calcularPoluicaoCausada();
	        
	        //this.cobrarImpostos();
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

	    	this.executaTransferencias();

            this.setArquivosByRole(this.etapa);
            
	    	this.rodada++;

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

	    	this.limpaTransfers();
	    	this.limpaTransferencias();
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
    
    public List<PessoaModel> getInfoPessoasByClasse(int classe){
    	List<PessoaModel> pessoas = new ArrayList<PessoaModel>();
    	if(classe == 1) {
    		for (Empresario emp : this.empresarios) {
        		pessoas.add(new PessoaModel(emp.getNome(), emp.getId()));
        	}
    	}
    	else if(classe == 2) {
    		for (Agricultor agricultor : this.agricultores) {
        		PessoaModel aux = new PessoaModel(agricultor.getNome(), agricultor.getId());
        		pessoas.add(aux);
        	}
    	}
    	else if(classe == 3) {
    		for (FiscalAmbiental fis : this.fiscais) {
        		pessoas.add(new PessoaModel(fis.getNome(), fis.getId()));
        	}
    	}
    	else if(classe == 4) {
    		for (Prefeito pref : this.prefeitos) {
        		pessoas.add(new PessoaModel(pref.getNome(), pref.getId()));
        	}
    	}
    	else if(classe == 5) {
    		for (Vereador ver : this.vereadores) {
        		pessoas.add(new PessoaModel(ver.getNome(), ver.getId()));
        	}
    	}
    	
    	if(!pessoas.isEmpty()) return pessoas;
    	else return null;
    }
    
    public List<PessoaModel> getInfoPessoas(int etapa){
    	List<PessoaModel> pessoas = new ArrayList<PessoaModel>();
    	if(etapa == 1 || etapa == 0) {
    		for (Empresario emp : this.empresarios) {
        		pessoas.add(new PessoaModel(emp.getNome(), emp.getId()));
        	}
        	for (Agricultor agr : this.agricultores) {
        		pessoas.add(new PessoaModel(agr.getNome(), agr.getId()));
        	}
    	}
    	if(etapa == 2 || etapa == 0) {
    		for (FiscalAmbiental fis : this.fiscais) {
        		pessoas.add(
        				new PessoaModel(
    							("Fiscal " + fis.getNome() + " (" + fis.getCidade() + ")"),
    							fis.getId()
    						)
    					);
        	}
        	for (Prefeito pref : this.prefeitos) {
        		pessoas.add(
        				new PessoaModel(
        						("Prefeito " + pref.getNome() + " (" + pref.getCidade() + ")"),
        						pref.getId()
        					)
        				);
        	}
        	for (Vereador ver : this.vereadores) {
        		pessoas.add(
        				new PessoaModel(
        						("Vereador " + ver.getNome() + " (" + ver.getCidade() + ")"),
        						ver.getId()
        					)
        				);
        	}
    	}
    	    	
    	return pessoas;
    }
    
    public JSONObject getFilePessoaByIdJSON(int id) throws IOException {
    	String fileName = "";
    	
    	switch(this.getTipoPessoaById(id)) {
    	case 1:
    		fileName = "arquivosResumo/empresario/" + id + ".json";
    		break;
    	case 2:
    		fileName = "arquivosResumo/agricultor/" + id + ".json";
            break;
    	case 3:
    		fileName = "arquivosResumo/fiscal/" + id + ".json";
    		break;
    	case 4:
	    	fileName = "arquivosResumo/prefeito/" + id + ".json";
	    	break;
    	case 5:
    		fileName = "arquivosResumo/vereador/" + id + ".json";
            break;
    	}
        
        JSONObject arquivo = new JSONObject();
        
        try (Reader reader = new FileReader(fileName)) {
        	
        	arquivo = (JSONObject) new JSONParser().parse(reader);
        	
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
		
        return arquivo;
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
    	Agricultor agr = getAgricultorById(idAgr, false);
    	
    	int cidade = (agr.getCidade().equals("Atlantis")) ? 0 : 1;
    	
    	this.fiscais.get(cidade).adicionaPedido(agr.getNome(), pedido);
    }
    
    public void adicionaOrcamentoById(Venda venda) {
    	venda.setNomeAgr(this.agricultores.get(venda.getIdAgr()-1-4).getNome());
    	venda.setNomeEmp(this.empresarios.get(venda.getIdEmp()-1).getNome());
    	
    	venda.setNomeProduto(
    			this.empresarios.get(venda.getIdEmp()-1).getTipoProdutoById(venda.getIdProduto())
    	);
    	
    	this.vendas.get(venda.getIdAgr()-1).add(venda);
    }
    
    public List<Venda> getOrcamentos(int idAgr){
    	return this.vendas.get(idAgr-1);
    }
    
    public void adicionaVendaById(Venda venda) {
    	this.vendas.get(venda.getIdEmp()-1).add(venda);
    }
    
    public void removeOrcamentoById(int idAgr, int idEmp, int idOrcamento) {
    	for (Venda orcamento : this.vendas.get(idAgr-1)) {
			if(orcamento.getIdOrcamento() == idOrcamento && orcamento.getIdEmp() == idEmp) {
				this.vendas.get(idAgr-1).remove(orcamento);
				break;
			}
		}
    	//this.vendas.get(venda.getIdAgr()-1).remove(venda.getIdJava());
    }
    
    public List<Venda> getVendas(int idEmp){
    	return this.vendas.get(idEmp-1);
    }
    
    public void limpaVendas() {
    	this.vendas.forEach(
			x -> x.clear()
    	);
    }
    
}
