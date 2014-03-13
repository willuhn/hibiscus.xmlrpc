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

import de.willuhn.jameica.hbci.rmi.SepaSammelUeberweisung;
import de.willuhn.jameica.hbci.rmi.SepaSammelUeberweisungBuchung;
import de.willuhn.jameica.hbci.xmlrpc.rmi.SepaSammelUeberweisungService;
import de.willuhn.jameica.hbci.xmlrpc.util.StringUtil;

/**
 * Implementierung des Service zur Erstellung von SEPA-Sammel-Ueberweisungen.
 */
public class SepaSammelUeberweisungServiceImpl extends AbstractSepaSammelTransferServiceImpl<SepaSammelUeberweisung, SepaSammelUeberweisungBuchung> implements SepaSammelUeberweisungService
{
  /**
   * ct.
   * @throws RemoteException
   */
  public SepaSammelUeberweisungServiceImpl() throws RemoteException
  {
    super();
  }

  /**
   * @see de.willuhn.datasource.Service#getName()
   */
  public String getName() throws RemoteException
  {
    return "[xml-rpc] sammelueberweisung";
  }

  /**
   * @see de.willuhn.jameica.hbci.xmlrpc.server.AbstractSepaSammelTransferServiceImpl#getBuchungType()
   */
  Class getBuchungType() throws RemoteException
  {
    return SepaSammelUeberweisungBuchung.class;
  }

  /**
   * @see de.willuhn.jameica.hbci.xmlrpc.server.AbstractSepaSammelTransferServiceImpl#getTransferType()
   */
  Class getTransferType() throws RemoteException
  {
    return SepaSammelUeberweisung.class;
  }

  /**
   * @see de.willuhn.jameica.hbci.xmlrpc.server.AbstractSepaSammelTransferServiceImpl#afterLoad(java.util.Map, de.willuhn.jameica.hbci.rmi.SepaSammelTransfer)
   */
  protected void afterLoad(Map params,SepaSammelUeberweisung auftrag) throws Exception
  {

	  List<Map<String,Object>> buchungen=new ArrayList<Map<String,Object>>();
	  for(SepaSammelUeberweisungBuchung sb:auftrag.getBuchungen())
	  {
  	  Map<String,Object> buchung = new HashMap<String,Object>();
  	  buchung.put(XmlRpcParameter.PARAM_BUCHUNGEN_KONTONUMMER,      StringUtil.notNull(sb.getGegenkontoNummer()));
  	  buchung.put(XmlRpcParameter.PARAM_BUCHUNGEN_NAME,             StringUtil.notNull(sb.getGegenkontoName()));
  	  buchung.put(XmlRpcParameter.PARAM_BUCHUNGEN_BLZ,              StringUtil.notNull(sb.getGegenkontoBLZ()));
  	  buchung.put(XmlRpcParameter.PARAM_BUCHUNGEN_BETRAG,           sb.getBetrag());
  	  buchung.put(XmlRpcParameter.PARAM_BUCHUNGEN_VERWENDUNGSZWECK, sb.getZweck());
  	  buchung.put(XmlRpcParameter.PARAM_ENDTOEND_ID,                StringUtil.notNull(sb.getEndtoEndId()));
  	  buchungen.add(buchung);
	  }
	  params.put(XmlRpcParameter.PARAM_BUCHUNGEN, buchungen);
  }
}
