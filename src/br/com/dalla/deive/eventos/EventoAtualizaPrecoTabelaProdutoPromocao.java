package br.com.dalla.deive.eventos;

import br.com.dalla.deive.util.ProdutoDaPromocao;
import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.jape.vo.DynamicVO;

public class EventoAtualizaPrecoTabelaProdutoPromocao implements EventoProgramavelJava {

	@Override
	public void afterDelete(PersistenceEvent arg0) throws Exception { }

	@Override
	public void afterInsert(PersistenceEvent persistenceEvent) throws Exception {
		this.inicio(persistenceEvent);
	}

	@Override
	public void afterUpdate(PersistenceEvent persistenceEvent) throws Exception {
		this.inicio(persistenceEvent);
	}

	@Override
	public void beforeCommit(TransactionContext arg0) throws Exception { }

	@Override
	public void beforeDelete(PersistenceEvent arg0) throws Exception { }

	@Override
	public void beforeInsert(PersistenceEvent persistenceEvent) throws Exception { }

	@Override
	public void beforeUpdate(PersistenceEvent arg0) throws Exception { }
	
	private void inicio(PersistenceEvent persistenceEvent) throws Exception {
		DynamicVO produtoDaPromocaoVO = (DynamicVO) persistenceEvent.getVo();
		
		ProdutoDaPromocao produtoDaPromocao = new ProdutoDaPromocao(produtoDaPromocaoVO, persistenceEvent);
		
		if (produtoDaPromocao.setCabecalhoDaPromocaoVO()) {
			if (produtoDaPromocao.setTabelaDePrecoVO()) {
				produtoDaPromocao.atualizarDadosDoProdutoNaPromocao();
			}
		}
	}

}
