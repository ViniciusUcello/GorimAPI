package com.gorim.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
import com.gorim.motorJogo.Prefeito;
import com.gorim.motorJogo.Vereador;
import com.gorim.service.MundoService;


@RequestMapping("request/api")
@RestController
public class API {
	private final MundoService mundoService;
	
	@Autowired
	public API(MundoService mundoService) {
		this.mundoService = mundoService;
	}
	
	@PostMapping(path = "/mestre")
	public int postForm(@RequestBody MestreForm mestreForm) throws IOException {
		return this.mundoService.processaMestre(mestreForm);
	}
	
	@GetMapping(path = "/mestre/infoMundo/{idJogo}")
	public MundoModel infoMundo(@PathVariable("idJogo") int idJogo) {
		return this.mundoService.getInfoMundo(idJogo);
	}
	
	@PostMapping(path = "/mestre/changeFlagFimEtapa")
	public void changeFlagFimEtapa() {
		this.mundoService.changeFlagFimEtapa();
	}
	
	@PostMapping(path = "/mestre/finalizarEtapa")
	public void finalizarEtapa() throws IOException{
		this.mundoService.finalizarEtapa();
	}
	
	@GetMapping(path = "/mestre/empresarios")
	public ArrayList<Empresario> getListaEmpresario(){
		return this.mundoService.getListaEmpresario();
	}
	
	@GetMapping(path = "/mestre/agricultores")
	public ArrayList<Agricultor> getListaAgricultor(){
		return this.mundoService.getListaAgricultor();
	}
	
	@PostMapping(path = "/mestre/infoPessoasByEtapa")
	public List<PessoaModel> getInfoPessoasByEtapa(@RequestBody int etapa){
		return this.mundoService.getInfoPessoasByEtapa(etapa);
	}
	
	@PostMapping(path = "/mestre/infoPessoasByClasse")
	public List<PessoaModel> getInfoPessoasByClasse(@RequestBody int classe){
		return this.mundoService.getInfoPessoasByClasse(classe);
	}
	
	@GetMapping(path = "/mestre/infoPessoasForVoting/{cidade}")
	public List<PessoaModel> getInfoPessoasForVoting(@PathVariable String cidade){
		return this.mundoService.getInfoPessoasByCidade(cidade, false);
	}
	
	@PostMapping(path = "/mestre/adicionaTransferencia")
	public void adicionaTransferencia(@RequestBody Transfer transferencia){
		this.mundoService.adicionaTransferencia(transferencia);
	}
	
	@PostMapping(path = "/mestre/verificaFinalizados")
	public boolean[] verificaFinalizados(@RequestBody int etapa) {
		return this.mundoService.verificaFinalizados(etapa);
	}
	
	@GetMapping(path = "/mestre/verificaFimEtapa/{etapa}")
	public int verificaFimEtapa(@PathVariable("etapa") int etapa) {
		return this.mundoService.verificaFimEtapa(etapa);
	}
	
	@GetMapping(path = "/mestre/papelSegundaEtapa/{idPessoa}")
	public int papelSegundaEtapa(@PathVariable("idPessoa") int idPessoa) {
		return this.mundoService.papelSegundaEtapa(idPessoa);
	}
	
	@PostMapping(path = "/mestre/votar")
	public void votar(@RequestBody int[] votos) {
		this.mundoService.contaVoto(votos);
	}
	
	@GetMapping(path = "/arquivoResumo/{id}")
	public JSONObject getArquivoResumo(@PathVariable("id") int id) throws IOException {
		return this.mundoService.getFilePessoaByIdJSON(id);
	}

	@PostMapping(path = "/empresario/{idEmp}")
	public void finalizaJogada(@PathVariable("idEmp") int idEmp, @RequestBody EmpresarioForm empForm) throws IOException {
		this.mundoService.processaJogadaEmpresario(idEmp, empForm);
	}
	
	@GetMapping(path = "/empresario/{id}")
	public Empresario getEmpresario(@PathVariable("id") int id) {
		return this.mundoService.getEmpresarioById(id);
	}
	
	@PostMapping(path = "/empresario/venda/{idAgr}")
	public void adicionaOrcamentoById(@RequestBody Venda venda) {
		this.mundoService.adicionaOrcamentoById(venda);
	}
	
	@GetMapping(path = "/empresario/venda/{idEmp}")
	public List<Venda> getVendas(@PathVariable("idEmp") int idEmp) {
		return this.mundoService.getVendas(idEmp);
	}
	
	@PostMapping(path = "/agricultor/{idAgr}")
	public void postForm(
			@PathVariable("idAgr") int idAgr,
			@RequestBody AgricultorForm postForm
	) throws IOException {
		this.mundoService.processaJogadaAgricultor(idAgr, postForm);
	}
	
	@GetMapping(path = "/agricultor/{idAgr}")
	public Agricultor getAgricultor(@PathVariable("idAgr") int idAgr) {
		return this.mundoService.getAgricultorById(idAgr);
	}
	
	@PostMapping(path = "/agricultor/venda/{idEmp}")
	public void adicionaVendaById(@RequestBody Venda venda) {
		this.mundoService.adicionaVendaById(venda);
	}
	
	@GetMapping(path = "/agricultor/venda/{idAgr}")
	public List<Venda> getOrcamentos(@PathVariable("idAgr") int idAgr) {
		return this.mundoService.getOrcamentos(idAgr);
	}
	
	@PostMapping(path = "/agricultor/venda/delete/{idEmp}/{idAgr}")
	public void removeOrcamento(@PathVariable("idAgr") int idAgr, @PathVariable("idEmp") int idEmp, @RequestBody int idOrcamento) {
		this.mundoService.removeOrcamentoById(idAgr, idEmp, idOrcamento);
	}
	
	@GetMapping(path = "/agricultor/empresarios/produtos")
	public List<ProdutoSimplifiedModel> getProdutosEmpresarios(){
		return this.mundoService.getProdutosEmpresarios();
	}
	
	@PostMapping(path = "/fiscal/{idFis}")
	public void processaJogadaFiscal(@PathVariable("idFis") int idFis, @RequestBody FiscalAmbientalForm fisForm) throws IOException {
		this.mundoService.processaJogadaFiscal(idFis, fisForm);
	}
	
	@GetMapping(path = "/fiscal/{idFis}")
	public FiscalAmbiental getFiscalAmbiental(@PathVariable("idFis") int idFis) {
		return this.mundoService.getFiscalAmbientalById(idFis);
	}
	
	@PostMapping(path = "/prefeito/{idPref}")
	public void processaJogadaPrefeito(@PathVariable("idPref") int idPref, @RequestBody PrefeitoForm prefForm) throws IOException {
		this.mundoService.processaJogadaPrefeito(idPref, prefForm);
	}
	
	@GetMapping(path = "/prefeito/{idPref}")
	public Prefeito getPrefeito(@PathVariable("idPref") int idPref) {
		return this.mundoService.getPrefeitoById(idPref);
	}
	
	@PostMapping(path = "/prefeito/adicionaRespostaSugestao/{idPref}")
	public void adicionaRespostaSugestaoVereador(@PathVariable("idPref") int idPref, @RequestBody SugestaoVereador sugestao) {
		this.mundoService.adicionaRespostaSugestaoVereador(idPref, sugestao);
	}
	
	@GetMapping(path = "/prefeito/getSugestoesVereador/{idPref}")
	public List<SugestaoVereador> getSugestoesVereador(@PathVariable("idPref") int idPref){
		return this.mundoService.getSugestoesVereador(idPref);
	}
	
	@PostMapping(path = "/vereador/{idVer}")
	public void processaJogadaVereador(@PathVariable("idVer") int idVer) {
		this.mundoService.processaJogadaVereador(idVer);
	}
	
	@GetMapping(path = "/vereador/{idVer}")
	public Vereador getVereador(@PathVariable("idVer") int idVer) {
		return this.mundoService.getVereadorById(idVer);
	}
	
	@PostMapping(path = "/vereador/adicionaSugestao/{idVer}")
	public void adicionaSugestaoVereador(@PathVariable("idVer") int idVer, @RequestBody SugestaoVereador sugestao) {
		this.mundoService.adicionaSugestaoVereador(idVer, sugestao);
	}
	
	@GetMapping(path = "/vereador/getRespostasPrefeito/{idVer}")
	public List<SugestaoVereador> getRespostaPrefeito(@PathVariable("idVer") int idVer){
		return this.mundoService.getSugestoesVereador(idVer);
	}
	
	@GetMapping(path = "/vereador/infoPrefeito/{idVer}")
	public Prefeito getInfoPrefeitoByVereador(@PathVariable("idVer") int idVer){
		return this.mundoService.getInfoPrefeitoByVereador(idVer);
	}
	
}
