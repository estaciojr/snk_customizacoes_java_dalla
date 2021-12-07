package br.com.dalla.deive.eventos;

import java.math.BigDecimal;

import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.modelcore.comercial.CentralFinanceiro;

public class FinanceiroEcommerce implements EventoProgramavelJava {

	public void beforeInsert(PersistenceEvent event) throws Exception { }
	
	public void afterInsert(PersistenceEvent event) throws Exception {
		//this.tgfcab(event);
		this.tgffin(event);
	}
	
	public void beforeUpdate(PersistenceEvent event) throws Exception { }
	
	public void afterUpdate(PersistenceEvent event) throws Exception {
		//this.tgfcab(event);
		//this.tgffin(event);
	}
	
	public void beforeDelete(PersistenceEvent event) throws Exception { }
	
	public void afterDelete(PersistenceEvent event) throws Exception { }

	public void beforeCommit(TransactionContext event) throws Exception { }
	
	public void tgffin(PersistenceEvent event) throws Exception {
		DynamicVO tituloAtual = (DynamicVO) event.getVo();
		
		int nroUnicoAtual = tituloAtual.asInt("NUNOTA");
		
		DynamicVO pedidoAtualVO = this.getTgfcab(nroUnicoAtual);
		
		if (pedidoAtualVO != null) {
			int nroUnicoOrigem = pedidoAtualVO.asInt("AD_NUNOTAORIG");
			
			DynamicVO pedidoOrigemVO = this.getTgfcab(nroUnicoOrigem);
			
			if (pedidoOrigemVO != null) {
				int codEmpOrig = pedidoOrigemVO.asInt("CODEMP");
				int codTipOperOrig = pedidoOrigemVO.asInt("CODTIPOPER");
				String nuPedidoVtex = pedidoOrigemVO.asString("AD_PEDIDOECOM");
				
				if (codEmpOrig == 9 && codTipOperOrig == 1009 && nuPedidoVtex != null && nroUnicoAtual != nroUnicoOrigem) {
					CentralFinanceiro centralFinanceiro = new CentralFinanceiro();
					centralFinanceiro.excluiFinanceiro(BigDecimal.valueOf(nroUnicoAtual));
				}
			}
		}
	}
	
	public void tgfcab(PersistenceEvent event) throws Exception {
		DynamicVO pedidoAtualVO = (DynamicVO) event.getVo();
		int nroUnicoPedAtual = pedidoAtualVO.asInt("NUNOTA");
		
		int nroUnicoPedOrigem = pedidoAtualVO.asInt("AD_NUNOTAORIG");
		
		DynamicVO pedidoOrigemVO = this.getTgfcab(nroUnicoPedOrigem);
		
		if (pedidoOrigemVO != null) {
			CentralFinanceiro centralFinanceiro = new CentralFinanceiro();
			centralFinanceiro.excluiFinanceiro(BigDecimal.valueOf(nroUnicoPedAtual));
			
			/*int codEmpOrig = pedidoOrigemVO.asInt("CODEMP");
			int codTipOperOrig = pedidoOrigemVO.asInt("CODTIPOPER");
			String nuPedidoVtex = pedidoOrigemVO.asString("AD_PEDIDOECOM");
			
			if (codEmpOrig == 9 && codTipOperOrig == 1009 && nuPedidoVtex != null && nroUnicoPedAtual != nroUnicoPedOrigem) {
				Collection<EntityFacade> titulosEntityFacade = EntityFacadeFactory.getDWFFacade().findByDynamicFinder(new FinderWrapper("Financeiro", "this.NUNOTA = ?", new Object[] { nroUnicoPedAtual }));
				
				Iterator<EntityFacade> titulosIterator = titulosEntityFacade.iterator();
				
				while (titulosIterator.hasNext()) {
					PersistentLocalEntity persistentLocalEntity = (PersistentLocalEntity) titulosIterator.next();
					persistentLocalEntity.remove();
				}
			}*/
		}
	}
	
	public DynamicVO getTgfcab(int nuNota) throws Exception {
		JapeWrapper DAO = JapeFactory.dao("CabecalhoNota");
		DynamicVO Vo = DAO.findOne("NUNOTA = ?", new Object[] { nuNota });
		return Vo;
	}
	
}
