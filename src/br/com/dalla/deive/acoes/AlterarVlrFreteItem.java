package br.com.dalla.deive.acoes;

import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;

public class AlterarVlrFreteItem implements AcaoRotinaJava {

	@Override
	public void doAction(ContextoAcao contextoAcao) throws Exception {
		Registro registrosSelecionados[] = contextoAcao.getLinhas();
		
		if (registrosSelecionados.length == 0 || registrosSelecionados.length > 1) {
			contextoAcao.mostraErro("Selecione apenas um registro.");
		} else {
			boolean confirmaTrueFalse = contextoAcao.confirmarSimNao("Confirmação", "Deseja realmente alterar o valor do frete do produto selecionado?", 0);
			
			if (confirmaTrueFalse) {
				for (Registro registroSelecionado : registrosSelecionados) {
					registroSelecionado.setCampo("AD_VLRFRETE", contextoAcao.getParam("P_VLRFRETE"));
				}
			}
		}
	}

}
