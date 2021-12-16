package br.com.dalla.deive.eventos;

import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;

public class TesteBotaoEmitirNotaEventosTgfite implements EventoProgramavelJava {

	public void beforeInsert(PersistenceEvent event) throws Exception {
		System.out.println("TGFITE beforeInsert");
	}

	public void afterInsert(PersistenceEvent event) throws Exception {
		System.out.println("TGFITE afterInsert");
	}

	public void beforeUpdate(PersistenceEvent event) throws Exception {
		System.out.println("TGFITE eforeUpdate");
	}
	
	public void afterUpdate(PersistenceEvent event) throws Exception {
		System.out.println("TGFITE afterUpdate");
	}

	public void beforeDelete(PersistenceEvent event) throws Exception {
		System.out.println("TGFITE beforeDelete");
	}

	public void afterDelete(PersistenceEvent event) throws Exception {
		System.out.println("TGFITE afterDelete");
	}

	public void beforeCommit(TransactionContext event) throws Exception { }
	
}
