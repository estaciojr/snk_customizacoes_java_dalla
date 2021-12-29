package br.com.dalla.deive.util;

import java.math.BigDecimal;

import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;

public class HelperGetDynamicVO {
	
	public static DynamicVO getCabecalhoPromocao(BigDecimal codGrupoDesc) throws Exception {
		JapeWrapper DAO = JapeFactory.dao("AD_PROMOCOES");
		DynamicVO Vo = DAO.findOne("CODGRUPODESC = ?", new Object[] { codGrupoDesc });
		return Vo;
	}
	
	public static DynamicVO getTabPrecoDynamicVO(BigDecimal nroUnicoTabPreco) throws Exception {
		JapeWrapper DAO = JapeFactory.dao("TabelaPreco");
		DynamicVO Vo = DAO.findOne("NUTAB = ?", new Object[] { nroUnicoTabPreco });
		return Vo;
	}

}
