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
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Override
	public void afterDelete(ContextoRegra arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void afterInsert(ContextoRegra arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void afterUpdate(ContextoRegra arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void beforeDelete(ContextoRegra arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void beforeInsert(ContextoRegra contextoRegra) throws Exception {
		// TODO Auto-generated method stub
		PrePersistEntityState prePersistEntityState = contextoRegra.getPrePersistEntityState();
		
		if (prePersistEntityState.getDao().getEntityName() != null && prePersistEntityState.getDao().getEntityName().equals("ItemNota")) {
			DynamicVO iteDynamicVOAtual = (DynamicVO) prePersistEntityState.getNewVO();
			DynamicVO cabDynamicVOAtual = this.getCabDynamicVO(iteDynamicVOAtual.asBigDecimal("NUNOTA"));
			
			BigDecimal nuNotaOrigem = iteDynamicVOAtual.asBigDecimalOrZero("AD_NUNOTAORIG");
			
			System.out.println("nuNotaOrigem = " + nuNotaOrigem);
			
			if (nuNotaOrigem.compareTo(BigDecimal.valueOf(0)) != 0) {
				DynamicVO cabOrigemDynamicVO = this.getCabDynamicVO(nuNotaOrigem);
				
				if (cabOrigemDynamicVO != null) {
					int codTipOperCabAtual = cabDynamicVOAtual.asInt("CODTIPOPER");
					int codTipOperCabOrigem = cabOrigemDynamicVO.asInt("CODTIPOPER");
					
					System.out.println("CODTIPOPER ATUAL = " + codTipOperCabAtual);
					System.out.println("CODTIPOPER ORIGEM = " + codTipOperCabOrigem);
					
					if (codTipOperCabAtual == 1079 && codTipOperCabOrigem == 1009) {
						PersistentLocalEntity persistentLocalEntityProduto = EntityFacadeFactory.getDWFFacade().findEntityByPrimaryKey("ItemNota", new Object[] { nuNotaOrigem, iteDynamicVOAtual.asBigDecimalOrZero("SEQUENCIA") });
						ItemNotaVO itemNotaVO = (ItemNotaVO) ((DynamicVO) persistentLocalEntityProduto.getValueObject()).wrapInterface(ItemNotaVO.class);
						itemNotaVO.setProperty("PENDENTE", "N");
						persistentLocalEntityProduto.setValueObject(itemNotaVO);
					}
				}
			}
		}
	}
	
	@Override
	public void beforeUpdate(ContextoRegra arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	public DynamicVO getCabDynamicVO(BigDecimal nuNota) throws Exception {
		JapeWrapper DAO = JapeFactory.dao("CabecalhoNota");
		DynamicVO Vo = DAO.findOne("NUNOTA = ?", new Object[] { nuNota });
		return Vo;
	}
	
}
