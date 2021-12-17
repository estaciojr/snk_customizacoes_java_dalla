package br.com.dalla.deive.eventos;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Iterator;

import br.com.sankhya.jape.bmp.PersistentLocalEntity;
import br.com.sankhya.jape.util.FinderWrapper;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.vo.PrePersistEntityState;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.modelcore.comercial.ContextoRegra;
import br.com.sankhya.modelcore.comercial.Regra;
import br.com.sankhya.modelcore.dwfdata.vo.ItemNotaVO;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

public class EventoPreferenciaMarcarNaoPendenteProdutoEcommerce implements Regra {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void afterDelete(ContextoRegra contextoRegra) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void afterInsert(ContextoRegra contextoRegra) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void afterUpdate(ContextoRegra contextoRegra) throws Exception {
		// TODO Auto-generated method stub
	}

	@Override
	public void beforeDelete(ContextoRegra arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void beforeInsert(ContextoRegra contextoRegra) throws Exception {
		PrePersistEntityState prePersistEntityState = contextoRegra.getPrePersistEntityState();
		
		DynamicVO tgfcabAtualDyVO = (DynamicVO) prePersistEntityState.getNewVO();
		
		BigDecimal nuNotaOrigem = tgfcabAtualDyVO.asBigDecimalOrZero("AD_NUNOTAORIG");
		
		if (nuNotaOrigem.compareTo(BigDecimal.valueOf(0)) != 0) {
			DynamicVO tgfcabDyVOOrigem = this.getCabDynamicVO(nuNotaOrigem);
			
			if (tgfcabDyVOOrigem != null) {
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
						
						this.mostrarNoConsole(
							"NUNOTA = " + itemNotaVO.asBigDecimal("NUNOTA") + "\n"
							+ "CODPROD = " + itemNotaVO.asBigDecimal("CODPROD") + "\n"
							+ "SEQUENCIA = " + itemNotaVO.asBigDecimal("SEQUENCIA")
						);
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
	
	public void mostrarNoConsole(String mensagem) {
		System.out.println(
			"====================== Mensagem ======================\n"
			+ "======= EventoMarcarNaoPendenteProdutoEcommerce ======\n"
			+ mensagem + "\n"
			+ "======================================================"
		);
	}
	
}
