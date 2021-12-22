package br.com.dalla.deive.eventos;

import java.math.BigDecimal;

import br.com.sankhya.jape.bmp.PersistentLocalEntity;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.vo.PrePersistEntityState;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.modelcore.comercial.ContextoRegra;
import br.com.sankhya.modelcore.comercial.Regra;
import br.com.sankhya.modelcore.dwfdata.vo.ItemNotaVO;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

public class EventoTgfiteMarcarNaoPendenteProdutoEcommerce implements Regra {
	
	/* Quando há apenas 1 unidade do produto no estoque, esta unidade é reservada
	 * no pedido 1009, quando o movimento automático tenta gerar a top 1079
	 * é apresentado erro informando que não há estoque suficiente do produto.
	 * 
	 * Esse evento faz com que antes de inserir o produto na TGFITE, o mesmo é marcado como
	 * não pendente no pedido origem.
	 * 
	 * Fazendo com que a reserva saia e seja possível inserí-lo no pedido de separação.
	 */
	private static final long serialVersionUID = 1L;
	
	@Override
	public void afterDelete(ContextoRegra arg0) throws Exception { }
	
	@Override
	public void afterInsert(ContextoRegra arg0) throws Exception { }
	
	@Override
	public void afterUpdate(ContextoRegra arg0) throws Exception { }
	
	@Override
	public void beforeDelete(ContextoRegra arg0) throws Exception { }
	
	@Override
	public void beforeInsert(ContextoRegra contextoRegra) throws Exception {
		this.marcarComoNaoPendenteOsProdutosNoPedido(contextoRegra);
	}
	
	@Override
	public void beforeUpdate(ContextoRegra arg0) throws Exception { }
	
	private void marcarComoNaoPendenteOsProdutosNoPedido(ContextoRegra contextoRegra) throws Exception {
		PrePersistEntityState prePersistEntityState = contextoRegra.getPrePersistEntityState();
		
		if (this.ehItemNota(prePersistEntityState)) {
			DynamicVO iteDynamicVOAtual = (DynamicVO) prePersistEntityState.getNewVO();
			DynamicVO cabDynamicVOAtual = this.getCabDynamicVO(iteDynamicVOAtual.asBigDecimal("NUNOTA"));
			
			BigDecimal nuNotaOrigem = iteDynamicVOAtual.asBigDecimalOrZero("AD_NUNOTAORIG");
			
			if (nuNotaOrigem.compareTo(BigDecimal.valueOf(0)) != 0) {
				DynamicVO cabOrigemDynamicVO = this.getCabDynamicVO(nuNotaOrigem);
				
				if (cabOrigemDynamicVO != null) {
					int codTipOperCabAtual = cabDynamicVOAtual.asInt("CODTIPOPER");
					int codTipOperCabOrigem = cabOrigemDynamicVO.asInt("CODTIPOPER");
					
					if (codTipOperCabAtual == 1079 && codTipOperCabOrigem == 1009) {
						PersistentLocalEntity persistentLocalEntityProduto = EntityFacadeFactory.getDWFFacade().findEntityByPrimaryKey("ItemNota", new Object[] { nuNotaOrigem, iteDynamicVOAtual.asBigDecimalOrZero("SEQUENCIA") });
						ItemNotaVO itemNotaVO = (ItemNotaVO) ((DynamicVO) persistentLocalEntityProduto.getValueObject()).wrapInterface(ItemNotaVO.class);
						itemNotaVO.setProperty("PENDENTE", "N");
						persistentLocalEntityProduto.setValueObject(itemNotaVO);

						System.out.println("EventoTgfiteMarcarNaoPendenteProdutoEcommerce. Nro único do pedido ecommerce=" + nuNotaOrigem + ". Sequência=" + itemNotaVO.asBigDecimal("SEQUENCIA"));
					}
				}
			}
		}
	}
	
	private boolean ehItemNota(PrePersistEntityState prePersistEntityState) {
		if (prePersistEntityState.getDao().getEntityName() != null && prePersistEntityState.getDao().getEntityName().equals("ItemNota")) {
			return true;
		}
		
		return false;
	}
	
	public DynamicVO getCabDynamicVO(BigDecimal nuNota) throws Exception {
		JapeWrapper DAO = JapeFactory.dao("CabecalhoNota");
		DynamicVO Vo = DAO.findOne("NUNOTA = ?", new Object[] { nuNota });
		return Vo;
	}
	
}
