package br.com.dalla.deive.eventos;

import java.math.BigDecimal;

import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;

public class PjVendaComPedidoAdicionaItemAMais implements EventoProgramavelJava {
	
	public void beforeInsert(PersistenceEvent persistenceEvent) throws Exception {
		this.acertarAdCodParcOrigPreFat(persistenceEvent);
	}
	
	public void afterInsert(PersistenceEvent persistenceEvent) throws Exception { }
	
	public void beforeUpdate(PersistenceEvent persistenceEvent) throws Exception {
		this.acertarAdCodParcOrigPreFat(persistenceEvent);
	}
	
	public void afterUpdate(PersistenceEvent persistenceEvent) throws Exception { }
	
	public void beforeDelete(PersistenceEvent persistenceEvent) throws Exception { }
	
	public void afterDelete(PersistenceEvent persistenceEvent) throws Exception { }

	public void beforeCommit(TransactionContext persistenceEvent) throws Exception { }
	
	private void acertarAdCodParcOrigPreFat(PersistenceEvent event) throws Exception {
		DynamicVO tgfiteVo = (DynamicVO) event.getVo();
		
		DynamicVO tgfcabVo = this.getTgfcabVo(tgfiteVo.asBigDecimal("NUNOTA"));
		Integer codTipOper = tgfcabVo.asInt("CODTIPOPER");
		Integer nuNota = tgfcabVo.asInt("NUNOTA");
		Integer nuNotaOrig = tgfcabVo.asInt("AD_NUNOTAORIG");
		Integer codParc = tgfcabVo.asInt("CODPARC");
		
		DynamicVO tgfparVo = this.getTgfparVo(codParc);
		String tipoPessoa = tgfparVo.asString("TIPPESSOA");
		
		if (codTipOper == 1100 && nuNota != nuNotaOrig && tipoPessoa == "J") {
			tgfiteVo.setProperty("AD_CODPARCORIG", codParc);
		}
	}
	
	private DynamicVO getTgfparVo(Integer codParc) throws Exception {
		JapeWrapper DAO = JapeFactory.dao("Parceiro");
		DynamicVO Vo = DAO.findOne("CODPARC = ?", new Object[] { codParc });
		return Vo;
	}
	
	private DynamicVO getTgfcabVo(BigDecimal nuNota) throws Exception {
		JapeWrapper DAO = JapeFactory.dao("CabecalhoNota");
		DynamicVO Vo = DAO.findOne("NUNOTA = ?", new Object[] { nuNota });
		return Vo;
	}
	
	/*private void exibirErro(String mensagem) throws Exception  {
		throw new PersistenceException("<p align=\"center\"><img src=\"https://dallabernardina.vteximg.com.br/arquivos/logo_header.png\"></img></p><br/><br/><br/><br/><br/><br/>\n\n\n\n<font size=\"12\" color=\"#BF2C2C\"><b> " + mensagem + "</b></font>\n\n\n");
	}*/

}