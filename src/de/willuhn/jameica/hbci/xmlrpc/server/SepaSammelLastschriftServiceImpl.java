/**********************************************************************
 *
 * Copyright (c) by Olaf Willuhn
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.hbci.xmlrpc.server;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.willuhn.jameica.hbci.HBCI;
import de.willuhn.jameica.hbci.rmi.SepaLastSequenceType;
import de.willuhn.jameica.hbci.rmi.SepaLastType;
import de.willuhn.jameica.hbci.rmi.SepaSammelLastBuchung;
import de.willuhn.jameica.hbci.rmi.SepaSammelLastschrift;
import de.willuhn.jameica.hbci.xmlrpc.rmi.SepaSammelLastschriftService;
import de.willuhn.jameica.hbci.xmlrpc.util.StringUtil;

/**
 * Implementierung des Service zur Erstellung von Sammel-Lastschriften
 */
public class SepaSammelLastschriftServiceImpl extends AbstractSepaSammelTransferServiceImpl<SepaSammelLastschrift, SepaSammelLastBuchung> implements SepaSammelLastschriftService
{
  /**
   * ct.
   * @throws RemoteException
   */
  public SepaSammelLastschriftServiceImpl() throws RemoteException
  {
    super();
  }

  /**
   * @see de.willuhn.datasource.Service#getName()
   */
  public String getName() throws RemoteException
  {
    return "[xml-rpc] sammellastschrift";
  }

  /**
   * @see de.willuhn.jameica.hbci.xmlrpc.server.AbstractSepaSammelTransferServiceImpl#getBuchungType()
   */
  Class getBuchungType() throws RemoteException
  {
    return SepaSammelLastBuchung.class;
  }

  /**
   * @see de.willuhn.jameica.hbci.xmlrpc.server.AbstractSepaSammelTransferServiceImpl#getTransferType()
   */
  Class getTransferType() throws RemoteException
  {
    return SepaSammelLastschrift.class;
  }

  /**
   * @see de.willuhn.jameica.hbci.xmlrpc.server.AbstractSepaSammelTransferServiceImpl#beforeStore(java.util.Map, de.willuhn.jameica.hbci.rmi.SepaSammelTransfer)
   */
  protected void beforeStore(Map params, SepaSammelLastschrift auftrag) throws Exception
  {
	  auftrag.setSequenceType(SepaLastSequenceType.valueOf((String)params.get(XmlRpcParameter.PARAM_SEQUENCETYPE)));
	  auftrag.setType(SepaLastType.valueOf((String) params.get(XmlRpcParameter.PARAM_SEPATYPE)));
	  auftrag.setTargetDate(de.willuhn.jameica.hbci.xmlrpc.util.DateUtil.parse(params.get(XmlRpcParameter.PARAM_TARGETDATE)));
  }
  
  /**
   * @see de.willuhn.jameica.hbci.xmlrpc.server.AbstractSepaSammelTransferServiceImpl#beforeStoreBuchung(java.util.Map, de.willuhn.jameica.hbci.rmi.SepaSammelTransferBuchung)
   */
  protected void beforeStoreBuchung(Map params, SepaSammelLastBuchung buchung) throws Exception
  {
    buchung.setCreditorId((String)params.get(XmlRpcParameter.PARAM_CREDITOR_ID));
    buchung.setSignatureDate(de.willuhn.jameica.hbci.xmlrpc.util.DateUtil.parse(params.get(XmlRpcParameter.PARAM_SIGNATUREDATE)));
    buchung.setMandateId((String)params.get(XmlRpcParameter.PARAM_MANDATE_ID));
  }
  
  /**
   * @see de.willuhn.jameica.hbci.xmlrpc.server.AbstractSepaSammelTransferServiceImpl#afterLoad(java.util.Map, de.willuhn.jameica.hbci.rmi.SepaSammelTransfer)
   */
  protected void afterLoad(Map params,SepaSammelLastschrift auftrag) throws Exception
  {
	  params.put(XmlRpcParameter.PARAM_SEQUENCETYPE,  auftrag.getSequenceType().name());
	  params.put(XmlRpcParameter.PARAM_SEPATYPE,      auftrag.getType() != null ? auftrag.getType().name() : StringUtil.notNull(null));
	  params.put(XmlRpcParameter.PARAM_TARGETDATE,    auftrag.getTargetDate() != null ? HBCI.DATEFORMAT.format(auftrag.getTargetDate()) : StringUtil.notNull(null));


	  List<Map<String,Object>> buchungen=new ArrayList<Map<String,Object>>();
	  for(SepaSammelLastBuchung lb:auftrag.getBuchungen())
	  {
  	  Map<String,Object> buchung = new HashMap<String,Object>();
  	  buchung.put(XmlRpcParameter.PARAM_BUCHUNGEN_KONTONUMMER,      StringUtil.notNull(lb.getGegenkontoNummer()));
  	  buchung.put(XmlRpcParameter.PARAM_BUCHUNGEN_NAME,             StringUtil.notNull(lb.getGegenkontoName()));
  	  buchung.put(XmlRpcParameter.PARAM_BUCHUNGEN_BLZ,              StringUtil.notNull(lb.getGegenkontoBLZ()));
  	  buchung.put(XmlRpcParameter.PARAM_BUCHUNGEN_BETRAG,           lb.getBetrag());
  	  buchung.put(XmlRpcParameter.PARAM_BUCHUNGEN_VERWENDUNGSZWECK, lb.getZweck());
  	  buchung.put(XmlRpcParameter.PARAM_ENDTOEND_ID,                StringUtil.notNull(lb.getEndtoEndId()));
  	  buchung.put(XmlRpcParameter.PARAM_CREDITOR_ID,                lb.getCreditorId());
  	  buchung.put(XmlRpcParameter.PARAM_MANDATE_ID,                 lb.getMandateId());
  	  buchung.put(XmlRpcParameter.PARAM_SIGNATUREDATE,              HBCI.DATEFORMAT.format(lb.getSignatureDate()));
  	  buchungen.add(buchung);
	  }
	  params.put(XmlRpcParameter.PARAM_BUCHUNGEN, buchungen);
  }
}
