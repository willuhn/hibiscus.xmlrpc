/**********************************************************************
 * $Source: /cvsroot/hibiscus/hibiscus.xmlrpc/src/de/willuhn/jameica/hbci/xmlrpc/server/SepaUeberweisungServiceImpl.java,v $
 * $Revision: 1.3 $
 * $Date: 2011/01/25 13:43:54 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn.webdesign
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.hbci.xmlrpc.server;

import java.rmi.RemoteException;
import java.util.Map;

import de.willuhn.jameica.hbci.rmi.AuslandsUeberweisung;
import de.willuhn.jameica.hbci.xmlrpc.rmi.SepaUeberweisungService;
import de.willuhn.jameica.hbci.xmlrpc.util.StringUtil;

/**
 * Implementierung des SEPA-Ueberweisung-Service.
 */
public class SepaUeberweisungServiceImpl extends AbstractBaseUeberweisungServiceImpl<AuslandsUeberweisung> implements SepaUeberweisungService
{
  final static String PARAM_ENDTOEND_ID   = "endtoendid";

  /**
   * ct.
   * @throws RemoteException
   */
  public SepaUeberweisungServiceImpl() throws RemoteException
  {
    super();
  }


  /**
   * @see de.willuhn.datasource.Service#getName()
   */
  public String getName() throws RemoteException
  {
    return "[xml-rpc] sepa-ueberweisung";
  }
  
  /**
   * @see de.willuhn.jameica.hbci.xmlrpc.server.AbstractBaseUeberweisungServiceImpl#createParams()
   */
  @Override
  public Map createParams() throws RemoteException
  {
    Map<String,Object> m = super.createParams();
    m.put(PARAM_ENDTOEND_ID, (String) null);
    return m;
  }
  
  /**
   * @see de.willuhn.jameica.hbci.xmlrpc.server.AbstractBaseUeberweisungServiceImpl#beforeStore(java.util.Map, de.willuhn.jameica.hbci.rmi.BaseUeberweisung)
   */
  protected void beforeStore(Map params, AuslandsUeberweisung auftrag) throws Exception
  {
    auftrag.setEndtoEndId((String)params.get(PARAM_ENDTOEND_ID));
  }
  
  /**
   * @see de.willuhn.jameica.hbci.xmlrpc.server.AbstractBaseUeberweisungServiceImpl#afterLoad(java.util.Map, de.willuhn.jameica.hbci.rmi.BaseUeberweisung)
   */
  protected void afterLoad(Map params, AuslandsUeberweisung auftrag) throws Exception
  {
    params.put(PARAM_ENDTOEND_ID,StringUtil.notNull(auftrag.getEndtoEndId()));
  }

  /**
   * @see de.willuhn.jameica.hbci.xmlrpc.server.AbstractBaseUeberweisungServiceImpl#getType()
   */
  Class getType()
  {
    return AuslandsUeberweisung.class;
  }
}
