package br.com.dalla.deive.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

import br.com.sankhya.jape.bmp.PersistentLocalEntity;
import br.com.sankhya.jape.event.ModifingFields;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.vo.EntityVO;
import br.com.sankhya.modelcore.comercial.ComercialUtils;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

public class ProdutoDaPromocao {
	
	private DynamicVO produtoDaPromocao = null;
	private DynamicVO cabecalhoDaPromocaoVO = null;
	private DynamicVO tabelaDePrecoVO = null;
	private PersistenceEvent persistenceEvent;
	private String ondeEstaOEvento;
	
	public ProdutoDaPromocao(PersistenceEvent persistenceEvent, String ondeEstaOEvento) throws Exception {
		this.produtoDaPromocao = (DynamicVO) persistenceEvent.getVo();
		this.persistenceEvent = persistenceEvent;
		this.ondeEstaOEvento = ondeEstaOEvento;
	}
	
	public boolean setCabecalhoDaPromocaoVO() throws Exception {
		this.cabecalhoDaPromocaoVO = HelperGetDynamicVO.getCabecalhoPromocao(this.produtoDaPromocao.asBigDecimalOrZero("CODGRUPODESC"));
		
		if (this.cabecalhoDaPromocaoVO != null) {
			return true;
		}
		
		return false;
	}
	
	public boolean setTabelaDePrecoVO() throws Exception {
		this.tabelaDePrecoVO = HelperGetDynamicVO.getTabPrecoDynamicVO(this.cabecalhoDaPromocaoVO.asBigDecimalOrZero("NUTAB"));
		
		if (this.tabelaDePrecoVO != null) {
			return true;
		}
		
		return false;
	}
	
	private BigDecimal getPrecoDaTabela() throws Exception {
		return ComercialUtils.obtemPreco2(
			this.cabecalhoDaPromocaoVO.asBigDecimalOrZero("NUTAB"),
	 		this.produtoDaPromocao.asBigDecimalOrZero("CODPROD"),
	 		this.tabelaDePrecoVO.asTimestamp("DTVIGOR")
		);
	}
	
	private BigDecimal getCustoGerencial() throws Exception {
		return ComercialUtils.getUltimoCusto(this.produtoDaPromocao.asBigDecimalOrZero("CODPROD"), BigDecimal.valueOf(1), BigDecimal.valueOf(0), "", "CUSGER");
	}
	
	private BigDecimal getPercentualDeDescontoCalculadoPeloPeloPrecoPromocional() throws Exception {
		double precoPromocional = this.produtoDaPromocao.asBigDecimalOrZero("PRECOPROM").doubleValue();
		double precoDeTabela = this.getPrecoDaTabela().doubleValue();
		
		BigDecimal percentualDeDesconto = BigDecimal.valueOf(100 - precoPromocional / precoDeTabela * 100);
		
		percentualDeDesconto = percentualDeDesconto.setScale(2, RoundingMode.HALF_EVEN);
		
		return percentualDeDesconto;
	}
	
	private BigDecimal getValorPromocionalCalculadoPelaPorcentagemDeDesconto() throws Exception {
		double percentualDeDesconto = this.produtoDaPromocao.asBigDecimalOrZero("PERCDESC").doubleValue();
		double precoDeTabela = this.getPrecoDaTabela().doubleValue();
		
		BigDecimal valorPromocional = BigDecimal.valueOf(precoDeTabela - precoDeTabela * percentualDeDesconto / 100);
		
		valorPromocional = valorPromocional.setScale(2, RoundingMode.HALF_EVEN);
		
		return valorPromocional;
	}
	
	private boolean valorDoDescontoEhMaiorQueValorDeVenda() throws Exception {
		if (this.produtoDaPromocao.asDouble("PRECOPROM") > this.getPrecoDaTabela().doubleValue()) {
			return true;
		}
		
		return false;
	}
	
	private boolean valorDoPrecoPromocionalEhNegativo() throws Exception {
		if (this.produtoDaPromocao.asBigDecimalOrZero("PRECOPROM").doubleValue() < 0) {
			return true;
		}
		
		return false;
	}
	
	private boolean percentualDeDescontoEhMaiorQue100() throws Exception {
		return (this.produtoDaPromocao.asBigDecimalOrZero("PERCDESC").doubleValue() > 100) ? true : false ;
	}
	
	private void executarValidacoes() throws Exception {
		if (this.valorDoDescontoEhMaiorQueValorDeVenda()) {
			HelperMensagemDeErro.exibirErro(
				"Valor promocional maior que valor do preço de venda 10x!\n"
				+ "Preço promocional: " + this.produtoDaPromocao.asBigDecimalOrZero("PRECOPROM") + "\n"
				+ "Preço de venda 10x: " + this.getPrecoDaTabela() + "\n"
				+ "Tabela de venda: " + this.cabecalhoDaPromocaoVO.asBigDecimalOrZero("NUTAB")
			);
		}
		
		if (this.percentualDeDescontoEhMaiorQue100()) {
			HelperMensagemDeErro.exibirErro(
				"Percentual de desconto inválido!\n"
				+ "Maior que 100%."
			);
		}
		
		if (this.valorDoPrecoPromocionalEhNegativo()) {
			HelperMensagemDeErro.exibirErro(
				"Valor promocional negativo. Inválido.\n"
				+ "Valor promocional: " + this.produtoDaPromocao.asBigDecimalOrZero("PRECOPROM")
			);
		}
	}
	
	public void atualizarDadosDoProdutoNaPromocao() throws Exception {
		System.out.println(
			"EventoAtualizaPrecoTabelaProdutoPromocao. "
			+ "Tabela de preco=" + this.cabecalhoDaPromocaoVO.asBigDecimalOrZero("NUTAB")
			+ ". Produto=" + this.produtoDaPromocao.asBigDecimalOrZero("CODPROD")
			+ ". Preco na tabela=" + this.getPrecoDaTabela()
			+ ". Preco promocional=" + this.produtoDaPromocao.asBigDecimalOrZero("PRECOPROM")
			+ ". Percentual de desconto=" + this.getPercentualDeDescontoCalculadoPeloPeloPrecoPromocional()
			+ ". Custo gerencial=" + this.getCustoGerencial()
		);
		
		this.executarValidacoes();
		
		/* Quando o evento de afterInsert é executado, é feito modificação no campo
		 * PERCDESC, que por consequência o evento tenta modificar o campo PRECOPROM, ele entra
		 * na cláusula do beforeUpdate. Dessa forma recalculando o valor da porcentagem de desconto.
		 * Essa variável faz a identificação que os dados estão sendo inseridos.
		 * Quando o evento entra na cláusula do beforeUpdate através do afterInsert, essa alteração
		 * da porcentagem de desconto é ignorada.
		 * Exemplo:
		 *  	Inserindo o produto 1260, com preço de venda 10x sendo 23,45.
		 *  	É inserido a porcentagem de 10% de desconto.
		 *  	O que dá 3,34 no valor do desconto.
		 *  	Quando o valor promocional/valor do desconto é modificado o evento
		 *  	entra no beforeUpdate, calculando a porcentagem de desconto usando como base o valor
		 *  	de 3,34. O que equivale a 9,98%. Dessa forma a porcentagem de desconto ficava sendo 9,98 ao invés de 10
		 */
		String estahSendoInserido = "";
		if (this.produtoDaPromocao.asString("ESTAH_SENDO_INSERIDO") == null) {
			estahSendoInserido = "S";
		} else {
			estahSendoInserido = this.produtoDaPromocao.asString("ESTAH_SENDO_INSERIDO");
		}
		
		if (ondeEstaOEvento.equals("afterInsert")) {
			this.eventoAfterInsert();
		} else if (ondeEstaOEvento.equals("beforeUpdate") && !estahSendoInserido.equals("S")) {
			this.eventoBeforeUpdate();
		}
	}
	
	private void eventoAfterInsert() throws Exception {
		PersistentLocalEntity persistentLocalEntity = EntityFacadeFactory.getDWFFacade().findEntityByPrimaryKey("AD_TGFDESC", new Object[] { this.produtoDaPromocao.asBigDecimalOrZero("CODGRUPODESC"), this.produtoDaPromocao.asBigDecimalOrZero("SEQUENCIA") });
		DynamicVO dynamicVO = (DynamicVO) persistentLocalEntity.getValueObject();
		dynamicVO.setProperty("PRECOTAB", this.getPrecoDaTabela());
		dynamicVO.setProperty("ESTAH_SENDO_INSERIDO", "S");
		
		if (this.produtoDaPromocao.asBigDecimalOrZero("PERCDESC").doubleValue() != 0) {
			dynamicVO.setProperty("PRECOPROM", this.getValorPromocionalCalculadoPelaPorcentagemDeDesconto());
		} else {
			dynamicVO.setProperty("PERCDESC", this.getPercentualDeDescontoCalculadoPeloPeloPrecoPromocional());
		}
		
		persistentLocalEntity.setValueObject((EntityVO) dynamicVO);
		
		this.marcarNaoEstaSendoInserido();
	}
	
	private void eventoBeforeUpdate() throws Exception {
		this.produtoDaPromocao.setProperty("PRECOTAB", this.getPrecoDaTabela());
		
		ModifingFields modifingFields = this.persistenceEvent.getModifingFields();
		if (modifingFields.isModifing("PERCDESC")) {
			System.out.println("Está sendo modificado o campo PERCDESC!!!!");
			this.produtoDaPromocao.setProperty("PRECOPROM", this.getValorPromocionalCalculadoPelaPorcentagemDeDesconto());
		} else if (modifingFields.isModifing("PRECOPROM")) {
			System.out.println("Está sendo modificado o campo PRECOPROM!!!!");
			this.produtoDaPromocao.setProperty("PERCDESC", this.getPercentualDeDescontoCalculadoPeloPeloPrecoPromocional());
		}
	}
	
	private void marcarNaoEstaSendoInserido() throws Exception {
		PersistentLocalEntity persistentLocalEntity = EntityFacadeFactory.getDWFFacade().findEntityByPrimaryKey("AD_TGFDESC", new Object[] { this.produtoDaPromocao.asBigDecimalOrZero("CODGRUPODESC"), this.produtoDaPromocao.asBigDecimalOrZero("SEQUENCIA") });
		DynamicVO dynamicVO = (DynamicVO) persistentLocalEntity.getValueObject();
		dynamicVO.setProperty("PRECOTAB", this.getPrecoDaTabela());
		dynamicVO.setProperty("ESTAH_SENDO_INSERIDO", "N");
		
		persistentLocalEntity.setValueObject((EntityVO) dynamicVO);
	}

}