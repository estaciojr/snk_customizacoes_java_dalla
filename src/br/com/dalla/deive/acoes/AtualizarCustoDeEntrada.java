package br.com.dalla.deive.acoes;

import br.com.sankhya.commons.xml.Element;
import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;
//import br.com.sankhya.modelcore.MGEModelException;
import br.com.sankhya.modelcore.comercial.PrecoCustoHelper;
//import br.com.sankhya.modelcore.comercial.centrais.CACHelper;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;
//import br.com.sankhya.ws.ServiceContext;
//import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.core.JapeSession;
//import br.com.sankhya.jape.dao.EntityPrimaryKey;
import br.com.sankhya.jape.dao.JdbcWrapper;
//import br.com.sankhya.jape.vo.DynamicVO;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Iterator;

public class AtualizarCustoDeEntrada implements AcaoRotinaJava {

	@Override
	public void doAction(ContextoAcao contextoAcao) throws Exception {
		
		Registro registrosSelecionados[] = contextoAcao.getLinhas();
        
        if (registrosSelecionados.length == 0) {
            contextoAcao.mostraErro("Selecione pelo menos uma linha.");
        } else {
        	
        	for (Registro registroSelecionado : registrosSelecionados) {
        		BigDecimal nuNota = (BigDecimal) registroSelecionado.getCampo("NUNOTA");
        		
        		this.recalcularCustoDeEntrada(contextoAcao, nuNota);
        	}
        	
        }
		
	}
	
	private void recalcularCustoDeEntrada(ContextoAcao contextoAcao, BigDecimal nuNota) throws Exception {
		JapeSession.SessionHandle hnd = null;
		JdbcWrapper jdbc = null;
	    
		try {
			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			
			jdbc = EntityFacadeFactory.getDWFFacade().getJdbcWrapper();
	    	jdbc.openSession();
			
			Collection<String> msgAvisos = null;
			
	    	PrecoCustoHelper precoCustoHelper = new PrecoCustoHelper();
	    	msgAvisos = precoCustoHelper.calcularCustoEntradaNota(nuNota, jdbc);
	    	
	    	if (!msgAvisos.isEmpty()) {
				Element avisosElem = new Element("avisos");
				
				for (String msg : msgAvisos) {
					Element msgElem = new Element("aviso");
					msgElem.addContent(msg);
					avisosElem.addContent(msgElem);
				}
	    	}
	    	
	    	
	    	String mensagemCompleta = "";
	    	Iterator<String> iteratorCollectionAvisos = msgAvisos.iterator();
	    	while (iteratorCollectionAvisos.hasNext()) {
	    		mensagemCompleta = mensagemCompleta + "\n" + iteratorCollectionAvisos;
	    		
	    		iteratorCollectionAvisos.next();
	    	}
	    	
	    	contextoAcao.setMensagemRetorno(mensagemCompleta);
		} catch (Exception e) {
			contextoAcao.mostraErro(e.getMessage());
	    } finally {
	    	JdbcWrapper.closeSession(jdbc);
    		JapeSession.close(hnd);
	    }
		
	}
	
}
