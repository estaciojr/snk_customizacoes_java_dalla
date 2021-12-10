package br.com.dalla.deive.eventos;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Iterator;

import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.bmp.PersistentLocalEntity;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.jape.util.FinderWrapper;
import br.com.sankhya.jape.util.JapeSessionContext;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.jape.wrapper.fluid.FluidCreateVO;
import br.com.sankhya.modelcore.auth.AuthenticationInfo;
import br.com.sankhya.modelcore.dwfdata.vo.tgf.FinanceiroVO;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

public class CopiaTitulosEcommerce implements EventoProgramavelJava {

	public void beforeInsert(PersistenceEvent event) throws Exception { }

	public void afterInsert(PersistenceEvent event) throws Exception {
		this.inicio(event);
	}

	public void beforeUpdate(PersistenceEvent event) throws Exception { }

	public void afterUpdate(PersistenceEvent event) throws Exception { }

	public void beforeDelete(PersistenceEvent event) throws Exception { }

	public void afterDelete(PersistenceEvent event) throws Exception { }

	public void beforeCommit(TransactionContext event) throws Exception { }
	
	public void inicio(PersistenceEvent event) throws Exception {
		DynamicVO pedidoAtualVO = (DynamicVO) event.getVo();
		
		int nroUnicoAtual = pedidoAtualVO.asInt("NUNOTA");
		int nroUnicoOrigem = pedidoAtualVO.asInt("AD_NUNOTAORIG");
		int numNotaAtual = pedidoAtualVO.asInt("NUMNOTA");
		
		DynamicVO tipOperAtualVO = this.getTgftop(pedidoAtualVO.asInt("CODTIPOPER"), pedidoAtualVO.asTimestamp("DHTIPOPER"));
		
		if (tipOperAtualVO != null) {
			String adNotaEcom = tipOperAtualVO.asString("AD_NOTAECOM") == null ? "N" : tipOperAtualVO.asString("AD_NOTAECOM");
			
			if (adNotaEcom.equals("S")) {
				DynamicVO pedidoOrigemVO = this.getTgfcab(nroUnicoOrigem);
				
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
	
	public DynamicVO getTgfcab(int nuNota) throws Exception {
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

		/*
		Collection<PersistentLocalEntity> itensDoPedido = EntityFacadeFactory.getDWFFacade().findByDynamicFinder(new FinderWrapper(DynamicEntityNames.FINANCEIRO, "this.NUNOTA = ?", new Object[] { nroUnicoPedOrigem }));		
		Iterator<?> iteratorDosItens = itensDoPedido.iterator();
		
		System.out.println("==================================== LINHA 96");
		
		while (iteratorDosItens.hasNext()) {
			System.out.println("==================================== LINHA 98");
			JapeWrapper financeiroDAO = JapeFactory.dao("Financeiro");
			FluidCreateVO tituloVO = financeiroDAO.create();
			
			PersistentLocalEntity tituloLocalEntity = (PersistentLocalEntity) iteratorDosItens.next();
			DynamicVO tituloOrigemVO = ((DynamicVO) tituloLocalEntity.getValueObject()).wrapInterface(FinanceiroVO.class);
			
			tituloVO.set("NUMNOTA", new BigDecimal(numNotaAtual));
			tituloVO.set("NUNOTA", new BigDecimal(nroUnicoAtual));
			tituloVO.set("CODPARC", tituloOrigemVO.asBigDecimal("CODPARC"));
			tituloVO.set("CODEMP", tituloOrigemVO.asBigDecimal("CODEMP"));
			tituloVO.set("DESDOBRAMENTO", tituloOrigemVO.asString("DESDOBRAMENTO"));
			tituloVO.set("DTVENC", tituloOrigemVO.asTimestamp("DTVENC"));
			tituloVO.set("VLRDESDOB", tituloOrigemVO.asBigDecimal("VLRDESDOB"));
			tituloVO.set("CODTIPTIT", tituloOrigemVO.asBigDecimal("CODTIPTIT"));
			tituloVO.set("CODBCO", tituloOrigemVO.asBigDecimal("CODBCO"));
			tituloVO.set("CODNAT", tituloOrigemVO.asBigDecimal("CODNAT"));
			tituloVO.set("DTNEG", tituloOrigemVO.asTimestamp("DTNEG"));
			tituloVO.set("ORIGEM", tituloOrigemVO.asString("ORIGEM"));
			
			tituloVO.save();
			
			System.out.println("NUNOTA = " + BigDecimal.valueOf(nroUnicoAtual));
		}
		/*
		
		/*while (iteratorDosItens.hasNext()) {
			System.out.println("==================================== LINHA 99");
			
			EntityFacade dwfFacade = EntityFacadeFactory.getDWFFacade();
			EntityVO tituloEntityVO = dwfFacade.getDefaultValueObjectInstance(DynamicEntityNames.FINANCEIRO);
			DynamicVO novoTituloDynamicVO = (DynamicVO) tituloEntityVO;

			PersistentLocalEntity tituloLocalEntity = (PersistentLocalEntity) iteratorDosItens.next();
			DynamicVO tituloOrigemVO = ((DynamicVO) tituloLocalEntity.getValueObject()).wrapInterface(FinanceiroVO.class);

			novoTituloDynamicVO = tituloOrigemVO.buildClone();
			
			novoTituloDynamicVO.setProperty("NUFIN", this.getUltimoNuFin());
			novoTituloDynamicVO.setProperty("AD_COPIATITULOECOM", "S");
			novoTituloDynamicVO.setProperty("NUNOTA", BigDecimal.valueOf(nroUnicoAtual));
			novoTituloDynamicVO.setProperty("CODPARC", BigDecimal.valueOf(265927));
			
			this.mostrarNoConsole("nufin = " + novoTituloDynamicVO.getProperty("NUFIN") + "\n"
					+ "nunota = " + novoTituloDynamicVO.getProperty("NUNOTA") + "\n"
					+ "codTipTit = " + novoTituloDynamicVO.asBigDecimal("CODTIPTIT") + "\n"
					+ "vlrDesdob = " + novoTituloDynamicVO.asBigDecimal("VLRDESDOB") + "\n"
					+ "dtVenc = " + novoTituloDynamicVO.getProperty("DTVENC") + "\n"
					+ "copiaTituloEcom = " + novoTituloDynamicVO.asString("AD_COPIATITULOECOM"));
			
//			tituloLocalEntity.setValueObject((EntityVO) novoTituloDynamicVO);
			
//			dwfFacade.createEntity(DynamicEntityNames.FINANCEIRO, (EntityVO) novoTituloDynamicVO);
//			dwfFacade.saveEntity(DynamicEntityNames.FINANCEIRO, (EntityVO) novoTituloDynamicVO);
			
			System.out.println("NUFIN ===== " + novoTituloDynamicVO.asBigDecimal("NUFIN"));
		}*/
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
	
//	private BigDecimal getUltimoNuFin() throws Exception {
//        BigDecimal nuFin = new java.math.BigDecimal(0);
//
//        JdbcWrapper jdbcV = null;
//        EntityFacade dwfFacadeV = EntityFacadeFactory.getDWFFacade();
//        jdbcV = dwfFacadeV.getJdbcWrapper();
//
//        NativeSql sql = new NativeSql(jdbcV);
//        sql.resetSqlBuf();
//        sql.appendSql("SELECT MAX(NUFIN) + 1 AS NUFIN FROM TGFFIN");
//        ResultSet query = sql.executeQuery();
//        while (query.next()) {
//            nuFin = query.getBigDecimal("NUFIN");
//        }
//        
//        return nuFin;
//	}
	
}
