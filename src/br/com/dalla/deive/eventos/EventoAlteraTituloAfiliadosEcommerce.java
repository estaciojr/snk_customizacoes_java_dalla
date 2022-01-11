package br.com.dalla.deive.eventos;

import java.math.BigDecimal;

import com.sankhya.util.StringUtils;

import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;

public class EventoAlteraTituloAfiliadosEcommerce implements EventoProgramavelJava {

	@Override
	public void afterDelete(PersistenceEvent arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void afterInsert(PersistenceEvent percistenceEvent) throws Exception { }

	@Override
	public void afterUpdate(PersistenceEvent arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void beforeCommit(TransactionContext arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void beforeDelete(PersistenceEvent arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void beforeInsert(PersistenceEvent persistenceEvent) throws Exception {
		// TODO Auto-generated method stub
		
		this.inicio(persistenceEvent);
	}

	@Override
	public void beforeUpdate(PersistenceEvent arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	private void inicio(PersistenceEvent persistenceEvent) throws Exception {
		DynamicVO financeiroEcom = (DynamicVO) persistenceEvent.getVo();
		
		DynamicVO pedidoDeVendaEcom = this.getTgfcab(financeiroEcom.asBigDecimalOrZero("NUNOTA"));
		
		if (pedidoDeVendaEcom != null) {
			String pedidoEcommerce = pedidoDeVendaEcom.asString("AD_PEDIDOECOM");
			
			System.out.println("**************** Pedido ecommerce: " + pedidoEcommerce);
			System.out.println("**************** isEmpty: " + StringUtils.isEmpty(pedidoEcommerce));
			
			if (!StringUtils.isEmpty(pedidoEcommerce)) {
				DynamicVO pedidoVtex = this.getPedidoVtex(pedidoEcommerce);
				
				String idAfiliado = pedidoVtex.asString("IDAFILIADO");
				
				if (!StringUtils.isEmpty(idAfiliado)) {
					System.out.println("**************** ID Afiliado: " + idAfiliado);
					System.out.println("**************** isEmpty: " + StringUtils.isEmpty(idAfiliado));
					
					DynamicVO pagamentoAfiliadoVtex = this.getPagamentoAfiliadoVtex(idAfiliado);
					
					if (pagamentoAfiliadoVtex == null) {
						System.out.println("EventoAlteraTituloAfiliadosEcommerce. Pagamento não cadastrado na tela Pagamentos Afiliados VTEX");
					} else {
						BigDecimal tipoDeTitulo = pagamentoAfiliadoVtex.asBigDecimalOrZero("CODTIPTIT");
						
						System.out.println("**************** CODTIPTIT: " + tipoDeTitulo);
						
						financeiroEcom.setProperty("CODTIPTIT", tipoDeTitulo);
						
						System.out.println("**************** NROUNICO: " + pedidoDeVendaEcom.asBigDecimalOrZero("NUNOTA"));
						
//						this.alterarTitulos(pedidoDeVendaEcom.asBigDecimalOrZero("NUNOTA"), tipoDeTitulo);
					}
				}
			}
		}
	}
	
	private DynamicVO getPedidoVtex(String pedidoVtex) throws Exception {
		JapeWrapper DAO = JapeFactory.dao("VtexPedidos");
		DynamicVO Vo = DAO.findOne("PEDIDO = ?", new Object[] { pedidoVtex });
		return Vo;
	}
	
	private DynamicVO getPagamentoAfiliadoVtex(String idAfiliado) throws Exception {
		JapeWrapper DAO = JapeFactory.dao("AD_DALPAGAMENTOSAFILIADOSVTEX");
		DynamicVO Vo = DAO.findOne("IDAFILIADO = ?", new Object[] { idAfiliado });
		return Vo;
	}
	
	public DynamicVO getTgfcab(BigDecimal nuNota) throws Exception {
		JapeWrapper DAO = JapeFactory.dao("CabecalhoNota");
		DynamicVO Vo = DAO.findOne("NUNOTA = ?", new Object[] { nuNota });
		return Vo;
	}
	
//	private void alterarTitulos(BigDecimal nroUnicoPedido, BigDecimal codTipTit) throws Exception {
//		EntityFacade dwf = EntityFacadeFactory.getDWFFacade();
//		FinderWrapper titulosFinder = new FinderWrapper("Financeiro", "this.NUNOTA = ?", new Object[]{ nroUnicoPedido });
//		Collection<PersistentLocalEntity> titulosFinderCollection = dwf.findByDynamicFinder(titulosFinder);
//		
//		Iterator<PersistentLocalEntity> titulosterator = titulosFinderCollection.iterator();
//		
//		while (titulosterator.hasNext()) {
//			PersistentLocalEntity tituloPersistentLocalEntity = (PersistentLocalEntity) titulosterator.next();
//			DynamicVO tituloVO = ((DynamicVO) tituloPersistentLocalEntity.getValueObject()).wrapInterface(FinanceiroVO.class);
//			
//			tituloVO.setProperty("CODTIPTIT", codTipTit);
//			
//			System.out.println("**************** nroUnico: " + nroUnicoPedido);
//		}
//	}

}
