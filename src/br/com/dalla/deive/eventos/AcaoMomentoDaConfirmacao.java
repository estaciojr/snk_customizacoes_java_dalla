package br.com.dalla.deive.eventos;

import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.modelcore.comercial.CentralFaturamento;
import br.com.sankhya.modelcore.comercial.ConfirmacaoNotaHelper;

public class AcaoMomentoDaConfirmacao implements EventoProgramavelJava {

	public void beforeInsert(PersistenceEvent event) throws Exception { }

	public void afterInsert(PersistenceEvent event) throws Exception { }

	public void beforeUpdate(PersistenceEvent event) throws Exception { }

	public void afterUpdate(PersistenceEvent event) throws Exception {
		DynamicVO pedidoAtual = (DynamicVO) event.getVo();
		if (ConfirmacaoNotaHelper.verificaConfComercial(pedidoAtual.asBigDecimal("NUNOTA"))) {
			
		}
		
	}

	public void beforeDelete(PersistenceEvent event) throws Exception { }

	public void afterDelete(PersistenceEvent event) throws Exception { }

	public void beforeCommit(TransactionContext event) throws Exception { }

}
