package com.gorim.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;
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
	private Mundo mundo;
	
	@Autowired
	public MundoService() {
		//this.mundo = new Mundo();
	}
	
	public int processaMestre(MestreForm mestreForm) throws IOException {
		this.mundo = new Mundo(mestreForm.getQuantidadeJogadores());
		this.mundo.iniciarJogo();
		return 1;
	}
	
	public void adicionaTransferencia(Transfer transferencia) {
		this.mundo.adicionaTransferencia(transferencia);
	}
	
	public void changeFlagFimEtapa() {
		this.mundo.changeFlagFimEtapa();
	}
	
	public boolean[] verificaFinalizados(int etapa) {
		return this.mundo.verificaFinalizados(etapa);
	}
	
	public int verificaFimEtapa(int etapa) {
		return this.mundo.verificaFimEtapa(etapa);
	}
	
	public int papelSegundaEtapa(int idPessoa) {
		return this.mundo.papelSegundaEtapa(idPessoa);
	}
	
	public void processaJogadaEmpresario(int idEmp, EmpresarioForm empForm) throws IOException {
		this.mundo.processaJogadaEmpresario(idEmp, empForm);
	}
	
	public Empresario getEmpresarioById(int id) {
		return this.mundo.getEmpresarioById(id, true);
	}
	
	public List<ProdutoSimplifiedModel> getProdutosEmpresarios(){
		return this.mundo.getProdutosEmpresarios();
	}
	
	public void processaJogadaAgricultor(int idAgr, AgricultorForm agrForm) throws IOException {
		this.mundo.processaJogadaAgricultor(idAgr, agrForm);
	}
	
	public Agricultor getAgricultorById(int id) {
		return this.mundo.getAgricultorById(id, true);
	}
	
	public void processaJogadaFiscal(int idFis, FiscalAmbientalForm fisForm) throws IOException {
		this.mundo.processaJogadaFiscal(idFis, fisForm);
	}
	
	public void processaJogadaPrefeito(int idPref, PrefeitoForm prefForm) throws IOException {
		this.mundo.processaJogadaPrefeito(idPref, prefForm);
	}
	
	public void processaJogadaVereador(int idVer) {
		this.mundo.processaJogadaVereador(idVer);
	}
	
	public FiscalAmbiental getFiscalAmbientalById(int idFis) {
		return this.mundo.getFiscalById(idFis, true);
	}
	
	public Prefeito getPrefeitoById(int idPref) {
		return this.mundo.getPrefeitoById(idPref, true);
	}
	
	public Vereador getVereadorById(int idVer) {
		return this.mundo.getVereadorById(idVer, true);
	}
	
	public Prefeito getInfoPrefeitoByVereador(int idVer) {
		return this.mundo.getInfoPrefeitoByVereador(idVer);
	}
	
	public ArrayList<Empresario> getListaEmpresario(){
		return this.mundo.getListaEmpresario();
	}
	
	public ArrayList<Agricultor> getListaAgricultor(){
		return this.mundo.getListaAgricultor();
	}
	
	public List<PessoaModel> getInfoPessoasByClasse(int classe){
		return this.mundo.getInfoPessoasByClasse(classe);
	}
	
	public List<PessoaModel> getInfoPessoasByEtapa(int etapa){
		return this.mundo.getInfoPessoasByEtapa(etapa);
	}
	
	public List<PessoaModel> getInfoPessoasByCidade(String cidade, boolean segundaEtapa){
		return this.mundo.getInfoPessoasByCidade(cidade, segundaEtapa);
	}
	
	public void finalizarEtapa() throws IOException{
		this.mundo.finalizarEtapa();
	}
	
	public JSONObject getFilePessoaByIdJSON(int id) throws IOException{
		return this.mundo.getFilePessoaByIdJSON(id);
	}
	
	public MundoModel getInfoMundo(int idJogo) {
		return new MundoModel(
				this.mundo.getRodada(),
				this.mundo.getEtapa(),
				this.mundo.getPoluicaoMundo(),
				this.mundo.getIdJogo(),
				this.mundo.calculaProdutividadeMundo(),
				this.mundo.getQuantidadeJogadores(),
				this.mundo.getNomeEleitos()
		);
	}
	
	public void adicionaOrcamentoById(Venda venda) {
		this.mundo.adicionaOrcamentoById(venda);
	}
	
	public List<Venda> getOrcamentos(int idAgr){
		return this.mundo.getOrcamentos(idAgr);
	}
	
	public void removeOrcamentoById(int idAgr, int idEmp, int idOrcamento) {
		this.mundo.removeOrcamentoById(idAgr, idEmp, idOrcamento);
	}
	
	public void adicionaVendaById(Venda venda) {
		this.mundo.adicionaVendaById(venda);
	}
	
	public List<Venda> getVendas(int idEmp){
		return this.mundo.getOrcamentos(idEmp);
	}
	
	public void adicionaSugestaoVereador(int idVer, SugestaoVereador sugestao) {
		this.mundo.adicionaSugestaoVereador(idVer, sugestao);
	}
	
	public void adicionaRespostaSugestaoVereador(int idPref, SugestaoVereador sugestao) {
		this.mundo.removeSugestaoVereador(idPref, sugestao.getIdSugestao());
		this.mundo.adicionaSugestaoVereador(idPref, sugestao);
	}
	
	public List<SugestaoVereador> getSugestoesVereador(int idPessoa){
		return this.mundo.getSugestoesVereador(idPessoa);
	}
	
	public void contaVoto(int[] votos) {
		for (int i = 0; i < votos.length; i++) {
			this.mundo.contaVoto(votos[i], i);
			System.out.print(votos[i] + " ");
		}
		System.out.println();
	}
}
