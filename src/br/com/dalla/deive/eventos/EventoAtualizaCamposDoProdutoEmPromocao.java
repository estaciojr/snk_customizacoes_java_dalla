package br.com.dalla.deive.eventos;

import br.com.dalla.deive.util.ProdutoDaPromocao;
import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;

public class EventoAtualizaCamposDoProdutoEmPromocao implements EventoProgramavelJava {

	@Override
	public void afterDelete(PersistenceEvent arg0) throws Exception { }

	@Override
	public void afterInsert(PersistenceEvent persistenceEvent) throws Exception {
		this.inicio(persistenceEvent, "afterInsert");
	}

	@Override
	public void afterUpdate(PersistenceEvent persistenceEvent) throws Exception { }

	@Override
	public void beforeCommit(TransactionContext arg0) throws Exception { }

	@Override
	public void beforeDelete(PersistenceEvent arg0) throws Exception { }

	@Override
	public void beforeInsert(PersistenceEvent persistenceEvent) throws Exception { }

	@Override
	public void beforeUpdate(PersistenceEvent persistenceEvent) throws Exception {
		this.inicio(persistenceEvent, "beforeUpdate");
	}
	
	private void inicio(PersistenceEvent persistenceEvent, String ondeEstaOEvento) throws Exception {
		ProdutoDaPromocao produtoDaPromocao = new ProdutoDaPromocao(persistenceEvent, ondeEstaOEvento);
		
		if (produtoDaPromocao.setCabecalhoDaPromocaoVO()) {
			if (produtoDaPromocao.setTabelaDePrecoVO()) {
				produtoDaPromocao.atualizarDadosDoProdutoNaPromocao();
			}
		}
	}

}
