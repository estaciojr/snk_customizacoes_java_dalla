package br.com.dalla.deive.eventos;

import java.math.BigDecimal;
import java.sql.Timestamp;

import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.modelcore.comercial.CentralFinanceiro;

public class FinanceiroEcommerce implements EventoProgramavelJava {

	public void beforeInsert(PersistenceEvent event) throws Exception { }

	public void afterInsert(PersistenceEvent event) throws Exception {
		this.tgffin(event);
	}

	public void beforeUpdate(PersistenceEvent event) throws Exception { }

	public void afterUpdate(PersistenceEvent event) throws Exception { }

	public void beforeDelete(PersistenceEvent event) throws Exception { }

	public void afterDelete(PersistenceEvent event) throws Exception { }

	public void beforeCommit(TransactionContext event) throws Exception { }

	public void tgffin(PersistenceEvent event) throws Exception {
		DynamicVO tituloAtual = (DynamicVO) event.getVo();
		
		int nroUnicoAtual = tituloAtual.asInt("NUNOTA");
		
		DynamicVO pedidoAtualVO = this.getTgfcab(nroUnicoAtual);
		DynamicVO tipOperVO = this.getTgftop(pedidoAtualVO.asInt("CODTIPOPER"), pedidoAtualVO.asTimestamp("DHTIPOPER"));
		
		if (tipOperVO != null) {
			String adNotaEcom = tipOperVO.asString("AD_NOTAECOM");
			
			if (pedidoAtualVO != null) {
				int nroUnicoOrigem = pedidoAtualVO.asInt("AD_NUNOTAORIG");
				
				DynamicVO pedidoOrigemVO = this.getTgfcab(nroUnicoOrigem);

				if (pedidoOrigemVO != null) {
					int codEmpOrig = pedidoOrigemVO.asInt("CODEMP");
					int codTipOperOrig = pedidoOrigemVO.asInt("CODTIPOPER");
					String nuPedidoVtex = pedidoOrigemVO.asString("AD_PEDIDOECOM");

					if (codEmpOrig == 9 && codTipOperOrig == 1009 && nuPedidoVtex != null
							&& nroUnicoAtual != nroUnicoOrigem && adNotaEcom.equals("S")) {
						CentralFinanceiro centralFinanceiro = new CentralFinanceiro();
						centralFinanceiro.excluiFinanceiro(BigDecimal.valueOf(nroUnicoAtual));
						
						this.mostrarNoConsole("nroUnicoAtual = " + nroUnicoAtual + "\n"
								+ "adNotaEcom = " + adNotaEcom + "\n"
								+ "nroUnicoOrigem = " + nroUnicoOrigem + "\n"
								+ "codEmpOrig = " + codEmpOrig + "\n"
								+ "codTipOperOrig = " + codTipOperOrig + "\n"
								+ "nuPedidoVtex = " + nuPedidoVtex);
					}
				}
			}
		}
	}

	public DynamicVO getTgfcab(int nuNota) throws Exception {
		JapeWrapper DAO = JapeFactory.dao("CabecalhoNota");
		DynamicVO Vo = DAO.findOne("NUNOTA = ?", new Object[] { nuNota });
		return Vo;
	}

	public DynamicVO getTgftop(int codTipOper, Timestamp dhAlter) throws Exception {
		JapeWrapper DAO = JapeFactory.dao("TipoOperacao");
		DynamicVO Vo = DAO.findOne("CODTIPOPER = ? AND DHALTER = ?", new Object[] { codTipOper, dhAlter });
		return Vo;
	}

	public void mostrarNoConsole(String mensagem) {
		System.out.println("====================== Mensagem ======================\n========== Exclui título de nota ecommerce ===========\n" + mensagem + "\n======================================================");
	}

}
