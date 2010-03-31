/**********************************************************************
 * $Source: /cvsroot/hibiscus/hibiscus.xmlrpc/src/de/willuhn/jameica/hbci/xmlrpc/server/KontoServiceImpl.java,v $
 * $Revision: 1.7 $
 * $Date: 2010/03/31 12:24:51 $
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
import java.util.Date;

import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.datasource.rmi.DBService;
import de.willuhn.jameica.hbci.HBCI;
import de.willuhn.jameica.hbci.rmi.Konto;
import de.willuhn.jameica.hbci.xmlrpc.rmi.KontoService;
import de.willuhn.jameica.hbci.xmlrpc.util.StringUtil;
import de.willuhn.jameica.messaging.QueryMessage;
import de.willuhn.jameica.system.Application;

/**
 * Implementierung des Konto-Service.
 */
public class KontoServiceImpl extends AbstractServiceImpl implements
    KontoService
{

  /**
   * ct.
   * @throws RemoteException
   */
  public KontoServiceImpl() throws RemoteException
  {
    super();
  }

  /**
   * @see de.willuhn.jameica.hbci.xmlrpc.rmi.KontoService#list()
   */
  public String[] list() throws RemoteException
  {
    try
    {
      DBService service = (DBService) Application.getServiceFactory().lookup(HBCI.class,"database");
      DBIterator i = service.createList(Konto.class);
      String[] list = new String[i.size()];
      int count = 0;
      while (i.hasNext())
      {
        Konto k = (Konto) i.next();
        StringBuffer sb = new StringBuffer();
        sb.append(StringUtil.quote(StringUtil.notNull(k.getID())));
        sb.append(":");
        sb.append(StringUtil.quote(StringUtil.notNull(k.getKontonummer())));
        sb.append(":");
        sb.append(StringUtil.quote(StringUtil.notNull(k.getBLZ())));
        sb.append(":");
        sb.append(StringUtil.quote(StringUtil.notNull(k.getBezeichnung())));
        sb.append(":");
        sb.append(StringUtil.quote(StringUtil.notNull(k.getKundennummer())));
        sb.append(":");
        sb.append(StringUtil.quote(StringUtil.notNull(k.getName())));
        
        double saldo = k.getSaldo();
        Date date    = k.getSaldoDatum();
        sb.append(":");
        sb.append(StringUtil.quote(StringUtil.notNull(date != null ? (""+saldo) : "")));
        sb.append(":");
        sb.append(StringUtil.quote(StringUtil.notNull(date != null ? HBCI.DATEFORMAT.format(date) : "")));
        list[count++] = sb.toString();
      }
      return list;
    }
    catch (RemoteException re)
    {
      throw re;
    }
    catch (Exception e)
    {
      throw new RemoteException(e.getMessage(),e);
    }
  }


  /**
   * @see de.willuhn.datasource.Service#getName()
   */
  public String getName() throws RemoteException
  {
    return "[xml-rpc] konto";
  }

  /**
   * @see de.willuhn.jameica.hbci.xmlrpc.rmi.KontoService#checkAccountCRC(java.lang.String, java.lang.String)
   */
  public boolean checkAccountCRC(String blz, String kontonummer) throws RemoteException
  {
    if (blz == null || kontonummer == null || blz.length() == 0 || kontonummer.length() == 0)
      return false;
    QueryMessage msg = new QueryMessage(blz + ":" + kontonummer);
    Application.getMessagingFactory().getMessagingQueue("hibiscus.query.accountcrc").sendSyncMessage(msg);
    Object value = msg.getData();
    if (value == null || !(value instanceof Boolean))
      return false;
    return ((Boolean)value).booleanValue();
  }

  /**
   * @see de.willuhn.jameica.hbci.xmlrpc.rmi.KontoService#getBankname(java.lang.String)
   */
  public String getBankname(String blz) throws RemoteException
  {
    if (blz == null || blz.length() == 0)
      return "";
    QueryMessage msg = new QueryMessage(blz);
    Application.getMessagingFactory().getMessagingQueue("hibiscus.query.bankname").sendSyncMessage(msg);
    Object value = msg.getData();
    return value == null ? "" : value.toString();
  }

}


/*********************************************************************
 * $Log: KontoServiceImpl.java,v $
 * Revision 1.7  2010/03/31 12:24:51  willuhn
 * @N neue XML-RPC-Funktion "find" zum erweiterten Suchen in Auftraegen
 * @C Code-Cleanup
 *
 * Revision 1.6  2009/11/19 22:58:05  willuhn
 * @R Konto#create entfernt - ist Unsinn
 *
 * Revision 1.5  2009/03/08 22:25:47  willuhn
 * @N optionales Quoting
 *
 * Revision 1.4  2007/11/27 15:17:13  willuhn
 * @N CRC-Check und Bankname-Lookup
 *
 * Revision 1.3  2007/07/06 13:21:18  willuhn
 * @N Saldo mit zurueckliefern
 *
 * Revision 1.2  2006/11/07 00:18:11  willuhn
 * *** empty log message ***
 *
 * Revision 1.1  2006/10/31 01:44:09  willuhn
 * @Ninitial checkin
 *
 **********************************************************************/