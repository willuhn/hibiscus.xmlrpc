/**********************************************************************
 * $Source: /cvsroot/hibiscus/hibiscus.xmlrpc/src/de/willuhn/jameica/hbci/xmlrpc/rmi/UmsatzService.java,v $
 * $Revision: 1.1 $
 * $Date: 2007/09/30 14:11:20 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn.webdesign
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.hbci.xmlrpc.rmi;

import java.rmi.RemoteException;

import de.willuhn.datasource.Service;

/**
 * XML-RPC-Service zum Zugriff auf Umsaetze.
 */
public interface UmsatzService extends Service
{
  /**
   * Liefert eine Liste der Umsaetze.
   * Jede Zeile entspricht einem Umsatz. Die einzelnen Werte sind durch Doppelpunkt getrennt.
   * @param text Suchbegriff.
   * @param von Datum im Format dd.mm.yyyy.
   * @param bis Datum im Format dd.mm.yyyy.
   * @return Liste der Konten.
   * @throws RemoteException
   */
  public String[] list(String text, String von, String bis) throws RemoteException;
}


/*********************************************************************
 * $Log: UmsatzService.java,v $
 * Revision 1.1  2007/09/30 14:11:20  willuhn
 * @N hibiscus.xmlrpc.umsatz.list
 *
 **********************************************************************/