package br.com.dalla.deive.eventos;

import java.math.BigDecimal;
import java.sql.Timestamp;
//import java.util.Collection;
import java.util.Date;
//import java.util.Iterator;

import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
//import br.com.sankhya.jape.PersistenceException;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.bmp.PersistentLocalEntity;
import br.com.sankhya.jape.dao.EntityPrimaryKey;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
//import br.com.sankhya.jape.util.FinderWrapper;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.vo.EntityVO;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;
//import br.com.sankhya.modelcore.dwfdata.vo.CabecalhoNotaVO;

public class AtualizarCampoTimestampCabConfirmada implements EventoProgramavelJava {
	
	public void beforeInsert(PersistenceEvent event) throws Exception { }
	
	public void afterInsert(PersistenceEvent event) throws Exception {
		DynamicVO tgfcabVO = (DynamicVO) event.getVo();
		
		Timestamp timestampDtInclusao = tgfcabVO.asTimestamp("AD_TIMESTAMP_INCLUSAO");
		
		if (timestampDtInclusao == null) {
			BigDecimal nuNota = tgfcabVO.asBigDecimal("NUNOTA");
			Timestamp timestampDtAlter = tgfcabVO.asTimestamp("DTALTER");
			
			this.alterarTimestampInclusao(event, nuNota, timestampDtAlter);
		}
	}
	
	public void beforeUpdate(PersistenceEvent event) throws Exception {
		this.setCampoTimestampConfirmacao(event);
	}
	
	public void afterUpdate(PersistenceEvent event) throws Exception { }
	
	public void beforeDelete(PersistenceEvent event) throws Exception { }
	
	public void afterDelete(PersistenceEvent event) throws Exception { }
	
	public void beforeCommit(TransactionContext event) throws Exception { }
	
	private void setCampoTimestampConfirmacao(PersistenceEvent event) throws Exception {
		DynamicVO tgfcabVONew = (DynamicVO) event.getVo();
		DynamicVO tgfcabVOOld = (DynamicVO) event.getOldVO();
		
		BigDecimal numNotaNew = tgfcabVONew.asBigDecimal("NUMNOTA");
		BigDecimal numNotaOld = tgfcabVOOld.asBigDecimal("NUMNOTA");
		
		if (numNotaOld.compareTo(new BigDecimal(0)) == 0 && numNotaNew.compareTo(numNotaOld) != 0) {
			//BigDecimal nuNota = tgfcabVONew.asBigDecimal("NUNOTA");
			//BigDecimal nuNotaOrig = tgfcabVONew.asBigDecimal("AD_NUNOTAORIG");
			
			//BigDecimal codTipOper = tgfcabVONew.asBigDecimal("CODTIPOPER");
			
			Timestamp timestampAgora = this.getTimestamp();
			
			/*
			if (nuNota.compareTo(nuNotaOrig) != 0 && codTipOper.compareTo(new BigDecimal(1100)) == 0) {
				EntityFacade DWFEntityFacade = EntityFacadeFactory.getDWFFacade();
				PersistentLocalEntity localEntity = DWFEntityFacade.findEntityByPrimaryKey("CabecalhoNota", new EntityPrimaryKey(new Object[] { nuNotaOrig }) );
				EntityVO NVO = localEntity.getValueObject();
				DynamicVO pedVendaVO = (DynamicVO) NVO;
				pedVendaVO.setProperty("AD_TIMESTAMP_CONFIRMADA", timestampAgora);
				localEntity.setValueObject(NVO);
			}
			*/
			
			//Timestamp timestampOld = null;
			
			/*
			if (codTipOper.compareTo(new BigDecimal(1100)) != 0) {
				timestampOld = this.getTimestampPreFaturamento(event, nuNotaOrig);
			}
			
			if (timestampOld == null) {
				timestampOld = timestampAgora;
			}
			*/
			
			//BigDecimal diferencaEmSegundos = new BigDecimal((timestampAgora.getTime() - timestampOld.getTime()) / 1000);
			
			tgfcabVONew.setProperty("AD_TIMESTAMP_CONFIRMACAO", timestampAgora);
			//tgfcabVONew.setProperty("AD_DIFERENCA_EM_SEGUNDOS", diferencaEmSegundos);
		}
	}
	
	private Timestamp getTimestamp() {
		Date date = new Date();
		long timeInMilliseconds = date.getTime();
		Timestamp timestamp = new Timestamp(timeInMilliseconds);
		
		return timestamp;
	}
	
	/*
	private Timestamp getTimestampPreFaturamento(PersistenceEvent event, BigDecimal nuNotaOrig)  throws Exception {
		Timestamp timestampPreFaturamento = null;
		
		EntityFacade DWFEntityFacade = EntityFacadeFactory.getDWFFacade();
		
		Collection<?> preFaturamento = DWFEntityFacade.findByDynamicFinder(new FinderWrapper("CabecalhoNota", "this.AD_NUNOTAORIG = ? and this.CODTIPOPER = 1100", new Object[]{ nuNotaOrig }));
		
		if (preFaturamento.isEmpty()) {
			return timestampPreFaturamento; 
		}
		
		Iterator<?> preFatIterator = preFaturamento.iterator();
		
		while (preFatIterator.hasNext()) {
			PersistentLocalEntity preFatEntity = (PersistentLocalEntity) preFatIterator.next();
			CabecalhoNotaVO preFatVO = (CabecalhoNotaVO) ((DynamicVO) preFatEntity.getValueObject()).wrapInterface(CabecalhoNotaVO.class);
			timestampPreFaturamento = preFatVO.asTimestamp("AD_TIMESTAMP_CONFIRMADA");
		}
		
		return timestampPreFaturamento;
	}
	*/
	
	/*
	private void exibirErro(String mensagem) throws Exception  {
		throw new PersistenceException("<p align=\"center\"><img src=\"https://dallabernardina.vteximg.com.br/arquivos/logo_header.png\" height=\"100\" width=\"300\"></img></p><br/><br/><br/><br/><br/><br/>\n\n\n\n<font size=\"15\" color=\"#BF2C2C\"><b> " + mensagem + "</b></font>\n\n\n");
	}
	*/
	
	private void alterarTimestampInclusao(PersistenceEvent event, BigDecimal nuNota, Timestamp timestamp) throws Exception {
		EntityFacade DWFEntityFacade = EntityFacadeFactory.getDWFFacade();
		PersistentLocalEntity localEntity = DWFEntityFacade.findEntityByPrimaryKey("CabecalhoNota", new EntityPrimaryKey(new Object[] { nuNota }) );
		EntityVO NVO = localEntity.getValueObject();
		DynamicVO pedVendaVO = (DynamicVO) NVO;
		pedVendaVO.setProperty("AD_TIMESTAMP_INCLUSAO", timestamp);
		localEntity.setValueObject(NVO);
	}
	
}