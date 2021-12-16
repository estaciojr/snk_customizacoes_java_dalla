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
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.modelcore.dwfdata.vo.ItemNotaVO;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

/* Criado para marcar como não pendente o produto na TOP 1009
 * antes de inserir na TOP 1079 e "liberar" o estoque
 */

public class EventoMarcarNaoPendenteProdutoEcommerce implements EventoProgramavelJava {

	public void beforeInsert(PersistenceEvent event) throws Exception {
		DynamicVO tgfcabAtualDyVO = (DynamicVO) event.getVo();
		
		BigDecimal nuNotaOrigem = tgfcabAtualDyVO.asBigDecimalOrZero("AD_NUNOTAORIG");
		
		if (nuNotaOrigem.compareTo(BigDecimal.valueOf(0)) != 0) {
			DynamicVO tgfcabDyVOOrigem = this.getCabDynamicVO(nuNotaOrigem);
			
			if (tgfcabDyVOOrigem != null) {
				System.out.println("NUNOTA = " + tgfcabDyVOOrigem.asBigDecimal("NUNOTA") + " - CODTIPOPER = " + tgfcabDyVOOrigem.asBigDecimal("CODTIPOPER"));
				
				int codTipOperCab = tgfcabAtualDyVO.asInt("CODTIPOPER");
				int codTipOperCabOrigem = tgfcabDyVOOrigem.asInt("CODTIPOPER");
				
				if (codTipOperCab == 1079 && codTipOperCabOrigem == 1009) {
					Collection<?> itensDoPedido = EntityFacadeFactory.getDWFFacade().findByDynamicFinder(new FinderWrapper("ItemNota", "this.NUNOTA = ? and this.SEQUENCIA > 0", new Object[] { tgfcabDyVOOrigem.asBigDecimal("NUNOTA") }));					
					Iterator<?> iteratorDosItens = itensDoPedido.iterator();
					
					while (iteratorDosItens.hasNext()) {
						PersistentLocalEntity itensProEntity = (PersistentLocalEntity) iteratorDosItens.next();
						ItemNotaVO itemNotaVO = (ItemNotaVO) ((DynamicVO) itensProEntity.getValueObject()).wrapInterface(ItemNotaVO.class);
						itemNotaVO.setProperty("PENDENTE", "N");
						itensProEntity.setValueObject(itemNotaVO);
					}
				}
			}
		}
	}

	public void afterInsert(PersistenceEvent event) throws Exception { }

	public void beforeUpdate(PersistenceEvent event) throws Exception { }
	
	public void afterUpdate(PersistenceEvent event) throws Exception { }

	public void beforeDelete(PersistenceEvent event) throws Exception { }

	public void afterDelete(PersistenceEvent event) throws Exception { }

	public void beforeCommit(TransactionContext event) throws Exception { }
	
	public DynamicVO getCabDynamicVO(BigDecimal nuNota) throws Exception {
		JapeWrapper DAO = JapeFactory.dao("CabecalhoNota");
		DynamicVO Vo = DAO.findOne("NUNOTA = ?", new Object[] { nuNota });
		return Vo;
	}
	
	public void marcarComoNaoPendente(	) {
		
	}
	
}
