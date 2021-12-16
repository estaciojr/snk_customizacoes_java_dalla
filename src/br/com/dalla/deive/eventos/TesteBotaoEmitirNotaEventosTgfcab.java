package br.com.dalla.deive.eventos;

import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;

public class TesteBotaoEmitirNotaEventosTgfcab implements EventoProgramavelJava {

	public void beforeInsert(PersistenceEvent event) throws Exception {
		System.out.println("TGFCAB beforeInsert");
	}

	public void afterInsert(PersistenceEvent event) throws Exception {
		System.out.println("TGFCAB afterInsert");
	}

	public void beforeUpdate(PersistenceEvent event) throws Exception {
		System.out.println("TGFCAB eforeUpdate");
	}
	
	public void afterUpdate(PersistenceEvent event) throws Exception {
		System.out.println("TGFCAB afterUpdate");
	}

	public void beforeDelete(PersistenceEvent event) throws Exception {
		System.out.println("TGFCAB beforeDelete");
	}

	public void afterDelete(PersistenceEvent event) throws Exception {
		System.out.println("TGFCAB afterDelete");
	}

	public void beforeCommit(TransactionContext event) throws Exception { }
	
}
