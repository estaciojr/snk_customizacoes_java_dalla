package br.com.dalla.deive.acoes;

import java.text.Normalizer;
import java.util.regex.Pattern;

import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;

public class RegularizacaoCaracteresInvalidos implements AcaoRotinaJava {

    @Override
	public void doAction(ContextoAcao contextoAcao) throws Exception {
		
		Registro registrosSelecionados[] = contextoAcao.getLinhas();
        
        if (registrosSelecionados.length == 0) {
            contextoAcao.mostraErro("Selecione pelo menos uma linha.");
        } else {
        	for (Registro registroSelecionado : registrosSelecionados) {
        		String descrProd = (String) registroSelecionado.getCampo("DESCRPROD");
        		
        		descrProd = semAcento(descrProd);
        		
        		registroSelecionado.setCampo("AD_DESCRICAO_NORMALIZADA", descrProd);
        	}
        }
		
	}
    
	public static String semAcento(String str) {
        String nfdNormalizedString = Normalizer.normalize(str, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(nfdNormalizedString).replaceAll("");
    }

}
