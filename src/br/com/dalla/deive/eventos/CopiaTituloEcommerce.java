package br.com.dalla.deive.eventos;

import java.sql.Timestamp;

import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;

public class CopiaTituloEcommerce implements EventoProgramavelJava {

	public void beforeInsert(PersistenceEvent event) throws Exception { }

	public void afterInsert(PersistenceEvent event) throws Exception {
		this.copiarTitulos();
	}

	public void beforeUpdate(PersistenceEvent event) throws Exception { }

	public void afterUpdate(PersistenceEvent event) throws Exception { }

	public void beforeDelete(PersistenceEvent event) throws Exception { }

	public void afterDelete(PersistenceEvent event) throws Exception { }

	public void beforeCommit(TransactionContext event) throws Exception { }
	
	public void copiarTitulos() {
		
	}
	
	public DynamicVO getTgfcab(int nuNota) throws Exception {
		JapeWrapper DAO = JapeFactory.dao("CabecalhoNota");
		DynamicVO Vo = DAO.findOne("NUNOTA = ?", new Object[] { nuNota });
		return Vo;
	}

	public DynamicVO getTgftop(int codTipOper, Timestamp dhAlter) throws Exception {
		JapeWrapper DAO = JapeFactory.dao("TipoOperacao");
		DynamicVO Vo = DAO.findOne("CODTIPOPER = ? AND DHALTER = ?", new Object[] { codTipOper, dhAlter });
		return Vo;
	}

	public void mostrarNoConsole(String mensagem) {
		System.out.println("====================== Mensagem ======================\n========== Exclui título de nota ecommerce ===========\n" + mensagem + "\n======================================================");
	}

}
