/**********************************************************************
 * $Source: /cvsroot/hibiscus/hibiscus.xmlrpc/src/de/willuhn/jameica/hbci/xmlrpc/server/KontoServiceImpl.java,v $
 * $Revision: 1.3 $
 * $Date: 2007/07/06 13:21:18 $
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
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;

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
        sb.append(k.getID());
        sb.append(":");
        sb.append(k.getKontonummer());
        sb.append(":");
        sb.append(k.getBLZ());
        sb.append(":");
        sb.append(k.getBezeichnung());
        sb.append(":");
        sb.append(k.getKundennummer());
        sb.append(":");
        sb.append(k.getName());
        
        double saldo = k.getSaldo();
        Date date    = k.getSaldoDatum();
        sb.append(":");
        sb.append(date != null ? (""+saldo) : "");
        sb.append(":");
        sb.append(date != null ? HBCI.DATEFORMAT.format(date) : "");
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
      Logger.error("unable to load list",e);
    }
    return null;
  }


  /**
   * @see de.willuhn.jameica.hbci.xmlrpc.rmi.KontoService#create(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
   */
  public String create(String kontonummer, String blz, String name, String kundennummer) throws RemoteException
  {
    try
    {
      DBService service = (DBService) Application.getServiceFactory().lookup(HBCI.class,"database");
      Konto k = (Konto) service.createObject(Konto.class,null);
      k.setKontonummer(kontonummer);
      k.setBLZ(blz);
      k.setName(name);
      k.setKundennummer(kundennummer);
      k.store();
      return null;
    }
    catch (ApplicationException ae)
    {
      return ae.getLocalizedMessage();
    }
    catch (RemoteException re)
    {
      throw re;
    }
    catch (Exception e)
    {
      Logger.error("unable to create account",e);
      return i18n.tr("Fehler beim Anlegen des Kontos: {0}", e.getMessage());
    }
  }


  /**
   * @see de.willuhn.datasource.Service#getName()
   */
  public String getName() throws RemoteException
  {
    return "[xml-rpc] konto";
  }

}


/*********************************************************************
 * $Log: KontoServiceImpl.java,v $
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