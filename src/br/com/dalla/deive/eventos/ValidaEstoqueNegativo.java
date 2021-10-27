package br.com.dalla.deive.eventos;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;

import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.PersistenceException;
import br.com.sankhya.jape.bmp.PersistentLocalEntity;
import br.com.sankhya.jape.dao.EntityPrimaryKey;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.vo.EntityVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

public class ValidaEstoqueNegativo implements EventoProgramavelJava {
	
	public void beforeInsert(PersistenceEvent event) throws Exception { }
	
	public void afterInsert(PersistenceEvent event) throws Exception {
		this.validaReservaNevativa(event);
		
		this.setCampoAtualEstoque(event);
	}
	
	public void beforeUpdate(PersistenceEvent event) throws Exception { }
	
	public void afterUpdate(PersistenceEvent event) throws Exception {
		this.validaReservaNevativa(event);
	}
	
	public void beforeDelete(PersistenceEvent event) throws Exception { }
	
	public void afterDelete(PersistenceEvent event) throws Exception { }
	
	public void beforeCommit(TransactionContext event) throws Exception { }
	
	private void validaReservaNevativa(PersistenceEvent event) throws Exception {
		DynamicVO tgfiteVO = (DynamicVO) event.getVo();
		
		if (tgfiteVO != null) {
			DynamicVO tgfproVO = this.getTgfproVo(tgfiteVO.asBigDecimal("CODPROD"));
			DynamicVO tgfgruVO = this.getTgfgruVo(tgfproVO.asBigDecimal("CODGRUPOPROD"));
			
			// Ignorar kits - base da nuvem
			// 103222222
			// 500000000
//			if (!(tgfgruVO.asInt("CODGRUPOPROD") < 500000000) || !(tgfgruVO.asInt("CODGRUPOPROD") >= 600000000)) {
				
				if (!tgfgruVO.asString("VALEST").equals("N")) {
					DynamicVO tgfcabVO = this.getTgfcabVo(tgfiteVO.asBigDecimal("NUNOTA"));
					
					if (tgfcabVO != null) {
						BigDecimal codTopTgfcabVO = tgfcabVO.asBigDecimal("CODTIPOPER");
						
						DynamicVO tgftopVO = this.getTgftopVo(codTopTgfcabVO, tgfcabVO.asTimestamp("DHTIPOPER"));
						
						if (tgftopVO != null) {
							String marcacaoValidaEstoque = tgftopVO.asString("AD_VALIDAESTOQUE");
							String marcacaoEncomenda = tgfiteVO.asString("AD_ENCOMENDA");
							
							if (marcacaoValidaEstoque == null) {
								marcacaoValidaEstoque = "N";
							}
							
							if (marcacaoEncomenda == null) {
								marcacaoEncomenda = "N";
							}
							
							if (marcacaoValidaEstoque.equals("S") && marcacaoEncomenda.equals("N")) {
								BigDecimal codEmpEst = tgfiteVO.asBigDecimal("AD_CODEMPEST");
								BigDecimal codProd = tgfiteVO.asBigDecimal("CODPROD");
								BigDecimal codLocal = tgfiteVO.asBigDecimal("CODLOCALORIG");
								String controle = tgfiteVO.asString("CONTROLE");
								
								if (codEmpEst == null) {
									codEmpEst = tgfcabVO.asBigDecimal("CODEMP");
								}
								
								if (codEmpEst.compareTo(new BigDecimal(4)) == 0) {
									codLocal = new BigDecimal(102);
								} else {
									codLocal = new BigDecimal(101);
								}
								
								BigDecimal qtdNeg = tgfiteVO.asBigDecimal("QTDNEG").setScale(2, RoundingMode.HALF_EVEN);
								
								DynamicVO tgfestVo = this.getTgfestVo(codEmpEst, codProd, codLocal, controle);
								
								if (tgfestVo == null) {
									this.exibirErro("Estoque insuficiente.\n\n" +
													"Código Produto: " + codProd + "\n" +
													"Quantidade: " + qtdNeg + "\n" +
													"Controle: " + controle + "\n" +
													"Empresa do Estoque: " + codEmpEst + "\n" +
													"Local: " + codLocal + "\n");
								}
								
								BigDecimal qtdEstoque = tgfestVo.asBigDecimal("ESTOQUE");
								BigDecimal qtdReservado = tgfestVo.asBigDecimal("RESERVADO");
								BigDecimal qtdBloqueadoWms = tgfestVo.asBigDecimal("WMSBLOQUEADO");
								BigDecimal qtdEmRecebimentoWms = tgfestVo.asBigDecimal("AD_EM_RECEB_WMS");
								BigDecimal qtdDisponivelCalc = tgfestVo.asBigDecimal("AD_DISPONIVEL_CALC").setScale(2, RoundingMode.HALF_EVEN);
								
								if (qtdBloqueadoWms == null) {
									qtdBloqueadoWms = new BigDecimal(0);
								}
								
								if (qtdEmRecebimentoWms == null) {
									qtdEmRecebimentoWms = new BigDecimal(0);
								}
								
								if (tgftopVO.asString("ATUALEST").equals("R")) {
									qtdReservado = qtdReservado.subtract(qtdNeg);
									qtdDisponivelCalc = qtdDisponivelCalc.add(qtdNeg);
								}
								
								BigDecimal reservaTot = qtdReservado.add(qtdBloqueadoWms);
								reservaTot = reservaTot.add(qtdEmRecebimentoWms);
								
								if (qtdDisponivelCalc.compareTo(qtdNeg) < 0) {
									this.exibirErro("Estoque insuficiente.\n\n" +
													"Código Produto: " + codProd + "\n" +
													"Quantidade: " + qtdNeg + "\n" +
													"Controle: " + controle + "\n" +
													"Empresa do Estoque: " + codEmpEst + "\n" +
													"Local: " + codLocal + "\n\n" +
													"Qtd. em Estoque: " + qtdEstoque + "\n" +
													"Qtd. Reservado: " + qtdReservado + "\n" +
													"Qtd. Bloqueado WMS: " + qtdBloqueadoWms + "\n" +
													"Qtd. em Recebimento WMS: " + qtdEmRecebimentoWms + "\n" +
													"Qtd. Reserva Total: " + reservaTot + "\n" +
													"Qtd. Disponível: " + qtdDisponivelCalc);
								}
							}
						}
					}
				}
//			}
		}
	}
	
	private DynamicVO getTgftopVo(BigDecimal codTipOper, Timestamp dhAlter) throws Exception {
		JapeWrapper DAO = JapeFactory.dao("TipoOperacao");
		DynamicVO Vo = DAO.findOne("CODTIPOPER = ? AND DHALTER = ?", new Object[] { codTipOper, dhAlter });
		return Vo;
	}
	
	private DynamicVO getTgfproVo(BigDecimal codProd) throws Exception {
		JapeWrapper DAO = JapeFactory.dao("Produto");
		DynamicVO Vo = DAO.findOne("CODPROD = ?", new Object[] { codProd });
		return Vo;
	}
	
	private DynamicVO getTgfgruVo(BigDecimal codGru) throws Exception {
		JapeWrapper DAO = JapeFactory.dao("GrupoProduto");
		DynamicVO Vo = DAO.findOne("CODGRUPOPROD = ?", new Object[] { codGru });
		return Vo;
	}
	
	private DynamicVO getTgfestVo(BigDecimal codEmp, BigDecimal codProd, BigDecimal codLocal, String controle) throws Exception {
		JapeWrapper DAO = JapeFactory.dao("Estoque");
		DynamicVO Vo = DAO.findOne("CODEMP = ? AND CODPROD = ? AND CODLOCAL = ? AND CONTROLE = ?", new Object[] { codEmp, codProd, codLocal, controle });
		return Vo;
	}
	
	private DynamicVO getTgfcabVo(BigDecimal nuNota) throws Exception {
		JapeWrapper DAO = JapeFactory.dao("CabecalhoNota");
		DynamicVO Vo = DAO.findOne("NUNOTA = ?", new Object[] { nuNota });
		return Vo;
	}
	
	private void setCampoAtualEstoque(PersistenceEvent event) throws Exception {
		DynamicVO iteVo = (DynamicVO) event.getVo();
		
		BigDecimal nuNota = iteVo.asBigDecimal("NUNOTA");
		
		DynamicVO cabVo = (DynamicVO) this.getTgfcabVo(nuNota);
		
		int codTipOperCab = cabVo.asInt("CODTIPOPER");
		String marcacaoEncomenda = iteVo.asString("AD_ENCOMENDA");
		
		if (marcacaoEncomenda == null) {
			marcacaoEncomenda = "N";
		}
		
		if (codTipOperCab == 1100 && marcacaoEncomenda.equals("S")) {
			BigDecimal sequencia = iteVo.asBigDecimal("SEQUENCIA");
			
			EntityFacade dwf = EntityFacadeFactory.getDWFFacade();
			
			PersistentLocalEntity localEntity = dwf.findEntityByPrimaryKey("ItemNota", new EntityPrimaryKey(new Object[] { nuNota, sequencia }) );
			EntityVO NVO = localEntity.getValueObject();
			DynamicVO VO = (DynamicVO) NVO;
			
			VO.setProperty("ATUALESTOQUE", new BigDecimal(0));
			
			localEntity.setValueObject(NVO);
		}
	}
	
	private void exibirErro(String mensagem) throws Exception  {
		throw new PersistenceException("<p align=\"center\" style=\"text-align:center;\"><img src=\"https://dallabernardina.vteximg.com.br/arquivos/logo_header.png\" style=\"margin-left:auto;margin-right:auto\"></img></p><br/><br/><br/><br/><br/><br/>\n\n\n\n<font size=\"12\" color=\"#BF2C2C\"><b> " + mensagem + "</b></font>\n\n\n");
	}
	
}