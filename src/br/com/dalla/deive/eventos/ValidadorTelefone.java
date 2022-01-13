package br.com.dalla.deive.eventos;

import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.PersistenceException;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.jape.vo.DynamicVO;

public class ValidadorTelefone implements EventoProgramavelJava {
	
	public void beforeInsert(PersistenceEvent persistenceEvent) throws Exception {
		this.validaCampoTelefoneParceiro(persistenceEvent);
	}
	
	public void afterInsert(PersistenceEvent persistenceEvent) throws Exception { }
	
	public void beforeUpdate(PersistenceEvent persistenceEvent) throws Exception {
		this.validaCampoTelefoneParceiro(persistenceEvent);
	}
	
	public void afterUpdate(PersistenceEvent persistenceEvent) throws Exception { }
	
	public void beforeDelete(PersistenceEvent persistenceEvent) throws Exception { }
	
	public void afterDelete(PersistenceEvent persistenceEvent) throws Exception { }

	public void beforeCommit(TransactionContext persistenceEvent) throws Exception { }

	private void validaCampoTelefoneParceiro(PersistenceEvent persistenceEvent) throws Exception {
		DynamicVO parVo = (DynamicVO) persistenceEvent.getVo();

		String email = (parVo.asString("EMAIL") == null) ? "" : parVo.asString("EMAIL");
		
		System.out.println("ValidadorTelefone. Email=" + email + ". Contains vtex=" + email.contains("vtex"));
		
		if (!email.contains("vtex")) {
			String telefone =  (parVo.asString("TELEFONE") == null) ? "" : parVo.asString("TELEFONE");
			
			String telefoneNovo = telefone.replace(" ", "");
			
			if (telefoneNovo != null) {
				if (telefoneNovo.length() < 10) {
					this.exibirErro("O campo Telefone deve conter pelo menos 10 dígitos\nTelefone: " + telefoneNovo + ". Qtd. caracteres: " + telefoneNovo.length());
				}
			}
		}
	}
	
	private void exibirErro(String mensagem) throws Exception  {
		throw new PersistenceException("<p align=\"center\"><img src=\"https://dallabernardina.vteximg.com.br/arquivos/logo_header.png\"></img></p><br/><br/><br/><br/><br/><br/>\n\n\n\n<font size=\"12\" color=\"#BF2C2C\"><b> " + mensagem + "</b></font>\n\n\n");
	}

}