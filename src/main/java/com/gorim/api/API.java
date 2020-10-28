package com.gorim.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.gorim.model.MundoModel;
import com.gorim.model.PessoaModel;
import com.gorim.model.ProdutoSimplifiedModel;
import com.gorim.model.forms.AgricultorForm;
import com.gorim.model.forms.EmpresarioForm;
import com.gorim.model.forms.EmpresarioSellFormParcel;
import com.gorim.model.forms.MestreForm;
import com.gorim.model.forms.Transfer;
import com.gorim.model.forms.Venda;
import com.gorim.motorJogo.Agricultor;
import com.gorim.motorJogo.Empresario;
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
	public int postForm(@RequestBody MestreForm mestreForm) {
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
	
//	@GetMapping(
//			path = "/arquivoResumo/{id}",
//			produces = MediaType.APPLICATION_OCTET_STREAM_VALUE
//	)
//	public @ResponseBody ResponseEntity<ByteArrayResource> getArquivoResumo(@PathVariable("id") int id) throws IOException{
//		return this.mundoService.getFilePessoaById(id);
//	}
	
	@GetMapping(path = "/arquivoResumo/{id}")
	public JSONObject getArquivoResumo(@PathVariable("id") int id) throws IOException{
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
	
	@GetMapping(path = "/agricultor/{id}")
	public Agricultor getAgricultor(@PathVariable("id") int id) {
		return this.mundoService.getAgricultorById(id);
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
	
}
