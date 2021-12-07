package br.com.dalla.deive.util;

import java.util.Collection;
import java.util.Iterator;

import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.bmp.PersistentLocalEntity;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.util.FinderWrapper;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

public class PedidoAtual {
	
	private DynamicVO pedidoAtualVO;
	private DynamicVO pedidoOrigemVO;
	
	private int nroUnicoPedidoAtual;
	private int nroUnicoPedidoOrigem;
	
	public PedidoAtual(PersistenceEvent event) {
		this.pedidoAtualVO = (DynamicVO) event.getVo();
		
		this.nroUnicoPedidoAtual = pedidoAtualVO.asInt("NUNOTA");
		this.nroUnicoPedidoOrigem = pedidoAtualVO.asInt("AD_NUNOTAORIG");
	}
	
	public void setPedidoOrigemVO() throws Exception {
		this.pedidoOrigemVO = SnkUtil.getTgfcab(this.nroUnicoPedidoAtual);
	}
	
	public boolean temPedidoOrigem() {
		if (this.pedidoOrigemVO != null) {
			return true;
		}
		
		return false;
	}
	
	public boolean pedidoOrigemVeioDaVtex() {
		int codEmpOrigem = this.pedidoOrigemVO.asInt("CODEMP");
		int codTipOperOrigem = this.pedidoOrigemVO.asInt("CODTIPOPER");
		String nuPedidoVtexOrigem = this.pedidoOrigemVO.asString("AD_PEDIDOECOM");
		
		if (
				codEmpOrigem == 9 
				&& codTipOperOrigem == 1009 
				&& nuPedidoVtexOrigem != null 
				&& this.nroUnicoPedidoAtual != this.nroUnicoPedidoOrigem
			) {
			return true;
		}
		
		return false;
	}
	
	public void deletarTitulos() throws Exception {
		Collection<EntityFacade> titulosEntityFacade = EntityFacadeFactory.getDWFFacade().findByDynamicFinder(new FinderWrapper("Financeiro", "this.NUNOTA = ?", new Object[] { this.nroUnicoPedidoAtual }));
		
		Iterator<EntityFacade> titulosIterator = titulosEntityFacade.iterator();
		
		while (titulosIterator.hasNext()) {
			PersistentLocalEntity persistentLocalEntity = (PersistentLocalEntity) titulosIterator.next();
			persistentLocalEntity.remove();
		}
	}

}
