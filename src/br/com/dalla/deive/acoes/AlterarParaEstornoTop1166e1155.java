package br.com.dalla.deive.acoes;

import java.math.BigDecimal;
import java.sql.Timestamp;

import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;

public class AlterarParaEstornoTop1166e1155 implements AcaoRotinaJava {
	
	/* Autor: Deivisson - 19/10/2021
	 * Bot�o de a��o para funcionar apenas com a TOP 1166 e 1155
	 * Apenas altera o campo DHTIPOPER no cabe�alho da nota de sa�da
	 * para que seja poss�vel utilizar a configura��o da TOP 1166 do dia '2021-10-01 10:41:28.000'
	 * visto que nessa data de altera��o o configurado no campo CODTIPOPERDESTINO est� como a TOP de estorno 1218
	 */
	
	/* Problemas:
	 * Caso tenha alguma altera��o na TOP 1155 ou 1166
	 * as altera��es voltaram para uma criada no dia 2021-10-01 10:41:28.000
	 */
	
	/* 
		select
			codtipoper,
			dhalter,
			CODTIPOPERDESTINO,
			AD_PERMITEDEVOLUCAO,
			AD_FRETEITE
		from
			tgftop (nolock)
		where
			codtipoper = 1155
		order by
			dhalter desc;
		
		AD_PERMITEDEVOLUCAO deve estar = 'S'
		CODTIPOPERDESTINO deve estar = 1218
	 */
	
	@Override
	public void doAction(ContextoAcao contextoAcao) throws Exception {
		
		Registro registrosSelecionados[] = contextoAcao.getLinhas();
        
        if (registrosSelecionados.length != 1) {
            contextoAcao.mostraErro("Selecione apenas uma linha.");
        } else {
        	boolean confirmaTrueFalse = contextoAcao.confirmarSimNao("Confirma��o", "Deseja realmente modificar o cabe�alho desta nota para que seja poss�vel fazer estorno na TOP 1218?\nN�o ser� poss�vel reverter as altera��es.", 0);
        	
        	if (confirmaTrueFalse) {
        		BigDecimal codTipOper = (BigDecimal) registrosSelecionados[0].getCampo("CODTIPOPER");
            	
            	if (codTipOper.compareTo(BigDecimal.valueOf(1166)) != 0 && codTipOper.compareTo(BigDecimal.valueOf(1155)) != 0) {
            		contextoAcao.mostraErro("Cabe�alho selecionado n�o � TOP 1166/1155");
            	} else {
            		for (Registro registroSelecionado : registrosSelecionados) {
            			Timestamp dhTipOper = Timestamp.valueOf("2021-10-01 10:41:28.000");
            			
        				registroSelecionado.setCampo("DHTIPOPER", dhTipOper);
        			}
            	}
        	}
        }
	}
	
}