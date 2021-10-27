package br.com.dalla.deive.eventos;

import java.math.BigDecimal;
//import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.Date;

import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
/*
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.PersistenceException;
import br.com.sankhya.jape.bmp.PersistentLocalEntity;
import br.com.sankhya.jape.dao.EntityPrimaryKey;
*/
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.jape.vo.DynamicVO;
/*
import br.com.sankhya.jape.vo.EntityVO;
import br.com.sankhya.modelcore.auth.AuthenticationInfo;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;
*/

public class TestePegarValorCampoCalculado implements EventoProgramavelJava {
	
	public void beforeInsert(PersistenceEvent event) throws Exception { }
	
	public void afterInsert(PersistenceEvent event) throws Exception { }
	
	public void beforeUpdate(PersistenceEvent event) throws Exception {
		this.setCampoTimestampConfirmacao(event);
	}
	
	public void afterUpdate(PersistenceEvent event) throws Exception {
		//this.setCampoTimestampConfirmacao(event);
	}
	
	public void beforeDelete(PersistenceEvent event) throws Exception { }
	
	public void afterDelete(PersistenceEvent event) throws Exception { }

	public void beforeCommit(TransactionContext event) throws Exception { }
	
	private void setCampoTimestampConfirmacao(PersistenceEvent event) throws Exception {
		
		DynamicVO tgfcabVO = (DynamicVO) event.getVo();
		DynamicVO tgfcabVOOld = (DynamicVO) event.getOldVO();
		
		BigDecimal numNota = tgfcabVO.asBigDecimal("NUMNOTA");
		BigDecimal numNotaOld = tgfcabVOOld.asBigDecimal("NUMNOTA");
		//String statusNota = tgfcabVO.asString("STATUSNOTA");
		
		if (numNota != numNotaOld) {
			Date date = new Date();
			long timeInMilliseconds = date.getTime();
			Timestamp timestamp = new Timestamp(timeInMilliseconds);
			
			tgfcabVO.setProperty("AD_TIMESTAMP_CONFIRMADA", timestamp);
		}
		
	}
	
	/*
	private void exibirErro(String mensagem) throws Exception  {
		throw new PersistenceException("<p align=\"center\"><img src=\"https://dallabernardina.vteximg.com.br/arquivos/logo_header.png\" height=\"100\" width=\"300\"></img></p><br/><br/><br/><br/><br/><br/>\n\n\n\n<font size=\"15\" color=\"#BF2C2C\"><b> " + mensagem + "</b></font>\n\n\n");
	}
	*/
	
	/*
	private Timestamp getTimestamp() {
		Date date = new Date();
		long timeInMilliseconds = date.getTime();
		Timestamp timestamp = new Timestamp(timeInMilliseconds);
		
		return timestamp;
	}
	*/
	
}