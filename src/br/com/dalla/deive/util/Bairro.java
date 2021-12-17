package br.com.dalla.deive.util;

import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;

public class Bairro {
	
	private int codigoDoBairro;
	private String nomeDoBairro;
	private DynamicVO dynamicVO;
	
	public Bairro(int codigoDoBairro) {
		this.codigoDoBairro = codigoDoBairro;
	}
	
	public void setNomeDoBairro() {
		if (this.dynamicVOExiste()) {
			this.nomeDoBairro = this.dynamicVO.asString("NOMEBAI");
		}
	}
	
	public String getNomeDoBairro() {
		return this.nomeDoBairro;
	}
	
	public void setDynamicVO() throws Exception {
		if (this.codigoDoBairro != 0) {
			this.dynamicVO = this.getTgfbaiDynamicVO(this.codigoDoBairro);
		}
	}
	
	private DynamicVO getTgfbaiDynamicVO(int codigoDoBairro) throws Exception {
		JapeWrapper DAO = JapeFactory.dao("Bairro");
		DynamicVO Vo = DAO.findOne("CODBAI = ?", new Object[] { codigoDoBairro });
		return Vo;
	}
	
	private boolean dynamicVOExiste() {
		return this.dynamicVO != null;
	}

}
