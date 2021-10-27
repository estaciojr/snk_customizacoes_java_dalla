package br.com.dalla.deive.acoes;

import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;
import br.com.sankhya.extensions.actionbutton.QueryExecutor;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.util.ArrayList;

public class InserirTiposDeNegociacaoPromo implements AcaoRotinaJava {

	@Override
	public void doAction(ContextoAcao contextoAcao) throws Exception {
		Registro registro = contextoAcao.getLinhaPai();
		
		BigDecimal codGrupoDesc = (BigDecimal) registro.getCampo("CODGRUPODESC");
		
		String acaoEscolhida = (String)contextoAcao.getParam("P_ACAO");
		String tiposDeNegociacaoEscolhida = (String)contextoAcao.getParam("P_TIPOSDENEGOCIACAO");
		
		if (acaoEscolhida.equals("I")) {
			ArrayList<Integer> listaTiposDeNegociacaoFaltantes = this.getTiposDeNegociacaoFaltantes(contextoAcao, codGrupoDesc, tiposDeNegociacaoEscolhida);
			
			if (listaTiposDeNegociacaoFaltantes.isEmpty()) {
				String descricaoTipoDeNegociacao = "";
				
				switch (tiposDeNegociacaoEscolhida) {
					case "A":
						descricaoTipoDeNegociacao = "À vista";
						break;
					case "5":
						descricaoTipoDeNegociacao = "5x (de 2 a 5x)";
						break;
					case "P":
						descricaoTipoDeNegociacao = "Padrão (de 6 a 10x)";
						break;
					default:
						contextoAcao.mostraErro("Campo dos tipos de negociação não preenchido.\\nEntrar em contato com o setor de TI.");
						break;
				}
				
				contextoAcao.mostraErro("Todos os tipos de negociação já estão inseridos.\n"
										+ "Tipos de negociação escolhido: " + descricaoTipoDeNegociacao);
			} else {
				this.inserirTiposDeNegociacaoFaltantes(contextoAcao, codGrupoDesc, listaTiposDeNegociacaoFaltantes);
			}
		} else if (acaoEscolhida.equals("D")) {
			deletarTiposDeNegociacao(contextoAcao, codGrupoDesc);
		}
	}
	
	private ArrayList<Integer> getTiposDeNegociacaoFaltantes(ContextoAcao contextoAcao, BigDecimal codGrupoDesc, String tiposDeNegociacaoEscolhida) throws Exception {
		QueryExecutor queryBuscaTiposDeNegociacao = contextoAcao.getQuery();
    	
		queryBuscaTiposDeNegociacao.setParam("P_CODGRUPODESC", codGrupoDesc);
		queryBuscaTiposDeNegociacao.setParam("P_TIPOSDENEGOCIACAO", tiposDeNegociacaoEscolhida);
    	
		queryBuscaTiposDeNegociacao.nativeSelect("SELECT TPV.CODTIPVENDA AS TPV_CODTIPVENDA FROM TGFTPV (NOLOCK) TPV LEFT JOIN AD_TGFTPVP (NOLOCK) TPVP ON TPV.CODTIPVENDA = TPVP.CODTIPVENDA AND TPVP.CODGRUPODESC = {P_CODGRUPODESC} WHERE TPV.ATIVO = 'S' AND TPV.GRUPOAUTOR = 'V' AND TPV.AD_TABPROMOCOES = {P_TIPOSDENEGOCIACAO} AND TPVP.CODTIPVENDA IS NULL AND TPV.DHALTER = ( SELECT MAX(TPV_AUX.DHALTER) FROM TGFTPV (NOLOCK) TPV_AUX WHERE TPV_AUX.CODTIPVENDA = TPV.CODTIPVENDA );");
    	
		ArrayList<Integer> listaTiposDeNegociacao = new ArrayList<>();
		
    	while (queryBuscaTiposDeNegociacao.next()) {
    		listaTiposDeNegociacao.add(queryBuscaTiposDeNegociacao.getInt("TPV_CODTIPVENDA"));
    	}
    	
    	queryBuscaTiposDeNegociacao.close();
    	
    	return listaTiposDeNegociacao;
	}
	
	private void inserirTiposDeNegociacaoFaltantes(ContextoAcao contextoAcao, BigDecimal codGrupoDesc, ArrayList<Integer> listaTiposDeNegociacaoFaltantes) throws Exception {
		QueryExecutor inserirTiposDeNegociacaoFaltantes = contextoAcao.getQuery();
		
		inserirTiposDeNegociacaoFaltantes.setParam("P_CODGRUPODESC", codGrupoDesc);
		
		for (int tipoDeNegociacaoFaltante : listaTiposDeNegociacaoFaltantes) {
			inserirTiposDeNegociacaoFaltantes.setParam("P_TIPODENEGOCIACAO", tipoDeNegociacaoFaltante);
			
			inserirTiposDeNegociacaoFaltantes.update("INSERT INTO AD_TGFTPVP (CODGRUPODESC, CODTIPVENDA) VALUES ({P_CODGRUPODESC}, {P_TIPODENEGOCIACAO});");
		}
		
		inserirTiposDeNegociacaoFaltantes.close();
	}
	
	private void deletarTiposDeNegociacao(ContextoAcao contextoAcao, BigDecimal codGrupoDesc) throws Exception {
		EntityFacade dwf = EntityFacadeFactory.getDWFFacade();
		JdbcWrapper jdbc = dwf.getJdbcWrapper();
		jdbc.openSession();
		PreparedStatement pstm = null;
		pstm = jdbc.getPreparedStatement("DELETE FROM AD_TGFTPVP WHERE CODGRUPODESC = " + codGrupoDesc + " AND CODTIPVENDA IN (SELECT TPV.CODTIPVENDA FROM TGFTPV (NOLOCK) TPV INNER JOIN AD_TGFTPVP (NOLOCK) TPVP ON TPV.CODTIPVENDA = TPVP.CODTIPVENDA AND TPVP.CODGRUPODESC = " + codGrupoDesc + " WHERE TPV.AD_TABPROMOCOES = '" + contextoAcao.getParam("P_TIPOSDENEGOCIACAO") + "' AND TPV.DHALTER = ( SELECT MAX(TPV_AUX.DHALTER) FROM TGFTPV (NOLOCK) TPV_AUX WHERE TPV_AUX.CODTIPVENDA = TPV.CODTIPVENDA ));");
	    pstm.executeUpdate();
	    jdbc.closeSession();
	}

}
