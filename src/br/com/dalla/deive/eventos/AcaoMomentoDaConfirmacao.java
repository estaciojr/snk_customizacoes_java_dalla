package br.com.dalla.deive.eventos;

import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.movautomaticos.model.utils.EntityFacadeW;
import br.com.sankhya.movautomaticos.model.utils.NotaHelper;
import br.com.sankhya.movautomaticos.model.utils.WrapperVO;

public class AcaoMomentoDaConfirmacao implements EventoProgramavelJava {

	public void beforeInsert(PersistenceEvent event) throws Exception { }

	public void afterInsert(PersistenceEvent event) throws Exception { }

	public void beforeUpdate(PersistenceEvent event) throws Exception {
		// Só pode ser usado before-update
		/*DynamicVO newCabVO = (DynamicVO) event.getVo();
		DynamicVO oldCabVO = (DynamicVO) event.getOldVO();
		
		System.out.println("NewVO " + newCabVO.asString("STATUSNOTA"));
		System.out.println("OldVO " + oldCabVO.asString("STATUSNOTA"));
		
		if (
			oldCabVO.asString("STATUSNOTA").equals("L") 
			&& newCabVO.asString("STATUSNOTA").equals("L")
		) {
			System.out.println(
					"######################### CONFIRMOU! " 
					+ LocalDateTime.now()
					+ "#########################");
		}*/
		/*ModifingFields modifingFields = event.getModifingFields();
		System.out.println("Está sendo modificado: " + modifingFields.isModifing("STATUSNOTA"));*/
	}
	
	public void afterUpdate(PersistenceEvent event) throws Exception {
		/*ModifingFields modifingFields = event.getModifingFields();
		if (modifingFields.isModifing("STATUSNOTA")) {
			System.out.println("NEW VALUE ==== " + modifingFields.getNewValue("STATUSNOTA"));
			System.out.println("OLD VALUE ==== " + modifingFields.getOldValue("STATUSNOTA"));
		}*/
		
		/*DynamicVO pedido = (DynamicVO) event.getVo();
		System.out.println(pedido.asString("STATUSNOTA"));*/
		
		// after-update - executa duas vezes
		/*ModifingFields modifingFields = event.getModifingFields();
		System.out.println("Está sendo modificado: " + modifingFields.isModifing("STATUSNOTA"));*/
		
		//br.com.sankhya.movautomaticos.model.utils.NotaHelper
		
		DynamicVO pedido = (DynamicVO) event.getVo();
		
		EntityFacadeW dwfFacade = EntityFacadeW.getNew();
        WrapperVO cabFatVO = dwfFacade.findEntityByPrimaryKey("CabecalhoNota", new Object[] { pedido.asBigDecimal("NUNOTA") });
		
		cabFatVO.reloadVO();
		
		System.out.println("cabFatVO ====== StatusNota " + cabFatVO.asString("STATUSNOTA"));
		
		System.out.println("NotaHelper ===== isConfirmada? " + NotaHelper.isConfirmada(((DynamicVO) event.getVo()).asBigDecimal("NUNOTA")));
		
		/*CACHelper cacHelper = new CACHelper();
		NotaHelper notaHelper = new NotaHelper();*/
		
		/*ModifingFields modifingFields = event.getModifingFields();
		System.out.println("Está sendo modificado: " + modifingFields.isModifing("STATUSNFE"));*/
	}

	public void beforeDelete(PersistenceEvent event) throws Exception { }

	public void afterDelete(PersistenceEvent event) throws Exception { }

	public void beforeCommit(TransactionContext event) throws Exception { }

}
