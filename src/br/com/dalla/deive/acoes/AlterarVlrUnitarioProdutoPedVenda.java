package br.com.dalla.deive.acoes;

import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;

public class AlterarVlrUnitarioProdutoPedVenda implements AcaoRotinaJava {

	@Override
	public void doAction(ContextoAcao contextoAcao) throws Exception {
		Registro registrosSelecionados[] = contextoAcao.getLinhas();
		
		if (registrosSelecionados.length == 0) {
			contextoAcao.mostraErro("Nenhum registro selecionado.");
		} else {
			boolean confirmaTrueFalse = contextoAcao.confirmarSimNao("Confirmação", "Deseja realmente alterar o valor unitário do produto?", 0);
			
			if (confirmaTrueFalse) {
				for (Registro registroSelecionado : registrosSelecionados) {
					registroSelecionado.setCampo("VLRUNIT", contextoAcao.getParam("P_VLRUNIT"));
				}
			}
		}
	}

}
