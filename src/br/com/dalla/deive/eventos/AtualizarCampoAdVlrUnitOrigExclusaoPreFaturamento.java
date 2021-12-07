package br.com.dalla.deive.eventos;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Iterator;

import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.bmp.PersistentLocalEntity;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.jape.util.FinderWrapper;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.modelcore.dwfdata.vo.ItemNotaVO;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

public class AtualizarCampoAdVlrUnitOrigExclusaoPreFaturamento implements EventoProgramavelJava {
	
	public void beforeInsert(PersistenceEvent persistenceEvent) throws Exception { }
	
	public void afterInsert(PersistenceEvent persistenceEvent) throws Exception { }
	
	public void beforeUpdate(PersistenceEvent persistenceEvent) throws Exception { }
	
	public void afterUpdate(PersistenceEvent persistenceEvent) throws Exception { }
	
	public void beforeDelete(PersistenceEvent persistenceEvent) throws Exception { }
	
	public void afterDelete(PersistenceEvent persistenceEvent) throws Exception {
		this.atualizarValorDoCampoAdVlrUnitOrig(persistenceEvent);
	}

	public void beforeCommit(TransactionContext persistenceEvent) throws Exception { }

	private void atualizarValorDoCampoAdVlrUnitOrig(PersistenceEvent persistenceEvent) throws Exception {
		DynamicVO cabVo = (DynamicVO) persistenceEvent.getVo();
		
		int codTipOper = cabVo.asInt("CODTIPOPER");
		
		if (codTipOper == 1100) {
			int nroUnicoPedOrigem = cabVo.asInt("AD_NUNOTAORIG");
			int nroUnicoPreFaturamento = cabVo.asInt("NUNOTA");
			
			if (nroUnicoPedOrigem != nroUnicoPreFaturamento) {
				Collection<?> itensDoPedido = EntityFacadeFactory.getDWFFacade().findByDynamicFinder(new FinderWrapper("ItemNota", "this.NUNOTA = ? and this.SEQUENCIA > 0", new Object[] { nroUnicoPedOrigem }));
				
				Iterator<?> iteratorDosItens = itensDoPedido.iterator();
				
				while (iteratorDosItens.hasNext()) {
					PersistentLocalEntity itensProEntity = (PersistentLocalEntity) iteratorDosItens.next();
					
					ItemNotaVO itemNotaVO = (ItemNotaVO) ((DynamicVO) itensProEntity.getValueObject()).wrapInterface(ItemNotaVO.class);
					
					// Criado um objeto sem referência
					BigDecimal bigDecimal = null;
					
					// Passado esse objeto como parâmetro, pois o parâmetro aceita apenas um Object
					// Dessa forma o valor volta a ser NULL
					itemNotaVO.setProperty("AD_VLRUNITORIG", bigDecimal);
					
					itensProEntity.setValueObject(itemNotaVO);
				}
			}
		}
	}

}