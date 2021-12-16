package br.com.dalla.deive.eventos;

import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.PersistenceException;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.jape.vo.DynamicVO;

import java.math.BigDecimal;

public class BloqueiaCadastroInvalido implements EventoProgramavelJava {
	
	public void beforeInsert(PersistenceEvent persistenceEvent) throws Exception {
		this.bloqueiaCadastroInvalido(persistenceEvent);
	}
	
	public void afterInsert(PersistenceEvent persistenceEvent) throws Exception { }
	
	public void beforeUpdate(PersistenceEvent persistenceEvent) throws Exception { }
	
	public void afterUpdate(PersistenceEvent persistenceEvent) throws Exception { }
	
	public void beforeDelete(PersistenceEvent persistenceEvent) throws Exception { }
	
	public void afterDelete(PersistenceEvent persistenceEvent) throws Exception { }

	public void beforeCommit(TransactionContext persistenceEvent) throws Exception { }

	private void bloqueiaCadastroInvalido(PersistenceEvent event) throws Exception {
		DynamicVO tgfparVO = (DynamicVO) event.getVo();
		
		BigDecimal codigoBairro = tgfparVO.asBigDecimal("CODBAI");
		DynamicVO tgfbaiVO = getTgfbairroVO(codigoBairro);
		String nomeBairro = tgfbaiVO.asString("NOMEBAI");
		
		char ultimoCharacter = this.getUltimoCharacter(nomeBairro);
		
		if (this.characterEhEspaco(ultimoCharacter)) {
			
		}
		
		this.exibirErro("" + nomeBairro);
	}
	
	private DynamicVO getTgfbairroVO(BigDecimal codigoBairro) throws Exception {
		JapeWrapper DAO = JapeFactory.dao("Bairro");
		DynamicVO Vo = DAO.findOne("CODBAI = ?", new Object[] { codigoBairro });
		return Vo;	
	}
	
	private void exibirErro(String mensagem) throws Exception  {
		throw new PersistenceException("<p align=\"center\"><img src=\"https://dallabernardina.vteximg.com.br/arquivos/logo_header.png\"></img></p><br/><br/><br/><br/><br/><br/>\n\n\n\n<font size=\"12\" color=\"#BF2C2C\"><b> " + mensagem + "</b></font>\n\n\n");
	}
	
	public char getUltimoCharacter(String nomeBairro) {
		char[] characterArray = nomeBairro.toCharArray();
		int ultimaPosicao = characterArray.length - 1;
		return characterArray[ultimaPosicao];
	}
	
	public boolean characterEhEspaco(char character) {
		return character == ' ';
	}
	
}