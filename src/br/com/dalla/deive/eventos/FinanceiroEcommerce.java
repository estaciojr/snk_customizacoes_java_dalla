package br.com.dalla.deive.eventos;

import java.util.Collection;
import java.util.Iterator;

import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.bmp.PersistentLocalEntity;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.jape.util.FinderWrapper;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

public class FinanceiroEcommerce implements EventoProgramavelJava {

	public void beforeInsert(PersistenceEvent event) throws Exception { }
	
	public void afterInsert(PersistenceEvent event) throws Exception {
		this.inicio(event);
	}
	
	public void beforeUpdate(PersistenceEvent event) throws Exception { }
	
	public void afterUpdate(PersistenceEvent event) throws Exception {
		this.inicio(event);
	}
	
	public void beforeDelete(PersistenceEvent event) throws Exception { }
	
	public void afterDelete(PersistenceEvent event) throws Exception { }

	public void beforeCommit(TransactionContext event) throws Exception { }
	
	public void inicio(PersistenceEvent event) throws Exception {
		DynamicVO pedidoAtualVO = (DynamicVO) event.getVo();
		int nroUnicoPedAtual = pedidoAtualVO.asInt("NUNOTA");
		
		int nroUnicoPedOrigem = pedidoAtualVO.asInt("AD_NUNOTAORIG");
		
		DynamicVO pedidoOrigemVO = this.getTgfcab(nroUnicoPedOrigem);
		
		if (pedidoOrigemVO != null) {
			int codEmpOrig = pedidoOrigemVO.asInt("CODEMP");
			int codTipOperOrig = pedidoOrigemVO.asInt("CODTIPOPER");
			String nuPedidoVtex = pedidoOrigemVO.asString("AD_PEDIDOECOM");
			
			if (codEmpOrig == 9 && codTipOperOrig == 1009 && nuPedidoVtex != null && nroUnicoPedAtual != nroUnicoPedOrigem) {
				Collection<EntityFacade> titulosEntityFacade = EntityFacadeFactory.getDWFFacade().findByDynamicFinder(new FinderWrapper("Financeiro", "this.NUNOTA = ?", new Object[] { nroUnicoPedAtual }));
				
				Iterator<EntityFacade> titulosIterator = titulosEntityFacade.iterator();
				
				while (titulosIterator.hasNext()) {
					PersistentLocalEntity persistentLocalEntity = (PersistentLocalEntity) titulosIterator.next();
					persistentLocalEntity.remove();
				}
			}
		}
	}
	
	public DynamicVO getTgfcab(int nuNota) throws Exception {
		JapeWrapper DAO = JapeFactory.dao("CabecalhoNota");
		DynamicVO Vo = DAO.findOne("NUNOTA = ?", new Object[] { nuNota });
		return Vo;
	}
	
}
