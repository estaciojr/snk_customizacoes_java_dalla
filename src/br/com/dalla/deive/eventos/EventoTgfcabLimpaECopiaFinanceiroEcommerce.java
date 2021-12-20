package br.com.dalla.deive.eventos;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Iterator;

import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.PersistenceException;
import br.com.sankhya.jape.bmp.PersistentLocalEntity;
import br.com.sankhya.jape.util.FinderWrapper;
import br.com.sankhya.jape.util.JapeSessionContext;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.vo.PrePersistEntityState;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.jape.wrapper.fluid.FluidCreateVO;
import br.com.sankhya.modelcore.auth.AuthenticationInfo;
import br.com.sankhya.modelcore.comercial.CentralFinanceiro;
import br.com.sankhya.modelcore.comercial.ComercialUtils;
import br.com.sankhya.modelcore.comercial.ContextoRegra;
import br.com.sankhya.modelcore.comercial.Regra;
import br.com.sankhya.modelcore.dwfdata.vo.tgf.FinanceiroVO;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

public class EventoTgfcabLimpaECopiaFinanceiroEcommerce implements Regra {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void afterDelete(ContextoRegra contextoRegra) throws Exception { }

	@Override
	public void afterInsert(ContextoRegra contextoRegra) throws Exception { }

	@Override
	public void afterUpdate(ContextoRegra contextoRegra) throws Exception {
		PrePersistEntityState prePersistEntityState = contextoRegra.getPrePersistEntityState();
		
		if (prePersistEntityState.getDao().getEntityName() != null && prePersistEntityState.getDao().getEntityName().equals("CabecalhoNota")) {
			DynamicVO newCabVO = (DynamicVO) prePersistEntityState.getNewVO();
			DynamicVO oldCabVO = (DynamicVO) prePersistEntityState.getOldVO();
			
			if (!oldCabVO.asString("STATUSNOTA").equals("L") && newCabVO.asString("STATUSNOTA").equals("L")) {
				this.iniciarLimpezaECopia(newCabVO);
			}
		}
	}

	@Override
	public void beforeDelete(ContextoRegra contextoRegra) throws Exception { }

	@Override
	public void beforeInsert(ContextoRegra contextoRegra) throws Exception { }

	@Override
	public void beforeUpdate(ContextoRegra contextoRegra) throws Exception { }
	
	public void iniciarLimpezaECopia(DynamicVO event) throws Exception {
		DynamicVO pedidoAtualVO = event;
		
		DynamicVO tipOperAtualVO = ComercialUtils.getTipoOperacao(pedidoAtualVO.asBigDecimal("CODTIPOPER"));
		
		if (tipOperAtualVO != null) {
			String adNotaEcom = tipOperAtualVO.asString("AD_NOTAECOM") == null ? "N" : tipOperAtualVO.asString("AD_NOTAECOM");
			
			if (adNotaEcom.equals("S")) {
				DynamicVO pedidoOrigemVO = this.getCabDynamicVO(pedidoAtualVO.asBigDecimal("AD_NUNOTAORIG"));
				
				if (pedidoOrigemVO != null) {
					int codEmpOrigem = pedidoOrigemVO.asInt("CODEMP");
					int codTipOperOrigem = pedidoOrigemVO.asInt("CODTIPOPER");
					String nuPedidoVtex = pedidoOrigemVO.asString("AD_PEDIDOECOM");
					
					if (nuPedidoVtex == null) {
						this.exibirErro("Pedido origem não veio da VTEX.");
					}

					if (
						codEmpOrigem == 9
						&& codTipOperOrigem == 1009
						&& nuPedidoVtex != null
						&& pedidoAtualVO.asInt("NUNOTA") != pedidoAtualVO.asInt("AD_NUNOTAORIG")
					) {
						this.apagarTitulos(pedidoAtualVO);
						this.copiarTitulos(pedidoAtualVO.asBigDecimal("AD_NUNOTAORIG"), pedidoAtualVO, tipOperAtualVO);
					}
				}
			}
		}
	}
	
	public DynamicVO getCabDynamicVO(BigDecimal nuNota) throws Exception {
		JapeWrapper DAO = JapeFactory.dao("CabecalhoNota");
		DynamicVO Vo = DAO.findOne("NUNOTA = ?", new Object[] { nuNota });
		return Vo;
	}
	
	public void copiarTitulos(BigDecimal nroUnicoPedOrigem, DynamicVO pedidoAtualVO, DynamicVO tipOperAtualVO) throws Exception {
		EntityFacade dwf = EntityFacadeFactory.getDWFFacade();
		FinderWrapper titulosOrigemFinder = new FinderWrapper("Financeiro", "this.NUNOTA = ?", new Object[]{ nroUnicoPedOrigem });
		Collection<PersistentLocalEntity> titulosOrigemFinderCollection = dwf.findByDynamicFinder(titulosOrigemFinder);
		
		Iterator<PersistentLocalEntity> titulosOrigemIterator = titulosOrigemFinderCollection.iterator();
		
		while (titulosOrigemIterator.hasNext()) {
			PersistentLocalEntity tituloOrigemPersistentLocalEntity = (PersistentLocalEntity) titulosOrigemIterator.next();
			DynamicVO tituloOrigemVO = ((DynamicVO) tituloOrigemPersistentLocalEntity.getValueObject()).wrapInterface(FinanceiroVO.class);
			
			JapeWrapper novoFinanceiroDAO = JapeFactory.dao("Financeiro");
			FluidCreateVO novoFinanceiroFluidVO = novoFinanceiroDAO.create();
			novoFinanceiroFluidVO.set("RECDESP", tituloOrigemVO.asBigDecimal("RECDESP"));
			novoFinanceiroFluidVO.set("CODNAT", tituloOrigemVO.asBigDecimal("CODNAT"));
			novoFinanceiroFluidVO.set("CODCENCUS", tituloOrigemVO.asBigDecimal("CODCENCUS"));
			novoFinanceiroFluidVO.set("CODEMP", tituloOrigemVO.asBigDecimal("CODEMP"));
			novoFinanceiroFluidVO.set("CODPARC", tituloOrigemVO.asBigDecimal("CODPARC"));
			novoFinanceiroFluidVO.set("DTNEG", tituloOrigemVO.asTimestamp("DTNEG"));
			novoFinanceiroFluidVO.set("CODTIPOPER", tituloOrigemVO.asBigDecimal("CODTIPOPER"));
			novoFinanceiroFluidVO.set("DHTIPOPER", tituloOrigemVO.asTimestamp("DHTIPOPER"));
			novoFinanceiroFluidVO.set("ORIGEM", tituloOrigemVO.asString("ORIGEM"));
			novoFinanceiroFluidVO.set("NUNOTA", pedidoAtualVO.asBigDecimal("NUNOTA"));
			novoFinanceiroFluidVO.set("NUMNOTA", pedidoAtualVO.asBigDecimal("NUMNOTA"));
			novoFinanceiroFluidVO.set("DESDOBRAMENTO", tituloOrigemVO.asString("DESDOBRAMENTO"));
			novoFinanceiroFluidVO.set("VLRDESDOB", tituloOrigemVO.asBigDecimal("VLRDESDOB"));
			novoFinanceiroFluidVO.set("CODTIPTIT", tituloOrigemVO.asBigDecimal("CODTIPTIT"));
			novoFinanceiroFluidVO.set("DTVENC", tituloOrigemVO.asTimestamp("DTVENC"));
			novoFinanceiroFluidVO.set("HISTORICO", tituloOrigemVO.asString("HISTORICO"));
			String provisao = tipOperAtualVO.asString("TIPATUALFIN").equals("P") ? "S" : "N";
			novoFinanceiroFluidVO.set("PROVISAO", provisao);
            DynamicVO novoFin = novoFinanceiroFluidVO.save();
			
			this.mostrarNoConsole(
				"Copiando o título com os seguintes dados:\n"
				+ "nufinOrigem = " + tituloOrigemVO.getProperty("NUFIN") + "\n"
				+ "nunotaOrigem = " + tituloOrigemVO.getProperty("NUNOTA") + "\n"
				+ "codTipTitOrigem = " + tituloOrigemVO.asBigDecimal("CODTIPTIT") + "\n"
				+ "vlrDesdobOrigem = " + tituloOrigemVO.asBigDecimal("VLRDESDOB") + "\n"
				+ "dtVencOrigem = " + tituloOrigemVO.getProperty("DTVENC") + "\n"
				+ "nuFinNovo = " + novoFin.asBigDecimal("NUFIN") + "\n"
				+ "nunotaNovo = " + pedidoAtualVO.asBigDecimal("NUNOTA")
			);
		}
	}
	
	public void mostrarNoConsole(String mensagem) {
		System.out.println(
			"====================== Mensagem ======================\n"
			+ "=========== AcaoLimpaECopiaTitulosEcommerce ==========\n"
			+ mensagem + "\n"
			+ "======================================================"
		);
	}
	
	public void apagarTitulos(DynamicVO pedidoAtualVO) throws Exception {
		this.setupContext();
		
		CentralFinanceiro centralFinanceiro = new CentralFinanceiro();
		centralFinanceiro.excluiFinanceiro(pedidoAtualVO.asBigDecimal("NUNOTA"));
		
		this.mostrarNoConsole(
			"Apagando todos os títulos do nro único "
			+ "nroUnicoAtual = " + pedidoAtualVO.asBigDecimal("NUNOTA") + "\n"
			+ "codTipOper = " + pedidoAtualVO.asBigDecimal("CODTIPOPER") + "\n"
			+ "dtNeg = " + pedidoAtualVO.asTimestamp("DTNEG") + "\n"
			+ "dtAlter = " + pedidoAtualVO.asTimestamp("DTALTER")
		);
	}
	
	public void setupContext() {
		AuthenticationInfo auth = AuthenticationInfo.getCurrent();
		JapeSessionContext.putProperty("usuario_logado", auth.getUserID());
		JapeSessionContext.putProperty("authInfo", auth);
		JapeSessionContext.putProperty("br.com.sankhya.com.CentralCompraVenda", Boolean.TRUE);
	}
	
	public boolean estaConfirmada(Registro registroSelecionado) {
		if (registroSelecionado.getCampo("STATUSNOTA").equals("L")) {
			return true;
		}
		
		return false;
	}
	
	private void exibirErro(String mensagem) throws Exception  {
		throw new PersistenceException("<p align=\"center\"><img src=\"https://dallabernardina.vteximg.com.br/arquivos/logo_header.png\"></img></p><br/><br/><br/><br/><br/><br/>\n\n\n\n<font size=\"12\" color=\"#BF2C2C\"><b> " + mensagem + "</b></font>\n\n\n");
	}
	
	public DynamicVO getTgfcab(BigDecimal nuNota) throws Exception {
		JapeWrapper DAO = JapeFactory.dao("CabecalhoNota");
		DynamicVO Vo = DAO.findOne("NUNOTA = ?", new Object[] { nuNota });
		return Vo;
	}
	
}
