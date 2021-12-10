package br.com.dalla.deive.acoes;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Iterator;

import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.bmp.PersistentLocalEntity;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.util.FinderWrapper;
import br.com.sankhya.jape.util.JapeSessionContext;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.jape.wrapper.fluid.FluidCreateVO;
import br.com.sankhya.modelcore.auth.AuthenticationInfo;
import br.com.sankhya.modelcore.dwfdata.vo.tgf.FinanceiroVO;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

public class AcaoCopiaTitulosEcommerce implements AcaoRotinaJava {

	@Override
	public void doAction(ContextoAcao contextoAcao) throws Exception {	
		Registro registrosSelecionados[] = contextoAcao.getLinhas();
        
        if (registrosSelecionados.length == 0) {
            contextoAcao.mostraErro("Selecione pelo menos uma linha.");
        } else {       	
        	for (Registro registroSelecionado : registrosSelecionados) {
        		this.inicio(registroSelecionado);
        	}
        }
	}
	
	public void inicio(Registro registroSelecionado) throws Exception {
		DynamicVO pedidoAtualVO = (DynamicVO) this.getTgfcab((BigDecimal) registroSelecionado.getCampo("NUNOTA"));
		
		int nroUnicoAtual = pedidoAtualVO.asInt("NUNOTA");
		int nroUnicoOrigem = pedidoAtualVO.asInt("AD_NUNOTAORIG");
		int numNotaAtual = pedidoAtualVO.asInt("NUMNOTA");
		
		DynamicVO tipOperAtualVO = this.getTgftop(pedidoAtualVO.asInt("CODTIPOPER"), pedidoAtualVO.asTimestamp("DHTIPOPER"));
		
		if (tipOperAtualVO != null) {
			String adNotaEcom = tipOperAtualVO.asString("AD_NOTAECOM") == null ? "N" : tipOperAtualVO.asString("AD_NOTAECOM");
			
			if (adNotaEcom.equals("S")) {
				DynamicVO pedidoOrigemVO = this.getTgfcab(new BigDecimal(nroUnicoOrigem));
				
				if (pedidoOrigemVO != null) {
					int codEmpOrigem = pedidoOrigemVO.asInt("CODEMP");
					int codTipOperOrigem = pedidoOrigemVO.asInt("CODTIPOPER");
					String nuPedidoVtex = pedidoOrigemVO.asString("AD_PEDIDOECOM");

					if (codEmpOrigem == 9
							&& codTipOperOrigem == 1009
							&& nuPedidoVtex != null
							&& nroUnicoAtual != nroUnicoOrigem) {
						this.copiaTitulos(nroUnicoOrigem, pedidoAtualVO);

//						this.mostrarNoConsole("nroUnicoAtual = " + nroUnicoAtual + "\n"
//								+ "nroUnicoOrigem = " + nroUnicoOrigem + "\n"
//								+ "adNotaEcom = " + adNotaEcom + "\n"
//								+ "codEmpOrigem = " + codEmpOrigem + "\n"
//								+ "codTipOperOrigem = " + codTipOperOrigem + "\n"
//								+ "nuPedidoVtex = " + nuPedidoVtex);
					}
				}
			}
		}
	}
	
	public DynamicVO getTgfcab(BigDecimal nuNota) throws Exception {
		JapeWrapper DAO = JapeFactory.dao("CabecalhoNota");
		DynamicVO Vo = DAO.findOne("NUNOTA = ?", new Object[] { nuNota });
		return Vo;
	}

	public DynamicVO getTgftop(int codTipOper, Timestamp dhAlter) throws Exception {
		JapeWrapper DAO = JapeFactory.dao("TipoOperacao");
		DynamicVO Vo = DAO.findOne("CODTIPOPER = ? AND DHALTER = ?", new Object[] { codTipOper, dhAlter });
		return Vo;
	}
	
	public void copiaTitulos(int nroUnicoPedOrigem, DynamicVO pedidoAtualVO) throws Exception {
		this.setupContext();
		
		EntityFacade dwf = EntityFacadeFactory.getDWFFacade();
		FinderWrapper titulosOrigemFinder = new FinderWrapper("Financeiro", "this.NUNOTA = ?", new Object[]{ nroUnicoPedOrigem });
		Collection<PersistentLocalEntity> titulosOrigemFinderCollection = dwf.findByDynamicFinder(titulosOrigemFinder);
		
		Iterator titulosOrigemIterator = titulosOrigemFinderCollection.iterator();
		
		while (titulosOrigemIterator.hasNext()) {
			PersistentLocalEntity tituloOrigemPersistentLocalEntity = (PersistentLocalEntity) titulosOrigemIterator.next();
			DynamicVO tituloOrigemVO = ((DynamicVO) tituloOrigemPersistentLocalEntity.getValueObject()).wrapInterface(FinanceiroVO.class);
			
			JapeWrapper novoFinanceiroDAO = JapeFactory.dao("Financeiro");
			FluidCreateVO novoFinanceiroFluidVO = novoFinanceiroDAO.create();
			novoFinanceiroFluidVO.set("PROVISAO", tituloOrigemVO.getProperty("PROVISAO"));
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
            DynamicVO novoFin = novoFinanceiroFluidVO.save();
			
			this.mostrarNoConsole("nufinOrigem = " + tituloOrigemVO.getProperty("NUFIN") + "\n"
					+ "nunotaOrigem = " + tituloOrigemVO.getProperty("NUNOTA") + "\n"
					+ "codTipTitOrigem = " + tituloOrigemVO.asBigDecimal("CODTIPTIT") + "\n"
					+ "vlrDesdobOrigem = " + tituloOrigemVO.asBigDecimal("VLRDESDOB") + "\n"
					+ "dtVencOrigem = " + tituloOrigemVO.getProperty("DTVENC") + "\n"
					+ "nuFinNovo = " + novoFin.asBigDecimal("NUFIN"));
		}
	}

	public void mostrarNoConsole(String mensagem) {
		System.out.println("\n====================== Mensagem ======================\n========== Copia título ecommerce ===========\n" + mensagem + "\n======================================================");
	}
	
	public void setupContext() {
		AuthenticationInfo auth = AuthenticationInfo.getCurrent();
		JapeSessionContext.putProperty("usuario_logado", auth.getUserID());
		JapeSessionContext.putProperty("authInfo", auth);
		JapeSessionContext.putProperty("br.com.sankhya.com.CentralCompraVenda", Boolean.TRUE);
		JapeSessionContext.putProperty("br.com.sankhya.com.CentralCompraVenda", Boolean.TRUE);
		JapeSession.putProperty("ItemNota.incluindo.alterando.pela.central", Boolean.TRUE);
	}
	
}
