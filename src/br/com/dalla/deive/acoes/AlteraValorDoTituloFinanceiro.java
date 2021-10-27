package br.com.dalla.deive.acoes;

import java.math.BigDecimal;

import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.extensions.actionbutton.QueryExecutor;

public class AlteraValorDoTituloFinanceiro implements AcaoRotinaJava {

	@Override
	public void doAction(ContextoAcao contextoAcao) throws Exception {
		
		Registro registrosSelecionados[] = contextoAcao.getLinhas();
        
        if (registrosSelecionados.length == 0 || registrosSelecionados.length > 1) {
            contextoAcao.mostraErro("Selecione apenas uma linha.");
        } else {
        	boolean confirmaTrueFalse = contextoAcao.confirmarSimNao("Confirmação", "Deseja realmente alterar o valor do título?", 0);
        	
        	if (confirmaTrueFalse) {
        		QueryExecutor queryAlterarVlrDesdob = contextoAcao.getQuery();
                
                BigDecimal nuFin = (BigDecimal) registrosSelecionados[0].getCampo("NUFIN");
                
                queryAlterarVlrDesdob.setParam("P_NUFIN", nuFin);
                queryAlterarVlrDesdob.setParam("P_VLRDESDOB", contextoAcao.getParam("P_VLRDESDOB"));
                
                queryAlterarVlrDesdob.update("UPDATE TGFFIN SET VLRDESDOB = {P_VLRDESDOB} WHERE NUFIN = {P_NUFIN}");
                
                queryAlterarVlrDesdob.close();
        	}
        }
		
	}
	
}
