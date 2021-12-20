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

/* No pedido origem
 * limpa o campo adicional AD_VLRUNITORIG da TGFITE
 * para resolver o problema que gera o pré-faturamento, é deletado,
 * voltar para o pedido de origem e o valor total não altera quando se altera
 * o tipo de negociação do pedido origem
 * De à vista para 10x por exemplo
 */

public class EventoLimparAdVlrUnitOrigDoPedido implements EventoProgramavelJava {

	@Override
	public void afterDelete(PersistenceEvent persistenceEvent) throws Exception {
		DynamicVO cabecalhoPreFaturamento = (DynamicVO) persistenceEvent.getVo();
		
		if (this.ehPreFaturamento(cabecalhoPreFaturamento)) {
			if (this.temPedidoOrigem(cabecalhoPreFaturamento)) {
				BigDecimal nroUnicoPedidoOrigem = cabecalhoPreFaturamento.asBigDecimal("AD_NUNOTAORIG");
				System.out.println("Limpando o campo AD_VLRUNITORIG do pedido de nro único " + nroUnicoPedidoOrigem);
				this.limparAdVlrUnitOrig(nroUnicoPedidoOrigem);
			}
		}
	}

	@Override
	public void afterInsert(PersistenceEvent arg0) throws Exception { }

	@Override
	public void afterUpdate(PersistenceEvent arg0) throws Exception { }

	@Override
	public void beforeCommit(TransactionContext arg0) throws Exception { }

	@Override
	public void beforeDelete(PersistenceEvent arg0) throws Exception { }

	@Override
	public void beforeInsert(PersistenceEvent arg0) throws Exception { }

	@Override
	public void beforeUpdate(PersistenceEvent arg0) throws Exception { }
	
	private boolean ehPreFaturamento(DynamicVO cabecalho) {
		if (cabecalho.asBigDecimalOrZero("CODTIPOPER").equals(BigDecimal.valueOf(1100))) {
			return true;
		}
		
		return false;
	}
	
	private boolean temPedidoOrigem(DynamicVO cabecalho) {
		if (!cabecalho.asBigDecimalOrZero("AD_NUNOTAORIG").equals(BigDecimal.valueOf(0))) {
			return true;
		}
		
		return false;
	}
	
	private void limparAdVlrUnitOrig(BigDecimal nroUnicoOrigem) throws Exception {
		Collection<?> itensDoPedido = EntityFacadeFactory.getDWFFacade().findByDynamicFinder(new FinderWrapper("ItemNota", "this.NUNOTA = ? and this.SEQUENCIA > 0", new Object[] { nroUnicoOrigem }));					
		Iterator<?> iteratorDosItens = itensDoPedido.iterator();
		
		while (iteratorDosItens.hasNext()) {
			PersistentLocalEntity itensProEntity = (PersistentLocalEntity) iteratorDosItens.next();
			ItemNotaVO itemNotaVO = (ItemNotaVO) ((DynamicVO) itensProEntity.getValueObject()).wrapInterface(ItemNotaVO.class);
			itemNotaVO.setProperty("AD_VLRUNITORIG", null);
			itensProEntity.setValueObject(itemNotaVO);
		}
	}

}
