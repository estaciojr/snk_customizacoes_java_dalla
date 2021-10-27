package br.com.dalla.deive.acoes;

import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;

import br.com.sankhya.modelcore.util.EntityFacadeFactory;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.dao.EntityPrimaryKey;
import br.com.sankhya.jape.vo.DynamicVO;

import java.math.BigDecimal;

public class AlterarCfopNotasPedidos implements AcaoRotinaJava {

	@Override
	public void doAction(ContextoAcao contextoAcao) throws Exception {
		
		Registro registrosSelecionados[] = contextoAcao.getLinhas();
        
        if (registrosSelecionados.length == 0) {
            contextoAcao.mostraErro("Selecione pelo menos uma linha.");
        } else {
        	BigDecimal nuNota = (BigDecimal) registrosSelecionados[0].getCampo("NUNOTA");
        	
        	EntityFacade dwfEntityFacade = EntityFacadeFactory.getDWFFacade();
        	DynamicVO tgfcabVO = (DynamicVO) dwfEntityFacade.findEntityByPrimaryKeyAsVO("CabecalhoNota", new EntityPrimaryKey(new Object[] { nuNota }));
        	
        	String statusNfe = tgfcabVO.asString("STATUSNFE");
        	
        	if (statusNfe == null) {
        		statusNfe = "R";
        	}
        	
    		if (statusNfe.equals("A") || statusNfe.equals("E") || statusNfe.equals("D")) {
    			contextoAcao.mostraErro("Nota selecionada com Status NF-e marcada como:\n"
    									+ "Aprovada, Aguardando Autorização ou Denegada.\n"
										+ "CFOP não pode ser alterada.");
    		} else {
    			for (Registro registroSelecionado : registrosSelecionados) {
    				registroSelecionado.setCampo("CODCFO", (int) contextoAcao.getParam("P_CFOP"));
    			}
    		}
    		
        }
		
	}
	
}
