package br.com.dalla.deive.eventos;

import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.jape.vo.DynamicVO;

import java.math.BigDecimal;

public class LiberacaoAutomatica implements EventoProgramavelJava {
	
	public void beforeInsert(PersistenceEvent event) throws Exception {
		this.liberarEvento(event);
	}
	
	public void afterInsert(PersistenceEvent event) throws Exception { }
	
	public void beforeUpdate(PersistenceEvent event) throws Exception { }
	
	public void afterUpdate(PersistenceEvent event) throws Exception { }
	
	public void beforeDelete(PersistenceEvent event) throws Exception { }
	
	public void afterDelete(PersistenceEvent event) throws Exception { }

	public void beforeCommit(TransactionContext event) throws Exception { }
	
	private void liberarEvento(PersistenceEvent event) throws Exception {
		DynamicVO tsilibVO = (DynamicVO) event.getVo();
		
		DynamicVO tgfcabVO = this.getTgfcab(tsilibVO.asBigDecimal("NUCHAVE"));
		
		int codTipOper = tgfcabVO.asInt("CODTIPOPER");
		
		if (codTipOper == 1052 || codTipOper == 1100) {
			tsilibVO.setProperty("VLRLIBERADO", tsilibVO.asBigDecimal("VLRATUAL"));
		}
	}

	private DynamicVO getTgfcab(BigDecimal nuNota) throws Exception {
		JapeWrapper DAO = JapeFactory.dao("CabecalhoNota");
		DynamicVO Vo = DAO.findOne("NUNOTA = ?", new Object[] { nuNota });
		return Vo;
	}
	
}