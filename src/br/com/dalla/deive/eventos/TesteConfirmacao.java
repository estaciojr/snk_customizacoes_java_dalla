package br.com.dalla.deive.eventos;

import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.vo.PrePersistEntityState;
import br.com.sankhya.modelcore.comercial.ContextoRegra;
import br.com.sankhya.modelcore.comercial.Regra;

public class TesteConfirmacao implements Regra {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void afterDelete(ContextoRegra contextoRegra) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void afterInsert(ContextoRegra contextoRegra) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void afterUpdate(ContextoRegra contextoRegra) throws Exception {
		// TODO Auto-generated method stub
		
		PrePersistEntityState prePersistEntityState = contextoRegra.getPrePersistEntityState();
		
		if (prePersistEntityState.getDao().getEntityName() != null && prePersistEntityState.getDao().getEntityName().equals("CabecalhoNota")) {
			DynamicVO newCabVO = prePersistEntityState.getNewVO();
			DynamicVO oldCabVO = prePersistEntityState.getOldVO();
			
			System.out.println("OLD VO = " + oldCabVO.asString("STATUSNOTA"));
			System.out.println("NEW VO = " + newCabVO.asString("STATUSNOTA"));
			
			if (!oldCabVO.asString("STATUSNOTA").equals("L") && newCabVO.asString("STATUSNOTA").equals("L")) {
				System.out.println("CONFIRMOU NESTE EXATO MOMENTO!");
			}
		}
	}

	@Override
	public void beforeDelete(ContextoRegra contextoRegra) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void beforeInsert(ContextoRegra contextoRegra) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void beforeUpdate(ContextoRegra contextoRegra) throws Exception {
		// TODO Auto-generated method stub
		
	}
	
}
