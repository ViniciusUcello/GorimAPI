package com.gorim.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
import com.gorim.model.forms.AgricultorForm;
import com.gorim.model.forms.EmpresarioForm;
import com.gorim.model.forms.EmpresarioSellFormParcel;
import com.gorim.model.forms.MestreForm;
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
		this.mundoService.processaMestre(mestreForm);
		return 1;
	}
	
	@GetMapping(path = "/mestre/infoMundo/{idJogo}")
	public MundoModel infoMundo(@PathVariable("idJogo") int idJogo) {
		return this.mundoService.getInfoMundo(idJogo);
	}
	
	@PostMapping(path = "/mestre/finalizaEtapa")
	public void testeFinalizaEtapa() throws IOException{
		this.mundoService.testeFinalizaEtapa();
	}
	
	@GetMapping(path = "/mestre/empresarios")
	public ArrayList<Empresario> getListaEmpresario(){
		return this.mundoService.getListaEmpresario();
	}
	
	@GetMapping(path = "/mestre/agricultores")
	public ArrayList<Agricultor> getListaAgricultor(){
		return this.mundoService.getListaAgricultor();
	}
	
	@GetMapping(
			path = "/arquivoResumo/{id}",
			produces = MediaType.APPLICATION_OCTET_STREAM_VALUE
	)
	public @ResponseBody ResponseEntity<ByteArrayResource> getArquivoResumo(@PathVariable("id") int id) throws IOException{
		return this.mundoService.getFilePessoaById(id);
	}

	@PostMapping(path = "/empresario")
	public void postForm(@RequestBody EmpresarioForm empresarioForm) throws IOException {
		this.mundoService.processaJogadaEmpresario(empresarioForm);
	}
	
	@GetMapping(path = "/empresario/{id}")
	public Empresario getEmpresario(@PathVariable("id") int id) {
		return this.mundoService.getEmpresarioById(id);
	}
	
	@PostMapping(path = "/empresario/{id}/venda")
	public void postEmpresarioSellFormParcel(
			@PathVariable("id") int id,
			@RequestBody EmpresarioSellFormParcel empSellForm
	) {
		this.mundoService.empresarioSellFormParcel(id, empSellForm);
	}
	
	@PostMapping(path = "/empresario/venda/{idAgr}")
	public void adicionaOrcamentoById(@RequestBody Venda venda) {
		this.mundoService.adicionaOrcamentoById(venda);
	}
	
	@GetMapping(path = "/empresario/venda/{idEmp}")
	public List<Venda> getVendas(@PathVariable("idEmp") int idEmp) {
		return this.mundoService.getVendas(idEmp);
	}
	
	@PostMapping(path = "/agricultor")
	public void postForm(@RequestBody AgricultorForm agricultorForm) throws IOException {
		this.mundoService.processaJogadaAgricultor(agricultorForm);
	}
	
	@PostMapping(path = "/agricultor/venda/{idEmp}")
	public void adicionaVendaById(@RequestBody Venda venda) {
		System.out.println("Entrou API.adicionavendaById()");
		this.mundoService.adicionaVendaById(venda);
	}
	
	@GetMapping(path = "/agricultor/venda/{idAgr}")
	public List<Venda> getOrcamentos(@PathVariable("idAgr") int idAgr) {
		return this.mundoService.getOrcamentos(idAgr);
	}
	
	@PostMapping(path = "/agricultor/venda/delete/{idAgr}")
	public void removeOrcamento(@RequestBody Venda venda) {
		this.mundoService.removeOrcamentoById(venda);
	}
	
	@GetMapping(path = "/agricultor/{id}")
	public Agricultor getAgricultor(@PathVariable("id") int id) {
		return this.mundoService.getAgricultorById(id);
	}
	
}
