package br.com.dalla.deive.acoes;

import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.PersistenceException;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.vo.EntityVO;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

public class TesteDWFFacade implements AcaoRotinaJava {

    @Override
    public void doAction(ContextoAcao contextoAcao) throws Exception {
    	EntityFacade entityFacade = EntityFacadeFactory.getDWFFacade();

        EntityVO tituloEntityVO = entityFacade.getDefaultValueObjectInstance("Financeiro");

        DynamicVO tituloDynamicVO = (DynamicVO) tituloEntityVO;
        
        tituloDynamicVO.setProperty("AD_OBSERV_LIBERACAO", "Teste");
        
        this.exibirErro("NUFIN: " + tituloDynamicVO.asString("AD_OBSERV_LIBERACAO"));
    }

    private void exibirErro(String mensagem) throws Exception {
        throw new PersistenceException("<p align=\"center\"><img src=\"https://dallabernardina.vteximg.com.br/arquivos/logo_header.png\" height=\"100\" width=\"300\"></img></p><br/><br/><br/><br/><br/><br/>\n\n\n\n<font size=\"15\" color=\"#BF2C2C\"><b> " + mensagem + "</b></font>\n\n\n");
    }

}