package com.gorim.service;

import java.io.IOException;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.gorim.model.forms.AgricultorForm;
import com.gorim.model.forms.EmpresarioForm;
import com.gorim.model.forms.EmpresarioSellFormParcel;
import com.gorim.model.forms.MestreForm;
import com.gorim.model.forms.Pedido;
import com.gorim.model.forms.Transfer;
import com.gorim.motorJogo.Agricultor;
import com.gorim.motorJogo.Empresario;
import com.gorim.motorJogo.Mundo;

@Service
public class MundoService {
	private Mundo mundo;
	private boolean[] et1;
	private boolean[] et2;
	
	@Autowired
	public MundoService() {
		this.mundo = new Mundo();
	}
	
	public void processaMestre(MestreForm mestreForm) {
		this.mundo.setPlayerQuantity(mestreForm.getQuantidadeJogadores());
		this.et1 = new boolean[mestreForm.getQuantidadeJogadores()];
		this.et2 = new boolean[6];
		this.limpaEts();
		this.mundo.iniciarJogo();
	}
	
	private void setJaJogou(int tipoJogador, int idJogador) {
		if(tipoJogador < 3) this.et1[idJogador-1] = true;
		else this.et2[idJogador-this.mundo.getQuantidadeJogadores()] = true;
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
	
	/*private boolean verificaFimEtapa1() {
		for (boolean et : this.et2)
			if(!et) return false;
		return true;
	}*/
	
	public void processaJogadaEmpresario(EmpresarioForm empForm) throws IOException {
		if(empForm.temTransferencias()) {
			for (Transfer transfer : empForm.getTransferencias() ) {
				this.mundo.transferirDinheiros(
						empForm.getId(),
						transfer.getDestinatario(),
						transfer.getValor()
				);
			}
		}
		this.setJaJogou(1, empForm.getId());
		//if(this.verificaFimEtapa1()) this.mundo.finalizaEtapa();
	}
	
	public Empresario getEmpresarioById(int id) {
		return this.mundo.getEmpresarioById(id);
	}
	
	public void empresarioSellFormParcel(int idEmp, EmpresarioSellFormParcel empSellForm) {
		this.mundo.venda(
				empSellForm.getIdAgr(),
				empSellForm.getNumParcela(),
				empSellForm.getIdProd(),
				empSellForm.getPrecoProd()
		);
	}
	
	public void processaJogadaAgricultor(AgricultorForm agrForm) throws IOException {
		
		if(agrForm.temTransferencias()) {
			for (Transfer transfer : agrForm.getTranferencias() ) {
				this.mundo.transferirDinheiros(
						agrForm.getId(),
						transfer.getDestinatario(),
						transfer.getValor()
				);
			}
		}
		
		// CONTINUAR AQUI pedidos
		
		if(agrForm.temPedidos()) {
			for(Pedido pedido : agrForm.getPedidos() ) {
				this.mundo.setPedidoFiscal(
						agrForm.getId(),
						pedido.toString()
				);
			}
		}

		this.setJaJogou(2, agrForm.getId());
		//if(this.verificaFimEtapa1()) this.mundo.finalizaEtapa();
	}
	
	public Agricultor getAgricultorById(int id) {
		return this.mundo.getAgricultorById(id);
	}
	
	public ArrayList<Empresario> getListaEmpresario(){
		return this.mundo.getListaEmpresario();
	}
	
	public ArrayList<Agricultor> getListaAgricultor(){
		return this.mundo.getListaAgricultor();
	}
	
	public void testeFinalizaEtapa() throws IOException{
		this.mundo.finalizaEtapa();
	}
	
	public ResponseEntity<ByteArrayResource> getFilePessoaById(int id) throws IOException{
		return this.mundo.getFilePessoaById(id);
	}
	
}
