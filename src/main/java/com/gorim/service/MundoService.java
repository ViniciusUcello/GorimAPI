package com.gorim.service;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
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
	
	public MundoService() {
		this.mundos = new ArrayList<Mundo>();
	}
	
	@SuppressWarnings("unchecked")
	public int processaMestre(MestreForm mestreForm) throws IOException {
		String fileName = "jogos/ultimoJogo.json";
		int idUltimoJogo = -1;

        JSONParser parser = new JSONParser();

		try (Reader reader = new FileReader(fileName)) {

    		JSONObject arquivoJSON = (JSONObject) parser.parse(reader);
    		
    		idUltimoJogo = ((Long) arquivoJSON.get("idJogo")).intValue();
    		  
    		
           
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
		
		int idJogoNovo = (idUltimoJogo > -1) ? (idUltimoJogo + 1) : idUltimoJogo;
		
		if(idJogoNovo > 0) {
			JSONObject jogoNovo = new JSONObject();
			jogoNovo.put("idJogo", idJogoNovo);

			try (FileWriter fileW = new FileWriter(fileName)) {
	            fileW.write(jogoNovo.toJSONString());
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
			
			
			this.mundos.add(new Mundo(idJogoNovo, mestreForm.getQuantidadeJogadores(), userRepository));
			this.mundos.get(this.getIndexMundoById(idJogoNovo)).iniciarJogo();
		}
		
		return idJogoNovo;
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
		return new MundoModel(
				this.mundos.get(indexMundo).getRodada(),
				this.mundos.get(indexMundo).getEtapa(),
				this.mundos.get(indexMundo).getPoluicaoMundo(),
				this.mundos.get(indexMundo).getIdJogo(),
				this.mundos.get(indexMundo).calculaProdutividadeMundo(),
				this.mundos.get(indexMundo).getQuantidadeJogadores(),
				this.mundos.get(indexMundo).getNomeEleitos()
		);
	}
	
	public void adicionaTransferencia(int idJogo, Transfer transferencia) {
		this.mundos.get(this.getIndexMundoById(idJogo)).adicionaTransferencia(transferencia);
	}
	
	public void changeFlagFimEtapa(int idJogo) {
		this.mundos.get(this.getIndexMundoById(idJogo)).changeFlagFimEtapa();
	}
	
	public boolean[] verificaFinalizados(int idJogo, int etapa) {
		return this.mundos.get(this.getIndexMundoById(idJogo)).verificaFinalizados(etapa);
	}
	
	public int verificaFimEtapa(int idJogo, int etapa) {
		return this.mundos.get(this.getIndexMundoById(idJogo)).verificaFimEtapa(etapa);
	}
	
	public int papelSegundaEtapa(int idJogo, int idPessoa) {
		return this.mundos.get(this.getIndexMundoById(idJogo)).papelSegundaEtapa(idPessoa);
	}
	
	public void processaJogadaEmpresario(int idJogo, int idEmp, EmpresarioForm empForm) throws IOException {
		this.mundos.get(this.getIndexMundoById(idJogo)).processaJogadaEmpresario(idEmp, empForm);
	}
	
	public Empresario getEmpresarioById(int idJogo, int id) {
		System.out.println("idjogo " + idJogo);
		return this.mundos.get(this.getIndexMundoById(idJogo)).getEmpresarioById(id, true);
	}
	
	public List<ProdutoSimplifiedModel> getProdutosEmpresarios(int idJogo){
		return this.mundos.get(this.getIndexMundoById(idJogo)).getProdutosEmpresarios();
	}
	
	public void processaJogadaAgricultor(int idJogo, int idAgr, AgricultorForm agrForm) throws IOException {
		this.mundos.get(this.getIndexMundoById(idJogo)).processaJogadaAgricultor(idAgr, agrForm);
	}
	
	public Agricultor getAgricultorById(int idJogo, int id) {
		return this.mundos.get(this.getIndexMundoById(idJogo)).getAgricultorById(id, true);
	}
	
	public void processaJogadaFiscal(int idJogo, int idFis, FiscalAmbientalForm fisForm) throws IOException {
		this.mundos.get(this.getIndexMundoById(idJogo)).processaJogadaFiscal(idFis, fisForm);
	}
	
	public void processaJogadaPrefeito(int idJogo, int idPref, PrefeitoForm prefForm) throws IOException {
		this.mundos.get(this.getIndexMundoById(idJogo)).processaJogadaPrefeito(idPref, prefForm);
	}
	
	public void processaJogadaVereador(int idJogo, int idVer) {
		this.mundos.get(this.getIndexMundoById(idJogo)).processaJogadaVereador(idVer);
	}
	
	public FiscalAmbiental getFiscalAmbientalById(int idJogo, int idFis) {
		return this.mundos.get(this.getIndexMundoById(idJogo)).getFiscalById(idFis, true);
	}
	
	public Prefeito getPrefeitoById(int idJogo, int idPref) {
		return this.mundos.get(this.getIndexMundoById(idJogo)).getPrefeitoById(idPref, true);
	}
	
	public Vereador getVereadorById(int idJogo, int idVer) {
		return this.mundos.get(this.getIndexMundoById(idJogo)).getVereadorById(idVer, true);
	}
	
	public Prefeito getInfoPrefeitoByVereador(int idJogo, int idVer) {
		return this.mundos.get(this.getIndexMundoById(idJogo)).getInfoPrefeitoByVereador(idVer);
	}
	
	public ArrayList<Empresario> getListaEmpresario(int idJogo){
		return this.mundos.get(this.getIndexMundoById(idJogo)).getListaEmpresario();
	}
	
	public ArrayList<Agricultor> getListaAgricultor(int idJogo){
		return this.mundos.get(this.getIndexMundoById(idJogo)).getListaAgricultor();
	}
	
	public List<PessoaModel> getInfoPessoasByClasse(int idJogo, int classe){
		System.out.println("idJogo " + idJogo);
		return this.mundos.get(this.getIndexMundoById(idJogo)).getInfoPessoas("", false, classe, 0);
	}
	
	public List<PessoaModel> getInfoPessoasByEtapa(int idJogo, int etapa){
		boolean segundaEtapa = (etapa == 2) ? true : false;
		int indexMundo = this.getIndexMundoById(idJogo);
		if(indexMundo > -1) {
			return this.mundos.get(indexMundo).getInfoPessoas("", segundaEtapa, (segundaEtapa ? -1 : 0), 0);
		}
		return null;
	}
	
	public List<PessoaModel> getInfoPessoas(int idJogo, String cidade, boolean segundaEtapa, int papeis){
		return this.mundos.get(this.getIndexMundoById(idJogo)).getInfoPessoas(cidade, segundaEtapa, papeis, 0);
	}
	
	public void finalizarEtapa(int idJogo) throws IOException{
		this.mundos.get(this.getIndexMundoById(idJogo)).finalizarEtapa();
	}
	
	public JSONObject getFilePessoaByIdJSON(int idJogo, int id) throws IOException{
		return this.mundos.get(this.getIndexMundoById(idJogo)).getFilePessoaByIdJSON(id);
	}
	
	public void adicionaOrcamentoById(int idJogo, Venda venda) {
		this.mundos.get(this.getIndexMundoById(idJogo)).adicionaOrcamentoById(venda);
	}
	
	public List<Venda> getOrcamentos(int idJogo, int idAgr){
		return this.mundos.get(this.getIndexMundoById(idJogo)).getOrcamentos(idAgr);
	}
	
	public void removeOrcamentoById(int idJogo, int idAgr, int idEmp, int idOrcamento) {
		this.mundos.get(this.getIndexMundoById(idJogo)).removeOrcamentoById(idAgr, idEmp, idOrcamento);
	}
	
	public void adicionaVendaById(int idJogo, Venda venda) {
		this.mundos.get(this.getIndexMundoById(idJogo)).adicionaVendaById(venda);
	}
	
	public List<Venda> getVendas(int idJogo, int idEmp){
		return this.mundos.get(this.getIndexMundoById(idJogo)).getOrcamentos(idEmp);
	}
	
	public void adicionaSugestaoVereador(int idJogo, int idVer, SugestaoVereador sugestao) {
		this.mundos.get(this.getIndexMundoById(idJogo)).adicionaSugestaoOuResposta(idVer, sugestao);
	}
	
	public void adicionaRespostaSugestaoVereador(int idJogo, int idPref, SugestaoVereador sugestao) {
		int indexMundo = this.getIndexMundoById(idJogo);
		this.mundos.get(indexMundo).removeSugestaoVereador(idPref, sugestao.getIdSugestao());
		this.mundos.get(indexMundo).adicionaSugestaoOuResposta(idPref, sugestao);
	}
	
	public List<SugestaoVereador> getSugestoesVereador(int idJogo, int idPessoa){
		return this.mundos.get(this.getIndexMundoById(idJogo)).getSugestoesVereador(idPessoa);
	}
	
	public void contaVoto(int idJogo, int[] votos) {
		for (int i = 0; i < votos.length; i++) {
			this.mundos.get(this.getIndexMundoById(idJogo)).contaVoto(votos[i], i);
			System.out.print(votos[i] + " ");
		}
		System.out.println();
	}
	
	public List<PessoaModel> getListaContatoChat(int idJogo, int idPessoa){
		return this.mundos.get(this.getIndexMundoById(idJogo)).getListaContatoChat(idPessoa);
	}
	
	/*
	public void mandarMessagem(int idJogo, Message mensagem) {
		this.mundos.get(this.getIndexMundoById(idJogo)).mandarMensagem(mensagem);
	}
	
	public List<Message> getNovasMensagens(int idJogo, int idPessoa, int idDestinatario, int ultimaMensagem){
		return this.mundos.get(this.getIndexMundoById(idJogo)).getNovasMensagens(idPessoa, idDestinatario, ultimaMensagem);
	}*/
	
	public void terminarJogo(int idJogo) {
		this.mundos.remove(this.getIndexMundoById(idJogo));
	}
}
