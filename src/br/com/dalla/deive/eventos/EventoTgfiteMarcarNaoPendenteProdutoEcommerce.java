package br.com.dalla.deive.eventos;

import java.math.BigDecimal;

import org.apache.log4j.Logger;

import br.com.sankhya.jape.bmp.PersistentLocalEntity;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.vo.PrePersistEntityState;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.modelcore.comercial.ContextoRegra;
import br.com.sankhya.modelcore.comercial.Regra;
import br.com.sankhya.modelcore.dwfdata.vo.ItemNotaVO;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;
import br.com.sankhya.movautomaticos.model.utils.LogUtils;
import br.com.sankhya.movautomaticos.model.utils.NotaHelper;

public class EventoTgfiteMarcarNaoPendenteProdutoEcommerce implements Regra {
	
	private static final Logger logger = (new LogUtils(NotaHelper.class)).getLogger();
	
	/**
	 * 
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
		
		if (this.ehNoItemNota(prePersistEntityState)) {
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

						logger.info("EventoTgfiteMarcarNaoPendenteProdutoEcommerce. Nro único do pedido ecommerce=" + nuNotaOrigem + ". Sequência=" + itemNotaVO.asBigDecimal("SEQUENCIA"));
					}
				}
			}
		}
	}
	
	private boolean ehNoItemNota(PrePersistEntityState prePersistEntityState) {
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
