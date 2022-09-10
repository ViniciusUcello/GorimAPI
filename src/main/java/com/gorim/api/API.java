package com.gorim.api;

import java.util.List;
import java.util.stream.Collectors;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gorim.model.AuthenticationRequest;
import com.gorim.model.AuthenticationResponse;
import com.gorim.model.CustomUserDetails;
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


@RequestMapping("/request/api")
@RestController
public class API {
	
	@Autowired
	private MundoService mundoService;
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private JwtUtil jwtUtil;
	
	@PostMapping(value = "/authenticate")
	public ResponseEntity<?> authenticateUser(@RequestBody AuthenticationRequest loginRequest) {

		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = jwtUtil.generateJwtToken(authentication);
		
		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();		
		@SuppressWarnings("unused")
		List<String> roles = userDetails.getAuthorities().stream()
				.map(item -> item.getAuthority())
				.collect(Collectors.toList());

		return ResponseEntity.ok(new AuthenticationResponse(jwt));
	}
	/**
	 * Endpoint para o recebimento do sinal de novo jogo (oriunda da página inicial)
	 * 
	 * @param mestreForm
	 * @return id do jogo novo (mundo novo)
	 */
	@PostMapping(path = "/mestre")
	public int postForm(@RequestBody MestreForm mestreForm) {
		return this.mundoService.processaMestre(mestreForm);
	}
	
	/**
	 * Endpoint que recebe o sinal de que o jogo tem que acabar
	 * 
	 * @param idJogo
	 * @return boolean para deu certo ou não a finalização
	 */
	@GetMapping(path = "/{idJogo}/mestre/finalizarJogo")
	public boolean terminarJogo(@PathVariable("idJogo") int idJogo) {
		return this.mundoService.finalizarJogo(idJogo);
	}
	
	/**
	 * Enpoint para pegar as informações de fim de jogo (resumo dos personagens)
	 * 
	 * @param idJogo
	 * @return JSON com os resultados finais dos jogadores
	 */
	@GetMapping(path = "/{idJogo}/mestre/gameover")
	public JSONObject getGameOverData(@PathVariable("idJogo") int idJogo) {
		return this.mundoService.getGameOverData(idJogo);
	}
	
	/**
	 * 
	 * @param idJogo
	 * @return
	 */
	@GetMapping(path = "/{idJogo}/mestre/infoMundo")
	public MundoModel infoMundo(@PathVariable("idJogo") int idJogo) {
		return this.mundoService.getInfoMundo(idJogo);
	}
	
	@PostMapping(path = "/{idJogo}/mestre/changeFlagFimEtapa")
	public void changeFlagFimEtapa(@PathVariable("idJogo") int idJogo) {
		this.mundoService.changeFlagFimEtapa(idJogo);
	}
	
	@PostMapping(path = "/{idJogo}/mestre/finalizarEtapa/{rodada}/{etapa}")
	public boolean finalizarEtapa(@PathVariable("idJogo") int idJogo, @PathVariable("rodada") int rodada, @PathVariable("etapa") int etapa) {
		return this.mundoService.finalizarEtapa(idJogo, rodada, etapa);
	}
	
	@PostMapping(path = "/{idJogo}/mestre/infoPessoasByEtapa")
	public List<PessoaModel> getInfoPessoasByEtapa(@PathVariable("idJogo") int idJogo, @RequestBody int etapa){
		return this.mundoService.getInfoPessoasByEtapa(idJogo, etapa);
	}
	
	@PostMapping(path = "/{idJogo}/mestre/infoPessoasByClasse")
	public List<PessoaModel> getInfoPessoasByClasse(@PathVariable("idJogo") int idJogo, @RequestBody int classe){
		return this.mundoService.getInfoPessoasByClasse(idJogo, classe);
	}
	
	@GetMapping(path = "/{idJogo}/mestre/infoPessoasForVoting/{cidade}")
	public List<PessoaModel> getInfoPessoasForVoting(
			@PathVariable("idJogo") int idJogo,
			@PathVariable String cidade
	){
		return this.mundoService.getInfoPessoas(idJogo, cidade, false, 0);
	}
	
	@GetMapping(path = "/{idJogo}/mestre/infoPessoasForFining/{cidade}")
	public List<PessoaModel> getInfoPessoasForFining(
			@PathVariable("idJogo") int idJogo,
			@PathVariable String cidade
	){
		return this.mundoService.getInfoPessoas(idJogo, cidade, false, 0);
	}
	
	@GetMapping(path = "/{idJogo}/mestre/infoPessoasForGreenSeal/{cidade}")
	public List<PessoaModel> getInfoPessoasForGreenSeal(
			@PathVariable("idJogo") int idJogo,
			@PathVariable String cidade
	){
		return this.mundoService.getInfoPessoas(idJogo, cidade, false, 2);
	}
	
	@PostMapping(path = "/{idJogo}/mestre/adicionaTransferencia")
	public boolean adicionaTransferencia(@PathVariable("idJogo") int idJogo, @RequestBody Transfer transferencia){
		return this.mundoService.adicionaTransferencia(idJogo, transferencia);
	}
	
	@PostMapping(path = "/{idJogo}/mestre/verificaFinalizados")
	public int[] verificaFinalizados(@PathVariable("idJogo") int idJogo, @RequestBody int etapa) {
		return this.mundoService.verificaFinalizados(idJogo, etapa);
	}
	
	@GetMapping(path = "/{idJogo}/mestre/verificaTodosTerminaramEtapa/{etapa}")
	public int verificaFimEtapa(@PathVariable("idJogo") int idJogo, @PathVariable("etapa") int etapa) {
		return this.mundoService.verificaTodosTerminaramEtapa(idJogo, etapa);
	}
	
	@GetMapping(path = "/{idJogo}/mestre/verificaTodosComecaramEtapa/{etapa}")
	public int verificaTodosComecaramEtapa(@PathVariable("idJogo") int idJogo, @PathVariable("etapa") int etapa) {
		return this.mundoService.verificaTodosComecaramEtapa(idJogo, etapa);
	}
	
	@GetMapping(path = "/{idJogo}/mestre/papelSegundaEtapa/{idPessoa}")
	public int papelSegundaEtapa(@PathVariable("idJogo") int idJogo, @PathVariable("idPessoa") int idPessoa) {
		return this.mundoService.papelSegundaEtapa(idJogo, idPessoa);
	}
	
	@PostMapping(path = "/{idJogo}/mestre/votar")
	public boolean votar(@PathVariable("idJogo") int idJogo, @RequestBody int[] votos) {
		return this.mundoService.contaVoto(idJogo, votos);
	}
	
	@GetMapping(path = "/{idJogo}/arquivoResumo/{idPessoa}")
	public JSONObject getArquivoResumo(
			@PathVariable("idJogo") int idJogo,
			@PathVariable("idPessoa") int idPessoa
	) {
		return this.mundoService.getFilePessoaByIdJSON(idJogo, idPessoa);
	}

	@PostMapping(path = "/{idJogo}/empresario/{idEmp}")
	public void processaJogadaEmpresario(
			@PathVariable("idJogo") int idJogo,
			@PathVariable("idEmp") int idEmp,
			@RequestBody EmpresarioForm empForm
	) {
		this.mundoService.processaJogadaEmpresario(idJogo, idEmp, empForm);
	}
	
	@GetMapping(path = "/{idJogo}/empresario/{idEmp}")
	public Empresario getEmpresario(@PathVariable("idJogo") int idJogo, @PathVariable("idEmp") int idEmp) {
		return this.mundoService.getEmpresarioById(idJogo, idEmp);
	}
	
	@PostMapping(path = "/{idJogo}/empresario/venda/{idAgr}")
	public void adicionaOrcamentoById(@PathVariable("idJogo") int idJogo, @RequestBody Venda venda) {
		this.mundoService.adicionaOrcamentoById(idJogo, venda);
	}
	
	@GetMapping(path = "/{idJogo}/empresario/venda/{idEmp}")
	public List<Venda> getVendas(@PathVariable("idJogo") int idJogo, @PathVariable("idEmp") int idEmp) {
		return this.mundoService.getVendas(idJogo, idEmp);
	}
	
	@PostMapping(path = "/{idJogo}/agricultor/{idAgr}")
	public void processaJogadaAgricultor(
			@PathVariable("idJogo") int idJogo,
			@PathVariable("idAgr") int idAgr,
			@RequestBody AgricultorForm postForm
	) {
		this.mundoService.processaJogadaAgricultor(idJogo, idAgr, postForm);
	}
	
	@GetMapping(path = "/{idJogo}/agricultor/{idAgr}")
	public Agricultor getAgricultor(@PathVariable("idJogo") int idJogo, @PathVariable("idAgr") int idAgr) {
		return this.mundoService.getAgricultorById(idJogo, idAgr);
	}
	
	@PostMapping(path = "/{idJogo}/agricultor/venda/{idEmp}")
	public void adicionaVendaById(@PathVariable("idJogo") int idJogo, @RequestBody Venda venda) {
		this.mundoService.adicionaVendaById(idJogo, venda);
	}
	
	@GetMapping(path = "/{idJogo}/agricultor/venda/{idAgr}")
	public List<Venda> getOrcamentos(@PathVariable("idJogo") int idJogo, @PathVariable("idAgr") int idAgr) {
		return this.mundoService.getOrcamentos(idJogo, idAgr);
	}
	
	@PostMapping(path = "/{idJogo}/agricultor/venda/delete/{idEmp}/{idAgr}")
	public void removeOrcamento(
			@PathVariable("idJogo") int idJogo,
			@PathVariable("idAgr") int idAgr,
			@PathVariable("idEmp") int idEmp,
			@RequestBody int idOrcamento
	) {
		this.mundoService.removeOrcamentoById(idJogo, idAgr, idEmp, idOrcamento);
	}
	
	@GetMapping(path = "/{idJogo}/agricultor/empresarios/produtos")
	public List<ProdutoSimplifiedModel> getProdutosEmpresarios(@PathVariable("idJogo") int idJogo){
		return this.mundoService.getProdutosEmpresarios(idJogo);
	}
	
	@PostMapping(path = "/{idJogo}/fiscal/{idFis}")
	public void processaJogadaFiscal(
			@PathVariable("idJogo") int idJogo,
			@PathVariable("idFis") int idFis,
			@RequestBody FiscalAmbientalForm fisForm
	) {
		this.mundoService.processaJogadaFiscal(idJogo, idFis, fisForm);
	}
	
	@GetMapping(path = "/{idJogo}/fiscal/{idFis}")
	public FiscalAmbiental getFiscalAmbiental(
			@PathVariable("idJogo") int idJogo,
			@PathVariable("idFis") int idFis
	) {
		return this.mundoService.getFiscalAmbientalById(idJogo, idFis);
	}
	
	@PostMapping(path = "/{idJogo}/prefeito/{idPref}")
	public void processaJogadaPrefeito(
			@PathVariable("idJogo") int idJogo,
			@PathVariable("idPref") int idPref,
			@RequestBody PrefeitoForm prefForm
	) {
		this.mundoService.processaJogadaPrefeito(idJogo, idPref, prefForm);
	}
	
	@GetMapping(path = "/{idJogo}/prefeito/{idPref}")
	public Prefeito getPrefeito(@PathVariable("idJogo") int idJogo, @PathVariable("idPref") int idPref) {
		return this.mundoService.getPrefeitoById(idJogo, idPref);
	}
	
	@PostMapping(path = "/{idJogo}/prefeito/adicionaRespostaSugestao/{idPref}")
	public void adicionaRespostaSugestaoVereador(
			@PathVariable("idJogo") int idJogo,
			@PathVariable("idPref") int idPref,
			@RequestBody SugestaoVereador sugestao
	) {
		this.mundoService.adicionaRespostaSugestaoVereador(idJogo, idPref, sugestao);
	}
	
	@GetMapping(path = "/{idJogo}/prefeito/getSugestoesVereador/{idPref}")
	public List<SugestaoVereador> getSugestoesVereador(
			@PathVariable("idJogo") int idJogo,
			@PathVariable("idPref") int idPref
	){
		return this.mundoService.getSugestoesVereador(idJogo, idPref);
	}
	
	@PostMapping(path = "/{idJogo}/vereador/{idVer}")
	public void processaJogadaVereador(@PathVariable("idJogo") int idJogo, @PathVariable("idVer") int idVer) {
		this.mundoService.processaJogadaVereador(idJogo, idVer);
	}
	
	@GetMapping(path = "/{idJogo}/vereador/{idVer}")
	public Vereador getVereador(@PathVariable("idJogo") int idJogo, @PathVariable("idVer") int idVer) {
		return this.mundoService.getVereadorById(idJogo, idVer);
	}
	
	@PostMapping(path = "/{idJogo}/vereador/adicionaSugestao/{idVer}")
	public void adicionaSugestaoVereador(
			@PathVariable("idJogo") int idJogo,
			@PathVariable("idVer") int idVer,
			@RequestBody SugestaoVereador sugestao
	) {
		this.mundoService.adicionaSugestaoVereador(idJogo, idVer, sugestao);
	}
	
	@GetMapping(path = "/{idJogo}/vereador/getRespostasPrefeito/{idVer}")
	public List<SugestaoVereador> getRespostaPrefeito(
			@PathVariable("idJogo") int idJogo,
			@PathVariable("idVer") int idVer
	){
		return this.mundoService.getSugestoesVereador(idJogo, idVer);
	}
	
	@GetMapping(path = "/{idJogo}/vereador/infoPrefeito/{idVer}")
	public Prefeito getInfoPrefeitoByVereador(
			@PathVariable("idJogo") int idJogo,
			@PathVariable("idVer") int idVer
	){
		return this.mundoService.getInfoPrefeitoByVereador(idJogo, idVer);
	}
	
	
	@GetMapping(path = "/{idJogo}/chat/listaContatoChat/{idPessoa}")
	public List<PessoaModel> getListaContatoChat(@PathVariable("idJogo") int idJogo, @PathVariable int idPessoa){
		return this.mundoService.getListaContatoChat(idJogo, idPessoa);
	}
	
}
