package br.com.dalla.deive.acoes;

import java.math.BigDecimal;

import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;
import java.sql.PreparedStatement;

public class RemoverTituloTgfcab implements AcaoRotinaJava {

	@Override
	public void doAction(ContextoAcao contextoAcao) throws Exception {
		
		Registro registrosSelecionados[] = contextoAcao.getLinhas();
        
        if (registrosSelecionados.length == 0 || registrosSelecionados.length > 1) {
            contextoAcao.mostraErro("Selecione apenas uma linha.");
        } else {
        	boolean confirmaTrueFalse = contextoAcao.confirmarSimNao("Confirmação", "Deseja realmente deletar o registro selecionado?", 0);
        	
        	if (confirmaTrueFalse) {
                BigDecimal nuFin = (BigDecimal) registrosSelecionados[0].getCampo("NUFIN");
                
        		EntityFacade dwf = EntityFacadeFactory.getDWFFacade();
        		
        		JdbcWrapper jdbc = dwf.getJdbcWrapper();
        		
        		jdbc.openSession();
        		
        		PreparedStatement pstm = null;
        		
        		pstm = jdbc.getPreparedStatement("DELETE FROM TGFFIN WHERE NUFIN = " + nuFin + ";");
        		
        	    pstm.executeUpdate();
        	    
        	    jdbc.closeSession();
        	}
        }
		
	}
	
}
