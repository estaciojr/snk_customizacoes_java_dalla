package br.com.dalla.deive.eventos;

import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.PersistenceException;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.jape.vo.DynamicVO;

import java.math.BigDecimal;

public class ValidaOpcoesDeEntrega implements EventoProgramavelJava {
	
	public void beforeInsert(PersistenceEvent persistenceEvent) throws Exception {
		this.validaOpcaoEntregaFaltante(persistenceEvent);
		
		this.validaTransferenciaCdParaLoja(persistenceEvent);
		
		this.naoPodeRetirarNaDoisEDoze(persistenceEvent);
	}
	
	public void afterInsert(PersistenceEvent persistenceEvent) throws Exception { }
	
	public void beforeUpdate(PersistenceEvent persistenceEvent) throws Exception {
		this.validaOpcaoEntregaFaltante(persistenceEvent);
		
		this.validaTransferenciaCdParaLoja(persistenceEvent);
		
		this.naoPodeRetirarNaDoisEDoze(persistenceEvent);
	}
	
	public void afterUpdate(PersistenceEvent persistenceEvent) throws Exception { }
	
	public void beforeDelete(PersistenceEvent persistenceEvent) throws Exception { }
	
	public void afterDelete(PersistenceEvent persistenceEvent) throws Exception { }

	public void beforeCommit(TransactionContext persistenceEvent) throws Exception { }

	private void naoPodeRetirarNaDoisEDoze(PersistenceEvent persistenceEvent) throws Exception {
		DynamicVO iteVo = (DynamicVO)persistenceEvent.getVo();
		
		DynamicVO cabVo = this.getTgfcab(iteVo.asBigDecimal("NUNOTA"));
		
		int codTipOper = cabVo.asInt("CODTIPOPER");
		
		if (codTipOper == 1001 || codTipOper == 1009 || codTipOper == 1100) {
			BigDecimal codEmpEst = iteVo.asBigDecimal("AD_CODEMPEST");
			BigDecimal codEmpDest = iteVo.asBigDecimal("AD_CODEMPDEST");
			String tipoEntrega = iteVo.asString("AD_ENTREGA");
			
			BigDecimal codEmp = cabVo.asBigDecimal("CODEMP");
			
			if (codEmpEst == null) {
				codEmpEst = codEmp;
			}
			
			if (codEmpDest == null) {
				codEmpDest = codEmp;
			}
			
			if (tipoEntrega == null) {
				tipoEntrega = "C";
			}
			
			if ((codEmpDest.compareTo(new BigDecimal(2)) == 0) && tipoEntrega.equals("X")) {
				exibirErro("Não é possível retirar na empresa 02.");
			}
			
			if (codEmpEst.compareTo(new BigDecimal(12)) == 0 && codEmpDest.compareTo(new BigDecimal(4)) == 0 && (tipoEntrega.equals("E") || tipoEntrega.equals("R"))) {
				exibirErro("Não é possível usar o estoque da empresa 12 para fazer retira ou entrega no CD.");
			}
			
			if (codEmp.compareTo(new BigDecimal(12)) == 0 && codEmpEst.compareTo(new BigDecimal(4)) != 0 && (tipoEntrega.equals("E") || tipoEntrega.equals("R"))) {
				exibirErro("Não é possível usar o estoque de outras lojas para Entrega ou Retira pelo CD.");
			}
			
			if ((codEmp.compareTo(new BigDecimal(2)) != 0 && codEmp.compareTo(new BigDecimal(12)) != 0) && codEmpEst.compareTo(new BigDecimal(12)) == 0) {
				exibirErro("Não é possível usar o estoque da loja 12 para vendas realizadas em outras lojas. Exceto vendas na loja 2 ou 12");
			}
			
		}
	}
	
	private void validaOpcaoEntregaFaltante(PersistenceEvent persistenceEvent) throws Exception {
		// Vo da tabela do evento (Dicionário de Dados - TGFITE)
		DynamicVO iteVo = (DynamicVO)persistenceEvent.getVo();
		
		// Obtendo VO da TGFCAB - Ligação entre tabelas
		DynamicVO cabVo = this.getTgfcab(iteVo.asBigDecimal("NUNOTA"));
		
		int codTipOper = cabVo.asInt("CODTIPOPER");
		
		if (codTipOper == 1001 || codTipOper == 1009 || codTipOper == 1100) {
			
			// Pegando campos de empresa estoque, destino e tipo de entrega
			BigDecimal codEmpEst = iteVo.asBigDecimal("AD_CODEMPEST");
			BigDecimal codEmpDest = iteVo.asBigDecimal("AD_CODEMPDEST");
			String tipoEntrega = iteVo.asString("AD_ENTREGA");
			
			
			BigDecimal codEmp = cabVo.asBigDecimal("CODEMP");
			int codParc = cabVo.asInt("CODPARC");
			
			
			// Se os campos forem nulos, considera o padrão
			if (codEmpEst == null) {
				codEmpEst = codEmp;
			}
			
			
			if (codEmpDest == null) {
				codEmpDest = codEmp;
			}
			
			
			if (tipoEntrega == null) {
				tipoEntrega = "C";
			}
			
			
			// Fazendo validações do tipo de entrega X - Cliente Retira em loja
			if (tipoEntrega.equals("X") && codEmpDest.equals(new BigDecimal(4))) {
				exibirErro("Tipo de entrega marcado como 'Cliente Retira em Loja', mas Empresa de Destino informada foi o CD");
			}
			
			// Se parceiro for Consumidor Final e Opção de Entrega for diferente de caixa
			if (codParc == 1000 && !tipoEntrega.equals("C")) {
				exibirErro("Parceiro Consumidor final não pode ser usado com as opções de entrega 'Cliente Retira em Loja', 'Cliente Retira no CD', 'Entrega' e 'Abastecimento de Lojas'. Pode ser usado apenas como 'Caixa'.");
			}
			
			
			// Fazendo validações do tipo de entrega T - Abastecimento de Lojas
			if (tipoEntrega.equals("T") && codParc >= 50) {
				exibirErro("Tipo de entrega marcado como 'Abastecimento de Lojas', mas Parceiro do cabeçalho não é uma loja");
			}	
			
			
			// Fazendo validação da encomenda - apenas pode ser encomenda sendo LOJA-4-4-Retira CD ou Entrega
			String marcacaoEncomenda = iteVo.asString("AD_ENCOMENDA");
			
			
			if (marcacaoEncomenda == null) {
				marcacaoEncomenda = "N";
			}
			
			
			if (marcacaoEncomenda.equals("S")) {
				if (codEmpEst.compareTo(new BigDecimal(4)) != 0 || codEmpDest.compareTo(new BigDecimal(4)) != 0) {
					this.exibirErro("Produtos vendidos como encomenda devem ter a 'Empresa do Estoque' e 'Empresa de Destino' preenchido com o CD (4)\n" +
									"Código produto: " + iteVo.asBigDecimal("CODPROD"));
				}
				
				
				if (tipoEntrega.equals("C") || tipoEntrega.equals("X") || tipoEntrega.equals("T")) {
					this.exibirErro("Produtos vendidos como encomenda devem ter o tipo de entrega sendo 'Retira no CD' ou 'Entrega'\n" +
									"Código produto: " + iteVo.asBigDecimal("CODPROD"));
				}
			}
		}
	}
	
	private void validaTransferenciaCdParaLoja(PersistenceEvent persistenceEvent) throws Exception {
		DynamicVO iteVo = (DynamicVO)persistenceEvent.getVo();
		
		DynamicVO cabVo = this.getTgfcab(iteVo.asBigDecimal("NUNOTA"));
		
		int codTipOper = cabVo.asInt("CODTIPOPER");
		
		if (codTipOper == 1061) {
			BigDecimal codEmpEst = iteVo.asBigDecimal("AD_CODEMPEST");
			BigDecimal codEmpDest = iteVo.asBigDecimal("AD_CODEMPDEST");
			String tipoEntrega = iteVo.asString("AD_ENTREGA");
			
			BigDecimal codEmp = cabVo.asBigDecimal("CODEMP");
			BigDecimal codParc = cabVo.asBigDecimal("CODPARC");
			
			if (codEmpEst == null) {
				codEmpEst = codEmp;
			}
			
			if (codEmpDest == null) {
				codEmpDest = codParc;
			}
			
			if (tipoEntrega == null) {
				tipoEntrega = "T";
			}
			
			if (!tipoEntrega.equals("T")) {
				this.exibirErro("Tipo de entrega deve ser 'Abastecimento de Lojas'");
			}
			
			if (codEmpEst.compareTo(new BigDecimal(4)) != 0) {
				this.exibirErro("'Empresa do Estoque' deve ser o CD (4)");
			}
			
			if (codEmpDest.compareTo(new BigDecimal(4)) == 0) {
				this.exibirErro("'Empresa de Destino' deve ser uma loja");
			}
			
			if (codEmpDest.compareTo(codParc) != 0) {
				this.exibirErro("'Empresa de Destino' deve ser o mesmo que o parceiro do cabeçalho");
			}
		}
	}

	private DynamicVO getTgfcab(BigDecimal nuNota) throws Exception {
		JapeWrapper DAO = JapeFactory.dao("CabecalhoNota");
		DynamicVO Vo = DAO.findOne("NUNOTA = ?", new Object[] { nuNota });
		return Vo;
	}
	
	private void exibirErro(String mensagem) throws Exception  {
		throw new PersistenceException("<p align=\"center\"><img src=\"https://dallabernardina.vteximg.com.br/arquivos/logo_header.png\"></img></p><br/><br/><br/><br/><br/><br/>\n\n\n\n<font size=\"12\" color=\"#BF2C2C\"><b> " + mensagem + "</b></font>\n\n\n");
	}

}