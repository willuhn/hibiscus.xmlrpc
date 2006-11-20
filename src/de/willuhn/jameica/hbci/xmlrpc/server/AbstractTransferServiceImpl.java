/**********************************************************************
 * $Source: /cvsroot/hibiscus/hibiscus.xmlrpc/src/de/willuhn/jameica/hbci/xmlrpc/server/Attic/AbstractTransferServiceImpl.java,v $
 * $Revision: 1.2 $
 * $Date: 2006/11/20 22:41:10 $
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
import de.willuhn.jameica.hbci.rmi.Transfer;
import de.willuhn.jameica.hbci.xmlrpc.rmi.TransferService;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;

/**
 * Implementierung des Transfer-Service.
 */
public abstract class AbstractTransferServiceImpl extends AbstractServiceImpl implements TransferService
{

  /**
   * ct.
   * @throws RemoteException
   */
  public AbstractTransferServiceImpl() throws RemoteException
  {
    super();
  }
  
  /**
   * Liefert den Objekttyp des Transfers.
   * @return Objekt-Typ.
   */
  abstract Class getTransferType();

  /**
   * @see de.willuhn.jameica.hbci.xmlrpc.rmi.TransferService#list()
   */
  public String[] list() throws RemoteException
  {
    try
    {
      DBService service = (DBService) Application.getServiceFactory().lookup(HBCI.class,"database");
      DBIterator i = service.createList(getTransferType());
      String[] list = new String[i.size()];
      int count = 0;
      while (i.hasNext())
      {
        Transfer t = (Transfer) i.next();
        Konto k = t.getKonto();
        StringBuffer sb = new StringBuffer();
        sb.append(k.getID());
        sb.append(":");
        sb.append(t.getGegenkontoNummer());
        sb.append(":");
        sb.append(t.getGegenkontoBLZ());
        sb.append(":");
        sb.append(t.getGegenkontoName());
        sb.append(":");
        sb.append(t.getZweck());
        sb.append(":");
        sb.append(HBCI.DECIMALFORMAT.format(t.getBetrag()));
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
   * @see de.willuhn.jameica.hbci.xmlrpc.rmi.TransferService#create(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, double)
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
      
      Transfer t = (Transfer) service.createObject(getTransferType(),null);
      t.setKonto(k);
      t.setGegenkontoNummer(kto);
      t.setGegenkontoBLZ(blz);
      t.setGegenkontoName(name);
      t.setZweck(zweck);
      t.setBetrag(betrag);
      t.store();
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
      return i18n.tr("Fehler beim Anlegen des Auftrages: {0}", e.getMessage());
    }
  }
}


/*********************************************************************
 * $Log: AbstractTransferServiceImpl.java,v $
 * Revision 1.2  2006/11/20 22:41:10  willuhn
 * @B wrong transfer type
 *
 * Revision 1.1  2006/11/16 22:11:26  willuhn
 * @N Added lastschrift support
 *
 * Revision 1.1  2006/11/07 00:18:11  willuhn
 * *** empty log message ***
 *
 **********************************************************************/