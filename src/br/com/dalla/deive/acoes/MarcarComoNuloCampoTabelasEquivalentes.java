package br.com.dalla.deive.acoes;

import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;

public class MarcarComoNuloCampoTabelasEquivalentes implements AcaoRotinaJava {

	@Override
	public void doAction(ContextoAcao contextoAcao) throws Exception {
		Registro registrosSelecionados[] = contextoAcao.getLinhas();
		
		if (registrosSelecionados.length == 0) {
			contextoAcao.mostraErro("Nenhum registro selecionado.");
		} else {
			boolean confirmaTrueFalse = contextoAcao.confirmarSimNao("Confirmação", "Deseja realmente marcar o campo \"Tabelas Equivalentes\" como NULO?", 0);
			
			if (confirmaTrueFalse) {
				for (Registro registroSelecionado : registrosSelecionados) {
					registroSelecionado.setCampo("AD_TABPROMOCOES", null);
				}
			}
		}
	}

}
