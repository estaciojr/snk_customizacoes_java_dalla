package br.com.dalla.deive.eventos;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Iterator;

import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.bmp.PersistentLocalEntity;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.jape.util.FinderWrapper;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.vo.EntityVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.modelcore.dwfdata.vo.tgf.FinanceiroVO;
import br.com.sankhya.modelcore.util.DynamicEntityNames;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

public class CopiaTituloEcommerce implements EventoProgramavelJava {

	public void beforeInsert(PersistenceEvent event) throws Exception { }

	public void afterInsert(PersistenceEvent event) throws Exception {
		this.copiarTitulos(event);
	}

	public void beforeUpdate(PersistenceEvent event) throws Exception { }

	public void afterUpdate(PersistenceEvent event) throws Exception { }

	public void beforeDelete(PersistenceEvent event) throws Exception { }

	public void afterDelete(PersistenceEvent event) throws Exception { }

	public void beforeCommit(TransactionContext event) throws Exception { }
	
	public void copiarTitulos(PersistenceEvent event) throws Exception {
		DynamicVO pedidoAtualVO = (DynamicVO) event.getVo();
		
		int nroUnicoAtual = pedidoAtualVO.asInt("NUNOTA");
		int nroUnicoOrigem = pedidoAtualVO.asInt("AD_NUNOTAORIG");
		
		DynamicVO tipOperAtualVO = this.getTgftop(pedidoAtualVO.asInt("CODTIPOPER"), pedidoAtualVO.asTimestamp("DHTIPOPER"));
		
		if (tipOperAtualVO != null) {
			String adNotaEcom = tipOperAtualVO.asString("AD_NOTAECOM");
			
			if (adNotaEcom == null) {
				adNotaEcom = "N";
			}
			
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
						this.copiaTitulos(nroUnicoOrigem, nroUnicoAtual);
						
						this.mostrarNoConsole("nroUnicoAtual = " + nroUnicoAtual + "\n"
								+ "nroUnicoOrigem = " + nroUnicoOrigem + "\n"
								+ "adNotaEcom = " + adNotaEcom + "\n"
								+ "codEmpOrigem = " + codEmpOrigem + "\n"
								+ "codTipOperOrigem = " + codTipOperOrigem);
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
	
	public void copiaTitulos(int nroUnicoPedOrigem, int nroUnicoAtual) throws Exception {
		Collection<?> itensDoPedido = EntityFacadeFactory.getDWFFacade().findByDynamicFinder(new FinderWrapper(DynamicEntityNames.FINANCEIRO, "this.NUNOTA = ?", new Object[] { nroUnicoPedOrigem }));		
		Iterator<?> iteratorDosItens = itensDoPedido.iterator();
		
		EntityFacade dwfFacade = EntityFacadeFactory.getDWFFacade();
		
		while (iteratorDosItens.hasNext()) {
			EntityVO tituloEntityVO = dwfFacade.getDefaultValueObjectInstance(DynamicEntityNames.FINANCEIRO);
			DynamicVO novoTituloDynamicVO = (DynamicVO) tituloEntityVO;
			
			PersistentLocalEntity tituloLocalEntity = (PersistentLocalEntity) iteratorDosItens.next();
			DynamicVO tituloOrigemVO = ((DynamicVO) tituloLocalEntity.getValueObject()).wrapInterface(FinanceiroVO.class);
			
			novoTituloDynamicVO = tituloOrigemVO.buildClone();
			
			novoTituloDynamicVO.setProperty("NUFIN", this.getUltimoNuFin());
			novoTituloDynamicVO.setProperty("AD_COPIATITULOECOM", "S");
			novoTituloDynamicVO.setProperty("NUNOTA", BigDecimal.valueOf(nroUnicoAtual));
			
			this.mostrarNoConsole("nufin = " + novoTituloDynamicVO.getProperty("NUFIN") + "\n"
					+ "nunota = " + novoTituloDynamicVO.getProperty("NUNOTA") + "\n"
					+ "codTipTit = " + novoTituloDynamicVO.asBigDecimal("CODTIPTIT") + "\n"
					+ "vlrDesdob = " + novoTituloDynamicVO.asBigDecimal("VLRDESDOB") + "\n"
					+ "dtVenc = " + novoTituloDynamicVO.getProperty("DTVENC") + "\n"
					+ "copiaTituloEcom = " + novoTituloDynamicVO.asString("AD_COPIATITULOECOM"));
			
			tituloLocalEntity.setValueObject((EntityVO) novoTituloDynamicVO);
			
//			dwfFacade.createEntity(DynamicEntityNames.FINANCEIRO, (EntityVO) novoTituloDynamicVO);
//			dwfFacade.saveEntity(DynamicEntityNames.FINANCEIRO, (EntityVO) novoTituloDynamicVO);
			
			System.out.println("NUFIN ===== " + novoTituloDynamicVO.asBigDecimal("NUFIN"));
		}
	}

	public void mostrarNoConsole(String mensagem) {
		System.out.println("\n====================== Mensagem ======================\n========== Copia título ecommerce ===========\n" + mensagem + "\n======================================================");
	}
	
	private BigDecimal getUltimoNuFin() throws Exception {
        BigDecimal nuFin = new java.math.BigDecimal(0);

        JdbcWrapper jdbcV = null;
        EntityFacade dwfFacadeV = EntityFacadeFactory.getDWFFacade();
        jdbcV = dwfFacadeV.getJdbcWrapper();

        NativeSql sql = new NativeSql(jdbcV);
        sql.resetSqlBuf();
        sql.appendSql("SELECT MAX(NUFIN) + 1 AS NUFIN FROM TGFFIN");
        ResultSet query = sql.executeQuery();
        while (query.next()) {
            nuFin = query.getBigDecimal("NUFIN");
        }
        
        return nuFin;
	}
	
}
