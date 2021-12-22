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

/* Quando há apenas 1 unidade do produto no estoque, esta unidade é reservada
 * no pedido 1009, quando o movimento automático tenta gerar a top 1079
 * é apresentado erro informando que não há estoque suficiente do produto.
 * 
 * Esse evento faz com que antes de inserir o produto na TGFITE, o mesmo é marcado como
 * não pendente no pedido origem.
 * 
 * Fazendo com que a reserva saia e seja possível inserí-lo no pedido de separação.
 */

public class EventoMarcarNaoPendenteProdutoEcommerce implements EventoProgramavelJava {

	public void beforeInsert(PersistenceEvent event) throws Exception {
		DynamicVO tgfiteAtualDyVO = (DynamicVO) event.getVo();
		
		BigDecimal nuNotaOrigem = tgfiteAtualDyVO.asBigDecimalOrZero("AD_NUNOTAORIG");
		
		if (nuNotaOrigem.compareTo(BigDecimal.valueOf(0)) != 0) {
			DynamicVO tgfcabDyVOOrigem = this.getCabDynamicVO(nuNotaOrigem);
			
			if (tgfcabDyVOOrigem != null) {
				int codTipOperCab = tgfiteAtualDyVO.asInt("CODTIPOPER");
				int codTipOperCabOrigem = tgfcabDyVOOrigem.asInt("CODTIPOPER");
				
				if (codTipOperCab == 1079 && codTipOperCabOrigem == 1009) {
					Collection<?> itensDoPedido = EntityFacadeFactory.getDWFFacade().findByDynamicFinder(new FinderWrapper("ItemNota", "this.NUNOTA = ? and this.SEQUENCIA = ?", new Object[] { tgfcabDyVOOrigem.asBigDecimal("NUNOTA"), tgfiteAtualDyVO.asBigDecimalOrZero("SEQUENCIA") }));					
					Iterator<?> iteratorDosItens = itensDoPedido.iterator();
					
					while (iteratorDosItens.hasNext()) {
						PersistentLocalEntity itensProEntity = (PersistentLocalEntity) iteratorDosItens.next();
						ItemNotaVO itemNotaVO = (ItemNotaVO) ((DynamicVO) itensProEntity.getValueObject()).wrapInterface(ItemNotaVO.class);
						itemNotaVO.setProperty("PENDENTE", "N");
						itensProEntity.setValueObject(itemNotaVO);
						
						System.out.println(
							"EventoMarcarNaoPendenteProdutoEcommerce. Nro Único=" + itemNotaVO.asBigDecimal("NUNOTA")
							+ ". Cód. Produto=" + itemNotaVO.asBigDecimal("CODPROD")
							+ ". Sequência=" + itemNotaVO.asBigDecimal("SEQUENCIA")
						);
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
	
}
