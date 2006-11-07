/**********************************************************************
 * $Source: /cvsroot/hibiscus/hibiscus.xmlrpc/src/de/willuhn/jameica/hbci/xmlrpc/server/UeberweisungServiceImpl.java,v $
 * $Revision: 1.1 $
 * $Date: 2006/11/07 00:18:11 $
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

import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.datasource.rmi.DBService;
import de.willuhn.datasource.rmi.ObjectNotFoundException;
import de.willuhn.jameica.hbci.HBCI;
import de.willuhn.jameica.hbci.rmi.Konto;
import de.willuhn.jameica.hbci.rmi.Ueberweisung;
import de.willuhn.jameica.hbci.xmlrpc.rmi.UeberweisungService;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;

/**
 * Implementierung des Ueberweisung-Service.
 */
public class UeberweisungServiceImpl extends AbstractServiceImpl implements
    UeberweisungService
{

  /**
   * ct.
   * @throws RemoteException
   */
  public UeberweisungServiceImpl() throws RemoteException
  {
    super();
  }

  /**
   * @see de.willuhn.jameica.hbci.xmlrpc.rmi.UeberweisungService#list()
   */
  public String[] list() throws RemoteException
  {
    try
    {
      DBService service = (DBService) Application.getServiceFactory().lookup(HBCI.class,"database");
      DBIterator i = service.createList(Ueberweisung.class);
      String[] list = new String[i.size()];
      int count = 0;
      while (i.hasNext())
      {
        Ueberweisung u = (Ueberweisung) i.next();
        Konto k = u.getKonto();
        StringBuffer sb = new StringBuffer();
        sb.append(k.getID());
        sb.append(":");
        sb.append(u.getGegenkontoNummer());
        sb.append(":");
        sb.append(u.getGegenkontoBLZ());
        sb.append(":");
        sb.append(u.getGegenkontoName());
        sb.append(":");
        sb.append(u.getZweck());
        sb.append(":");
        sb.append(HBCI.DECIMALFORMAT.format(u.getBetrag()));
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
   * @see de.willuhn.jameica.hbci.xmlrpc.rmi.UeberweisungService#create(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, double)
   */
  public String create(String kontoID, String kto, String blz, String name, String zweck, double betrag) throws RemoteException
  {
    try
    {
      DBService service = (DBService) Application.getServiceFactory().lookup(HBCI.class,"database");
      Konto k = null;
      try
      {
        k = (Konto) service.createObject(Konto.class,kontoID);
      }
      catch (ObjectNotFoundException oe)
      {
        return i18n.tr("Das Konto mit der ID {0} wurde nicht gefunden",kontoID);
      }
      
      Ueberweisung u = (Ueberweisung) service.createObject(Ueberweisung.class,null);
      u.setKonto(k);
      u.setGegenkontoNummer(kto);
      u.setGegenkontoBLZ(blz);
      u.setGegenkontoName(name);
      u.setZweck(zweck);
      u.setBetrag(betrag);
      u.store();
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
      Logger.error("unable to create transfer",e);
      return i18n.tr("Fehler beim Anlegen der Überweisung: {0}", e.getMessage());
    }
  }


  /**
   * @see de.willuhn.datasource.Service#getName()
   */
  public String getName() throws RemoteException
  {
    return "[xml-rpc] ueberweisung";
  }

}


/*********************************************************************
 * $Log: UeberweisungServiceImpl.java,v $
 * Revision 1.1  2006/11/07 00:18:11  willuhn
 * *** empty log message ***
 *
 **********************************************************************/