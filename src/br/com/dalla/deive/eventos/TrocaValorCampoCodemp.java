package br.com.dalla.deive.eventos;

import java.math.BigDecimal;

import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.EntityFacade;
//import br.com.sankhya.jape.PersistenceException;
import br.com.sankhya.jape.bmp.PersistentLocalEntity;
import br.com.sankhya.jape.dao.EntityPrimaryKey;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.vo.EntityVO;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

public class TrocaValorCampoCodemp implements EventoProgramavelJava {
		
		public void beforeInsert(PersistenceEvent event) throws Exception { }
		
		public void afterInsert(PersistenceEvent event) throws Exception { }
		
		public void beforeUpdate(PersistenceEvent event) throws Exception {
			this.alteraCodEmp(event);
		}
		
		public void afterUpdate(PersistenceEvent event) throws Exception { }
		
		public void beforeDelete(PersistenceEvent event) throws Exception { }
		
		public void afterDelete(PersistenceEvent event) throws Exception { }

		public void beforeCommit(TransactionContext event) throws Exception { }
		
		private void alteraCodEmp(PersistenceEvent event) throws Exception {
			BigDecimal[] valores = this.getCampos(event);
			
			BigDecimal nuNota = valores[0];
			BigDecimal sequencia = valores[1];
			BigDecimal codEmpEst = valores[2];
			
			EntityFacade dwf = EntityFacadeFactory.getDWFFacade();
			
			PersistentLocalEntity localEntity = dwf.findEntityByPrimaryKey("ItemNota", new EntityPrimaryKey(new Object[] { nuNota, sequencia }) );
			EntityVO NVO = localEntity.getValueObject();
			DynamicVO VO = (DynamicVO) NVO;
			
			VO.setProperty("CODEMP", codEmpEst);
			
			localEntity.setValueObject(NVO);
		}
		
		private BigDecimal[] getCampos(PersistenceEvent event) {
			DynamicVO iteVo = (DynamicVO) event.getVo();
			
			BigDecimal[] valores = new BigDecimal[3];
			
			valores[0] = iteVo.asBigDecimal("NUNOTA");
			valores[1] = iteVo.asBigDecimal("SEQUENCIA");
			valores[2] = iteVo.asBigDecimal("AD_CODEMPEST");
			
			return valores;
		}
		
		/*
		private void exibirErro(String mensagem) throws Exception  {
			throw new PersistenceException("<p align=\"center\"><img src=\"https://dallabernardina.vteximg.com.br/arquivos/logo_header.png\" height=\"100\" width=\"300\"></img></p><br/><br/><br/><br/><br/><br/>\n\n\n\n<font size=\"15\" color=\"#BF2C2C\"><b> " + mensagem + "</b></font>\n\n\n");
		}
		*/
		
}