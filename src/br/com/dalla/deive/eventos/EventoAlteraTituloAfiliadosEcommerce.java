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
	public void afterDelete(PersistenceEvent arg0) throws Exception { }

	@Override
	public void afterInsert(PersistenceEvent percistenceEvent) throws Exception { }

	@Override
	public void afterUpdate(PersistenceEvent arg0) throws Exception { }

	@Override
	public void beforeCommit(TransactionContext arg0) throws Exception { }

	@Override
	public void beforeDelete(PersistenceEvent arg0) throws Exception { }

	@Override
	public void beforeInsert(PersistenceEvent persistenceEvent) throws Exception {
		this.inicio(persistenceEvent);
	}

	@Override
	public void beforeUpdate(PersistenceEvent arg0) throws Exception { }
	
	private void inicio(PersistenceEvent persistenceEvent) throws Exception {
		DynamicVO financeiroEcom = (DynamicVO) persistenceEvent.getVo();
		
		DynamicVO pedidoDeVendaEcom = this.getTgfcab(financeiroEcom.asBigDecimalOrZero("NUNOTA"));
		
		if (pedidoDeVendaEcom == null) {
			System.out.println("EventoAlteraTituloAfiliadosEcommerce. Financeiro não vinculado a nota ou pedido.");
		} else {
			String pedidoEcommerce = pedidoDeVendaEcom.asString("AD_PEDIDOECOM");
			BigDecimal codTipOperPedidoEcommerce = pedidoDeVendaEcom.asBigDecimalOrZero("CODTIPOPER");
			
			if (!StringUtils.isEmpty(pedidoEcommerce) && codTipOperPedidoEcommerce.compareTo(BigDecimal.valueOf(1009)) == 0) {
				DynamicVO pedidoVtex = this.getPedidoVtex(pedidoEcommerce);
				
				String idAfiliado = pedidoVtex.asString("IDAFILIADO");
				
				if (!StringUtils.isEmpty(idAfiliado)) {
					DynamicVO pagamentoAfiliadoVtex = this.getPagamentoAfiliadoVtex(idAfiliado);
					
					if (pagamentoAfiliadoVtex == null) {
						System.out.println("EventoAlteraTituloAfiliadosEcommerce. Pagamento não cadastrado na tela Pagamentos Afiliados VTEX");
					} else {
						BigDecimal tipoDeTitulo = pagamentoAfiliadoVtex.asBigDecimalOrZero("CODTIPTIT");
						
						financeiroEcom.setProperty("CODTIPTIT", tipoDeTitulo);
						
						System.out.println("EventoAlteraTituloAfiliadosEcommerce. Título alterado. Nro único=" + pedidoDeVendaEcom.asBigDecimalOrZero("NUNOTA") + ". Tipo de Título=" + tipoDeTitulo);
					}
				} else {
					System.out.println("EventoAlteraTituloAfiliadosEcommerce. ID Afiliado não preenchido no pedido da VTEX. Pedido=" + pedidoEcommerce);
				}
			} else {
				System.out.println("EventoAlteraTituloAfiliadosEcommerce. Campo AD_PEDIDOECOM não preenchido. Nro único=" + pedidoDeVendaEcom.asBigDecimalOrZero("NUNOTA"));
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
	
}
