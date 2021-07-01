package com.gorim.motorJogo;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.gorim.model.GameOver;
import com.gorim.model.MundoModel;
import com.gorim.model.PessoaModel;
import com.gorim.model.ProdutoSimplifiedModel;
import com.gorim.model.ResumoJogador;
import com.gorim.model.db.User;
import com.gorim.model.forms.AgricultorForm;
import com.gorim.model.forms.EmpresarioForm;
import com.gorim.model.forms.FiscalAmbientalForm;
import com.gorim.model.forms.Imposto;
import com.gorim.model.forms.Multa;
import com.gorim.model.forms.Parcela;
import com.gorim.model.forms.PrefeitoForm;
import com.gorim.model.forms.Produto;
import com.gorim.model.forms.SeloVerde;
import com.gorim.model.forms.SugestaoVereador;
import com.gorim.model.forms.Transfer;
import com.gorim.model.forms.Venda;
import com.gorim.service.UserRepository;

public class Mundo {
	// CONSTANTES
	private static final int NUM_CIDADES = 2; // numero de cidades
	private static final int NUM_PAPEIS_ELEGIVEIS = 6; // numero de papeis elegiveis
	private static final double POLUICAO_INICIAL = 0.2; // poluição inicial do mundo
	private static final int QNTD_EMPRESARIOS = 4; // quantidade de empresarios no jogo 
	private static final int QNTD_PARCELAS = 6; // quantidade de parcelas para cada agricultor
	
	// VARIAVEIS DA CLASSE
	private int idJogo;
	
    private int idPessoa;
    private int idParcelas;
    private int qntdParcelasPorAgricultor;
    private int quantidadeJogadores;
    private int rodada;
    private int etapa;
    private double poluicaoMundo;

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
    private List<List<SugestaoVereador>> sugestoesVereador;
    private ArrayList<Transfer> transferencias;
    

	private boolean[] et1;
	private boolean[] et2;
	private boolean fimEtapa;
	private boolean flagNaoJogadoresEtapaDois;
	
	private int[] votacaoFiscal;
	private int[] votacaoPrefeito;
	private int[] votacaoVereador;
	
	private String storePath;
	
	// persistir personagem no banco
	private UserRepository userRepository;
	
	// serviço para colocar mensagens no log (systemlogs/console.log)
	private Logger logger = LoggerFactory.getLogger(Mundo.class);

    @SuppressWarnings("unchecked")
	public Mundo(int idJogo, int quantidadeJogadores, UserRepository userRepository, String filesAbsolutePath) {
        this.idPessoa = 1;
        this.idParcelas = 1;
        this.qntdParcelasPorAgricultor = QNTD_PARCELAS;
        this.quantidadeJogadores = quantidadeJogadores;
        this.rodada = 1;
        this.etapa = 1;
        this.poluicaoMundo = POLUICAO_INICIAL;

        this.empresarios = new ArrayList<>();
        this.agricultores = new ArrayList<>();
        this.fiscais = new ArrayList<>();
        this.prefeitos = new ArrayList<>();
        this.vereadores = new ArrayList<>();

        this.separadorCSV = ";";
        this.saldosAnteriores = new ArrayList<>();
        
        this.acoesAmbientaisExecutadas = new ArrayList<>(NUM_CIDADES);
        
        this.transferencias = new ArrayList<>();
        
		this.et1 = new boolean[this.quantidadeJogadores];
		this.et2 = new boolean[NUM_PAPEIS_ELEGIVEIS];
		this.fimEtapa = false;
		this.flagNaoJogadoresEtapaDois = false;
		
		this.votacaoFiscal = new int[this.quantidadeJogadores];
		this.votacaoPrefeito = new int[this.quantidadeJogadores];
		this.votacaoVereador = new int[this.quantidadeJogadores];
		
		this.userRepository = userRepository;
		
		this.idJogo = idJogo;
		this.storePath = filesAbsolutePath + "jogos/" + idJogo;
		
		String fileName = this.storePath + "/saves/config.json";
		
    	JSONObject config = new JSONObject();
    	config.put("idJogo", idJogo);
    	config.put("quantidadeJogadores", quantidadeJogadores);

    	File file = new File(fileName);
    	if(!file.exists()) {
    		file.getParentFile().mkdirs();
    		try {
				file.createNewFile();
			} catch (IOException e) {
				logger.error("Mundo.Mundo: Exception : " + e.getClass() + " : " + e.getMessage());
				e.printStackTrace();
			}
    	}
    	

        try (FileWriter fileW = new FileWriter(fileName)) {
            fileW.write(config.toJSONString());
        } catch (IOException e) {
			logger.error("Mundo.Mundo: Exception : " + e.getClass() + " : " + e.getMessage());
            e.printStackTrace();
        }
        
    }
    
    public MundoModel getInfoMundo() {
    	return new MundoModel(
				this.rodada,
				this.etapa,
				this.poluicaoMundo,
				this.idJogo,
				this.calculaProdutividadeMundo(),
				this.quantidadeJogadores,
				this.getNomeEleitos()
		);
    }
    
    public int getIdJogo() {
    	return this.idJogo;
    }
    
	public void iniciarJogo() throws IOException {
		// cria username pro MESTRE
		this.userRepository.save(new User(this.idJogo, 0, ("mestre" + this.idJogo), "{noop}mestre0"));
		
		//Cria empresarios e seus usernames
    	String[] nomes = {"EmpSem", "EmpFer", "EmpMaq", "EmpAgr"};
    	String setor;
        for (int i = 1; i < 5; i++) {
            if (i == 1) {
                setor = ConstantesGorim.c_Semente;
            } else if (i == 2) {
                setor = ConstantesGorim.c_Fertilizante;
            } else if (i == 3) {
                setor = ConstantesGorim.c_Maquina;
            } else {
                setor = ConstantesGorim.c_Agrotoxico;
            }
            
            String nome = nomes[i-1];
            
            this.userRepository.save(new User(this.idJogo, this.idPessoa, (nome + this.idJogo), ("{noop}" + nome + this.idPessoa)));
            
            this.criaEmpresario(setor, nome);
        }
        
        //Cria agricultores e seus usernames
        String nome;
        int numNome = 1;
        for (int i = 0; i < (this.quantidadeJogadores-QNTD_EMPRESARIOS); i++) {
        	int idPessoaAux = this.idPessoa;
            if (i % 2 == 0) {
                nome = ConstantesGorim.c_PrefixoNomesPessoasCidadeA + numNome;
                this.criaAgricultor(nome, ConstantesGorim.c_CidadeA);
            } else {
                nome = ConstantesGorim.c_PrefixoNomesPessoasCidadeB + numNome;
                this.criaAgricultor(nome, ConstantesGorim.c_CidadeB);
                numNome++;
            }
            

            this.userRepository.save(new User(this.idJogo, idPessoaAux, (nome + this.idJogo), ("{noop}" + nome + idPessoaAux)));
        }
        
        // Cria cargos políticos e seus usernames
        List<User> userNamesPoliticos = this.criaCargosPoliticos();
        for (User user : userNamesPoliticos) {
            this.userRepository.save(user);
		}
        
        // Faz eleição aleatória
        this.primeiraEleicao();
        
        // Inicia Array auxiliar de orçamento
        this.vendas = new ArrayList<>();
        while(this.vendas.size() < this.quantidadeJogadores)
        	this.vendas.add(new ArrayList<Venda>());
        
        // Inicia Arrays de sugestões dos vereadores
        this.sugestoesVereador = new ArrayList<>();
        int tamanho = this.prefeitos.size() + this.vereadores.size();
        while(this.sugestoesVereador.size() < tamanho)
        	this.sugestoesVereador.add(new ArrayList<SugestaoVereador>());
        
        // Inicia Arrais de treansferências
        int qnt = this.quantidadeJogadores + 6;
        this.transferenciasSent = new ArrayList<JSONArray>(qnt);
        this.transferenciasReceived = new ArrayList<JSONArray>(qnt);

        for (int i = 0; i < qnt; i++) {
            this.transferenciasSent.add(new JSONArray());
            this.transferenciasReceived.add(new JSONArray());
        }
        
        // Inicia buffer para ações ambientais a serem computadas na rodada
        while(this.acoesAmbientaisExecutadas.size() < 2)
        	this.acoesAmbientaisExecutadas.add(new JSONArray());

        // Inicia array de votação
        this.limpaVotacao();
        
        // Inicia array de saldos anteriores (para resumos)
        this.criaHistoricoSaldos();
        
        // Inicia array de verificação de quem já terminou a jogada
        this.limpaEts(true);
        
        // Inicia arquivos de log
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		String data = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss").format(timestamp.getTime());
		this.colocaArquivoLog("\n===============================================\n" + data + "\nRodada:" + this.rodada);
		this.colocaLogCSV("rodada " + this.rodada);
        
    }

	public GameOver finalizarJogo() throws IOException {
		List<ResumoJogador> resumos = new ArrayList<ResumoJogador>();
		
		for (Empresario emp : this.empresarios) 
			resumos.add(new ResumoJogador(emp.nome, emp.saldo, emp.cidade, "Empresário"));
		
		for (Agricultor agr : this.agricultores) 
			resumos.add(new ResumoJogador(agr.nome, agr.saldo, agr.cidade, "Agricultor"));
		
		GameOver gameoverData = new GameOver(this.poluicaoMundo, this.rodada, this.etapa, this.idJogo, resumos);
		
		String fileName = this.storePath + "/gameOverData.json";
		
		File file = new File(fileName);
    	if(!file.exists()) {
    		file.getParentFile().mkdirs();
    		file.createNewFile();
    	}
    	
        try (FileWriter fileW = new FileWriter(fileName)) {
            fileW.write(gameoverData.toJSONString());
        } catch (IOException e) {
            e.printStackTrace();
        }
		
		return gameoverData;
	}
	
    public void primeiraEleicao() throws IOException {
    	Random rand = new Random();
    	int seed = (int) Math.floor(this.quantidadeJogadores/2);

    	// Eleger em Atlantis
    	int idFis;
    	do{
    		idFis = rand.nextInt(seed)*2 + 1;
    	} while( (idFis < 1) && (idFis > this.quantidadeJogadores) );
    	
    	int idPref;
    	do {
    		idPref = rand.nextInt(seed)*2 + 1;
    	} while((idPref == idFis) && (idPref < 1) && (idPref > this.quantidadeJogadores));
    	
    	int idVer;
    	do {
    		idVer = rand.nextInt(seed)*2 + 1;
    	} while( ((idVer == idFis) || (idVer == idPref)) && (idVer < 1) && (idVer > this.quantidadeJogadores) );
    	
    	System.out.println("Mundo.primeiraEleicao: Atlantis: idFis=" + idFis);
    	this.eleger(idFis, 0);
    	System.out.println("Mundo.primeiraEleicao: Atlantis: idPref=" + idPref);
    	this.eleger(idPref, 1);
    	System.out.println("Mundo.primeiraEleicao: Atlantis: idVer=" + idVer);
    	this.eleger(idVer, 2);
    	
    	// Eleger em Cidadela
    	do {
    		idFis = (1+rand.nextInt(seed))*2;
    	} while ( (idFis < 1) && (idFis > this.quantidadeJogadores) );
    	
    	do {
    		idPref = (1+rand.nextInt(seed))*2;
    	} while((idPref == idFis) && (idPref < 1) && (idPref > this.quantidadeJogadores));
    	
    	do {
    		idVer = (1+rand.nextInt(seed))*2;
    	} while( ((idVer == idFis) || (idVer == idPref)) && (idVer < 1) && (idVer > this.quantidadeJogadores) );
    	
    	System.out.println("Mundo.primeiraEleicao: Cidadela: idFis=" + idFis);
    	this.eleger(idFis, 0);
    	System.out.println("Mundo.primeiraEleicao: Cidadela: idPref=" + idPref);
    	this.eleger(idPref, 1);
    	System.out.println("Mundo.primeiraEleicao: Cidadela: idVer=" + idVer);
    	this.eleger(idVer, 2);
    	
    }
    
    public void limpaVotacao() {
        for (int i=0; i < this.votacaoFiscal.length; i++) {
        	this.votacaoFiscal[i] = 0;
        	this.votacaoPrefeito[i] = 0;
        	this.votacaoVereador[i] = 0;
        }
    }
    
    /*
     * @param idPessoaVotada: id da pessoa que conseguiu voto
     * @param cargo: 0 => Fiscal Ambiental; 1 => Prefeito; 2 => Vereador
     */
    public void contaVoto(int idPessoaVotada, int cargo) {
    	if(cargo == 0) this.votacaoFiscal[idPessoaVotada-1]++;
    	else if(cargo == 1) this.votacaoPrefeito[idPessoaVotada-1]++;
    	else if(cargo == 2) this.votacaoVereador[idPessoaVotada-1]++;
    }
    
    /*
     * Vê qual foi o mais votado. Se teve empate, vê se o mais votado (sem empate) dos outros
     * papeis esta entre os empatados. Se sim, retira esse id e faz um random pra decidir quem
     * é o novo eleito.
     * Exemplo: Empatados para Fiscal: 1, 3 e 5. Mais votado Prefeito: 3. Mais votado Vereador: 7.
     * Logo, para empatados para o cargo de Fiscal: 1 e 5 e faz random para decidir quem vence.
     */
    public void processaEleicao() throws IOException {
    	int idEleitoFiscal = 0;
    	int i = 1;
    	int contagemMaxima = 0;
    	ArrayList<Integer> empateFiscal = new ArrayList<Integer>();
    	
    	for (int votosCandidato : this.votacaoFiscal) {
			if(votosCandidato > contagemMaxima) {
				contagemMaxima = votosCandidato;
				idEleitoFiscal = i;
				empateFiscal.clear();
			}
			else if(votosCandidato == contagemMaxima) {
				empateFiscal.add(i);
			}
			i++;
		}
    	
    	int idEleitoPrefeito = 0;
    	i = 1;
    	contagemMaxima = 0;
    	ArrayList<Integer> empatePrefeito = new ArrayList<Integer>();
    	
    	for (int votosCandidato : this.votacaoPrefeito) {
			if(votosCandidato > contagemMaxima) {
				contagemMaxima = votosCandidato;
				idEleitoPrefeito = i;
				empatePrefeito.clear();
			}
			else if(votosCandidato == contagemMaxima) {
				empatePrefeito.add(votosCandidato);
			}
			i++;
		}
    	
    	int idEleitoVereador = 0;
    	i = 1;
    	contagemMaxima = 0;
    	ArrayList<Integer> empateVereador = new ArrayList<Integer>();
    	
    	for (int votosCandidato : this.votacaoVereador) {
			if(votosCandidato > contagemMaxima) {
				contagemMaxima = votosCandidato;
				idEleitoVereador = i;
				empateVereador.clear();
			}
			else if(votosCandidato == contagemMaxima) {
				empateVereador.add(votosCandidato);
			}
			i++;
		}
    	
		Random rand = new Random();
    	
    	if(empateFiscal.size() > 0) {
    		if(empatePrefeito.size() == 0) if(empateFiscal.contains(idEleitoPrefeito)) empateFiscal.remove(empateFiscal.indexOf(idEleitoPrefeito));
    		if(empateVereador.size() == 0) if(empateFiscal.contains(idEleitoVereador)) empateFiscal.remove(empateFiscal.indexOf(idEleitoVereador));
    		
    		empateFiscal.add(idEleitoFiscal);
    		
    		idEleitoFiscal = empateFiscal.get(rand.nextInt(empateFiscal.size()));    		
    	}
    	
    	if(empatePrefeito.size() > 0) {
    		if(empatePrefeito.contains(idEleitoFiscal)) empatePrefeito.remove(empatePrefeito.indexOf(idEleitoFiscal));
    		if(empateVereador.size() == 0) if(empatePrefeito.contains(idEleitoVereador)) empatePrefeito.remove(empatePrefeito.indexOf(idEleitoVereador));
    		
    		empatePrefeito.add(idEleitoPrefeito);
    		
    		idEleitoPrefeito = empatePrefeito.get(rand.nextInt(empatePrefeito.size()));
    	}
    	
    	if(empateVereador.size() > 0) {
    		if(empateVereador.contains(idEleitoFiscal)) empateVereador.remove(empateVereador.indexOf(idEleitoFiscal));
    		if(empateVereador.contains(idEleitoPrefeito)) empateVereador.remove(empateVereador.indexOf(idEleitoPrefeito));
    		
    		empateVereador.add(idEleitoVereador);
    		
    		idEleitoVereador = empateVereador.get(rand.nextInt(empateVereador.size()));    		
    	}
    	
    	this.eleger(idEleitoFiscal, 0);
    	this.eleger(idEleitoPrefeito, 1);
    	this.eleger(idEleitoVereador, 2);
    }
    
    public int getRodada() {
    	return this.rodada;
    }
    
    public int getEtapa() {
    	return this.etapa;
    }
    
    public void changeFlagFimEtapa() {
    	this.fimEtapa = !this.fimEtapa;
    	if(this.etapa == 2) this.flagNaoJogadoresEtapaDois = true;
    	else this.flagNaoJogadoresEtapaDois = false;
    }
	
	@SuppressWarnings("unused")
	private void setJaJogou(int tipoJogador, int idJogador) {
		if(tipoJogador < 3) this.et1[idJogador-1] = true;
		else this.et2[idJogador-this.quantidadeJogadores] = true;
	}
	
	private void limpaEts(boolean valorDefault) {
		int i = 0;
		while(i < this.et1.length) {
			this.et1[i] = valorDefault;
			i++;
		}

		i = 0;
		while(i < this.et2.length) {
			this.et2[i] = valorDefault;
			i++;
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
		
		if(etapa == 2 && situacaoEtapa == 0 && !this.flagNaoJogadoresEtapaDois) situacaoEtapa = 1;
		
		
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
    
    public String[] getNomeEleitos() {
    	String[] nomes = {"", "", "", "", "", ""};
    	
    	int i = 0;
    	for (FiscalAmbiental fis : this.fiscais) {
			nomes[i] = "Fiscal " + fis.getNome() + " (" + fis.getCidade() + ")";
			i++;
		}
    	for (Prefeito pref : this.prefeitos) {
			nomes[i] = "Prefeito " + pref.getNome() + " (" + pref.getCidade() + ")";
			i++;
		}
    	for (Vereador ver : this.vereadores) {
			nomes[i] = "Vereador " + ver.getNome() + " (" + ver.getCidade() + ")";
			i++;
		}
    	
    	return nomes;
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
        if (setor.equals(ConstantesGorim.c_Semente) || setor.equals(ConstantesGorim.c_Maquina)) {
            if (setor.equals(ConstantesGorim.c_TipoSementeC)) {
                emp = new Empresario(this.idPessoa, 0, nome, ConstantesGorim.c_CidadeA);
            } else {
                emp = new Empresario(this.idPessoa, 2, nome, ConstantesGorim.c_CidadeA);
            }
        } else {
            if (setor.equals(ConstantesGorim.c_Fertilizante)) {
                emp = new Empresario(this.idPessoa, 1, nome, ConstantesGorim.c_CidadeB);
            } else {
                emp = new Empresario(this.idPessoa, 3, nome, ConstantesGorim.c_CidadeB);
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
    			if(prod.getSetor() == ConstantesGorim.c_Fertilizante) prod.setTipo("F. " + prod.getTipo());
    			if(prod.getSetor() == ConstantesGorim.c_Agrotoxico) prod.setTipo("A. " + prod.getTipo());
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
    	logger.info("Mundo.daAgricultor: idAgr=" + idAgr + "; Payload=" + agrForm.toString());
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
            fis = new FiscalAmbiental(this.idPessoa, nomeEleito, ConstantesGorim.c_CidadeA, idEleito);
        } else {
            fis = new FiscalAmbiental(this.idPessoa, nomeEleito, ConstantesGorim.c_CidadeB, idEleito);
        }
        this.fiscais.add(fis);
        this.idPessoa++;
    }

    public FiscalAmbiental getFiscalById(int id, boolean chamadaFront) {
        for (FiscalAmbiental fis : this.fiscais) {
            if (fis.getId() == id) {
            	if(chamadaFront) this.et2[fis.getId() - this.quantidadeJogadores - 1] = false;
                return fis;
            }
        }
        return null;
    }
    
    public void processaJogadaFiscal(int idFis, FiscalAmbientalForm fisForm) throws IOException {
    	logger.info("Mundo.daAgricultor: idFis=" + idFis + "; Payload=" + fisForm.toString());
    	this.et2[idFis - this.quantidadeJogadores - 1] = true;
    	
    	FiscalAmbiental fis = this.fiscais.get(idFis - this.quantidadeJogadores - 1);
    	int cidade = idFis - this.quantidadeJogadores - 1;
    	if(fisForm.getMultas() != null && fisForm.getMultas().length > 0) {
    		for (Multa multa: fisForm.getMultas()) {
                int tipoMultado = getTipoPessoaById(multa.getIdPessoa());
        		String nomeMultado = "";
        		
        		double novaMulta = (double) 0;
        		
                if (tipoMultado == 1) {
                    Empresario multado = getEmpresarioById(multa.getIdPessoa(), false);
                    novaMulta = fis.multar(multado, this.prefeitos.get(cidade), multa.getTipo());
                    nomeMultado = multado.getNome();
                } else if (tipoMultado == 2) {
                	Agricultor multado = getAgricultorById(multa.getIdPessoa(), false);
                    novaMulta = fis.multar(multado, this.prefeitos.get(cidade), multa.getTipo());
                    nomeMultado = multado.getNome();
                }
                this.colocaArquivoLog("Fiscal " + fis.getNome() + " multou a pessoa de nome " + nomeMultado + " em D$ " + novaMulta + "");
                this.colocaLogCSV("multa" + this.separadorCSV + fis.getNome() + this.separadorCSV + nomeMultado + this.separadorCSV + novaMulta);
    		}
    	}
    	if(fisForm.getSelosVerde() != null && fisForm.getSelosVerde().length > 0) {
    		for (SeloVerde seloVerde: fisForm.getSelosVerde()) {
        		Agricultor agr = this.getAgricultorById(seloVerde.getIdAgr(), false);
        		for (int parcela : seloVerde.getParcelas()) {
            		fis.setSeloVerde(agr, parcela, seloVerde.isAtribuir());
    				if (seloVerde.isAtribuir()) {
    					this.colocaArquivoLog("" + fis.getNome() + " deu Selo Verde para o Agricultor " + agr.getNome() + " na Parcela " + parcela + "");
    					this.colocaLogCSV("deu selo" + this.separadorCSV + fis.getNome() + this.separadorCSV + agr.getNome() + this.separadorCSV + parcela);
    				}
    				else {
    					this.colocaArquivoLog("" + fis.getNome() + " tirou o Selo Verde do Agricultor " + agr.getNome() + " na Parcela " + parcela + "");
    					this.colocaLogCSV("tirou selo" + this.separadorCSV + fis.getNome() + this.separadorCSV + agr.getNome() + this.separadorCSV + parcela);
    				}
    			}
    		}
    	}
    }

    /**
     * Cargos Politicos
     */
    public List<User> criaCargosPoliticos() {
    	List<User> loginData = new ArrayList<User>();

		String preA = ConstantesGorim.c_PrefixoNomesPessoasCidadeA;
		String preB = ConstantesGorim.c_PrefixoNomesPessoasCidadeB;
    	
        this.fiscais.add(0, new FiscalAmbiental(this.idPessoa, "", ConstantesGorim.c_CidadeA, 0));
        loginData.add(
        		new User(this.idJogo, this.idPessoa, ("Fis" + preA + this.idJogo), ("{noop}" + "Fis" + preA + this.idPessoa))
        );
        this.idPessoa++;
        
        this.fiscais.add(1, new FiscalAmbiental(this.idPessoa, "", ConstantesGorim.c_CidadeB, 0));
        loginData.add(
        		new User(this.idJogo, this.idPessoa, ("Fi" + preB + this.idJogo), ("{noop}" + "Fis" + preB + this.idPessoa))
        );
        this.idPessoa++;
        
        this.prefeitos.add(0, new Prefeito(this.idPessoa, "", ConstantesGorim.c_CidadeA));
        loginData.add(
        		new User(this.idJogo, this.idPessoa, ("Pref" + preA + this.idJogo), ("{noop}" + "Pref" + preA + this.idPessoa))
        );
        this.idPessoa++;
        
        this.prefeitos.add(1, new Prefeito(this.idPessoa, "", ConstantesGorim.c_CidadeB));
        loginData.add(
        		new User(this.idJogo, this.idPessoa, ("Pref" + preB + this.idJogo), ("{noop}" + "Pref" + preB + this.idPessoa))
        );
        this.idPessoa++;
        
        this.vereadores.add(0, new Vereador(this.idPessoa, "", ConstantesGorim.c_CidadeA, 0));
        loginData.add(
        		new User(this.idJogo, this.idPessoa, ("Ver" + preA + this.idJogo), ("{noop}" + "Ver" + preA + this.idPessoa))
        );
        this.idPessoa++;
        
        this.vereadores.add(1, new Vereador(this.idPessoa, "", ConstantesGorim.c_CidadeB, 0));
        loginData.add(
        		new User(this.idJogo, this.idPessoa, ("Ver" + preB + this.idJogo), ("{noop}" + "Ver" + preB + this.idPessoa))
        );
        this.idPessoa++;
        
        return loginData;
    }

    public boolean eleger(int idEleito, int cargo) throws IOException {
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
                cidade = ConstantesGorim.c_CidadeA;
            } else {
                this.fiscais.get(1).eleger(pessoa.getId(), pessoa.getNome());
                cidade = ConstantesGorim.c_CidadeB;
            }
            cargoString = "Fiscal";
        }
        else if (cargo == 1) { // Prefeito
            if (idEleito % 2 != 0) {
                this.prefeitos.get(0).eleger(pessoa.getId(), pessoa.getNome());
                cidade = ConstantesGorim.c_CidadeA;
            } else {
                this.prefeitos.get(1).eleger(pessoa.getId(), pessoa.getNome());
                cidade = ConstantesGorim.c_CidadeB;
            }
            cargoString = "Prefeito";
        }
        else if (cargo == 2){
            if (idEleito % 2 != 0) {
                this.vereadores.get(0).eleger(pessoa.getId(), pessoa.getNome());
                cidade = ConstantesGorim.c_CidadeA;
            } else {
                this.vereadores.get(1).eleger(pessoa.getId(), pessoa.getNome());
                cidade = ConstantesGorim.c_CidadeB;
            }
            cargoString = "Vereador";
        }

        this.colocaArquivoLog("" + pessoa.getNome().substring(0, 1).toUpperCase() + pessoa.getNome().substring(1).toLowerCase() + " eleito como " + cargoString + " na cidade de " + cidade + "");
        this.colocaLogCSV("eleicao" + this.separadorCSV + cargoString + this.separadorCSV + cidade + this.separadorCSV + pessoa.getNome());

        return true;
    }

    /**
     * Metodos referentes a classe de Prefeito
     */
    public Prefeito getPrefeitoById(int id, boolean chamadaFront) {
        for (Prefeito pref : this.prefeitos) {
            if (pref.getId() == id) {
            	if(chamadaFront) this.et2[pref.getId() - this.quantidadeJogadores - 1] = false;
                return pref;
            }
        }
        return null;
    }
    
    public void processaJogadaPrefeito(int idPref, PrefeitoForm prefForm) throws IOException {
    	logger.info("Mundo.daAgricultor: idPref=" + idPref + "; Payload=" + prefForm.toString());
    	this.et2[idPref - this.quantidadeJogadores - 1] = true;
		Prefeito pref = this.prefeitos.get(idPref - this.quantidadeJogadores - 2 - 1);
    	for (Imposto imposto : prefForm.getImpostos()) {
    		double novaTaxa = 0;
    		if (imposto.getTipo() == 1) {
    		    if (imposto.getTaxa().equals("B")) {
    		        novaTaxa = (double) 5;
    		    } else if (imposto.getTaxa().equals("M")) {
    		        novaTaxa = (double) 10;
    		    } else if (imposto.getTaxa().equals("A")) {
    		        novaTaxa = (double) 15;
    		    }
    		} else if (imposto.getTipo() == 2) {
    		    if (imposto.getTaxa().equals("B")) {
    		        novaTaxa = (double) 0.05;
    		    } else if (imposto.getTaxa().equals("M")) {
    		        novaTaxa = (double) 0.1;
    		    } else if (imposto.getTaxa().equals("A")) {
    		        novaTaxa = (double) 0.15;
    		    }
    		} else if (imposto.getTipo() == 3) {
    		    if (imposto.getTaxa().equals("B")) {
    		        novaTaxa = (double) 0.25;
    		    } else if (imposto.getTaxa().equals("M")) {
    		        novaTaxa = (double) 0.30;
    		    } else if (imposto.getTaxa().equals("A")) {
    		        novaTaxa = (double) 0.35;
    		    }
    		}
    		pref.mudarTaxa(imposto.getTipo(), novaTaxa);
    		
    		String taxaString = (imposto.getTipo() == 1) ? "" + novaTaxa + "" : "" + (novaTaxa * 100) + "%";
            this.colocaArquivoLog("Prefeito " + pref.getNome() + " trocou a taxa do tipo " + imposto.getTipo() + " para " + taxaString + "");
            this.colocaLogCSV("troca taxa" + this.separadorCSV + pref.getNome() + this.separadorCSV + imposto.getTipo() + this.separadorCSV + taxaString);
		}
    	
    	for (int acao : prefForm.getIdAcoesAmbientais()) {
			pref.setUsarAcao(acao, this.poluicaoMundo);
			this.colocaArquivoLog("Prefeito " + pref.getNome() + " investiu na Acao Ambiental " + pref.getTipoAcao(acao) + "");
            this.colocaLogCSV("usa acao" + this.separadorCSV + "prefeito " + pref.getNome() + this.separadorCSV + pref.getTipoAcao(acao));
		}
    }
    
    public void cobrarImpostos() throws IOException {
        double imposto = 0;
        for (Empresario emp : this.empresarios) {
            if (emp.getCidade().equals(this.prefeitos.get(0).getCidade())) {
                Prefeito pref = this.prefeitos.get(0);
                imposto = pref.cobrarImposto(emp);

                this.colocaArquivoLog("Prefeito " + pref.getNome() + " cobrou um imposto de D$" + imposto + " do empresario " + emp.getNome() + "");
                this.colocaLogCSV("imposto" + this.separadorCSV + "prefeito " + pref.getNome() + this.separadorCSV + emp.getNome() + this.separadorCSV + imposto);
            } else {
                Prefeito pref = this.prefeitos.get(1);
                imposto = pref.cobrarImposto(emp);
                this.colocaArquivoLog("Prefeito " + pref.getNome() + " cobrou um imposto de D$" + imposto + " do empresario " + emp.getNome() + "");
                this.colocaLogCSV("imposto" + this.separadorCSV + "prefeito " + pref.getNome() + this.separadorCSV + emp.getNome() + this.separadorCSV + imposto);
            }
        }
        for (Agricultor agr : this.agricultores) {
            if (agr.getCidade().equals(this.prefeitos.get(0).getCidade())) {
                Prefeito pref = this.prefeitos.get(0);
                imposto = pref.cobrarImposto(agr);
                this.colocaArquivoLog("Prefeito " + pref.getNome() + " cobrou um imposto de D$" + imposto + " do agricultor " + agr.getNome() + "");
                this.colocaLogCSV("imposto" + this.separadorCSV + "prefeito " + pref.getNome() + this.separadorCSV + agr.getNome() + this.separadorCSV + imposto);
            } else {
                Prefeito pref = this.prefeitos.get(1);
                imposto = pref.cobrarImposto(agr);
                this.colocaArquivoLog("Prefeito " + pref.getNome() + " cobrou um imposto de D$" + imposto + " do agricultor " + agr.getNome() + "");
                this.colocaLogCSV("imposto" + this.separadorCSV + "prefeito " + pref.getNome() + this.separadorCSV + agr.getNome() + this.separadorCSV + imposto);
            }
        }
    }

    /**
     * Metodos referentes a classe de Vereador
     */
    public Vereador getVereadorById(int id, boolean chamadaFront) {
        for (Vereador ver : this.vereadores) {
            if (ver.getId() == id) {
            	if(chamadaFront) this.et2[ver.getId() - this.quantidadeJogadores - 1] = false;
                return ver;
            }
        }
        return null;
    }
    
    public void processaJogadaVereador(int idVer) {
    	this.et2[idVer - this.quantidadeJogadores - 1] = true;
    }
    
    public Prefeito getInfoPrefeitoByVereador(int idVer) {
    	return this.prefeitos.get(idVer - this.quantidadeJogadores - 5);
    }

    /**
     * Metodos em relacao do jogo
     */

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

    public void colocaArquivoLog(String comando) throws IOException {
        String fileName = this.storePath + "/arquivoslog/log.txt";

        File file = new File(fileName);
    	if(!file.exists()) {
    		file.getParentFile().mkdirs();
    		file.createNewFile();
    	}
    	
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

    public void colocaLogCSV(String comando) throws IOException {
        String fileName = this.storePath + "/arquivoslog/log.csv";

        File file = new File(fileName);
    	if(!file.exists()) {
    		file.getParentFile().mkdirs();
    		file.createNewFile();
    	}
    	
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

    public void fechaRodadaLog() throws IOException {
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
    	JSONObject novaRodada = new JSONObject();
    	
    	novaRodada.put("rodada", this.rodada);
    	novaRodada.put("saldoAnterior", this.saldosAnteriores.get(emp.getId()-1));
    	novaRodada.put("produtividade", emp.getProdutividade());
    	novaRodada.put("imposto", emp.getImposto());
    	if(etapa == 2) novaRodada.put("multa", emp.getMulta());
    	
    	JSONObject transferencias = new JSONObject();
    	transferencias.put("enviado", this.transferenciasSent.get((emp.getId()-1)));
    	transferencias.put("recebido", this.transferenciasReceived.get((emp.getId()-1)));
    	novaRodada.put("transferencias", transferencias);
    	
    	int cidade = 1;
    	if(emp.getCidade().equals(ConstantesGorim.c_CidadeA) ) cidade = 0;
    	
		novaRodada.put("acoesUtilizadas", this.prefeitos.get(cidade).getAcoesUsadasJSON());
		novaRodada.put("impostosModificados", this.prefeitos.get(cidade).getTaxasMudadasJSON());
    	
    	novaRodada.put("saldoAtual", emp.getSaldo());
    	novaRodada.put("poluicaoPessoal", emp.getPoluicao());
    	novaRodada.put("poluicaoCausadaMundo", (emp.getPoluicao()*1000));
    	novaRodada.put("poluicaoMundial", (this.poluicaoMundo*100));
    	
    	return novaRodada;
    	
    }

    @SuppressWarnings({ "unchecked" })
    private JSONObject setArquivoAgrJSON(Agricultor agr, int etapa) {
    	JSONObject novaRodada = new JSONObject();
    	
    	novaRodada.put("rodada", this.rodada);
    	novaRodada.put("saldoAnterior", this.saldosAnteriores.get(agr.getId()-1));
    	novaRodada.put("produtividade", agr.getProdutividade());
    	novaRodada.put("imposto", agr.getImposto());
    	if(etapa == 2) novaRodada.put("multa", agr.getMulta());
    	novaRodada.put("gastos", agr.getGastos());
    	    	
    	JSONObject transferencias = new JSONObject();
    	transferencias.put("enviado", this.transferenciasSent.get((agr.getId()-1)));
    	transferencias.put("recebido", this.transferenciasReceived.get((agr.getId()-1)));
    	novaRodada.put("transferencias", transferencias);
    	
    	int cidade = 1;
    	if(agr.getCidade().equals(ConstantesGorim.c_CidadeA) ) cidade = 0;
    	
		novaRodada.put("acoesUtilizadas", this.prefeitos.get(cidade).getAcoesUsadasJSON());
		novaRodada.put("impostosModificados", this.prefeitos.get(cidade).getTaxasMudadasJSON());
    	
    	novaRodada.put("saldoAtual", agr.getSaldo());
    	novaRodada.put("poluicaoPessoal", agr.getPoluicao());
    	novaRodada.put("poluicaoCausadaMundo", (agr.getPoluicao()*1000));
    	novaRodada.put("poluicaoMundial", (this.poluicaoMundo*100));
    	
    	novaRodada.put("parcelas", agr.contentParcelaJSON());
    	
    	return novaRodada;
    	
    }

    @SuppressWarnings({ "unchecked" })
    private JSONObject setArquivoFisJSON(FiscalAmbiental fis, int etapa) {
    	JSONObject novaEtapa = new JSONObject();
    	
    	novaEtapa.put("etapa", this.etapa);
    	novaEtapa.put("saldoAnterior", this.saldosAnteriores.get(fis.getId()-1));
    	
    	JSONObject transferencias = new JSONObject();
    	transferencias.put("enviado", this.transferenciasSent.get((fis.getId()-1)));
    	transferencias.put("recebido", this.transferenciasReceived.get((fis.getId()-1)));
    	novaEtapa.put("transferencias", transferencias);

    	novaEtapa.put("saldoAtual", fis.getSaldo());
    	novaEtapa.put("poluicaoMundial", (this.poluicaoMundo*100));

		for(Prefeito pref : this.prefeitos) {
    		if(pref.getCidade() == fis.getCidade()) {
    			novaEtapa.put("acoesAmbientais", pref.getAcoesUsadasJSON());
    			break;
    		}
    	}
    	
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
    	novaEtapa.put("empresarios", empresarios);
    	
    	JSONArray agricultores = new JSONArray();
    	for(Agricultor agr : this.agricultores) {
    		if(agr.getCidade() == fis.getCidade()) {
    			JSONObject agricultor = new JSONObject();
    			agricultor.put("policaoMedia", agr.getPoluicao());
    			if(etapa == 2)  agricultor.put("multa", agr.getMulta());
    			agricultor.put("parcelas", agr.contentParcelaJSON());
    			agricultor.put("produtividade", agr.getProdutividade());
    			agricultor.put("nome", agr.getNome());
        		agricultores.add(agricultor);
    		}
    	}
    	novaEtapa.put("agricultores", agricultores);
    	
    	return novaEtapa;
    	
    }

    @SuppressWarnings({ "unchecked" })
    private JSONObject setArquivoPrefJSON(Prefeito pref, int etapa) {
    	JSONObject novaEtapa = new JSONObject();
    	
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
    			agricultor.put("produtividade", agr.getProdutividade());
        		agricultores.add(agricultor);
    		}
    	}
    	
    	novaEtapa.put("etapa", this.etapa);
    	novaEtapa.put("saldoAnterior", this.saldosAnteriores.get(pref.getId()-1));
    	novaEtapa.put("impostos", impostos);
    	if(etapa == 2) novaEtapa.put("multas", multas);
    	
    	JSONObject transferencias = new JSONObject();
    	transferencias.put("enviado", this.transferenciasSent.get((pref.getId()-1)));
    	transferencias.put("recebido", this.transferenciasReceived.get((pref.getId()-1)));
    	novaEtapa.put("transferencias", transferencias);

    	novaEtapa.put("saldoAtual", pref.getSaldo());
    	novaEtapa.put("poluicaoMundial", (this.poluicaoMundo*100));
    	
		novaEtapa.put("acoesAmbientais", pref.getAcoesUsadasJSON());
		novaEtapa.put("impostosModificados", pref.getTaxasMudadasJSON());

    	novaEtapa.put("empresarios", empresarios);
    	novaEtapa.put("agricultores", agricultores);
    	
    	return novaEtapa;
    	
    }

    @SuppressWarnings({ "unchecked" })
    private JSONObject setArquivoVerJSON(Vereador ver, int etapa) {
    	JSONObject novaEtapa = new JSONObject();

    	novaEtapa.put("etapa", this.etapa);
    	novaEtapa.put("saldoAnterior", this.saldosAnteriores.get(ver.getId()-1));
    	
    	JSONObject transferencias = new JSONObject();
    	transferencias.put("enviado", this.transferenciasSent.get((ver.getId()-1)));
    	transferencias.put("recebido", this.transferenciasReceived.get((ver.getId()-1)));
    	novaEtapa.put("transferencias", transferencias);
    	
		for(Prefeito pref : this.prefeitos) {
    		if(pref.getCidade() == ver.getCidade()) {
    			novaEtapa.put("acoesAmbientais", pref.getAcoesUsadasJSON());
    			novaEtapa.put("impostosModificados", pref.getTaxasMudadasJSON());
    			break;
    		}
    	}

    	novaEtapa.put("saldoAtual", ver.getSaldo());
    	novaEtapa.put("poluicaoMundial", (this.poluicaoMundo*100));

    	
    	return novaEtapa;
    	
    }
    
    /**
    * Para criar um arquivo resumo por papel por etapa utilizar esse método.
    * Para utilizar um arquivo unico por etapa, utilizar setArquivos
    */
    public void setArquivosByRole(int etapa) throws IOException{
    	String path = this.storePath + "/arquivosResumo";
        
        if (etapa == 2) {
            for (Empresario emp : this.empresarios) {
                this.escreveFinalArquivoJSON(
                		path + "/empresario/"+ emp.getId() + ".json",
                		this.setArquivoEmpJSON(emp, etapa),
                		1,
                		emp.getNome()
                );
            }
            for (Agricultor agr : this.agricultores) {
                this.escreveFinalArquivoJSON(
                		path + "/agricultor/" + agr.getId() + ".json",
                		this.setArquivoAgrJSON(agr, etapa),
                		2,
                		agr.getNome()
                );
            }
        }
        
        for (FiscalAmbiental fis : this.fiscais) {
            this.escreveFinalArquivoJSON(
            		path + "/fiscal/"+ fis.getId() + ".json",
            		this.setArquivoFisJSON(fis, etapa),
            		3,
            		fis.getNome()
            );
        }
    	for (Prefeito pref : this.prefeitos) {
            this.escreveFinalArquivoJSON(
            		path + "/prefeito/"+ pref.getId() + ".json",
            		this.setArquivoPrefJSON(pref, etapa),
            		4,
            		pref.getNome()
            );
        }
    	for (Vereador ver : this.vereadores) {
            this.escreveFinalArquivoJSON(
            		path + "/vereador/"+ ver.getId() + ".json",
            		this.setArquivoVerJSON(ver, etapa),
            		5,
            		ver.getNome()
            );
        }

    }
    
    public void escreveFinalArquivo(String fileName, String escrita) throws IOException {

        BufferedWriter writer = null;
    	
    	File file = new File(fileName);
    	if(!file.exists()) {
    		file.getParentFile().mkdirs();
    		file.createNewFile();
    	}

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
                    ioe2.printStackTrace();
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
        
        if(papel > 2) {
        	if( !( (this.rodada == 1) && (this.etapa == 1) ) ) {
            	try (Reader reader = new FileReader(fileName)) {

            		JSONObject arquivoJSON = (JSONObject) parser.parse(reader);
            		
            		JSONArray rodadasJSON = (JSONArray) arquivoJSON.get("rodadas");
            		  
            		for (Object rodada : rodadasJSON) {
            			rodadasNovo.add(rodada);
    				}
                   
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        	
        	JSONObject lastRodada = new JSONObject();
        	JSONArray etapasLastRodada = new JSONArray();
        	
        	if(this.etapa != 1) {
        		lastRodada = (JSONObject) rodadasNovo.remove(rodadasNovo.size()-1);
        		etapasLastRodada = (JSONArray) lastRodada.remove("etapas");
        	}
        	else {
        		lastRodada.put("rodada", this.rodada);
        		lastRodada.put("nome", nomePessoa);
        	}
        	
    		etapasLastRodada.add(novaRodada);
    		lastRodada.put("etapas", etapasLastRodada);
    		rodadasNovo.add(lastRodada);
    		
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
        	rodadasNovo.add(novaRodada);
        }
        
    	arquivoNovo.put("nome", nomePessoa);
    	arquivoNovo.put("rodadas", rodadasNovo);
    	
    	File file = new File(fileName);
    	if(!file.exists()) {
    		file.getParentFile().mkdirs();
    		file.createNewFile();
    	}
    	

        try (FileWriter fileW = new FileWriter(fileName)) {
            fileW.write(arquivoNovo.toJSONString());
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
            FiscalAmbiental fis = this.getFiscalById(idChamador, false);
            nomePessoaChamadora = fis.getNome() + " (Fiscal Ambiental)";
            pessoaChamadora = fis;
        } else if (tipoPessoaChamadora == 4) {
            Prefeito prefeito = this.getPrefeitoById(idChamador, false);
            nomePessoaChamadora = prefeito.getNome() + " (Prefeito)";
            pessoaChamadora = prefeito;
        } else if (tipoPessoaChamadora == 5) {
            Vereador vereador = this.getVereadorById(idChamador, false);
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
            FiscalAmbiental fis = this.getFiscalById(idRecebedor, false);
            nomePessoaRecebedora = fis.getNome() + " (Fiscal Ambiental)";
            pessoaRecebedora = fis;
        } else if (tipoPessoaRecebedora == 4) {
            Prefeito prefeito = this.getPrefeitoById(idRecebedor, false);
            nomePessoaRecebedora = prefeito.getNome() + " (Prefeito)";
            pessoaRecebedora = prefeito;
        } else if (tipoPessoaRecebedora == 5) {
            Vereador vereador = this.getVereadorById(idRecebedor, false);
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
	    	this.poluicaoMundo += this.calcularPoluicaoCausada();
	        
	        this.cobrarImpostos();
	        for (Prefeito pref : this.prefeitos) {
	            pref.receberContribuicoes();
	        }
            if( (this.rodada-1)%2 == 0 && this.rodada != 1) {
    	        this.processaEleicao();
            	
            }
	
	        this.setArquivosByRole(this.etapa);
	        this.etapa = 2;
	        
	    }
	    else {
	    	double poluicaoReduzida = 0;

	    	for (int i = 0; i < et2.length; i++) {
				this.et2[i] = true;
			}
	    	
            for(Prefeito pref : this.prefeitos){
                poluicaoReduzida += pref.usarAcoes();
                pref.receberContribuicoes();
            }
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
            for(FiscalAmbiental fis : this.fiscais) {
            	Pessoa pessoa;
            	int idEleito = fis.getIdEleito();
            	if(this.getTipoPessoaById(idEleito) == 1) pessoa = this.getEmpresarioById(idEleito, false);
            	else pessoa = this.getAgricultorById(idEleito, false);
            	fis.finalizarRodada(pessoa);
            }
            for(Prefeito pref : this.prefeitos) pref.iniciaRodada();
            
            for(Vereador ver : this.vereadores) {
            	Pessoa pessoa;
            	int idEleito = ver.getIdEleito();
            	if(this.getTipoPessoaById(idEleito) == 1) pessoa = this.getEmpresarioById(idEleito, false);
            	else pessoa = this.getAgricultorById(idEleito, false);
            	ver.finalizarRodada(pessoa);
            }

	    	this.limpaTransfers();
	    	this.limpaTransferencias();
            this.limpaVendas();
            this.limpaSugestoes();
            
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
    			pessoas.add(new PessoaModel(
    					emp.getId(),
    					emp.getNome(),
    					emp.getNome()
    				));
        	}
    	}
    	else if(classe == 2) {
    		for (Agricultor agr : this.agricultores) {
    			pessoas.add(new PessoaModel(
    					agr.getId(),
    					agr.getNome(),
    					agr.getNome()
    				));
        	}
    	}
    	else if(classe == 3) {
    		for (FiscalAmbiental fis : this.fiscais) {
    			pessoas.add(new PessoaModel(
    					fis.getId(),
    					("Fiscal " + fis.getNome() + " (" + fis.getCidade() + ")"),
    					(fis.cidade.equals(ConstantesGorim.c_CidadeA)) ? "FisAT" : "FisCD"
    				));
        	}
    	}
    	else if(classe == 4) {
        	for (Prefeito pref : this.prefeitos) {
        		pessoas.add(new PessoaModel(
    					pref.getId(),
    					("Prefeito " + pref.getNome() + " (" + pref.getCidade() + ")"),
    					(pref.cidade.equals(ConstantesGorim.c_CidadeA)) ? "PrefAT" : "PrefCD"
    				));
        	}
    	}
    	else if(classe == 5) {
        	for (Vereador ver : this.vereadores) {
        		pessoas.add(new PessoaModel(
    					ver.getId(),
    					("Vereador " + ver.getNome() + " (" + ver.getCidade() + ")"),
    					(ver.cidade.equals(ConstantesGorim.c_CidadeA)) ? "VerAT" : "VerCD"
    				));
        	}
    	}
    	
    	if(!pessoas.isEmpty()) return pessoas;
    	else return null;
    }
    
    public List<PessoaModel> getInfoPessoas(String cidade, boolean segundaEtapa, int classe, int idPessoa){
    	List<PessoaModel> pessoas = new ArrayList<PessoaModel>();
    	
    	if(classe == 1 || classe == 0) {
        	for(Empresario emp : this.empresarios) {
        		if((emp.getCidade().equals(cidade) || cidade == "") && emp.getId() != idPessoa) {
        			pessoas.add(new PessoaModel(
    					emp.getId(),
    					emp.getNome(),
    					emp.getNome()
    				));
        		}
        	}    		
    	}
    	
    	if(classe == 2 || classe == 0) {
        	for(Agricultor agr : this.agricultores) {
        		if((agr.getCidade().equals(cidade) || cidade == "") && agr.getId() != idPessoa) {
        			pessoas.add(new PessoaModel(
    					agr.getId(),
    					agr.getNome(),
    					agr.getNome()
    				));
        		}
        	}
    	}
    	
    	if(segundaEtapa || classe > 2) {
    		if(classe == 3 || classe < 1) {
    			for(FiscalAmbiental fis : this.fiscais) {
            		if(
            			(fis.getCidade().equals(cidade) || cidade == "") &&
            			(fis.getId() != idPessoa)
            		) {
            			String aux = (fis.getCidade().equals(ConstantesGorim.c_CidadeA)) ? "FisAT" : "FisCD";
            			pessoas.add(new PessoaModel(
        					fis.getId(),
        					(aux + " " + fis.getNome()),
        					aux
        				));
            		}
            	}
    		}
    		if(classe == 4 || classe < 1) {
            	for(Prefeito pref : this.prefeitos) {
            		if(
            			(pref.getCidade().equals(cidade) || cidade == "") &&
            			(pref.getId() != idPessoa)
            		) {
            			String aux = (pref.getCidade().equals(ConstantesGorim.c_CidadeA)) ? "PrefAT" : "PrefCD";
            			pessoas.add(new PessoaModel(
        					pref.getId(),
        					(aux + " " + pref.getNome()),
        					aux
        				));
            		}
            	}    			
    		}
    		if(classe == 5 || classe < 1) {
    			for(Vereador ver : this.vereadores) {
            		if(
            			(ver.getCidade().equals(cidade) || cidade == "") &&
            			(ver.getId() != idPessoa)
            		) {
            			String aux = (ver.getCidade().equals(ConstantesGorim.c_CidadeA)) ? "VerAT" : "VerCD";
            			pessoas.add(new PessoaModel(
        					ver.getId(),
        					(aux + " " + ver.getNome()),
        					aux
        				));
            		}
            	}
    		}
    	}
    	
    	return pessoas;    	
    }
    
    public List<PessoaModel> getListaContatoChat(int idPessoa){
    	List<PessoaModel> aux = getInfoPessoas("", true, 0, idPessoa);
    	
    	if(idPessoa <= this.quantidadeJogadores) {
    		if(idPessoa == 0) return aux;
    		
	    	aux.add(0, new PessoaModel(0, "Mestre", "Mestre"));
	    	return aux;
    	}

    	List<PessoaModel> listaContato = new ArrayList<PessoaModel>();
    	listaContato.add(new PessoaModel(0, "Mestre", "Mestre"));    	
    	for(PessoaModel person: aux) {
    		if(
    			(person.getId() <= this.quantidadeJogadores &&
    			!isSamePerson(person.getId(), idPessoa)) ||
    			
    			(person.getId() > this.quantidadeJogadores)
    			
    		) listaContato.add(person);
    	}
    	
    	return listaContato;
    }
    
    private boolean isSamePerson(int idPessoa, int idCargoEleitoral) {
    	int cidade = (idCargoEleitoral%2 == 0) ? 1 : 0;
    	
    	if(
    		(this.getFiscalById(idCargoEleitoral, false) != null) && 
    		(this.fiscais.get(cidade).getIdEleito() == idPessoa)
    	) return true;
    	
    	else if(
    		(this.getPrefeitoById(idCargoEleitoral, false) != null) &&
    		(this.prefeitos.get(cidade).getIdEleito() == idPessoa)
    	) return true;
    	
    	else if(
    		(this.getVereadorById(idCargoEleitoral, false) != null) &&
    		(this.vereadores.get(cidade).getIdEleito() == idPessoa)
    	) return true;
    	
    	return false;
    		
    }
    
    public JSONObject getFilePessoaByIdJSON(int id) throws IOException {
    	String fileName = this.storePath + "/";
    	
    	switch(this.getTipoPessoaById(id)) {
    	case 1:
    		fileName += "arquivosResumo/empresario/" + id + ".json";
    		break;
    	case 2:
    		fileName += "arquivosResumo/agricultor/" + id + ".json";
            break;
    	case 3:
    		fileName += "arquivosResumo/fiscal/" + id + ".json";
    		break;
    	case 4:
	    	fileName += "arquivosResumo/prefeito/" + id + ".json";
	    	break;
    	case 5:
    		fileName += "arquivosResumo/vereador/" + id + ".json";
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
        
        String filePath = this.storePath + "/arquivosResumo";
        
    	if(tipoPessoa == 1) {
    		file = new File(filePath + "/empresario/" + id + ".json");
            path = Paths.get(file.getAbsolutePath());
            resource = new ByteArrayResource(Files.readAllBytes(path));
            
    	}
    	else if(tipoPessoa == 2) {
    		file = new File(filePath + "/agricultor/" + id + ".json");
            path = Paths.get(file.getAbsolutePath());
            resource = new ByteArrayResource(Files.readAllBytes(path));
    	}
    	else if(tipoPessoa == 3) {
    		file = new File(filePath + "/fiscal/" + id + ".json");
            path = Paths.get(file.getAbsolutePath());
            resource = new ByteArrayResource(Files.readAllBytes(path));
    	}
    	else if(tipoPessoa == 4) {
    		file = new File(filePath + "/prefeito/" + id + ".json");
            path = Paths.get(file.getAbsolutePath());
            resource = new ByteArrayResource(Files.readAllBytes(path));
    	}
    	else if(tipoPessoa == 5) {
    		file = new File(filePath + "/vereador/" + id + ".json");
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
    	
    	int cidade = (agr.getCidade().equals(ConstantesGorim.c_CidadeA)) ? 0 : 1;
    	
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
    
    public List<Venda> getOrcamentos(int idPEssoa){
    	return this.vendas.get(idPEssoa-1);
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
    }
    
    public void limpaVendas() {
    	this.vendas.forEach(
			x -> x.clear()
    	);
    }
    
    public void adicionaSugestaoOuResposta(int idPessoa, SugestaoVereador sugestao) {
    	int idRecebedora;
    	if(this.getTipoPessoaById(idPessoa) == 4) {
    		idRecebedora = this.vereadores.get(idPessoa - this.quantidadeJogadores - 3).getId();
    	}
    	else {
    		idRecebedora = this.prefeitos.get(idPessoa - this.quantidadeJogadores - 5).getId();
    	}
    	
    	this.sugestoesVereador.get(idRecebedora - this.quantidadeJogadores - 3).add(sugestao);
    }
    
    public List<SugestaoVereador> getSugestoesVereador(int idPessoa){
    	return this.sugestoesVereador.get(idPessoa - this.quantidadeJogadores - 3);
    }
    
    public void removeSugestaoVereador(int idPref, int idSugestao) {
    	for (SugestaoVereador sugestao : this.sugestoesVereador.get(idPref - this.quantidadeJogadores - 3)) {
			if(sugestao.getIdSugestao() == idSugestao) {
				this.sugestoesVereador.get(idPref - this.quantidadeJogadores - 3).remove(sugestao);
				break;
			}
		}
    }
    
    public void limpaSugestoes() {
    	this.sugestoesVereador.forEach(
			x -> x.clear()
    	);
    }
    
}
