package br.com.dalla.deive.acoes;

import java.math.BigDecimal;
import java.sql.Timestamp;

import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;

public class AlterarParaEstornoTop1166e1155 implements AcaoRotinaJava {
	
	/* Autor: Deivisson - 19/10/2021
	 * Botão de ação para funcionar apenas com a TOP 1166 e 1155
	 * Apenas altera o campo DHTIPOPER no cabeçalho da nota de saída
	 * para que seja possível utilizar a configuração da TOP 1166 do dia '2021-10-01 10:41:28.000'
	 * visto que nessa data de alteração o configurado no campo CODTIPOPERDESTINO está como a TOP de estorno 1218
	 */
	
	/* Problemas:
	 * Caso tenha alguma alteração na TOP 1155, 1166, 1157, 1153 e 1156
	 * as alterações voltarão para uma criada no dia 2021-10-01 10:41:28.000
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
		CODTIPOPERDESTINO deve estar = 1218--1220
	 */
	
	@Override
	public void doAction(ContextoAcao contextoAcao) throws Exception {
		Registro registrosSelecionados[] = contextoAcao.getLinhas();
        
        if (registrosSelecionados.length != 1) {
            contextoAcao.mostraErro("Selecione apenas uma linha.");
        } else {
        	boolean confirmaTrueFalse = contextoAcao.confirmarSimNao("Confirmação", "1157 = 1220\n1153 = 1220\n1166 = 1218\n1155 = 1218\n1156 = 1218\nDeseja realmente modificar o cabeçalho desta nota para que seja possível fazer estorno?\n", 0);
        	
        	if (confirmaTrueFalse) {
        		BigDecimal codTipOper = (BigDecimal) registrosSelecionados[0].getCampo("CODTIPOPER");
            	
            	if (codTipOper.compareTo(BigDecimal.valueOf(1166)) != 0 && codTipOper.compareTo(BigDecimal.valueOf(1155)) != 0 && codTipOper.compareTo(BigDecimal.valueOf(1157)) != 0 && codTipOper.compareTo(BigDecimal.valueOf(1153)) != 0 && codTipOper.compareTo(BigDecimal.valueOf(1156)) != 0) {
            		contextoAcao.mostraErro("Cabeçalho selecionado não é TOP 1166/1155/1157/1153/1156");
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
