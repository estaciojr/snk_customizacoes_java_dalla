package br.com.dalla.deive.eventos;

import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.PersistenceException;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.jape.vo.DynamicVO;

import java.math.BigDecimal;

public class BloquearInserirProdPreFat implements EventoProgramavelJava {
	
	public void beforeInsert(PersistenceEvent persistenceEvent) throws Exception {
		this.bloquearInserirProduto(persistenceEvent);
	}
	
	public void afterInsert(PersistenceEvent persistenceEvent) throws Exception { }
	
	public void beforeUpdate(PersistenceEvent persistenceEvent) throws Exception { }
	
	public void afterUpdate(PersistenceEvent persistenceEvent) throws Exception { }
	
	public void beforeDelete(PersistenceEvent persistenceEvent) throws Exception { }
	
	public void afterDelete(PersistenceEvent persistenceEvent) throws Exception { }

	public void beforeCommit(TransactionContext persistenceEvent) throws Exception { }
	
	private void bloquearInserirProduto(PersistenceEvent event) throws Exception {
		DynamicVO tgfiteVo = (DynamicVO) event.getVo();
		
		DynamicVO tgfcabVo = this.getTgfcabVo(tgfiteVo.asBigDecimal("NUNOTA"));
		
		String adEntregaIte = tgfiteVo.asString("AD_ENTREGA");
		
		if (tgfcabVo.asInt("CODTIPOPER") == 1100 && adEntregaIte != null) {
			if (!adEntregaIte.equals("C") && tgfcabVo.asInt("NUNOTA") != tgfcabVo.asInt("AD_NUNOTAORIG")) {
				DynamicVO tgfcabOrigVo = this.getTgfcabVo(tgfcabVo.asBigDecimal("AD_NUNOTAORIG"));
					
				if (tgfcabOrigVo.asInt("CODTIPOPER") == 1001) {
					DynamicVO tgfiteOrigVo = this.getTgfiteVo(tgfcabOrigVo.asBigDecimal("NUNOTA"), tgfiteVo.asBigDecimal("SEQUENCIA"));
					
					if (tgfiteOrigVo == null) {
						this.exibirErro("'Opção de Entrega' diferente de 'Caixa' e produto não encontrado no pedido de vendas original.\n\n" +
										"É necessário inserir o produto no pedido de vendas e gerar novo pré-faturamento.");
					}
				}
			} else if (!adEntregaIte.equals("C") && tgfcabVo.asInt("NUNOTA") == tgfcabVo.asInt("AD_NUNOTAORIG")) {
				this.exibirErro("Pré-faturamento sem pedido de venda não pode conter produtos com 'Opção de Entrega' diferente de 'Caixa'.");
			}
		}
	}
	
	private DynamicVO getTgfiteVo(BigDecimal nuNota, BigDecimal sequencia) throws Exception {
		JapeWrapper DAO = JapeFactory.dao("ItemNota");
		DynamicVO Vo = DAO.findOne("NUNOTA = ? AND SEQUENCIA = ?", new Object[] { nuNota, sequencia });
		return Vo;
	}
	
	private DynamicVO getTgfcabVo(BigDecimal nuNota) throws Exception {
		JapeWrapper DAO = JapeFactory.dao("CabecalhoNota");
		DynamicVO Vo = DAO.findOne("NUNOTA = ?", new Object[] { nuNota });
		return Vo;
	}
	
	private void exibirErro(String mensagem) throws Exception  {
		throw new PersistenceException("<p align=\"center\"><img src=\"https://dallabernardina.vteximg.com.br/arquivos/logo_header.png\"></img></p><br/><br/><br/><br/><br/><br/>\n\n\n\n<font size=\"12\" color=\"#BF2C2C\"><b> " + mensagem + "</b></font>\n\n\n");
	}

}