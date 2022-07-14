package com.gorim.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gorim.api.GameEventsController;
import com.gorim.enums.GameStatus;
import com.gorim.model.MundoModel;
import com.gorim.model.PessoaModel;
import com.gorim.model.ProdutoSimplifiedModel;
import com.gorim.model.forms.AgricultorForm;
import com.gorim.model.forms.EmpresarioForm;
import com.gorim.model.forms.FiscalAmbientalForm;
import com.gorim.model.forms.MestreForm;
import com.gorim.model.forms.PrefeitoForm;
import com.gorim.model.forms.SugestaoVereador;
import com.gorim.model.forms.Transfer;
import com.gorim.model.forms.Venda;
import com.gorim.motorJogo.Agricultor;
import com.gorim.motorJogo.Empresario;
import com.gorim.motorJogo.FiscalAmbiental;
import com.gorim.motorJogo.Mundo;
import com.gorim.motorJogo.Prefeito;
import com.gorim.motorJogo.Vereador;

@Service
public class MundoService {
	private List<Mundo> mundos;
	private String filesAbsolutePath = "/usr/local/bin/gorimAPI/data/"; // PARA PRODUÇÃO USAR ESSE
	//private String filesAbsolutePath = "data/";							// PARA EXECUTAR LOCAL, USAR ESSE
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private GameEventsController geController;
	
	private final static Logger logger = LoggerFactory.getLogger(MundoService.class);
	
	/**
	 *	Construtor da classe. Cria a lista de mundos
	 */
	public MundoService() {
		this.mundos = new ArrayList<Mundo>();
	}
	
	/**
	 * Recebe mensagem de início de jogo do mestre e inicia um novo mundo
	 * 
	 * @param mestreForm
	 * @return id do novo jogo
	 */
	@SuppressWarnings("unchecked") // Função de add() da JSON larga um warning de 'unchecked'
	public int processaMestre(MestreForm mestreForm) {
		String fileName = filesAbsolutePath + "jogos/ultimoJogo.json";
		int idUltimoJogo = -1;

		// Procura arquivo de informações do último jogo
		// Se não encontra, cria um
		// Se encontra, pega infos para saber qual vai ser o próximo id de mundo (idUltimoJogo)
    	File file = new File(fileName);
    	if(!file.exists()) {
    		file.getParentFile().mkdirs();
    		try {
				file.createNewFile();
			} catch (IOException e) {
				logger.error("MundoService.ProcessaMestre: Exception : " + e.getClass() + " : " + e.getMessage());
				e.printStackTrace();
			}
    	}
    	else {
	        JSONParser parser = new JSONParser();
			try (Reader reader = new FileReader(fileName)) {
	
	    		JSONObject arquivoJSON = (JSONObject) parser.parse(reader);
	    		
	    		idUltimoJogo = ((Long) arquivoJSON.get("idJogo")).intValue();
	    		
	        } catch (IOException e) {
				logger.error("MundoService.processaMestre: Exception : " + e.getClass() + " : " + e.getMessage());
	            e.printStackTrace();
	        } catch (ParseException e) {
				logger.error("MundoService.processaMestre: Exception : " + e.getClass() + " : " + e.getMessage());
	            e.printStackTrace();
	        }
    	}
		
		// Caso tenha achado o último id, soma um nele.
		// Caso não, diz que o próximo id é 1
		int idJogoNovo = (idUltimoJogo > -1) ? (idUltimoJogo + 1) : 1;
		
		// Se é um id válido, substitui no arquivo ultimoJogo.json
		// para ser utilizado como base para o próximo jogo criado
		// e cria o novo mundo
		if(idJogoNovo > 0) {
			JSONObject jogoNovo = new JSONObject();
			jogoNovo.put("idJogo", idJogoNovo);

			try (FileWriter fileW = new FileWriter(fileName)) {
	            fileW.write(jogoNovo.toJSONString());
	        } catch (IOException e) {
				logger.error("MundoService.processaMestre: Exception : " + e.getClass() + " : " + e.getMessage());
	            e.printStackTrace();
	        }
			
			try {
				logger.info("MundoService.ProcessaMestre: Payload=" + mestreForm.toString());
				this.mundos.add(new Mundo(idJogoNovo, mestreForm.getQuantidadeJogadores(), filesAbsolutePath, userRepository, geController));
				this.mundos.get(this.getIndexMundoById(idJogoNovo)).iniciarJogo();
			} catch (Exception e) {
				logger.error("MundoService.processaMestre: Exception : " + e.getClass() + " : " + e.getMessage());
				e.printStackTrace();
			}
		}
		
		// Retorna o id para a interface
		return idJogoNovo;
	}
	
	/**
	 * Recebe mensagem de finalização de jogo. Retorna se conseguiu
	 * ou não finalizar o mundo/jogo
	 * 
	 * @param idJogo
	 * @return sucesso ou não
	 */
	public boolean finalizarJogo(int idJogo) {
		// Pega o id do mundo a ser finalizado da mensagem
		// vê seexiste um mundo com esse id e finaliza o mundo
		int indexMundo = this.getIndexMundoById(idJogo);
		if(indexMundo > -1) {
			try {
				logger.info("MundoService.finalizarJogo: Mundo " + idJogo + " requisitado para finalização.");
				this.mundos.get(this.getIndexMundoById(idJogo)).finalizarJogo();
				logger.info("MundoService.finalizarJogo: Mundo " + idJogo + " finalizado.");
			} catch (IOException e) {
				logger.error("MundoService.finalizarJogo: Exception : " + e.getClass() + " : " + e.getMessage());
				e.printStackTrace();
				return false;
			}

			// Remove o mundo da lista
			this.mundos.remove(this.getIndexMundoById(idJogo));
			
			return true;
		}
		// Caso seja um id inválido, retorna falha pra interface
		logger.warn("MundoService.finalizarJogo: O jogo " + idJogo + " não foi finalizado pois não está na lista.");
		return false;
	}
	
	/**
	 * Retorna as informações para a página de game over, que são retiradas
	 * do arquivo gameOverData.json que é preenchido quando o mundo é
	 * finalizado (gorim.motorJogo.Mundo.finalizarJogo())
	 * 
	 * @param idJogo
	 * @return dados da finalização do jogo em formato JSON
	 */
	public JSONObject getGameOverData(int idJogo) {
		JSONParser parser = new JSONParser();
		JSONObject gameOverDataJSON = new JSONObject();
		
		// Tenta recuperar os dados do arquivo
		// Se não consegue, o retorno vai vazio
		try (Reader reader = new FileReader(filesAbsolutePath + "jogos/" + idJogo + "/gameOverData.json")) {

			gameOverDataJSON = (JSONObject) parser.parse(reader);
    		
        } catch (FileNotFoundException e) {
			logger.error("MundoService.getGameOverData: Exception : " + e.getClass() + " : " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			logger.error("MundoService.getGameOverData: Exception : " + e.getClass() + " : " + e.getMessage());
            e.printStackTrace();
        } catch (ParseException e) {
			logger.error("MundoService.getGameOverData: Exception : " + e.getClass() + " : " + e.getMessage());
            e.printStackTrace();
        }
		
		return gameOverDataJSON;
	}
	
	/**
	 * Método auxiliar interno que retorna o índice da lista de mundos
	 * que o mundo com id recebido está. Caso não esteja na lista, é
	 * retornado -1
	 * 
	 * @param idJogo
	 * @return índice do mundo na lista ou -1 caso não tenha achado
	 */
	private int getIndexMundoById(int idJogo) {
		int i = 0;
		for (Mundo mundo : mundos) {
			if(mundo.getIdJogo() == idJogo) return i;
			i++;
		}
		return -1;
	}
	
	/**
	 * Retorna as infos do mundo com id recebido
	 * 
	 * @param idJogo
	 * @return informações do mundo em MundoModel
	 */
	public MundoModel getInfoMundo(int idJogo) {
		int indexMundo = this.getIndexMundoById(idJogo);
		if(indexMundo > -1)
			return this.mundos.get(indexMundo).getInfoMundo();
		logger.warn("MundoService.getInfoMundo: O jogo " + idJogo + " não está na lista.");
		return null;
	}
	
	/**
	 * Adiciona a transferência para ser processada no final da etapa na lista
	 * interna do mundo do id recebido. Retorna se foi possível ou não
	 * 
	 * @param idJogo
	 * @param transferencia
	 * @return sucesso ou não
	 */
	public boolean adicionaTransferencia(int idJogo, Transfer transferencia) {
		int indexMundo = this.getIndexMundoById(idJogo);
		if(indexMundo > -1) {
			logger.info("MundoService.adicionaTransferencia: Payload=" + transferencia.toString());
			this.mundos.get(indexMundo).adicionaTransferencia(transferencia);
			return true;
		}
		logger.warn("MundoService.adicionaTransferencia: Transferência não adicionada pois o jogo " + idJogo + " não está na lista. Payload=" + transferencia.toString());
		return false;
	}
	
	/**
	 * Função resevada à interface do Mestre que muda a flag de
	 * término de fim de etapa quando todos os jogadores entram na
	 * etapa seguinte. Influencia no GameStatus
	 * 
	 * @param idJogo
	 */
	public void changeFlagFimEtapa(int idJogo) {
		int indexMundo = this.getIndexMundoById(idJogo);
		if(indexMundo > -1)
			this.mundos.get(indexMundo).changeFlagFimEtapa();
		else
			logger.warn("MundoService.changeFlagFimEtapa: Flag não foi modificada pois o jogo " + idJogo + " não está na lista.");
	}
	
	/**
	 * Retorna o id dospersonagens que já terminaram a jogada no
	 * mundo de id recebido
	 * 
	 * @param idJogo
	 * @param etapa
	 * @return array com id dos personagens que terminaram etapa
	 */
	public int[] verificaFinalizados(int idJogo, int etapa) {
		int indexMundo = this.getIndexMundoById(idJogo);
		if(indexMundo > -1)
			return this.mundos.get(indexMundo).verificaFinalizados(etapa);
		logger.warn("MundoService.verificaFinalizados: O jogo " + idJogo + " não está na lista.");
		return null;
	}
	
	/**
	 * Retorna se todos já terminaram a etapa ou se o jogo acabou. Endpoint
	 * usado pelas interfaces dos personagens caso o websocket falhe. Retorna
	 * -1 caso o id de mundo recebido não exista/não esteja nos arquivos de
	 * mundos terminados
	 * 
	 * @param idJogo
	 * @param etapa
	 * @return GameStatus ou -1
	 */
	public int verificaTodosTerminaramEtapa(int idJogo, int etapa) {
		int indexMundo = this.getIndexMundoById(idJogo);

		// Vê se o id é algum mundo em aberto, se sim, retorna o status da etapa
		if(indexMundo > -1)
			return this.mundos.get(indexMundo).hasUnfinishedPlayers(etapa);
		
		// Caso não esteja na lista, procura nos arquivos de mundos terminados
		// e, caso ache, retorna que o jogo acabou
		File tempFile = new File(filesAbsolutePath + "jogos/" + idJogo + "/gameOverData.json");
		if(tempFile.exists())
			return GameStatus.FIM_JOGO.status;

		// Caso não ache nem nos terminados, retorna que o id de mundo é inválido
		logger.warn("MundoService.verificaFimEtapa: O jogo " + idJogo + " não está na lista e não foi finalizado.");
		return -1;
	}
	
	/**
	 * Retorna se todos os jogadores já estão na janela da nova etapa.
	 * Endpoint de apoio para caso o websocket falhe
	 * 
	 * @param idJogo
	 * @param etapa
	 * @return GameStatus ou -1
	 */
	public int verificaTodosComecaramEtapa(int idJogo, int etapa) {
		int indexMundo = this.getIndexMundoById(idJogo);

		// Vê se o id é algum mundo em aberto, se sim, retorna o status da etapa
		if(indexMundo > -1)
			return this.mundos.get(indexMundo).hasEveryoneStarted(etapa);
		
		// Caso não esteja na lista, procura nos arquivos de mundos terminados
		// e, caso ache, retorna que o jogo acabou
		File tempFile = new File(filesAbsolutePath + "jogos/" + idJogo + "/gameOverData.json");
		if(tempFile.exists())
			return GameStatus.FIM_JOGO.status;

		// Caso não ache nem nos terminados, retorna que o id de mundo é inválido
		logger.warn("MundoService.verificaFimEtapa: O jogo " + idJogo + " não está na lista e não foi finalizado.");
		return -2;
	}
	
	/**
	 * Retorna o papel que o personagem do id recebido é na segunda etapa.
	 * 
	 * @param idJogo
	 * @param idPessoa
	 * @return id do personagem na segunda etapa
	 */
	public int papelSegundaEtapa(int idJogo, int idPessoa) {
		int indexMundo = this.getIndexMundoById(idJogo);
		if(indexMundo > -1)
			return this.mundos.get(indexMundo).papelSegundaEtapa(idPessoa);

		logger.warn("MundoService.papelSegundaEtapa: O jogo " + idJogo + " não está na lista");
		return -1;
	}
	
	/**
	 * Processa a jogada do empresário caso seja um id de mundo válido
	 * 
	 * @param idJogo
	 * @param idEmp
	 * @param empForm
	 */
	public void processaJogadaEmpresario(int idJogo, int idEmp, EmpresarioForm empForm){
		int indexMundo = this.getIndexMundoById(idJogo);
		if(indexMundo > -1) {
			logger.info("MundoService.processaJogadaEmpresario: Payload=" + empForm.toString());
			this.mundos.get(indexMundo).processaJogadaEmpresario(idEmp, empForm);
		}
		else
			logger.warn("MundoService.processaJogadaEmpresario: Não foi possível processar. O jogo " + idJogo + " não está na lista. Payload=" + empForm.toString());
	}
	
	/**
	 * Retorna as informações do empresário caso o id do mundo e do empresário
	 * forem válidos
	 * 
	 * @param idJogo
	 * @param idPessoa
	 * @return Empresario ou null
	 */
	public Empresario getEmpresarioById(int idJogo, int idPessoa) {
		int indexMundo = this.getIndexMundoById(idJogo);
		if(indexMundo > -1)
			return this.mundos.get(indexMundo).getEmpresarioById(idPessoa, true);

		logger.warn("MundoService.getEmpresarioById: Empresario id=" + idPessoa + " não foi achado pois o jogo " + idJogo + " não está na lista.");
		return null;
	}
	
	/**
	 * Retorna as informações simplificadas dos produtos de todos
	 * os empresários caso seja um id de mundo válido
	 * 
	 * @param idJogo
	 * @return lista de ProdutoSimplifiedModel ou null
	 */
	public List<ProdutoSimplifiedModel> getProdutosEmpresarios(int idJogo){
		int indexMundo = this.getIndexMundoById(idJogo);
		if (indexMundo > -1)
			return this.mundos.get(indexMundo).getProdutosEmpresarios();
		logger.warn("MundoService.getProdutosEmpresarios: Produtos não encontrados pois o jogo " + idJogo + " não está na lista.");
		return null;
	}
	
	/**
	 * Processa a jogada do agricultor caso seja um id de mundo válido
	 * 
	 * @param idJogo
	 * @param idAgr
	 * @param agrForm
	 */
	public void processaJogadaAgricultor(int idJogo, int idAgr, AgricultorForm agrForm) {
		int indexMundo = this.getIndexMundoById(idJogo);
		if (indexMundo > -1) {
			logger.info("MundoService.processaJogadaAgricultor: Payload=" + agrForm.toString());
			this.mundos.get(indexMundo).processaJogadaAgricultor(idAgr, agrForm);
		}
		else
			logger.warn("MundoService.processaJogadaAgricultor: Jogada não processada pois o jogo " + idJogo + " não está na lista. Payload=" + agrForm.toString());
	}
	
	/**
	 * Retorna as infos do agricultor caso o id do personagem e do mundo
	 * sejam válidos
	 * 
	 * @param idJogo
	 * @param id
	 * @return Agricultor ou null
	 */
	public Agricultor getAgricultorById(int idJogo, int id) {
		int indexMundo = this.getIndexMundoById(idJogo);
		if(indexMundo > -1)
			return this.mundos.get(indexMundo).getAgricultorById(id, true);

		logger.warn("MundoService.getAgricultorById: Agricultor id=" + id +" não foi achado pois o jogo " + idJogo + " não está na lista.");
		return null;
	}
	
	/**
	 * Processa a jogada do fiscal caso seja um id de mundo válido
	 * 
	 * @param idJogo
	 * @param idFis
	 * @param fisForm
	 */
	public void processaJogadaFiscal(int idJogo, int idFis, FiscalAmbientalForm fisForm){
		int indexMundo = this.getIndexMundoById(idJogo);
		if (indexMundo > -1) {
			try {
		    	logger.info("MundoService.processaJogadaFiscal: idFis=" + idFis + "; Payload=" + fisForm.toString());
				this.mundos.get(indexMundo).processaJogadaFiscal(idFis, fisForm);
			} catch (IOException e) {
				logger.error("MundoService.processaJogadaFiscal: Exception : " + e.getClass() + " : " + e.getMessage());
				e.printStackTrace();
			}
		}
		else
			logger.warn("MundoService.processaJogadaFiscal: Não foi possível processar a jogada do fiscal pois o jogo " + idJogo + " não está na lista. Payload=" + fisForm.toString());
	}
	
	/**
	 * Processa a jogada do prefeito caso seja um id de mundo válido
	 * 
	 * @param idJogo
	 * @param idPref
	 * @param prefForm
	 */
	public void processaJogadaPrefeito(int idJogo, int idPref, PrefeitoForm prefForm) {
		int indexMundo = this.getIndexMundoById(idJogo);
		if (indexMundo > -1) {
			try {
		    	logger.info("MundoService.processaJogadaPrefeito: idPref=" + idPref + "; Payload=" + prefForm.toString());
				this.mundos.get(indexMundo).processaJogadaPrefeito(idPref, prefForm);
			} catch (IOException e) {
				logger.error("MundoService.getGameOverData: Exception : " + e.getClass() + " : " + e.getMessage());
				e.printStackTrace();
			}
		}
		else
			logger.warn("MundoService.processaJogadaPrefeito: Não foi possível processar a jogada do prefeito pois o jogo " + idJogo + " não está na lista. Payload=" + prefForm.toString());
	}
	
	/**
	 * Processa a jogada do vereador caso seja um id de mundo válido
	 * 
	 * @param idJogo
	 * @param idVer
	 */
	public void processaJogadaVereador(int idJogo, int idVer) {
		int indexMundo = this.getIndexMundoById(idJogo);
		if (indexMundo > -1) {
	    	logger.info("MundoService.processaJogadaVereador: idVer=" + idVer);
			this.mundos.get(indexMundo).processaJogadaVereador(idVer);
		}
		else
			logger.warn("MundoService.processaJogadaVereador: Não foi possível processar a jogada do vereador id=" + idVer + " pois o jogo " + idJogo + " não está na lista.");
	}
	
	/**
	 * Retorna as infos do Fiscal Ambiental caso o id do personagem e do mundo forem válidos
	 * 
	 * @param idJogo
	 * @param idFis
	 * @return FiscalAmbiental ou null
	 */
	public FiscalAmbiental getFiscalAmbientalById(int idJogo, int idFis) {
		int indexMundo = this.getIndexMundoById(idJogo);
		if (indexMundo > -1)
			return this.mundos.get(indexMundo).getFiscalById(idFis, true);
		logger.warn("MundoService.getFisclaAmbientalById: Fiscal id=" + idFis + " não encontrado pois o jogo " + idJogo + " não está na lista.");
		return null;
	}
	
	/**
	 * Retorna as infos do prefeito caso o id do personagem e do mundo forem válidos
	 * 
	 * @param idJogo
	 * @param idPref
	 * @return Prefeito ou null
	 */
	public Prefeito getPrefeitoById(int idJogo, int idPref) {
		int indexMundo = this.getIndexMundoById(idJogo);
		if (indexMundo > -1)
			return this.mundos.get(indexMundo).getPrefeitoById(idPref, true);
		
		logger.warn("MundoService.getPrefeitoById: Prefeito id=" + idPref + " não encontrado pois o jogo " + idJogo + " não está na lista.");
		return null;
	}
	
	/**
	 * Retorna as infos do vereador caso o id do personagem e do mundo sejam válidos
	 * 
	 * @param idJogo
	 * @param idVer
	 * @return Vereador ou null
	 */
	public Vereador getVereadorById(int idJogo, int idVer) {
		int indexMundo = this.getIndexMundoById(idJogo);
		if (indexMundo > -1)
			return this.mundos.get(indexMundo).getVereadorById(idVer, true);
		
		logger.warn("MundoService.getVereadorById: Vereador id=" + idVer + " não encontrado pois o jogo " + idJogo + " não está na lista.");
		return null;
	}
	
	public Prefeito getInfoPrefeitoByVereador(int idJogo, int idVer) {
		int indexMundo = this.getIndexMundoById(idJogo);
		if (indexMundo > -1)
			return this.mundos.get(indexMundo).getInfoPrefeitoByVereador(idVer);
		logger.warn("MundoService.getInfoPrefeitoByVereador: Prefeito do Vereador id=" + idVer + " não encontrado pois o jogo " + idJogo + " não está na lista.");
		return null;
	}
	
	/**
	 * Retorna uma lista com as infos de todos os empresários do mundo caso
	 * o id de mundo seja válido
	 * 
	 * @param idJogo
	 * @return ArrayList de Empresário ou null
	 */
	public ArrayList<Empresario> getListaEmpresario(int idJogo){
		int indexMundo = this.getIndexMundoById(idJogo);
		if (indexMundo > -1)
			return this.mundos.get(indexMundo).getListaEmpresario();
		logger.warn("MundoService.getListaEmpresario: Empresários não encontrados pois o jogo " + idJogo + " não está na lista.");
		return null;
	}
	
	/**
	 * Retorna uma lista com as infos de todos os agricultores do mundo caso
	 * o id de mundo seja válido
	 * 
	 * @param idJogo
	 * @return ArrayList de Agricultor ou null
	 */
	public ArrayList<Agricultor> getListaAgricultor(int idJogo){
		int indexMundo = this.getIndexMundoById(idJogo);
		if (indexMundo > -1)
			return this.mundos.get(this.getIndexMundoById(idJogo)).getListaAgricultor();
		logger.warn("MundoService.getListaAgricultor: Agricultores não encontrados pois o jogo " + idJogo + " não está na lista.");
		return null;
	}
	
	/**
	 * Retorna lista com informações simplificadas dos personagens do mundo da classe requisitada
	 * caso o id de mundo e a classe sejam válidos (classes: Empresario, Agricultor, FiscalAmbiental,
	 * Prefeito e Vereador)
	 * 
	 * @param idJogo
	 * @param classe 0: todas, 1: empresario, 2: agricutor, 3: fiscal ambiental, 4: prefeito, 5: vereador
	 * @return Lista de PessoaModel ou null
	 */
	public List<PessoaModel> getInfoPessoasByClasse(int idJogo, int classe){
		int indexMundo = this.getIndexMundoById(idJogo);
		if (indexMundo > -1)
			return this.mundos.get(indexMundo).getInfoPessoas("", false, classe, 0);
		logger.warn("MundoService.getInfoPessoasByClasse: Personagens da classe=" + classe + " não encontrados pois o jogo " + idJogo + " não está na lista.");
		return null;
	}
	
	/**
	 * Retorna as informações simplificadas dos personagens do mundo de uma mesma etapa
	 * caso o id de mundo e a etapa sejam válidos. Se primeira etapa, retorna prefeito
	 * e agricultor. Se segunda, retorna fiscal, prefeito e vereador
	 * 
	 * @param idJogo
	 * @param etapa 1 ou 2
	 * @return Lista de PessoaModel ou null
	 */
	public List<PessoaModel> getInfoPessoasByEtapa(int idJogo, int etapa){
		int indexMundo = this.getIndexMundoById(idJogo);
		if((indexMundo > -1) && ((etapa == 1) || (etapa == 2))){
			boolean segundaEtapa = (etapa == 2) ? true : false;
			return this.mundos.get(indexMundo).getInfoPessoas("", segundaEtapa, (segundaEtapa ? -1 : 0), 0);
		}
		logger.warn("MundoService.getInfoPessoasByEtapa: Personagens não encontrados pois o jogo " + idJogo + " não está na lista.");
		return null;
	}
	
	/**
	 * Retorna as informações simplificadas dos personagens a partir da cidade, da etapa e dos papeis
	 * caso esses dados e o id do jogo forem válidos. Essa função é usada quando recebemos requisições
	 * para montagem da lista para votação, multa e para atribuir selo verde
	 * 
	 * @param idJogo
	 * @param cidade
	 * @param segundaEtapa
	 * @param papeis
	 * @return Lista de PessoaModel ou null
	 */
	public List<PessoaModel> getInfoPessoas(int idJogo, String cidade, boolean segundaEtapa, int papeis){
		int indexMundo = this.getIndexMundoById(idJogo);
		if(indexMundo > -1)
			return this.mundos.get(indexMundo).getInfoPessoas(cidade, segundaEtapa, papeis, 0);
		logger.warn("MundoService.getInfoPessoas: Personagens não encontrados pois o jogo " + idJogo + " não está na lista. Cidade=" + cidade + "; segundaEtapa=" + segundaEtapa + "; papeis=" + papeis);
		return null;
	}
	
	/**
	 * Encerra a etapa do mundo de id recebido se este for válido
	 * 
	 * @param idJogo
	 * @param rodada
	 * @param etapa
	 * @return secesso ou não
	 */
	public boolean finalizarEtapa(int idJogo, int rodada, int etapa){
		int indexMundo = this.getIndexMundoById(idJogo);
		if(indexMundo > -1) {
	    	logger.info("MundoService.finalizarEtapa: mestre finalizando etapa. idJogo=" + idJogo + "; rodada=" + rodada + "; etapa=" + etapa);
			this.mundos.get(indexMundo).avisoMestreFimEtapa(rodada, etapa);
			return true;
		}
		logger.warn("MundoService.finalizarEtapa: Etapa não finalizada pois o jogo " + idJogo + " não está na lista.");
		return false;
	}
	
	/**
	 * Retorna o JSON com o resumo das etapas anteriores do personagem com o id recebido
	 * caso esse e o id do mundo sejam válidos.
	 * 
	 * @param idJogo
	 * @param idPessoa
	 * @return JSONObject ou null
	 */
	public JSONObject getFilePessoaByIdJSON(int idJogo, int idPessoa){
		int indexMundo = this.getIndexMundoById(idJogo);
		if(indexMundo > -1) {
			try {
				return this.mundos.get(indexMundo).getFilePessoaByIdJSON(idPessoa);
			} catch (IOException e) {
				logger.error("MundoService.getFilePessoaByIdJSON: Exception : " + e.getClass() + " : " + e.getMessage());
				e.printStackTrace();
			}
		}
		logger.warn("MundoService.getFilePessoaByIdJSON: arquivo não encontrado pois o jogo " + idJogo + " não está na lista. idPessoa=" + idPessoa);
		return null;
	}
	
	/**
	 * Adiciona orçamento enviado do Empresario para o Agricultor na lista interna no mundo
	 * caso o id do mundo e os ids presentes dentro do objeto Venda sejam válidos.
	 * 
	 * @param idJogo
	 * @param venda
	 */
	public void adicionaOrcamentoById(int idJogo, Venda venda) {
		int indexMundo = this.getIndexMundoById(idJogo);
		if(indexMundo > -1)
			this.mundos.get(indexMundo).adicionaOrcamentoById(venda);
		else
			logger.warn("MundoService.adicionaOrcamentoById: Orçamento não adicionado pois o jogo " + idJogo + " não está na lista. Payload=" + venda.toString());
	}
	
	/**
	 * Retorna os orçamentos que foram enviados para o agricultor de id recebido
	 * caso esse e o id de mundo sejam válidos. Utilizado caso o websocket falhe
	 * 
	 * @param idJogo
	 * @param idAgr
	 * @return Lista de Venda ou null
	 */
	public List<Venda> getOrcamentos(int idJogo, int idAgr){
		int indexMundo = this.getIndexMundoById(idJogo);
		if(indexMundo > -1)
			return this.mundos.get(indexMundo).getOrcamentos(idAgr);
		logger.warn("MundoService.getOrcamentos: Orçamentos não encontrados pois o jogo " + idJogo + " não está na lista. idAgr=" + idAgr);
		return null;
	}
	
	/**
	 * Remove o orçamento da lista interna do mundo onde o idOrcamento e o idEmp sejam
	 * iguais aos presentes na lista do agricultor de id recebido caso esses dados e
	 * o id do mundo sejam válidos
	 * 
	 * @param idJogo
	 * @param idAgr
	 * @param idEmp
	 * @param idOrcamento
	 */
	public void removeOrcamentoById(int idJogo, int idAgr, int idEmp, int idOrcamento) {
		int indexMundo = this.getIndexMundoById(idJogo);
		if(indexMundo > -1)
			this.mundos.get(indexMundo).removeOrcamentoById(idAgr, idEmp, idOrcamento);
		else
			logger.warn("MundoService.removeOrcamentoById: Não foi possível remover o orçamento pois o jogo " + idJogo + " não está na lista. idAgr=" + idAgr + "; idEmp=" + idEmp + "; idOrcamento=" + idOrcamento);
	}
	
	/**
	 * Adiciona a venda enviada do agricultor para o empresario na lista interna do mundo
	 * caso os ids presentes dentro do objeto Venda e o id do mundo recebidos sejam válidos
	 * 
	 * @param idJogo
	 * @param venda
	 */
	public void adicionaVendaById(int idJogo, Venda venda) {
		int indexMundo = this.getIndexMundoById(idJogo);
		if (indexMundo > -1)
			this.mundos.get(indexMundo).adicionaVendaById(venda);
		else
			logger.warn("MundoService.adicionaVendaById: Não foi possível adicionar a venda pois o jogo " + idJogo + " não está na lista. Payload=" + venda.toString());
	}
	
	/**
	 * Retorna as vendas feitas pelo empresário de id recebido caso esse e o id
	 * do mundo sejam válidos
	 * 
	 * @param idJogo
	 * @param idEmp
	 * @return List de Venda ou null
	 */
	public List<Venda> getVendas(int idJogo, int idEmp){
		int indexMundo = this.getIndexMundoById(idJogo);
		if(indexMundo > -1)
			return this.mundos.get(indexMundo).getOrcamentos(idEmp);
		logger.warn("MundoService.getVendas: Vendas não encontradas pois o jogo " + idJogo + " não está na lista. idEmp=" + idEmp);
		return null;
	}
	
	/**
	 * Adiciona a sugestão enviada do do vereador para o prefeito da própria cidade na lista
	 * interna do mundo caso esses dados e o id do mundo sejam válidos
	 * 
	 * @param idJogo
	 * @param idVer
	 * @param sugestao
	 */
	public void adicionaSugestaoVereador(int idJogo, int idVer, SugestaoVereador sugestao) {
		int indexMundo = this.getIndexMundoById(idJogo);
		if(indexMundo > -1)
			this.mundos.get(indexMundo).adicionaSugestaoOuResposta(idVer, sugestao);
		else
			logger.warn("MundoService.adicionaSugestaoVereador: Não foi possível adicionar a sugestão pois o jogo " + idJogo + " não está na lista. Payload=" + sugestao.toString());
	}
	
	/**
	 * Adiciona a resposta à sugestão enviada do prefeito ao vereador da própria cidade na
	 * lista interna do mundo de id recebido caso o id do prefeito e do mundo sejam válidos
	 * 
	 * @param idJogo
	 * @param idPref
	 * @param sugestao
	 */
	public void adicionaRespostaSugestaoVereador(int idJogo, int idPref, SugestaoVereador sugestao) {
		int indexMundo = this.getIndexMundoById(idJogo);
		if(indexMundo > -1) {
			this.mundos.get(indexMundo).removeSugestaoVereador(idPref, sugestao.getIdSugestao());
			this.mundos.get(indexMundo).adicionaSugestaoOuResposta(idPref, sugestao);
		}
		else
			logger.warn("MundoService.adicionaRespostaSugestaoVereador: Não foi possível adicionar a resposta pois o jogo " + idJogo + " não está na lista. Payload=" + sugestao.toString());
	}
	
	/**
	 * Retorna as sugestões ou respostas às sugestões dependendo do idPessoa recebido
	 * caso esse e o id do jogo forem válidos
	 * 
	 * @param idJogo
	 * @param idPessoa
	 * @return List de SugestaoVereador ou null
	 */
	public List<SugestaoVereador> getSugestoesVereador(int idJogo, int idPessoa){
		int indexMundo = this.getIndexMundoById(idJogo);
		if(indexMundo > -1)
			return this.mundos.get(indexMundo).getSugestoesVereador(idPessoa);
		System.out.println("MundoService.getSugestoesVereador: Não foi possível localizar as sugestões pois o jogo " + idJogo + " não está na lista. idPessoa=" + idPessoa);
		return null;
	}
	
	/**
	 * Adiciona os votos de um personagem, onde o índice do array de votos recebido
	 * equivale ao cargo do voto (0: fiscal, 1: prefeito, 2: vereador) e o valor do
	 * array é o id da pessoa que recebeu o voto.
	 * Ex.: [1, 5, 3] => Fiscal: EmpSem, Prefeito: AgrAT1 e Vereador: EmpMaq, em um
	 * mundo com 6 jogadores
	 * 
	 * @param idJogo
	 * @param votos
	 * @return sucesso ou não
	 */
	public boolean contaVoto(int idJogo, int[] votos) {
		int indexMundo = this.getIndexMundoById(idJogo);
		if(indexMundo > -1) {
			for (int i = 0; i < votos.length; i++) this.mundos.get(indexMundo).contaVoto(votos[i], i);
			return true;
		}
		System.out.println("MundoService.contaVoto: Não foi possível adicionar o voto pois o jogo " + idJogo + " não está na lista. Payload=" + votos.toString());
		return false;
	}
	
	/**
	 * Retorna uma lista de contatos do chat com informações simplificadas para o
	 * personagem de id recebido caso esse e o id do mundo sejam válidos
	 *  
	 * @param idJogo
	 * @param idPessoa
	 * @return List de PessoaModel ou null
	 */
	public List<PessoaModel> getListaContatoChat(int idJogo, int idPessoa){
		int indexMundo = this.getIndexMundoById(idJogo);
		if (indexMundo > -1)
			return this.mundos.get(indexMundo).getListaContatoChat(idPessoa);
		logger.warn("MundoService.getListaContatoChat: Lista de contatos não encontrada pois o jogo " + idJogo + " não está na lista. idPessoa=" + idPessoa);
		return null;
	}
}
