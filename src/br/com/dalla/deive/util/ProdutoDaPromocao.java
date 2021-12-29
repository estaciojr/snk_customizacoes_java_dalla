package br.com.dalla.deive.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

import br.com.sankhya.jape.bmp.PersistentLocalEntity;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.vo.EntityVO;
import br.com.sankhya.modelcore.comercial.ComercialUtils;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

public class ProdutoDaPromocao {
	
	private DynamicVO produtoDaPromocao = null;
	private DynamicVO cabecalhoDaPromocaoVO = null;
	private DynamicVO tabelaDePrecoVO = null;
	
	public ProdutoDaPromocao(DynamicVO vo, PersistenceEvent persistenceEvent) throws Exception {
		this.produtoDaPromocao = vo;
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
	
	private BigDecimal getPercentualDeDesconto() throws Exception {
		double precoPromocional = this.produtoDaPromocao.asBigDecimalOrZero("PRECOPROM").doubleValue();
		double precoDeTabela = this.getPrecoDaTabela().doubleValue();
		
		BigDecimal percentualDeDesconto = BigDecimal.valueOf(precoPromocional / precoDeTabela * 100);
		
		percentualDeDesconto = percentualDeDesconto.setScale(2, RoundingMode.HALF_EVEN);
		
		return percentualDeDesconto;
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
	
	public void atualizarDadosDoProdutoNaPromocao() throws Exception {
		System.out.println(
			"EventoAtualizaPrecoTabelaProdutoPromocao. "
			+ "Tabela de preco=" + this.cabecalhoDaPromocaoVO.asBigDecimalOrZero("NUTAB")
			+ ". Produto=" + this.produtoDaPromocao.asBigDecimalOrZero("CODPROD")
			+ ". Preco na tabela=" + this.getPrecoDaTabela()
			+ ". Preco promocional=" + this.produtoDaPromocao.asBigDecimalOrZero("PRECOPROM")
			+ ". Percentual de desconto=" + this.getPercentualDeDesconto()
			+ ". Custo gerencial=" + this.getCustoGerencial()
		);
		
		if (this.valorDoDescontoEhMaiorQueValorDeVenda()) {
			HelperMensagemDeErro.exibirErro(
				"Valor promocional maior que valor do preço de venda 10x!\n"
				+ "Preço promocional: " + this.produtoDaPromocao.asBigDecimalOrZero("PRECOPROM") + "\n"
				+ "Preço de venda 10x: " + this.getPrecoDaTabela() + "\n"
				+ "Tabela de venda: " + this.cabecalhoDaPromocaoVO.asBigDecimalOrZero("NUTAB")
			);
		}
		
		if (this.valorDoPrecoPromocionalEhNegativo()) {
			HelperMensagemDeErro.exibirErro(
				"Valor promocional negativo. Inválido.\n"
				+ "Valor promocional: " + this.produtoDaPromocao.asBigDecimalOrZero("PRECOPROM")
			);
		}
		
		PersistentLocalEntity persistentLocalEntity = EntityFacadeFactory.getDWFFacade().findEntityByPrimaryKey("AD_TGFDESC", new Object[] { this.produtoDaPromocao.asBigDecimalOrZero("CODGRUPODESC"), this.produtoDaPromocao.asBigDecimalOrZero("SEQUENCIA") });
		DynamicVO dynamicVO = (DynamicVO) persistentLocalEntity.getValueObject();
		dynamicVO.setProperty("PRECOTAB", this.getPrecoDaTabela());
		dynamicVO.setProperty("PERCDESC", this.getPercentualDeDesconto());
		persistentLocalEntity.setValueObject((EntityVO) dynamicVO);
	}

}