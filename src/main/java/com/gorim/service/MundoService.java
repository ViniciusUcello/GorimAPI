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
	
	@Autowired
	private UserRepository userRepository;
	
	private final static Logger logger = LoggerFactory.getLogger(MundoService.class);
	
	public MundoService() {
		this.mundos = new ArrayList<Mundo>();
	}
	
	@SuppressWarnings("unchecked")
	public int processaMestre(MestreForm mestreForm) {
		String fileName = "jogos/ultimoJogo.json";
		int idUltimoJogo = -1;

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
		
		int idJogoNovo = (idUltimoJogo > -1) ? (idUltimoJogo + 1) : idUltimoJogo;
		
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
				this.mundos.add(new Mundo(idJogoNovo, mestreForm.getQuantidadeJogadores(), userRepository));
				this.mundos.get(this.getIndexMundoById(idJogoNovo)).iniciarJogo();
			} catch (IOException e) {
				logger.error("MundoService.processaMestre: Exception : " + e.getClass() + " : " + e.getMessage());
				e.printStackTrace();
			}
		}
		
		return idJogoNovo;
	}
	
	public boolean finalizarJogo(int idJogo) {
		int indexMundo = this.getIndexMundoById(idJogo);
		if(indexMundo > -1) {
			try {
				this.mundos.get(this.getIndexMundoById(idJogo)).finalizarJogo();
			} catch (IOException e) {
				logger.error("MundoService.finalizarJogo: Exception : " + e.getClass() + " : " + e.getMessage());
				e.printStackTrace();
				return false;
			}
	
			this.mundos.remove(this.getIndexMundoById(idJogo));
			
			return true;
		}
		logger.warn("MundoService.finalizarJogo: O jogo " + idJogo + " não foi finalizado pois não está na lista.");
		return false;
	}
	
	public JSONObject getGameOverData(int idJogo) {
		JSONParser parser = new JSONParser();
		JSONObject gameOverDataJSON = new JSONObject();
		
		try (Reader reader = new FileReader("jogos/" + idJogo + "/gameOverData.json")) {

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
	
	private int getIndexMundoById(int idJogo) {
		int i = 0;
		for (Mundo mundo : mundos) {
			if(mundo.getIdJogo() == idJogo) return i;
			i++;
		}
		return -1;
	}
	
	public MundoModel getInfoMundo(int idJogo) {
		int indexMundo = this.getIndexMundoById(idJogo);
		if(indexMundo > -1)
			return this.mundos.get(indexMundo).getInfoMundo();
		logger.warn("MundoService.getInfoMundi: O jogo " + idJogo + " não está na lista.");
		return null;
	}
	
	public boolean adicionaTransferencia(int idJogo, Transfer transferencia) {
		int indexMundo = this.getIndexMundoById(idJogo);
		if(indexMundo > -1) {
			this.mundos.get(indexMundo).adicionaTransferencia(transferencia);
			return true;
		}
		logger.warn("MundoService.adicionaTransferencia: Transferência não adicionada pois o jogo " + idJogo + " não está na lista. Payload=" + transferencia.toString());
		return false;
	}
	
	public void changeFlagFimEtapa(int idJogo) {
		int indexMundo = this.getIndexMundoById(idJogo);
		if(indexMundo > -1)
			this.mundos.get(indexMundo).changeFlagFimEtapa();
		logger.warn("MundoService.changeFlagFimEtapa: Flag não foi modificada pois o jogo " + idJogo + " não está na lista.");
	}
	
	public boolean[] verificaFinalizados(int idJogo, int etapa) {
		int indexMundo = this.getIndexMundoById(idJogo);
		if(indexMundo > -1)
			return this.mundos.get(indexMundo).verificaFinalizados(etapa);
		logger.warn("MundoService.verificaFinalizados: O jogo " + idJogo + " não está na lista.");
		return null;
	}
	
	public int verificaFimEtapa(int idJogo, int etapa) {
		int indexMundo = this.getIndexMundoById(idJogo);
		if(indexMundo > -1)
			return this.mundos.get(indexMundo).verificaFimEtapa(etapa);
		
		File tempFile = new File("jogos/" + idJogo + "/gameOverData.json");
		if(tempFile.exists())
			return 3;

		logger.warn("MundoService.verificaFimEtapa: O jogo " + idJogo + " não está na lista e não foi finalizado.");
		return -1;
	}
	
	public int papelSegundaEtapa(int idJogo, int idPessoa) {
		int indexMundo = this.getIndexMundoById(idJogo);
		if(indexMundo > -1)
			return this.mundos.get(indexMundo).papelSegundaEtapa(idPessoa);

		logger.warn("MundoService.papelSegundaEtapa: O jogo " + idJogo + " não está na lista");
		return -1;
	}
	
	public void processaJogadaEmpresario(int idJogo, int idEmp, EmpresarioForm empForm){
		int indexMundo = this.getIndexMundoById(idJogo);
		if(indexMundo > -1) {
			this.mundos.get(indexMundo).processaJogadaEmpresario(idEmp, empForm);
		}
		logger.warn("MundoService.processaJogadaEmpresario: Não foi possível processar. O jogo " + idJogo + " não está na lista. Payload=" + empForm.toString());
	}
	
	public Empresario getEmpresarioById(int idJogo, int id) {
		int indexMundo = this.getIndexMundoById(idJogo);
		if(indexMundo > -1)
			return this.mundos.get(indexMundo).getEmpresarioById(id, true);

		logger.warn("MundoService.getEmpresarioById: Empresario id=" + id + " não foi achado pois o jogo " + idJogo + " não está na lista.");
		return null;
	}
	
	public List<ProdutoSimplifiedModel> getProdutosEmpresarios(int idJogo){
		int indexMundo = this.getIndexMundoById(idJogo);
		if (indexMundo > -1)
			return this.mundos.get(indexMundo).getProdutosEmpresarios();
		logger.warn("MundoService.getProdutosEmpresarios: Produtos não encontrados pois o jogo " + idJogo + " não está na lista.");
		return null;
	}
	
	public void processaJogadaAgricultor(int idJogo, int idAgr, AgricultorForm agrForm) {
		int indexMundo = this.getIndexMundoById(idJogo);
		if (indexMundo > -1)
			this.mundos.get(indexMundo).processaJogadaAgricultor(idAgr, agrForm);
		logger.warn("MundoService.processaJogadaAgricultor: Jogada não processada pois o jogo " + idJogo + " não está na lista. Payload=" + agrForm.toString());
	}
	
	public Agricultor getAgricultorById(int idJogo, int id) {
		int indexMundo = this.getIndexMundoById(idJogo);
		if(indexMundo > -1)
			return this.mundos.get(indexMundo).getAgricultorById(id, true);

		logger.warn("MundoService.getAgricultorById: Agricultor id=" + id +" não foi achado pois o jogo " + idJogo + " não está na lista.");
		return null;
	}
	
	public void processaJogadaFiscal(int idJogo, int idFis, FiscalAmbientalForm fisForm){
		int indexMundo = this.getIndexMundoById(idJogo);
		if (indexMundo > -1) {
			try {
				this.mundos.get(indexMundo).processaJogadaFiscal(idFis, fisForm);
			} catch (IOException e) {
				logger.error("MundoService.processaJogadaFiscal: Exception : " + e.getClass() + " : " + e.getMessage());
				e.printStackTrace();
			}
		}
		logger.warn("MundoService.processaJogadaFiscal: Não foi possível processar a jogada do fiscal pois o jogo " + idJogo + " não está na lista. Payload=" + fisForm.toString());
	}
	
	public void processaJogadaPrefeito(int idJogo, int idPref, PrefeitoForm prefForm) {
		int indexMundo = this.getIndexMundoById(idJogo);
		if (indexMundo > -1) {
			try {
				this.mundos.get(indexMundo).processaJogadaPrefeito(idPref, prefForm);
			} catch (IOException e) {
				logger.error("MundoService.getGameOverData: Exception : " + e.getClass() + " : " + e.getMessage());
				e.printStackTrace();
			}
		}
		logger.warn("MundoService.processaJogadaPrefeito: Não foi possível processar a jogada do prefeito pois o jogo " + idJogo + " não está na lista. Payload=" + prefForm.toString());
	}
	
	public void processaJogadaVereador(int idJogo, int idVer) {
		int indexMundo = this.getIndexMundoById(idJogo);
		if (indexMundo > -1)
			this.mundos.get(indexMundo).processaJogadaVereador(idVer);
		
		logger.warn("MundoService.processaJogadaVereador: Não foi possível processar a jogada do vereador id=" + idVer + " pois o jogo " + idJogo + " não está na lista.");
	}
	
	public FiscalAmbiental getFiscalAmbientalById(int idJogo, int idFis) {
		int indexMundo = this.getIndexMundoById(idJogo);
		if (indexMundo > -1)
			return this.mundos.get(indexMundo).getFiscalById(idFis, true);
		logger.warn("MundoService.getFisclaAmbientalById: Fiscal id=" + idFis + " não encontrado pois o jogo " + idJogo + " não está na lista.");
		return null;
	}
	
	public Prefeito getPrefeitoById(int idJogo, int idPref) {
		int indexMundo = this.getIndexMundoById(idJogo);
		if (indexMundo > -1)
			return this.mundos.get(indexMundo).getPrefeitoById(idPref, true);
		
		logger.warn("MundoService.getPrefeitoById: Prefeito id=" + idPref + " não encontrado pois o jogo " + idJogo + " não está na lista.");
		return null;
	}
	
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
	
	public ArrayList<Empresario> getListaEmpresario(int idJogo){
		int indexMundo = this.getIndexMundoById(idJogo);
		if (indexMundo > -1)
			return this.mundos.get(indexMundo).getListaEmpresario();
		logger.warn("MundoService.getListaEmpresario: Empresários não encontrados pois o jogo " + idJogo + " não está na lista.");
		return null;
	}
	
	public ArrayList<Agricultor> getListaAgricultor(int idJogo){
		int indexMundo = this.getIndexMundoById(idJogo);
		if (indexMundo > -1)
			return this.mundos.get(this.getIndexMundoById(idJogo)).getListaAgricultor();
		logger.warn("MundoService.getListaAgricultor: Agricultores não encontrados pois o jogo " + idJogo + " não está na lista.");
		return null;
	}
	
	public List<PessoaModel> getInfoPessoasByClasse(int idJogo, int classe){
		int indexMundo = this.getIndexMundoById(idJogo);
		if (indexMundo > -1)
			return this.mundos.get(indexMundo).getInfoPessoas("", false, classe, 0);
		logger.warn("MundoService.getInfoPessoasByClasse: Personagens da classe=" + classe + " não encontrados pois o jogo " + idJogo + " não está na lista.");
		return null;
	}
	
	public List<PessoaModel> getInfoPessoasByEtapa(int idJogo, int etapa){
		boolean segundaEtapa = (etapa == 2) ? true : false;
		int indexMundo = this.getIndexMundoById(idJogo);
		if(indexMundo > -1) {
			return this.mundos.get(indexMundo).getInfoPessoas("", segundaEtapa, (segundaEtapa ? -1 : 0), 0);
		}
		logger.warn("MundoService.getInfoPessoasByEtapa: Personagens não encontrados pois o jogo " + idJogo + " não está na lista.");
		return null;
	}
	
	public List<PessoaModel> getInfoPessoas(int idJogo, String cidade, boolean segundaEtapa, int papeis){
		int indexMundo = this.getIndexMundoById(idJogo);
		if(indexMundo > -1)
			return this.mundos.get(indexMundo).getInfoPessoas(cidade, segundaEtapa, papeis, 0);
		logger.warn("MundoService.getInfoPessoas: Personagens não encontrados pois o jogo " + idJogo + " não está na lista. Cidade=" + cidade + "; segundaEtapa=" + segundaEtapa + "; papeis=" + papeis);
		return null;
	}
	
	public boolean finalizarEtapa(int idJogo){
		int indexMundo = this.getIndexMundoById(idJogo);
		if(indexMundo > -1) {
			try {
				this.mundos.get(indexMundo).finalizarEtapa();
			} catch (IOException e) {
				logger.error("MundoService.finalizarEtapa: Exception : " + e.getClass() + " : " + e.getMessage());
				e.printStackTrace();
			}
			return true;
		}
		logger.warn("MundoService.finalizarEtapa: Etapa não finalizada pois o jogo " + idJogo + " não está na lista.");
		return false;
	}
	
	public JSONObject getFilePessoaByIdJSON(int idJogo, int id){
		int indexMundo = this.getIndexMundoById(idJogo);
		if(indexMundo > -1) {
			try {
				return this.mundos.get(indexMundo).getFilePessoaByIdJSON(id);
			} catch (IOException e) {
				logger.error("MundoService.getFilePessoaByIdJSON: Exception : " + e.getClass() + " : " + e.getMessage());
				e.printStackTrace();
			}
		}
		logger.warn("MundoService.getFilePessoaByIdJSON: arquivo não encontrado pois o jogo " + idJogo + " não está na lista. Id=" + id);
		return null;
	}
	
	public void adicionaOrcamentoById(int idJogo, Venda venda) {
		int indexMundo = this.getIndexMundoById(idJogo);
		if(indexMundo > -1)
			this.mundos.get(indexMundo).adicionaOrcamentoById(venda);
		logger.warn("MundoService.adicionaOrcamentoById: Orçamento não adicionado pois o jogo " + idJogo + " não está na lista. Payload=" + venda.toString());
	}
	
	public List<Venda> getOrcamentos(int idJogo, int idAgr){
		int indexMundo = this.getIndexMundoById(idJogo);
		if(indexMundo > -1)
			return this.mundos.get(indexMundo).getOrcamentos(idAgr);
		logger.warn("MundoService.getOrcamentos: Orçamentos não encontrados pois o jogo " + idJogo + " não está na lista. idAgr=" + idAgr);
		return null;
	}
	
	public void removeOrcamentoById(int idJogo, int idAgr, int idEmp, int idOrcamento) {
		int indexMundo = this.getIndexMundoById(idJogo);
		if(indexMundo > -1)
			this.mundos.get(indexMundo).removeOrcamentoById(idAgr, idEmp, idOrcamento);
		logger.warn("MundoService.removeOrcamentoById: Não foi possível remover o orçamento pois o jogo " + idJogo + " não está na lista. idAgr=" + idAgr + "; idEmp=" + idEmp + "; idOrcamento=" + idOrcamento);
	}
	
	public void adicionaVendaById(int idJogo, Venda venda) {
		int indexMundo = this.getIndexMundoById(idJogo);
		if (indexMundo > -1)
			this.mundos.get(indexMundo).adicionaVendaById(venda);
		System.out.println("MundoService.adicionaVendaById: Não foi possível adicionar a venda pois o jogo " + idJogo + " não está na lista. Payload=" + venda.toString());
	}
	
	public List<Venda> getVendas(int idJogo, int idEmp){
		int indexMundo = this.getIndexMundoById(idJogo);
		if(indexMundo > -1)
			return this.mundos.get(indexMundo).getOrcamentos(idEmp);
		logger.warn("MundoService.getVendas: Vendas não encontradas pois o jogo " + idJogo + " não está na lista. idEmp=" + idEmp);
		return null;
	}
	
	public void adicionaSugestaoVereador(int idJogo, int idVer, SugestaoVereador sugestao) {
		int indexMundo = this.getIndexMundoById(idJogo);
		if(indexMundo > -1)
			this.mundos.get(indexMundo).adicionaSugestaoOuResposta(idVer, sugestao);
		logger.warn("MundoService.adicionaSugestaoVereador: Não foi possível adicionar a sugestão pois o jogo " + idJogo + " não está na lista. Payload=" + sugestao.toString());
	}
	
	public void adicionaRespostaSugestaoVereador(int idJogo, int idPref, SugestaoVereador sugestao) {
		int indexMundo = this.getIndexMundoById(idJogo);
		if(indexMundo > -1) {
			this.mundos.get(indexMundo).removeSugestaoVereador(idPref, sugestao.getIdSugestao());
			this.mundos.get(indexMundo).adicionaSugestaoOuResposta(idPref, sugestao);
		}
		logger.warn("MundoService.adicionaRespostaSugestaoVereador: Não foi possível adicionar a resposta pois o jogo " + idJogo + " não está na lista. Payload=" + sugestao.toString());
	}
	
	public List<SugestaoVereador> getSugestoesVereador(int idJogo, int idPessoa){
		int indexMundo = this.getIndexMundoById(idJogo);
		if(indexMundo > -1)
			return this.mundos.get(indexMundo).getSugestoesVereador(idPessoa);
		System.out.println("MundoService.getSugestoesVereador: Não foi possível localizar as sugestões pois o jogo " + idJogo + " não está na lista. idPessoa=" + idPessoa);
		return null;
	}
	
	public boolean contaVoto(int idJogo, int[] votos) {
		int indexMundo = this.getIndexMundoById(idJogo);
		if(indexMundo > -1) {
			for (int i = 0; i < votos.length; i++) this.mundos.get(indexMundo).contaVoto(votos[i], i);
			return true;
		}
		System.out.println("MundoService.contaVoto: Não foi possível adicionar o voto pois o jogo " + idJogo + " não está na lista. Payload=" + votos.toString());
		return false;
	}
	
	public List<PessoaModel> getListaContatoChat(int idJogo, int idPessoa){
		int indexMundo = this.getIndexMundoById(idJogo);
		if (indexMundo > -1)
			return this.mundos.get(indexMundo).getListaContatoChat(idPessoa);
		logger.warn("MundoService.getListaContatoChat: Lista de contatos não encontrada pois o jogo " + idJogo + " não está na lista. idPessoa=" + idPessoa);
		return null;
	}
}
